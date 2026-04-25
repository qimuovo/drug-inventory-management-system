package com.hxl.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.common.BaseResponse;
import com.hxl.inventory.common.ResultUtils;
import com.hxl.inventory.model.dto.inbound.InboundAddRequest;
import com.hxl.inventory.model.dto.inbound.InboundQueryRequest;
import com.hxl.inventory.model.vo.InboundVO;
import com.hxl.inventory.service.InboundService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 入库管理接口
 */
@RestController
@RequestMapping("/inbound")
public class InboundController {

    @Resource
    private InboundService inboundService;

    /**
     * 新增入库
     */
    @PostMapping
    @ApiOperation("新增入库")
    public BaseResponse<Boolean> addInbound(@RequestBody InboundAddRequest addRequest) {
        return ResultUtils.success(inboundService.addInbound(addRequest));
    }

    /**
     * 分页查询入库记录
     */
    @PostMapping("/page")
    @ApiOperation("分页查询入库记录")
    public BaseResponse<Page<InboundVO>> listInboundByPage(@RequestBody InboundQueryRequest queryRequest) {
        return ResultUtils.success(inboundService.listInboundByPage(queryRequest));
    }
}
