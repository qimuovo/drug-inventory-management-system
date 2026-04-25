package com.hxl.inventory.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.exception.ErrorCode;
import com.hxl.inventory.exception.ThrowUtils;
import com.hxl.inventory.model.dto.inbound.InboundAddItemRequest;
import com.hxl.inventory.model.dto.inbound.InboundAddRequest;
import com.hxl.inventory.model.dto.inbound.InboundQueryRequest;
import com.hxl.inventory.model.entity.Drug;
import com.hxl.inventory.model.entity.Inbound;
import com.hxl.inventory.model.entity.InboundItem;
import com.hxl.inventory.model.entity.Inventory;
import com.hxl.inventory.model.entity.User;
import com.hxl.inventory.model.vo.InboundItemVO;
import com.hxl.inventory.model.vo.InboundVO;
import com.hxl.inventory.service.DrugService;
import com.hxl.inventory.service.InboundItemService;
import com.hxl.inventory.service.InboundService;
import com.hxl.inventory.service.InventoryService;
import com.hxl.inventory.mapper.InboundMapper;
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
* @description 针对表【t_inbound(入库单)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class InboundServiceImpl extends ServiceImpl<InboundMapper, Inbound>
    implements InboundService{

    @Resource
    private InboundItemService inboundItemService;

    @Resource
    private InventoryService inventoryService;

    @Resource
    private DrugService drugService;

    private static final List<String> SORT_FIELD_WHITE_LIST = Arrays.asList(
            "id", "inbound_no", "inbound_date", "create_time", "update_time"
    );

    @Override
    public QueryWrapper<Inbound> getQueryWrapper(InboundQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        QueryWrapper<Inbound> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(queryRequest.getInboundNo()), "inbound_no", queryRequest.getInboundNo());
        String sortField = queryRequest.getSortField();
        boolean isAsc = "ascend".equalsIgnoreCase(queryRequest.getSortOrder());
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField) && SORT_FIELD_WHITE_LIST.contains(sortField), isAsc, sortField);
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addInbound(InboundAddRequest addRequest) {
        ThrowUtils.throwIf(addRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(StrUtil.isBlank(addRequest.getInboundNo()), ErrorCode.PARAMS_ERROR, "入库单号不能为空");
        List<InboundAddItemRequest> itemList = addRequest.getItemList();
        ThrowUtils.throwIf(itemList == null || itemList.isEmpty(), ErrorCode.PARAMS_ERROR, "入库明细不能为空");

        User loginUser = LoginUserHolder.getUser();
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        Inbound inbound = new Inbound();
        // 一次入库请求只生成一张主单，明细共享同一入库单号
        inbound.setInboundNo(addRequest.getInboundNo());
        inbound.setInboundDate(addRequest.getInboundDate() == null ? new Date() : addRequest.getInboundDate());
        inbound.setRemark(addRequest.getRemark());
        inbound.setOperatorId(loginUser.getId());
        boolean saveInboundResult = this.save(inbound);
        ThrowUtils.throwIf(!saveInboundResult, ErrorCode.OPERATION_ERROR, "新增入库单失败");

        for (InboundAddItemRequest itemRequest : itemList) {
            validateInboundItem(itemRequest);
            InboundItem inboundItem = new InboundItem();
            inboundItem.setInboundId(inbound.getId());
            inboundItem.setDrugId(itemRequest.getDrugId());
            inboundItem.setBatchNo(itemRequest.getBatchNo());
            inboundItem.setQuantity(itemRequest.getQuantity());
            inboundItem.setPrice(itemRequest.getPrice());
            inboundItem.setAmount(itemRequest.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            boolean saveItemResult = inboundItemService.save(inboundItem);
            ThrowUtils.throwIf(!saveItemResult, ErrorCode.OPERATION_ERROR, "新增入库明细失败");

            // 按 药品+批号 维度维护库存，不同批次独立管理
            updateInventoryWhenInbound(itemRequest.getDrugId(), itemRequest.getBatchNo(), itemRequest.getQuantity());
        }
        return true;
    }

    @Override
    public Page<InboundVO> listInboundByPage(InboundQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0, ErrorCode.PARAMS_ERROR, "分页参数错误");

        Page<Inbound> inboundPage = this.page(new Page<>(current, pageSize), this.getQueryWrapper(queryRequest));
        List<Inbound> inboundList = inboundPage.getRecords();
        if (inboundList.isEmpty()) {
            return new Page<>(current, pageSize, 0);
        }

        List<Long> inboundIds = inboundList.stream().map(Inbound::getId).collect(Collectors.toList());
        List<InboundItem> inboundItemList = inboundItemService.list(
                new LambdaQueryWrapper<InboundItem>().in(InboundItem::getInboundId, inboundIds));
        // 先按入库单分组，便于后续组装主子结构的 VO
        Map<Long, List<InboundItem>> inboundItemMap = inboundItemList.stream()
                .collect(Collectors.groupingBy(InboundItem::getInboundId));

        List<Long> drugIds = inboundItemList.stream()
                .map(InboundItem::getDrugId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Drug> tempDrugMap = new HashMap<>();
        if (!drugIds.isEmpty()) {
            // 批量查询药品，避免循环内逐条查询导致 N+1 问题
            tempDrugMap = drugService.listByIds(drugIds).stream().collect(Collectors.toMap(Drug::getId, drug -> drug));
        }
        final Map<Long, Drug> drugMap = tempDrugMap;

        List<InboundVO> inboundVOList = inboundList.stream().map(inbound -> {
            InboundVO inboundVO = new InboundVO();
            BeanUtils.copyProperties(inbound, inboundVO);
            List<InboundItem> items = inboundItemMap.getOrDefault(inbound.getId(), Collections.emptyList());
            List<InboundItemVO> itemVOList = items.stream().map(item -> {
                InboundItemVO itemVO = new InboundItemVO();
                BeanUtils.copyProperties(item, itemVO);
                Drug drug = drugMap.get(item.getDrugId());
                if (drug != null) {
                    itemVO.setDrugName(drug.getDrugName());
                    itemVO.setDrugCode(drug.getDrugCode());
                }
                return itemVO;
            }).collect(Collectors.toList());
            inboundVO.setItemList(itemVOList);
            return inboundVO;
        }).collect(Collectors.toList());

        Page<InboundVO> voPage = new Page<>(inboundPage.getCurrent(), inboundPage.getSize(), inboundPage.getTotal());
        voPage.setRecords(inboundVOList);
        return voPage;
    }

    private void validateInboundItem(InboundAddItemRequest itemRequest) {
        ThrowUtils.throwIf(itemRequest == null, ErrorCode.PARAMS_ERROR, "入库明细不能为空");
        ThrowUtils.throwIf(itemRequest.getDrugId() == null || itemRequest.getDrugId() <= 0, ErrorCode.PARAMS_ERROR, "药品ID错误");
        ThrowUtils.throwIf(StrUtil.isBlank(itemRequest.getBatchNo()), ErrorCode.PARAMS_ERROR, "批号不能为空");
        ThrowUtils.throwIf(itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0, ErrorCode.PARAMS_ERROR, "入库数量错误");
        ThrowUtils.throwIf(itemRequest.getPrice() == null || itemRequest.getPrice().compareTo(BigDecimal.ZERO) < 0, ErrorCode.PARAMS_ERROR, "入库价格错误");
        boolean drugExists = drugService.getById(itemRequest.getDrugId()) != null;
        ThrowUtils.throwIf(!drugExists, ErrorCode.NOT_FOUND_ERROR, "药品不存在");
    }

    private void updateInventoryWhenInbound(Long drugId, String batchNo, Integer quantity) {
        LambdaQueryWrapper<Inventory> inventoryQuery = new LambdaQueryWrapper<>();
        inventoryQuery.eq(Inventory::getDrugId, drugId);
        inventoryQuery.eq(Inventory::getBatchNo, batchNo);
        Inventory inventory = inventoryService.getOne(inventoryQuery);
        if (inventory == null) {
            // 首次出现该药品批次，直接创建库存记录
            inventory = new Inventory();
            inventory.setDrugId(drugId);
            inventory.setBatchNo(batchNo);
            inventory.setQuantity(quantity);
            boolean saveInventoryResult = inventoryService.save(inventory);
            ThrowUtils.throwIf(!saveInventoryResult, ErrorCode.OPERATION_ERROR, "新增库存失败");
            return;
        }
        // 已有批次库存则累加数量
        inventory.setQuantity(inventory.getQuantity() + quantity);
        boolean updateInventoryResult = inventoryService.updateById(inventory);
        ThrowUtils.throwIf(!updateInventoryResult, ErrorCode.OPERATION_ERROR, "更新库存失败");
    }
}




