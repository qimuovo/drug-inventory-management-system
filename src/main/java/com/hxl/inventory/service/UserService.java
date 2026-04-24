package com.hxl.inventory.service;

import com.hxl.inventory.model.dto.user.UserLoginRequest;
import com.hxl.inventory.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hxl.inventory.model.vo.UserLoginVO;
import com.hxl.inventory.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 29358
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-02-12 10:46:33
*/
public interface UserService extends IService<User> {

    /**
     * 密码加密
     */
    String getEncryptPassword(String password);

    /**
     * 用户登录
     */
    UserLoginVO userLogin(UserLoginRequest request);

    /**
     * 获取当前登录用户信息
     */
    User getLoginUser();

    /**
     * User转UserVO
     */
    UserVO getUserVO(User user);
}
