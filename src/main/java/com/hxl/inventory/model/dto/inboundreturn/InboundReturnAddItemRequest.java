package com.hxl.inventory.model.dto.inboundreturn;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 入库退货明细新增请求
 */
@Data
public class InboundReturnAddItemRequest implements Serializable {

    /**
     * 入库明细ID
     */
    private Long inboundItemId;

    /**
     * 退货数量
     */
    private Integer returnQuantity;

    /**
     * 退货价格
     */
    private BigDecimal returnPrice;

    /**
     * 退货原因（可选，优先使用明细原因）
     */
    private String reason;

    private static final long serialVersionUID = 1L;
}
