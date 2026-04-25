package com.hxl.inventory.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.exception.BusinessException;
import com.hxl.inventory.exception.ErrorCode;
import com.hxl.inventory.exception.ThrowUtils;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerAddRequest;
import com.hxl.inventory.model.entity.Manufacturer;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerQueryRequest;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerUpdateRequest;
import com.hxl.inventory.service.ManufacturerService;
import com.hxl.inventory.mapper.ManufacturerMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
* @author 29358
* @description 针对表【t_manufacturer(生产厂家表)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class ManufacturerServiceImpl extends ServiceImpl<ManufacturerMapper, Manufacturer>
    implements ManufacturerService{

    /**
     * 允许排序字段
     */
    private static final List<String> SORT_FIELD_WHITE_LIST = Arrays.asList(
            "id", "manufacturer_name", "create_time", "update_time"
    );

    @Override
    public QueryWrapper<Manufacturer> getQueryWrapper(ManufacturerQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String manufacturerName = queryRequest.getManufacturerName();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        QueryWrapper<Manufacturer> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(manufacturerName), "manufacturer_name", manufacturerName);
        boolean isAsc = "ascend".equalsIgnoreCase(sortOrder);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField) && SORT_FIELD_WHITE_LIST.contains(sortField), isAsc, sortField);
        return queryWrapper;
    }

    @Override
    public Page<Manufacturer> listManufacturerByPage(ManufacturerQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0, ErrorCode.PARAMS_ERROR, "分页参数错误");
        return this.page(new Page<>(current, pageSize), this.getQueryWrapper(queryRequest));
    }

    @Override
    public boolean addManufacturer(ManufacturerAddRequest addRequest) {
        ThrowUtils.throwIf(addRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(StrUtil.isBlank(addRequest.getManufacturerName()), ErrorCode.PARAMS_ERROR, "厂家名称不能为空");
        Manufacturer manufacturer = new Manufacturer();
        BeanUtils.copyProperties(addRequest, manufacturer);
        return this.save(manufacturer);
    }

    @Override
    public boolean updateManufacturer(Long id, ManufacturerUpdateRequest updateRequest) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "厂家ID错误");
        ThrowUtils.throwIf(updateRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(StrUtil.isBlank(updateRequest.getManufacturerName()), ErrorCode.PARAMS_ERROR, "厂家名称不能为空");
        Manufacturer manufacturer = new Manufacturer();
        BeanUtils.copyProperties(updateRequest, manufacturer);
        manufacturer.setId(id);
        return this.updateById(manufacturer);
    }

    @Override
    public boolean deleteManufacturer(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "厂家ID错误");
        return this.removeById(id);
    }
}




