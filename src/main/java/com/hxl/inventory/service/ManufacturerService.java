package com.hxl.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerAddRequest;
import com.hxl.inventory.model.entity.Manufacturer;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerQueryRequest;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerUpdateRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 29358
* @description 针对表【t_manufacturer(生产厂家表)】的数据库操作Service
* @createDate 2026-04-24 23:24:54
*/
public interface ManufacturerService extends IService<Manufacturer> {

    /**
     * 组装厂家查询条件
     *
     * @param queryRequest 查询请求
     * @return 查询条件
     */
    QueryWrapper<Manufacturer> getQueryWrapper(ManufacturerQueryRequest queryRequest);

    /**
     * 分页获取厂家列表
     *
     * @param queryRequest 查询请求
     * @return 厂家分页数据
     */
    Page<Manufacturer> listManufacturerByPage(ManufacturerQueryRequest queryRequest);

    /**
     * 新增厂家
     *
     * @param addRequest 新增请求
     * @return 是否成功
     */
    boolean addManufacturer(ManufacturerAddRequest addRequest);

    /**
     * 编辑厂家
     *
     * @param id            厂家ID
     * @param updateRequest 编辑请求
     * @return 是否成功
     */
    boolean updateManufacturer(Long id, ManufacturerUpdateRequest updateRequest);

    /**
     * 删除厂家
     *
     * @param id 厂家ID
     * @return 是否成功
     */
    boolean deleteManufacturer(Long id);
}
