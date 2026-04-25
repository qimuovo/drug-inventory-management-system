package com.hxl.inventory.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 药品展示对象（包含厂家信息）
 */
@Data
public class DrugVO implements Serializable {

    private Long id;

    private String drugName;

    private String drugCode;

    private String specification;

    private Long manufacturerId;

    /**
     * 厂家名称
     */
    private String manufacturerName;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
