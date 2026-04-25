package com.hxl.inventory.model.dto.inbound;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 入库明细新增请求
 */
@Data
public class InboundAddItemRequest implements Serializable {

    /**
     * 药品ID
     */
    private Long drugId;

    /**
     * 批号
     */
    private String batchNo;

    /**
     * 入库数量
     */
    private Integer quantity;

    /**
     * 入库价格
     */
    private BigDecimal price;

    private static final long serialVersionUID = 1L;
}
