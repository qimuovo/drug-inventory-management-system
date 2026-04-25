package com.hxl.inventory.model.dto.drug;

import lombok.Data;

import java.io.Serializable;

/**
 * 药品新增请求
 */
@Data
public class DrugAddRequest implements Serializable {

    /**
     * 药品名称
     */
    private String drugName;

    /**
     * 药品编码
     */
    private String drugCode;

    /**
     * 规格
     */
    private String specification;

    /**
     * 生产厂家ID
     */
    private Long manufacturerId;

    private static final long serialVersionUID = 1L;
}
