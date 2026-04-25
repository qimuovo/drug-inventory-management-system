package com.hxl.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.common.BaseResponse;
import com.hxl.inventory.common.ResultUtils;
import com.hxl.inventory.model.dto.outboundreturn.OutboundReturnAddRequest;
import com.hxl.inventory.model.dto.outboundreturn.OutboundReturnQueryRequest;
import com.hxl.inventory.model.vo.OutboundReturnVO;
import com.hxl.inventory.service.OutboundReturnService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 出库退库管理接口
 */
@RestController
@RequestMapping("/outbound-return")
public class OutboundReturnController {

    @Resource
    private OutboundReturnService outboundReturnService;

    /**
     * 新增出库退库（一次可退多个药品）
     */
    @PostMapping
    @ApiOperation("新增出库退库")
    public BaseResponse<Boolean> addOutboundReturn(@RequestBody OutboundReturnAddRequest addRequest) {
        return ResultUtils.success(outboundReturnService.addOutboundReturn(addRequest));
    }

    /**
     * 分页查询出库退库记录
     */
    @PostMapping("/page")
    @ApiOperation("分页查询出库退库记录")
    public BaseResponse<Page<OutboundReturnVO>> listOutboundReturnByPage(
            @RequestBody OutboundReturnQueryRequest queryRequest) {
        return ResultUtils.success(outboundReturnService.listOutboundReturnByPage(queryRequest));
    }
}
