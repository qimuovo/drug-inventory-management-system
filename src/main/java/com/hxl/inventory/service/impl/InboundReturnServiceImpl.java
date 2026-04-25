package com.hxl.inventory.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.exception.ErrorCode;
import com.hxl.inventory.exception.ThrowUtils;
import com.hxl.inventory.model.dto.inboundreturn.InboundReturnAddItemRequest;
import com.hxl.inventory.model.dto.inboundreturn.InboundReturnAddRequest;
import com.hxl.inventory.model.dto.inboundreturn.InboundReturnQueryRequest;
import com.hxl.inventory.model.entity.Drug;
import com.hxl.inventory.model.entity.InboundItem;
import com.hxl.inventory.model.entity.Inventory;
import com.hxl.inventory.model.entity.InboundReturn;
import com.hxl.inventory.model.entity.User;
import com.hxl.inventory.model.vo.InboundReturnVO;
import com.hxl.inventory.service.DrugService;
import com.hxl.inventory.service.InboundItemService;
import com.hxl.inventory.service.InventoryService;
import com.hxl.inventory.service.InboundReturnService;
import com.hxl.inventory.mapper.InboundReturnMapper;
import com.hxl.inventory.utils.LoginUserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 29358
* @description 针对表【t_inbound_return(入库退货)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class InboundReturnServiceImpl extends ServiceImpl<InboundReturnMapper, InboundReturn>
    implements InboundReturnService{

    @Resource
    private InboundItemService inboundItemService;

    @Resource
    private InventoryService inventoryService;

    @Resource
    private DrugService drugService;

    private static final List<String> SORT_FIELD_WHITE_LIST = Arrays.asList(
            "id", "return_date", "create_time"
    );

    @Override
    public QueryWrapper<InboundReturn> getQueryWrapper(InboundReturnQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        QueryWrapper<InboundReturn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(queryRequest.getInboundItemId() != null, "inbound_item_id", queryRequest.getInboundItemId());
        String sortField = queryRequest.getSortField();
        boolean isAsc = "ascend".equalsIgnoreCase(queryRequest.getSortOrder());
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField) && SORT_FIELD_WHITE_LIST.contains(sortField), isAsc, sortField);
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addInboundReturn(InboundReturnAddRequest addRequest) {
        ThrowUtils.throwIf(addRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        List<InboundReturnAddItemRequest> itemList = addRequest.getItemList();
        ThrowUtils.throwIf(itemList == null || itemList.isEmpty(), ErrorCode.PARAMS_ERROR, "退货明细不能为空");

        User loginUser = LoginUserHolder.getUser();
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        Date returnDate = addRequest.getReturnDate() == null ? new Date() : addRequest.getReturnDate();
        String commonReason = addRequest.getReason();
        for (InboundReturnAddItemRequest itemRequest : itemList) {
            validateInboundReturnItem(itemRequest);

            // 退货基于入库明细执行，确保批次和药品信息可追溯
            InboundItem inboundItem = inboundItemService.getById(itemRequest.getInboundItemId());
            ThrowUtils.throwIf(inboundItem == null, ErrorCode.NOT_FOUND_ERROR, "入库明细不存在");
            ThrowUtils.throwIf(itemRequest.getReturnQuantity() > inboundItem.getQuantity(),
                    ErrorCode.OPERATION_ERROR, "退货数量不能大于入库数量");

            // 先扣减对应批次库存，再落退货记录，统一受事务保护
            updateInventoryWhenReturn(inboundItem.getDrugId(), inboundItem.getBatchNo(), itemRequest.getReturnQuantity());

            InboundReturn inboundReturn = new InboundReturn();
            inboundReturn.setInboundItemId(itemRequest.getInboundItemId());
            inboundReturn.setReturnQuantity(itemRequest.getReturnQuantity());
            inboundReturn.setReturnPrice(itemRequest.getReturnPrice());
            // 明细原因优先，未传则回退使用请求级公共原因
            inboundReturn.setReason(StrUtil.isNotBlank(itemRequest.getReason()) ? itemRequest.getReason() : commonReason);
            inboundReturn.setOperatorId(loginUser.getId());
            inboundReturn.setReturnDate(returnDate);
            boolean saveResult = this.save(inboundReturn);
            ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "新增入库退货失败");
        }
        return true;
    }

    @Override
    public Page<InboundReturnVO> listInboundReturnByPage(InboundReturnQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0, ErrorCode.PARAMS_ERROR, "分页参数错误");

        Page<InboundReturn> returnPage = this.page(new Page<>(current, pageSize), this.getQueryWrapper(queryRequest));
        List<InboundReturn> returnList = returnPage.getRecords();
        if (returnList.isEmpty()) {
            return new Page<>(current, pageSize, 0);
        }

        List<Long> inboundItemIds = returnList.stream().map(InboundReturn::getInboundItemId).distinct().collect(Collectors.toList());
        // 批量拉取入库明细并转 map，减少重复查库
        Map<Long, InboundItem> inboundItemMap = inboundItemService.listByIds(inboundItemIds).stream()
                .collect(Collectors.toMap(InboundItem::getId, item -> item));

        List<Long> drugIds = inboundItemMap.values().stream().map(InboundItem::getDrugId).distinct().collect(Collectors.toList());
        Map<Long, Drug> drugMap = new HashMap<>();
        if (!drugIds.isEmpty()) {
            // 批量查询药品，补齐药品名称/编码展示字段
            drugMap = drugService.listByIds(drugIds).stream().collect(Collectors.toMap(Drug::getId, drug -> drug));
        }

        Map<Long, Drug> finalDrugMap = drugMap;
        List<InboundReturnVO> voList = returnList.stream().map(inboundReturn -> {
            InboundReturnVO vo = new InboundReturnVO();
            BeanUtils.copyProperties(inboundReturn, vo);
            InboundItem inboundItem = inboundItemMap.get(inboundReturn.getInboundItemId());
            if (inboundItem != null) {
                vo.setDrugId(inboundItem.getDrugId());
                vo.setBatchNo(inboundItem.getBatchNo());
                Drug drug = finalDrugMap.get(inboundItem.getDrugId());
                if (drug != null) {
                    vo.setDrugName(drug.getDrugName());
                    vo.setDrugCode(drug.getDrugCode());
                }
            }
            return vo;
        }).collect(Collectors.toList());

        Page<InboundReturnVO> voPage = new Page<>(returnPage.getCurrent(), returnPage.getSize(), returnPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    private void validateInboundReturnItem(InboundReturnAddItemRequest itemRequest) {
        ThrowUtils.throwIf(itemRequest == null, ErrorCode.PARAMS_ERROR, "退货明细不能为空");
        ThrowUtils.throwIf(itemRequest.getInboundItemId() == null || itemRequest.getInboundItemId() <= 0,
                ErrorCode.PARAMS_ERROR, "入库明细ID错误");
        ThrowUtils.throwIf(itemRequest.getReturnQuantity() == null || itemRequest.getReturnQuantity() <= 0,
                ErrorCode.PARAMS_ERROR, "退货数量错误");
        ThrowUtils.throwIf(itemRequest.getReturnPrice() == null || itemRequest.getReturnPrice().doubleValue() < 0,
                ErrorCode.PARAMS_ERROR, "退货价格错误");
    }

    private void updateInventoryWhenReturn(Long drugId, String batchNo, Integer returnQuantity) {
        LambdaQueryWrapper<Inventory> inventoryQuery = new LambdaQueryWrapper<>();
        inventoryQuery.eq(Inventory::getDrugId, drugId);
        inventoryQuery.eq(Inventory::getBatchNo, batchNo);
        Inventory inventory = inventoryService.getOne(inventoryQuery);
        ThrowUtils.throwIf(inventory == null, ErrorCode.NOT_FOUND_ERROR, "对应批次库存不存在");
        ThrowUtils.throwIf(inventory.getQuantity() < returnQuantity, ErrorCode.OPERATION_ERROR, "退货数量超过当前库存");
        // 退货会减少可用库存
        inventory.setQuantity(inventory.getQuantity() - returnQuantity);
        boolean updateResult = inventoryService.updateById(inventory);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新库存失败");
    }
}




