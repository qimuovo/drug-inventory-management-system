package com.hxl.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.model.dto.inbound.InboundAddRequest;
import com.hxl.inventory.model.dto.inbound.InboundQueryRequest;
import com.hxl.inventory.model.entity.Inbound;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hxl.inventory.model.vo.InboundVO;

/**
* @author 29358
* @description 针对表【t_inbound(入库单)】的数据库操作Service
* @createDate 2026-04-24 23:24:54
*/
public interface InboundService extends IService<Inbound> {

    /**
     * 组装入库查询条件
     */
    QueryWrapper<Inbound> getQueryWrapper(InboundQueryRequest queryRequest);

    /**
     * 新增入库（支持单据下多药品）
     */
    boolean addInbound(InboundAddRequest addRequest);

    /**
     * 分页查询入库记录
     */
    Page<InboundVO> listInboundByPage(InboundQueryRequest queryRequest);
}
