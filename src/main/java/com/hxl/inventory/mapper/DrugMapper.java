package com.hxl.inventory.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hxl.inventory.model.entity.Drug;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hxl.inventory.model.dto.drug.DrugInventorySummaryQueryRequest;
import com.hxl.inventory.model.vo.DrugInventorySummaryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 29358
* @description 针对表【t_drug(药品表)】的数据库操作Mapper
* @createDate 2026-04-24 23:24:54
* @Entity com.hxl.inventory.model.entity.Drug
*/
public interface DrugMapper extends BaseMapper<Drug> {

    /**
     * 分页查询库存汇总
     */
    IPage<DrugInventorySummaryVO> selectInventorySummaryPage(
            IPage<DrugInventorySummaryVO> page,
            @Param("query") DrugInventorySummaryQueryRequest queryRequest
    );

    /**
     * 查询库存汇总列表（导出）
     */
    List<DrugInventorySummaryVO> selectInventorySummaryList(@Param("query") DrugInventorySummaryQueryRequest queryRequest);
}




