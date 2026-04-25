package com.hxl.inventory.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}
