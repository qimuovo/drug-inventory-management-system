package com.hxl.inventory.model.dto.outboundreturn;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 出库退库明细新增请求
 */
@Data
public class OutboundReturnAddItemRequest implements Serializable {

    /**
     * 出库明细ID
     */
    private Long outboundItemId;

    /**
     * 退库数量
     */
    private Integer returnQuantity;

    /**
     * 退库价格
     */
    private BigDecimal returnPrice;

    /**
     * 退库原因（可选，优先使用明细原因）
     */
    private String reason;

    private static final long serialVersionUID = 1L;
}
