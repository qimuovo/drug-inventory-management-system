package com.hxl.inventory.model.dto.inbound;

import com.hxl.inventory.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 入库分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InboundQueryRequest extends PageRequest implements Serializable {

    /**
     * 入库单号（模糊匹配）
     */
    private String inboundNo;

    private static final long serialVersionUID = 1L;
}
