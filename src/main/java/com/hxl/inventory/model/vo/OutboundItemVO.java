package com.hxl.inventory.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 出库明细展示对象
 */
@Data
public class OutboundItemVO implements Serializable {

    private Long id;

    private Long outboundId;

    private Long drugId;

    private String drugName;

    private String drugCode;

    private String batchNo;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal amount;

    private static final long serialVersionUID = 1L;
}
