package com.hxl.inventory.model.dto.manufacturer;

import com.hxl.inventory.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 厂家分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ManufacturerQueryRequest extends PageRequest implements Serializable {

    /**
     * 厂家名称（模糊匹配）
     */
    private String manufacturerName;

    private static final long serialVersionUID = 1L;
}
