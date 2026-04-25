package com.hxl.inventory.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.exception.ErrorCode;
import com.hxl.inventory.exception.ThrowUtils;
import com.hxl.inventory.model.dto.outbound.OutboundAddItemRequest;
import com.hxl.inventory.model.dto.outbound.OutboundAddRequest;
import com.hxl.inventory.model.dto.outbound.OutboundQueryRequest;
import com.hxl.inventory.model.entity.Drug;
import com.hxl.inventory.model.entity.Inventory;
import com.hxl.inventory.model.entity.Outbound;
import com.hxl.inventory.model.entity.OutboundItem;
import com.hxl.inventory.model.entity.User;
import com.hxl.inventory.model.vo.OutboundItemVO;
import com.hxl.inventory.model.vo.OutboundVO;
import com.hxl.inventory.service.DrugService;
import com.hxl.inventory.service.InventoryService;
import com.hxl.inventory.service.OutboundItemService;
import com.hxl.inventory.service.OutboundService;
import com.hxl.inventory.mapper.OutboundMapper;
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
* @description 针对表【t_outbound(出库单)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class OutboundServiceImpl extends ServiceImpl<OutboundMapper, Outbound>
    implements OutboundService{

    @Resource
    private OutboundItemService outboundItemService;

    @Resource
    private InventoryService inventoryService;

    @Resource
    private DrugService drugService;

    /**
     * 允许排序字段
     */
    private static final List<String> SORT_FIELD_WHITE_LIST = Arrays.asList(
            "id", "outbound_no", "outbound_date", "create_time", "update_time"
    );

    @Override
    public QueryWrapper<Outbound> getQueryWrapper(OutboundQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        QueryWrapper<Outbound> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(queryRequest.getOutboundNo()), "outbound_no", queryRequest.getOutboundNo());
        String sortField = queryRequest.getSortField();
        boolean isAsc = "ascend".equalsIgnoreCase(queryRequest.getSortOrder());
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField) && SORT_FIELD_WHITE_LIST.contains(sortField), isAsc, sortField);
        return queryWrapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOutbound(OutboundAddRequest addRequest) {
        ThrowUtils.throwIf(addRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(StrUtil.isBlank(addRequest.getOutboundNo()), ErrorCode.PARAMS_ERROR, "出库单号不能为空");
        List<OutboundAddItemRequest> itemList = addRequest.getItemList();
        ThrowUtils.throwIf(itemList == null || itemList.isEmpty(), ErrorCode.PARAMS_ERROR, "出库明细不能为空");

        User loginUser = LoginUserHolder.getUser();
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 一次出库请求只生成一张主单，明细共享同一出库单号
        Outbound outbound = new Outbound();
        outbound.setOutboundNo(addRequest.getOutboundNo());
        outbound.setOutboundDate(addRequest.getOutboundDate() == null ? new Date() : addRequest.getOutboundDate());
        outbound.setRemark(addRequest.getRemark());
        outbound.setOperatorId(loginUser.getId());
        boolean saveOutboundResult = this.save(outbound);
        ThrowUtils.throwIf(!saveOutboundResult, ErrorCode.OPERATION_ERROR, "新增出库单失败");

        for (OutboundAddItemRequest itemRequest : itemList) {
            validateOutboundItem(itemRequest);
            // 先校验并扣减库存，确保出库数量不超过批次余量
            decreaseInventoryWhenOutbound(itemRequest.getDrugId(), itemRequest.getBatchNo(), itemRequest.getQuantity());

            OutboundItem outboundItem = new OutboundItem();
            outboundItem.setOutboundId(outbound.getId());
            outboundItem.setDrugId(itemRequest.getDrugId());
            outboundItem.setBatchNo(itemRequest.getBatchNo());
            outboundItem.setQuantity(itemRequest.getQuantity());
            outboundItem.setPrice(itemRequest.getPrice());
            outboundItem.setAmount(itemRequest.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            boolean saveItemResult = outboundItemService.save(outboundItem);
            ThrowUtils.throwIf(!saveItemResult, ErrorCode.OPERATION_ERROR, "新增出库明细失败");
        }
        return true;
    }

    @Override
    public Page<OutboundVO> listOutboundByPage(OutboundQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0, ErrorCode.PARAMS_ERROR, "分页参数错误");

        Page<Outbound> outboundPage = this.page(new Page<>(current, pageSize), this.getQueryWrapper(queryRequest));
        List<Outbound> outboundList = outboundPage.getRecords();
        if (outboundList.isEmpty()) {
            return new Page<>(current, pageSize, 0);
        }

        List<Long> outboundIds = outboundList.stream().map(Outbound::getId).collect(Collectors.toList());
        List<OutboundItem> outboundItemList = outboundItemService.list(
                new LambdaQueryWrapper<OutboundItem>().in(OutboundItem::getOutboundId, outboundIds));
        // 先按出库单分组，便于后续组装主子结构 VO
        Map<Long, List<OutboundItem>> outboundItemMap = outboundItemList.stream()
                .collect(Collectors.groupingBy(OutboundItem::getOutboundId));

        List<Long> drugIds = outboundItemList.stream()
                .map(OutboundItem::getDrugId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Drug> tempDrugMap = new HashMap<>();
        if (!drugIds.isEmpty()) {
            // 批量查询药品，避免循环内逐条查询导致 N+1 问题
            tempDrugMap = drugService.listByIds(drugIds).stream().collect(Collectors.toMap(Drug::getId, drug -> drug));
        }
        final Map<Long, Drug> drugMap = tempDrugMap;

        List<OutboundVO> outboundVOList = outboundList.stream().map(outbound -> {
            OutboundVO outboundVO = new OutboundVO();
            BeanUtils.copyProperties(outbound, outboundVO);
            List<OutboundItem> items = outboundItemMap.getOrDefault(outbound.getId(), Collections.emptyList());
            List<OutboundItemVO> itemVOList = items.stream().map(item -> {
                OutboundItemVO itemVO = new OutboundItemVO();
                BeanUtils.copyProperties(item, itemVO);
                Drug drug = drugMap.get(item.getDrugId());
                if (drug != null) {
                    itemVO.setDrugName(drug.getDrugName());
                    itemVO.setDrugCode(drug.getDrugCode());
                }
                return itemVO;
            }).collect(Collectors.toList());
            outboundVO.setItemList(itemVOList);
            return outboundVO;
        }).collect(Collectors.toList());

        Page<OutboundVO> voPage = new Page<>(outboundPage.getCurrent(), outboundPage.getSize(), outboundPage.getTotal());
        voPage.setRecords(outboundVOList);
        return voPage;
    }

    /**
     * 出库明细参数校验
     */
    private void validateOutboundItem(OutboundAddItemRequest itemRequest) {
        ThrowUtils.throwIf(itemRequest == null, ErrorCode.PARAMS_ERROR, "出库明细不能为空");
        ThrowUtils.throwIf(itemRequest.getDrugId() == null || itemRequest.getDrugId() <= 0, ErrorCode.PARAMS_ERROR, "药品ID错误");
        ThrowUtils.throwIf(StrUtil.isBlank(itemRequest.getBatchNo()), ErrorCode.PARAMS_ERROR, "批号不能为空");
        ThrowUtils.throwIf(itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0, ErrorCode.PARAMS_ERROR, "出库数量错误");
        ThrowUtils.throwIf(itemRequest.getPrice() == null || itemRequest.getPrice().compareTo(BigDecimal.ZERO) < 0,
                ErrorCode.PARAMS_ERROR, "出库价格错误");
        boolean drugExists = drugService.getById(itemRequest.getDrugId()) != null;
        ThrowUtils.throwIf(!drugExists, ErrorCode.NOT_FOUND_ERROR, "药品不存在");
    }

    /**
     * 出库扣减库存（按药品+批号）
     */
    private void decreaseInventoryWhenOutbound(Long drugId, String batchNo, Integer quantity) {
        LambdaQueryWrapper<Inventory> inventoryQuery = new LambdaQueryWrapper<>();
        inventoryQuery.eq(Inventory::getDrugId, drugId);
        inventoryQuery.eq(Inventory::getBatchNo, batchNo);
        Inventory inventory = inventoryService.getOne(inventoryQuery);
        ThrowUtils.throwIf(inventory == null, ErrorCode.NOT_FOUND_ERROR, "对应批次库存不存在");
        ThrowUtils.throwIf(inventory.getQuantity() < quantity, ErrorCode.OPERATION_ERROR, "出库数量不能超过当前库存");
        inventory.setQuantity(inventory.getQuantity() - quantity);
        boolean updateResult = inventoryService.updateById(inventory);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新库存失败");
    }
}




