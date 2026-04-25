package com.hxl.inventory.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 库存汇总展示对象
 */
@Data
public class DrugInventorySummaryVO implements Serializable {

    /**
     * 药品ID
     */
    private Long drugId;

    /**
     * 药品编码
     */
    private String drugCode;

    /**
     * 药品名称
     */
    private String drugName;

    /**
     * 规格
     */
    private String specification;

    /**
     * 生产厂家
     */
    private String manufacturerName;

    /**
     * 入库数量
     */
    private Integer inboundQuantity;

    /**
     * 入库退货数量
     */
    private Integer inboundReturnQuantity;

    /**
     * 出库数量
     */
    private Integer outboundQuantity;

    /**
     * 出库退库数量
     */
    private Integer outboundReturnQuantity;

    /**
     * 当前库存
     */
    private Integer currentInventory;

    private static final long serialVersionUID = 1L;
}
