package com.hxl.inventory.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 出库明细
 * @TableName t_outbound_item
 */
@TableName(value ="t_outbound_item")
@Data
public class OutboundItem implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 出库单ID
     */
    private Long outboundId;

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

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 是否删除（0：未删除，1：已删除）
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}