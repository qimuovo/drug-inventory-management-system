package com.hxl.inventory.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 药品表
 * @TableName t_drug
 */
@TableName(value ="t_drug")
@Data
public class Drug implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 药品名称
     */
    private String drugName;

    /**
     * 药品编号
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

    /**
     * 是否删除（0：未删除，1：已删除）
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}