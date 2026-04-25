package com.hxl.inventory.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.exception.ErrorCode;
import com.hxl.inventory.exception.ThrowUtils;
import com.hxl.inventory.model.dto.outboundreturn.OutboundReturnAddItemRequest;
import com.hxl.inventory.model.dto.outboundreturn.OutboundReturnAddRequest;
import com.hxl.inventory.model.dto.outboundreturn.OutboundReturnQueryRequest;
import com.hxl.inventory.model.entity.Drug;
import com.hxl.inventory.model.entity.Inventory;
import com.hxl.inventory.model.entity.OutboundItem;
import com.hxl.inventory.model.entity.Outbound;
import com.hxl.inventory.model.entity.OutboundReturn;
import com.hxl.inventory.model.entity.User;
import com.hxl.inventory.model.vo.OutboundReturnVO;
import com.hxl.inventory.service.DrugService;
import com.hxl.inventory.service.InventoryService;
import com.hxl.inventory.service.OutboundItemService;
import com.hxl.inventory.service.OutboundService;
import com.hxl.inventory.service.OutboundReturnService;
import com.hxl.inventory.mapper.OutboundReturnMapper;
import com.hxl.inventory.utils.LoginUserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 29358
* @description 针对表【t_outbound_return(出库退库)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class OutboundReturnServiceImpl extends ServiceImpl<OutboundReturnMapper, OutboundReturn>
    implements OutboundReturnService{

    @Resource
    private OutboundItemService outboundItemService;

    @Resource
    private InventoryService inventoryService;

    @Resource
    private DrugService drugService;

    @Resource
    private OutboundService outboundService;

    /**
     * 允许排序字段
     */
    private static final List<String> SORT_FIELD_WHITE_LIST = Arrays.asList(
            "id", "return_date", "create_time"
    );

    @Override
    public QueryWrapper<OutboundReturn> getQueryWrapper(OutboundReturnQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        QueryWrapper<OutboundReturn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(queryRequest.getOutboundItemId() != null, "outbound_item_id", queryRequest.getOutboundItemId());
        String search = queryRequest.getSearch();
        if (StrUtil.isNotBlank(search)) {
            List<Long> drugIds = drugService.lambdaQuery()
                    .and(wrapper -> wrapper.like(Drug::getDrugName, search).or().like(Drug::getDrugCode, search))
                    .list()
                    .stream()
                    .map(Drug::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            if (drugIds.isEmpty()) {
                queryWrapper.eq("id", -1L);
            } else {
                List<Long> outboundItemIds = outboundItemService.lambdaQuery()
                        .in(OutboundItem::getDrugId, drugIds)
                        .list()
                        .stream()
                        .map(OutboundItem::getId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .collect(Collectors.toList());
                if (outboundItemIds.isEmpty()) {
                    queryWrapper.eq("id", -1L);
                } else {
                    queryWrapper.in("outbound_item_id", outboundItemIds);
                }
            }
        }
        String sortField = queryRequest.getSortField();
        boolean isAsc = "ascend".equalsIgnoreCase(queryRequest.getSortOrder());
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField) && SORT_FIELD_WHITE_LIST.contains(sortField), isAsc, sortField);
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOutboundReturn(OutboundReturnAddRequest addRequest) {
        ThrowUtils.throwIf(addRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        List<OutboundReturnAddItemRequest> itemList = addRequest.getItemList();
        ThrowUtils.throwIf(itemList == null || itemList.isEmpty(), ErrorCode.PARAMS_ERROR, "退库明细不能为空");

        User loginUser = LoginUserHolder.getUser();
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        Date returnDate = addRequest.getReturnDate() == null ? new Date() : addRequest.getReturnDate();
        String commonReason = addRequest.getReason();
        for (OutboundReturnAddItemRequest itemRequest : itemList) {
            validateOutboundReturnItem(itemRequest);

            // 退库基于出库明细执行，确保批次和药品信息可追溯
            OutboundItem outboundItem = outboundItemService.getById(itemRequest.getOutboundItemId());
            ThrowUtils.throwIf(outboundItem == null, ErrorCode.NOT_FOUND_ERROR, "出库明细不存在");

            // 校验累计退库数量不能超过该出库明细的原始出库数量
            int returnedQuantity = this.lambdaQuery()
                    .eq(OutboundReturn::getOutboundItemId, itemRequest.getOutboundItemId())
                    .list()
                    .stream()
                    .map(OutboundReturn::getReturnQuantity)
                    .filter(Objects::nonNull)
                    .mapToInt(Integer::intValue)
                    .sum();
            ThrowUtils.throwIf(returnedQuantity + itemRequest.getReturnQuantity() > outboundItem.getQuantity(),
                    ErrorCode.OPERATION_ERROR, "累计退库数量不能超过出库数量");

            // 退库会回补对应批次库存
            increaseInventoryWhenOutboundReturn(outboundItem.getDrugId(), outboundItem.getBatchNo(), itemRequest.getReturnQuantity());

            OutboundReturn outboundReturn = new OutboundReturn();
            outboundReturn.setOutboundItemId(itemRequest.getOutboundItemId());
            outboundReturn.setReturnQuantity(itemRequest.getReturnQuantity());
            outboundReturn.setReturnPrice(itemRequest.getReturnPrice());
            // 明细原因优先，未传则回退使用请求级公共原因
            outboundReturn.setReason(StrUtil.isNotBlank(itemRequest.getReason()) ? itemRequest.getReason() : commonReason);
            outboundReturn.setOperatorId(loginUser.getId());
            outboundReturn.setReturnDate(returnDate);
            boolean saveResult = this.save(outboundReturn);
            ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "新增出库退库失败");
        }
        return true;
    }

    @Override
    public Page<OutboundReturnVO> listOutboundReturnByPage(OutboundReturnQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0, ErrorCode.PARAMS_ERROR, "分页参数错误");

        Page<OutboundReturn> returnPage = this.page(new Page<>(current, pageSize), this.getQueryWrapper(queryRequest));
        List<OutboundReturn> returnList = returnPage.getRecords();
        if (returnList.isEmpty()) {
            return new Page<>(current, pageSize, 0);
        }

        List<Long> outboundItemIds = returnList.stream().map(OutboundReturn::getOutboundItemId).distinct().collect(Collectors.toList());
        // 批量拉取出库明细并转 map，减少重复查库
        Map<Long, OutboundItem> outboundItemMap = outboundItemService.listByIds(outboundItemIds).stream()
                .collect(Collectors.toMap(OutboundItem::getId, item -> item));

        List<Long> drugIds = outboundItemMap.values().stream()
                .map(OutboundItem::getDrugId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Drug> tempDrugMap = new HashMap<>();
        if (!drugIds.isEmpty()) {
            // 批量查询药品，补齐药品名称/编码展示字段
            tempDrugMap = drugService.listByIds(drugIds).stream().collect(Collectors.toMap(Drug::getId, drug -> drug));
        }
        final Map<Long, Drug> drugMap = tempDrugMap;
        List<Long> outboundIds = outboundItemMap.values().stream()
                .map(OutboundItem::getOutboundId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Outbound> outboundMap = new HashMap<>();
        if (!outboundIds.isEmpty()) {
            outboundMap = outboundService.listByIds(outboundIds).stream().collect(Collectors.toMap(Outbound::getId, outbound -> outbound));
        }
        final Map<Long, Outbound> finalOutboundMap = outboundMap;

        List<OutboundReturnVO> voList = returnList.stream().map(outboundReturn -> {
            OutboundReturnVO vo = new OutboundReturnVO();
            BeanUtils.copyProperties(outboundReturn, vo);
            OutboundItem outboundItem = outboundItemMap.get(outboundReturn.getOutboundItemId());
            if (outboundItem != null) {
                vo.setDrugId(outboundItem.getDrugId());
                vo.setBatchNo(outboundItem.getBatchNo());
                Outbound outbound = finalOutboundMap.get(outboundItem.getOutboundId());
                if (outbound != null) {
                    vo.setOutboundNo(outbound.getOutboundNo());
                }
                Drug drug = drugMap.get(outboundItem.getDrugId());
                if (drug != null) {
                    vo.setDrugName(drug.getDrugName());
                    vo.setDrugCode(drug.getDrugCode());
                }
            }
            return vo;
        }).collect(Collectors.toList());

        Page<OutboundReturnVO> voPage = new Page<>(returnPage.getCurrent(), returnPage.getSize(), returnPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 退库明细参数校验
     */
    private void validateOutboundReturnItem(OutboundReturnAddItemRequest itemRequest) {
        ThrowUtils.throwIf(itemRequest == null, ErrorCode.PARAMS_ERROR, "退库明细不能为空");
        ThrowUtils.throwIf(itemRequest.getOutboundItemId() == null || itemRequest.getOutboundItemId() <= 0,
                ErrorCode.PARAMS_ERROR, "出库明细ID错误");
        ThrowUtils.throwIf(itemRequest.getReturnQuantity() == null || itemRequest.getReturnQuantity() <= 0,
                ErrorCode.PARAMS_ERROR, "退库数量错误");
        ThrowUtils.throwIf(itemRequest.getReturnPrice() == null || itemRequest.getReturnPrice().compareTo(BigDecimal.ZERO) < 0,
                ErrorCode.PARAMS_ERROR, "退库价格错误");
    }

    /**
     * 出库退库回补库存（按药品+批号）
     */
    private void increaseInventoryWhenOutboundReturn(Long drugId, String batchNo, Integer returnQuantity) {
        LambdaQueryWrapper<Inventory> inventoryQuery = new LambdaQueryWrapper<>();
        inventoryQuery.eq(Inventory::getDrugId, drugId);
        inventoryQuery.eq(Inventory::getBatchNo, batchNo);
        Inventory inventory = inventoryService.getOne(inventoryQuery);
        if (inventory == null) {
            inventory = new Inventory();
            inventory.setDrugId(drugId);
            inventory.setBatchNo(batchNo);
            inventory.setQuantity(returnQuantity);
            boolean saveResult = inventoryService.save(inventory);
            ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR, "新增库存失败");
            return;
        }
        inventory.setQuantity(inventory.getQuantity() + returnQuantity);
        boolean updateResult = inventoryService.updateById(inventory);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新库存失败");
    }
}




