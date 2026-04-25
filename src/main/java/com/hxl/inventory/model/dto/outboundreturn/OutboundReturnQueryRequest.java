package com.hxl.inventory.model.dto.outboundreturn;

import com.hxl.inventory.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 出库退库分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OutboundReturnQueryRequest extends PageRequest implements Serializable {

    /**
     * 出库明细ID
     */
    private Long outboundItemId;

    /**
     * 搜索关键词（模糊匹配药品名称、药品编码）
     */
    private String search;

    private static final long serialVersionUID = 1L;
}
