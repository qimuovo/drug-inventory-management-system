package com.hxl.inventory.controller;

import com.hxl.inventory.common.BaseResponse;
import com.hxl.inventory.common.ResultUtils;
import com.hxl.inventory.exception.BusinessException;
import com.hxl.inventory.exception.ErrorCode;
import com.hxl.inventory.model.dto.user.UserLoginRequest;
import com.hxl.inventory.model.entity.User;
import com.hxl.inventory.model.vo.UserLoginVO;
import com.hxl.inventory.model.vo.UserVO;
import com.hxl.inventory.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public BaseResponse<UserLoginVO> login(@RequestBody UserLoginRequest request) {
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserLoginVO loginUserVO = userService.userLogin(request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/get/login")
    @ApiOperation("获取当前登录用户")
    public BaseResponse<UserVO> getLoginUser() {
        User loginUser = userService.getLoginUser();
        return ResultUtils.success(userService.getUserVO(loginUser));
    }

}
