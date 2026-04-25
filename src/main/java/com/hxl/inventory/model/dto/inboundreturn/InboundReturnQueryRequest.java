package com.hxl.inventory.model.dto.inboundreturn;

import com.hxl.inventory.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 入库退货分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InboundReturnQueryRequest extends PageRequest implements Serializable {

    /**
     * 入库明细ID
     */
    private Long inboundItemId;

    private static final long serialVersionUID = 1L;
}
