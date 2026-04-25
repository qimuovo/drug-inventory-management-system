package com.hxl.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.model.dto.outboundreturn.OutboundReturnAddRequest;
import com.hxl.inventory.model.dto.outboundreturn.OutboundReturnQueryRequest;
import com.hxl.inventory.model.entity.OutboundReturn;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hxl.inventory.model.vo.OutboundReturnVO;

/**
* @author 29358
* @description 针对表【t_outbound_return(出库退库)】的数据库操作Service
* @createDate 2026-04-24 23:24:54
*/
public interface OutboundReturnService extends IService<OutboundReturn> {

    /**
     * 组装出库退库查询条件
     */
    QueryWrapper<OutboundReturn> getQueryWrapper(OutboundReturnQueryRequest queryRequest);

    /**
     * 新增出库退库（支持一次退多个药品）
     */
    boolean addOutboundReturn(OutboundReturnAddRequest addRequest);

    /**
     * 分页查询出库退库记录
     */
    Page<OutboundReturnVO> listOutboundReturnByPage(OutboundReturnQueryRequest queryRequest);
}
