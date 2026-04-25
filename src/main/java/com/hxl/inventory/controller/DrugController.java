package com.hxl.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.common.BaseResponse;
import com.hxl.inventory.common.ResultUtils;
import com.hxl.inventory.model.dto.drug.DrugAddRequest;
import com.hxl.inventory.model.dto.drug.DrugInventorySummaryQueryRequest;
import com.hxl.inventory.model.dto.drug.DrugQueryRequest;
import com.hxl.inventory.model.dto.drug.DrugUpdateRequest;
import com.hxl.inventory.model.vo.DrugInventorySummaryVO;
import com.hxl.inventory.model.vo.DrugVO;
import com.hxl.inventory.service.DrugService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 药品管理接口
 */
@RestController
@RequestMapping("/drug")
public class DrugController {

    @Resource
    private DrugService drugService;

    /**
     * 分页查询药品列表
     */
    @PostMapping("/page")
    @ApiOperation("分页获取药品列表")
    public BaseResponse<Page<DrugVO>> listDrugByPage(@RequestBody DrugQueryRequest queryRequest) {
        return ResultUtils.success(drugService.listDrugByPage(queryRequest));
    }

    /**
     * 分页查询库存汇总
     */
    @PostMapping("/inventory/summary/page")
    @ApiOperation("分页查询库存汇总")
    public BaseResponse<Page<DrugInventorySummaryVO>> listDrugInventorySummaryByPage(
            @RequestBody DrugInventorySummaryQueryRequest queryRequest) {
        return ResultUtils.success(drugService.listDrugInventorySummaryByPage(queryRequest));
    }

    /**
     * 新增药品
     */
    @PostMapping
    @ApiOperation("新增药品")
    public BaseResponse<Boolean> addDrug(@RequestBody DrugAddRequest addRequest) {
        return ResultUtils.success(drugService.addDrug(addRequest));
    }

    /**
     * 编辑药品
     */
    @PutMapping("/{id}")
    @ApiOperation("编辑药品")
    public BaseResponse<Boolean> updateDrug(@PathVariable Long id, @RequestBody DrugUpdateRequest updateRequest) {
        return ResultUtils.success(drugService.updateDrug(id, updateRequest));
    }

    /**
     * 删除药品
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除药品")
    public BaseResponse<Boolean> deleteDrug(@PathVariable Long id) {
        return ResultUtils.success(drugService.deleteDrug(id));
    }
}
