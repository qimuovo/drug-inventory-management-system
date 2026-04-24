package com.hxl.inventory.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 出库退库
 * @TableName t_outbound_return
 */
@TableName(value ="t_outbound_return")
@Data
public class OutboundReturn implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 出库明细ID
     */
    private Long outboundItemId;

    /**
     * 退库数量
     */
    private Integer returnQuantity;

    /**
     * 退库价格
     */
    private BigDecimal returnPrice;

    /**
     * 退库原因
     */
    private String reason;

    /**
     * 操作人
     */
    private Long operatorId;

    /**
     * 退库日期
     */
    private Date returnDate;

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