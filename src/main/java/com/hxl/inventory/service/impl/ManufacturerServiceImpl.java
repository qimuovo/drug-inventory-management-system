package com.hxl.inventory.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.exception.BusinessException;
import com.hxl.inventory.exception.ErrorCode;
import com.hxl.inventory.exception.ThrowUtils;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerAddRequest;
import com.hxl.inventory.model.entity.Drug;
import com.hxl.inventory.model.entity.Manufacturer;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerQueryRequest;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerUpdateRequest;
import com.hxl.inventory.model.vo.ManufacturerVO;
import com.hxl.inventory.service.ManufacturerService;
import com.hxl.inventory.service.DrugService;
import com.hxl.inventory.mapper.ManufacturerMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 29358
* @description 针对表【t_manufacturer(生产厂家表)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class ManufacturerServiceImpl extends ServiceImpl<ManufacturerMapper, Manufacturer>
    implements ManufacturerService{

    @Lazy
    @Resource
    private DrugService drugService;

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
    public Page<ManufacturerVO> listManufacturerByPage(ManufacturerQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0, ErrorCode.PARAMS_ERROR, "分页参数错误");
        Page<Manufacturer> manufacturerPage = this.page(new Page<>(current, pageSize), this.getQueryWrapper(queryRequest));
        Page<ManufacturerVO> voPage = new Page<>(manufacturerPage.getCurrent(), manufacturerPage.getSize(), manufacturerPage.getTotal());
        voPage.setRecords(manufacturerPage.getRecords().stream().map(this::getManufacturerVO).collect(Collectors.toList()));
        return voPage;
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
        // 已被药品关联的厂家不允许删除，避免脏数据
        long relatedDrugCount = drugService.lambdaQuery()
                .eq(Drug::getManufacturerId, id)
                .count();
        ThrowUtils.throwIf(relatedDrugCount > 0, ErrorCode.OPERATION_ERROR, "该厂家已关联药品，无法删除");
        return this.removeById(id);
    }

    /**
     * 实体转厂家展示对象
     */
    private ManufacturerVO getManufacturerVO(Manufacturer manufacturer) {
        if (manufacturer == null) {
            return null;
        }
        ManufacturerVO manufacturerVO = new ManufacturerVO();
        BeanUtils.copyProperties(manufacturer, manufacturerVO);
        return manufacturerVO;
    }
}




