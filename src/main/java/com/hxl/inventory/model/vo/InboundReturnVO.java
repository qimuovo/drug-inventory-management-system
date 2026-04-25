package com.hxl.inventory.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 入库退货展示对象
 */
@Data
public class InboundReturnVO implements Serializable {

    private Long id;

    private Long inboundItemId;

    private Long drugId;

    private String drugName;

    private String drugCode;

    private String batchNo;

    private Integer returnQuantity;

    private BigDecimal returnPrice;

    private String reason;

    private Long operatorId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date returnDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private static final long serialVersionUID = 1L;
}
