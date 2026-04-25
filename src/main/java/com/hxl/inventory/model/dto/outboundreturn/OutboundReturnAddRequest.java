package com.hxl.inventory.model.dto.outboundreturn;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 出库退库新增请求（一次可退多个药品）
 */
@Data
public class OutboundReturnAddRequest implements Serializable {

    /**
     * 退库日期
     */
    private Date returnDate;

    /**
     * 退库原因（作为明细原因默认值）
     */
    private String reason;

    /**
     * 退库明细列表
     */
    private List<OutboundReturnAddItemRequest> itemList;

    private static final long serialVersionUID = 1L;
}
