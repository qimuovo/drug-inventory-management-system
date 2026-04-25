package com.hxl.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.model.dto.outbound.OutboundAddRequest;
import com.hxl.inventory.model.dto.outbound.OutboundQueryRequest;
import com.hxl.inventory.model.entity.Outbound;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hxl.inventory.model.vo.OutboundVO;

/**
* @author 29358
* @description 针对表【t_outbound(出库单)】的数据库操作Service
* @createDate 2026-04-24 23:24:54
*/
public interface OutboundService extends IService<Outbound> {

    /**
     * 组装出库查询条件
     */
    QueryWrapper<Outbound> getQueryWrapper(OutboundQueryRequest queryRequest);

    /**
     * 新增出库（支持单据下多药品）
     */
    boolean addOutbound(OutboundAddRequest addRequest);

    /**
     * 分页查询出库记录
     */
    Page<OutboundVO> listOutboundByPage(OutboundQueryRequest queryRequest);
}
