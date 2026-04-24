package com.hxl.inventory.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 入库单
 * @TableName t_inbound
 */
@TableName(value ="t_inbound")
@Data
public class Inbound implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 入库单号
     */
    private String inboundNo;

    /**
     * 操作人
     */
    private Long operatorId;

    /**
     * 入库日期
     */
    private Date inboundDate;

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