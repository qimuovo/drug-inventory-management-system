package com.hxl.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.model.dto.drug.DrugAddRequest;
import com.hxl.inventory.model.dto.drug.DrugQueryRequest;
import com.hxl.inventory.model.dto.drug.DrugUpdateRequest;
import com.hxl.inventory.model.entity.Drug;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hxl.inventory.model.vo.DrugVO;

/**
* @author 29358
* @description 针对表【t_drug(药品表)】的数据库操作Service
* @createDate 2026-04-24 23:24:54
*/
public interface DrugService extends IService<Drug> {

    /**
     * 组装药品查询条件
     */
    QueryWrapper<Drug> getQueryWrapper(DrugQueryRequest queryRequest);

    /**
     * 分页获取药品列表（包含厂家信息）
     */
    Page<DrugVO> listDrugByPage(DrugQueryRequest queryRequest);

    /**
     * 新增药品
     */
    boolean addDrug(DrugAddRequest addRequest);

    /**
     * 编辑药品
     */
    boolean updateDrug(Long id, DrugUpdateRequest updateRequest);

    /**
     * 删除药品
     */
    boolean deleteDrug(Long id);
}
