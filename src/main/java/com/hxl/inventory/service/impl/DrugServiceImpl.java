package com.hxl.inventory.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.exception.ErrorCode;
import com.hxl.inventory.exception.ThrowUtils;
import com.hxl.inventory.model.entity.Drug;
import com.hxl.inventory.model.dto.drug.DrugAddRequest;
import com.hxl.inventory.model.dto.drug.DrugQueryRequest;
import com.hxl.inventory.model.dto.drug.DrugUpdateRequest;
import com.hxl.inventory.model.entity.Manufacturer;
import com.hxl.inventory.model.vo.DrugVO;
import com.hxl.inventory.service.DrugService;
import com.hxl.inventory.mapper.DrugMapper;
import com.hxl.inventory.service.ManufacturerService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author 29358
* @description 针对表【t_drug(药品表)】的数据库操作Service实现
* @createDate 2026-04-24 23:24:54
*/
@Service
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug>
    implements DrugService{

    @Resource
    private ManufacturerService manufacturerService;

    /**
     * 允许排序字段
     */
    private static final List<String> SORT_FIELD_WHITE_LIST = Arrays.asList(
            "id", "drug_name", "drug_code", "create_time", "update_time"
    );

    @Override
    public QueryWrapper<Drug> getQueryWrapper(DrugQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        String drugName = queryRequest.getDrugName();
        String drugCode = queryRequest.getDrugCode();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        QueryWrapper<Drug> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(drugName), "drug_name", drugName);
        queryWrapper.like(StrUtil.isNotBlank(drugCode), "drug_code", drugCode);
        boolean isAsc = "ascend".equalsIgnoreCase(sortOrder);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField) && SORT_FIELD_WHITE_LIST.contains(sortField), isAsc, sortField);
        return queryWrapper;
    }

    @Override
    public Page<DrugVO> listDrugByPage(DrugQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        ThrowUtils.throwIf(current <= 0 || pageSize <= 0, ErrorCode.PARAMS_ERROR, "分页参数错误");
        Page<Drug> drugPage = this.page(new Page<>(current, pageSize), this.getQueryWrapper(queryRequest));

        List<Drug> records = drugPage.getRecords();
        // 获取厂家ids
        List<Long> manufacturerIds = records.stream()
                .map(Drug::getManufacturerId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        // 获取厂家名称
        Map<Long, String> tempManufacturerNameMap = new HashMap<>();
        if (!manufacturerIds.isEmpty()) {
            List<Manufacturer> manufacturerList = manufacturerService.listByIds(manufacturerIds);
            tempManufacturerNameMap = manufacturerList.stream()
                    .collect(Collectors.toMap(Manufacturer::getId, Manufacturer::getManufacturerName));
        }
        final Map<Long, String> manufacturerNameMap = tempManufacturerNameMap;

        List<DrugVO> voList = records.stream().map(drug -> {
            DrugVO drugVO = new DrugVO();
            BeanUtils.copyProperties(drug, drugVO);
            drugVO.setManufacturerName(manufacturerNameMap.get(drug.getManufacturerId()));
            return drugVO;
        }).collect(Collectors.toList());

        Page<DrugVO> voPage = new Page<>(drugPage.getCurrent(), drugPage.getSize(), drugPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public boolean addDrug(DrugAddRequest addRequest) {
        ThrowUtils.throwIf(addRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        validateDrugRequest(addRequest.getDrugName(), addRequest.getDrugCode(), addRequest.getManufacturerId());
        Drug drug = new Drug();
        BeanUtils.copyProperties(addRequest, drug);
        return this.save(drug);
    }

    @Override
    public boolean updateDrug(Long id, DrugUpdateRequest updateRequest) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "药品ID错误");
        ThrowUtils.throwIf(updateRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        validateDrugRequest(updateRequest.getDrugName(), updateRequest.getDrugCode(), updateRequest.getManufacturerId());
        Drug drug = new Drug();
        BeanUtils.copyProperties(updateRequest, drug);
        drug.setId(id);
        return this.updateById(drug);
    }

    @Override
    public boolean deleteDrug(Long id) {
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR, "药品ID错误");
        return this.removeById(id);
    }

    /**
     * 药品参数校验
     */
    private void validateDrugRequest(String drugName, String drugCode, Long manufacturerId) {
        ThrowUtils.throwIf(StrUtil.isBlank(drugName), ErrorCode.PARAMS_ERROR, "药品名称不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(drugCode), ErrorCode.PARAMS_ERROR, "药品编码不能为空");
        ThrowUtils.throwIf(manufacturerId == null || manufacturerId <= 0, ErrorCode.PARAMS_ERROR, "厂家ID错误");
        boolean manufacturerExists = manufacturerService.getById(manufacturerId) != null;
        ThrowUtils.throwIf(!manufacturerExists, ErrorCode.NOT_FOUND_ERROR, "厂家不存在");
    }
}




