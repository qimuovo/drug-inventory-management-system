package com.hxl.inventory.model.dto.inboundreturn;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 入库退货新增请求（一次可退多个药品）
 */
@Data
public class InboundReturnAddRequest implements Serializable {

    /**
     * 退货日期
     */
    private Date returnDate;

    /**
     * 退货原因（作为明细原因默认值）
     */
    private String reason;

    /**
     * 退货明细列表
     */
    private List<InboundReturnAddItemRequest> itemList;

    private static final long serialVersionUID = 1L;
}
