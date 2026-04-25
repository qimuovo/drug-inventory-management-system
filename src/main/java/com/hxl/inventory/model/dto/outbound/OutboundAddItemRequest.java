package com.hxl.inventory.model.dto.outbound;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 出库明细新增请求
 */
@Data
public class OutboundAddItemRequest implements Serializable {

    /**
     * 药品ID
     */
    private Long drugId;

    /**
     * 批号
     */
    private String batchNo;

    /**
     * 出库数量
     */
    private Integer quantity;

    /**
     * 出库价格
     */
    private BigDecimal price;

    private static final long serialVersionUID = 1L;
}
