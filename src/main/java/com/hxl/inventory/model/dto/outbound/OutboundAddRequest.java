package com.hxl.inventory.model.dto.outbound;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 出库新增请求
 */
@Data
public class OutboundAddRequest implements Serializable {

    /**
     * 出库单号
     */
    private String outboundNo;

    /**
     * 出库日期
     */
    private Date outboundDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 出库药品明细列表
     */
    private List<OutboundAddItemRequest> itemList;

    private static final long serialVersionUID = 1L;
}
