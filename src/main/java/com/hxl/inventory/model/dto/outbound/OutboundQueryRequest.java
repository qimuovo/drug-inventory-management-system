package com.hxl.inventory.model.dto.outbound;

import com.hxl.inventory.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 出库分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OutboundQueryRequest extends PageRequest implements Serializable {

    /**
     * 出库单号（模糊匹配）
     */
    private String outboundNo;

    private static final long serialVersionUID = 1L;
}
