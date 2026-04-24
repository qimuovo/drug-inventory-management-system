package com.hxl.inventory.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 出库单
 * @TableName t_outbound
 */
@TableName(value ="t_outbound")
@Data
public class Outbound implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 出库单号
     */
    private String outboundNo;

    /**
     * 操作人
     */
    private Long operatorId;

    /**
     * 出库日期
     */
    private Date outboundDate;

    /**
     * 备注
     */
    private String remark;

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