package com.hxl.inventory.model.dto.inbound;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 入库新增请求
 */
@Data
public class InboundAddRequest implements Serializable {

    /**
     * 入库单号
     */
    private String inboundNo;

    /**
     * 入库日期
     */
    private Date inboundDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 入库药品明细列表
     */
    private List<InboundAddItemRequest> itemList;

    private static final long serialVersionUID = 1L;
}
