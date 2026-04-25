package com.hxl.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.common.BaseResponse;
import com.hxl.inventory.common.ResultUtils;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerAddRequest;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerQueryRequest;
import com.hxl.inventory.model.dto.manufacturer.ManufacturerUpdateRequest;
import com.hxl.inventory.model.entity.Manufacturer;
import com.hxl.inventory.service.ManufacturerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 厂家管理接口
 */
@RestController
@RequestMapping("/manufacturer")
public class ManufacturerController {

    @Resource
    private ManufacturerService manufacturerService;

    /**
     * 分页查询厂家列表
     */
    @PostMapping("/page")
    @ApiOperation("分页获取厂家列表")
    public BaseResponse<Page<Manufacturer>> listManufacturerByPage(
            @RequestBody ManufacturerQueryRequest queryRequest) {
        return ResultUtils.success(manufacturerService.listManufacturerByPage(queryRequest));
    }

    /**
     * 新增厂家
     */
    @PostMapping
    @ApiOperation("新增厂家")
    public BaseResponse<Boolean> addManufacturer(@RequestBody ManufacturerAddRequest addRequest) {
        return ResultUtils.success(manufacturerService.addManufacturer(addRequest));
    }

    /**
     * 编辑厂家
     */
    @PutMapping("/{id}")
    @ApiOperation("编辑厂家")
    public BaseResponse<Boolean> updateManufacturer(@PathVariable Long id,
                                                    @RequestBody ManufacturerUpdateRequest updateRequest) {
        return ResultUtils.success(manufacturerService.updateManufacturer(id, updateRequest));
    }

    /**
     * 删除厂家
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除厂家")
    public BaseResponse<Boolean> deleteManufacturer(@PathVariable Long id) {
        return ResultUtils.success(manufacturerService.deleteManufacturer(id));
    }
}
