package com.hxl.inventory.model.dto.drug;

import com.hxl.inventory.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 药品分页查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DrugQueryRequest extends PageRequest implements Serializable {

    /**
     * 药品名称（模糊匹配）
     */
    private String drugName;

    /**
     * 药品编码（模糊匹配）
     */
    private String drugCode;

    private static final long serialVersionUID = 1L;
}
