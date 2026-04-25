package com.hxl.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.common.BaseResponse;
import com.hxl.inventory.common.ResultUtils;
import com.hxl.inventory.model.dto.outbound.OutboundAddRequest;
import com.hxl.inventory.model.dto.outbound.OutboundQueryRequest;
import com.hxl.inventory.model.vo.OutboundVO;
import com.hxl.inventory.service.OutboundService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 出库管理接口
 */
@RestController
@RequestMapping("/outbound")
public class OutboundController {

    @Resource
    private OutboundService outboundService;

    /**
     * 新增出库
     */
    @PostMapping
    @ApiOperation("新增出库")
    public BaseResponse<Boolean> addOutbound(@RequestBody OutboundAddRequest addRequest) {
        return ResultUtils.success(outboundService.addOutbound(addRequest));
    }

    /**
     * 分页查询出库记录
     */
    @PostMapping("/page")
    @ApiOperation("分页查询出库记录")
    public BaseResponse<Page<OutboundVO>> listOutboundByPage(@RequestBody OutboundQueryRequest queryRequest) {
        return ResultUtils.success(outboundService.listOutboundByPage(queryRequest));
    }
}
