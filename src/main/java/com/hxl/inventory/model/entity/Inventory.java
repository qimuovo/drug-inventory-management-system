package com.hxl.inventory.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 库存表
 * @TableName t_inventory
 */
@TableName(value ="t_inventory")
@Data
public class Inventory implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 药品ID
     */
    private Long drugId;

    /**
     * 批号
     */
    private String batchNo;

    /**
     * 库存数量
     */
    private Integer quantity;

    /**
     * 是否删除（0：未删除，1：已删除）
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}