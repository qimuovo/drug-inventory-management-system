package com.hxl.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.model.dto.inboundreturn.InboundReturnAddRequest;
import com.hxl.inventory.model.dto.inboundreturn.InboundReturnQueryRequest;
import com.hxl.inventory.model.entity.InboundReturn;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hxl.inventory.model.vo.InboundReturnVO;

/**
* @author 29358
* @description 针对表【t_inbound_return(入库退货)】的数据库操作Service
* @createDate 2026-04-24 23:24:54
*/
public interface InboundReturnService extends IService<InboundReturn> {

    /**
     * 组装入库退货查询条件
     */
    QueryWrapper<InboundReturn> getQueryWrapper(InboundReturnQueryRequest queryRequest);

    /**
     * 新增入库退货（支持一次退多个药品）
     */
    boolean addInboundReturn(InboundReturnAddRequest addRequest);

    /**
     * 分页查询入库退货记录
     */
    Page<InboundReturnVO> listInboundReturnByPage(InboundReturnQueryRequest queryRequest);
}
