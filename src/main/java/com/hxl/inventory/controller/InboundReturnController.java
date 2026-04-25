package com.hxl.inventory.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxl.inventory.common.BaseResponse;
import com.hxl.inventory.common.ResultUtils;
import com.hxl.inventory.model.dto.inboundreturn.InboundReturnAddRequest;
import com.hxl.inventory.model.dto.inboundreturn.InboundReturnQueryRequest;
import com.hxl.inventory.model.vo.InboundReturnVO;
import com.hxl.inventory.service.InboundReturnService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 入库退货管理接口
 */
@RestController
@RequestMapping("/inbound-return")
public class InboundReturnController {

    @Resource
    private InboundReturnService inboundReturnService;

    /**
     * 新增入库退货（一次可退多个药品）
     */
    @PostMapping
    @ApiOperation("新增入库退货")
    public BaseResponse<Boolean> addInboundReturn(@RequestBody InboundReturnAddRequest addRequest) {
        return ResultUtils.success(inboundReturnService.addInboundReturn(addRequest));
    }

    /**
     * 分页查询入库退货记录
     */
    @PostMapping("/page")
    @ApiOperation("分页查询入库退货记录")
    public BaseResponse<Page<InboundReturnVO>> listInboundReturnByPage(
            @RequestBody InboundReturnQueryRequest queryRequest) {
        return ResultUtils.success(inboundReturnService.listInboundReturnByPage(queryRequest));
    }
}
