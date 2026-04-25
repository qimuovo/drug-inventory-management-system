package com.hxl.inventory.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 出库展示对象
 */
@Data
public class OutboundVO implements Serializable {

    private Long id;

    private String outboundNo;

    private Long operatorId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date outboundDate;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 出库明细列表
     */
    private List<OutboundItemVO> itemList;

    private static final long serialVersionUID = 1L;
}
