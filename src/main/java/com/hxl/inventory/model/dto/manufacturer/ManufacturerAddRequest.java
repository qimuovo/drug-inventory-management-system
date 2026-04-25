package com.hxl.inventory.model.dto.manufacturer;

import lombok.Data;

import java.io.Serializable;

/**
 * 厂家新增请求
 */
@Data
public class ManufacturerAddRequest implements Serializable {

    /**
     * 厂家名称
     */
    private String manufacturerName;

    /**
     * 联系人
     */
    private String contactPerson;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 地址
     */
    private String address;

    private static final long serialVersionUID = 1L;
}
