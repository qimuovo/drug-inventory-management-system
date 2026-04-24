package com.hxl.inventory.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxl.inventory.constant.UserConstant;
import com.hxl.inventory.exception.BusinessException;
import com.hxl.inventory.exception.ErrorCode;
import com.hxl.inventory.exception.ThrowUtils;
import com.hxl.inventory.mapper.UserMapper;
import com.hxl.inventory.model.dto.user.UserLoginRequest;
import com.hxl.inventory.model.entity.User;
import com.hxl.inventory.model.vo.UserLoginVO;
import com.hxl.inventory.model.vo.UserVO;
import com.hxl.inventory.service.UserService;
import com.hxl.inventory.utils.JwtUtils;
import com.hxl.inventory.utils.LoginUserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author 29358
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2025-02-12 10:46:33
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public String getEncryptPassword(String password){
        return DigestUtils.md5DigestAsHex((UserConstant.PASSWORD_SALT + password).getBytes());
    }

    @Override
    public UserLoginVO userLogin(UserLoginRequest request) {
        String account = request.getAccount();
        String password = request.getPassword();
        // 检验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(account, password), ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(account.length() < 4, ErrorCode.PARAMS_ERROR, "用户账号错误");
        ThrowUtils.throwIf(password.length() < 6, ErrorCode.PARAMS_ERROR, "密码错误");

        // 检查账号是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, account);
        User user = this.getOne(queryWrapper);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "账号不存在");

        // 查询用户密码是否正确
        String encryptPassword = getEncryptPassword(password);
        System.out.println(encryptPassword);
        ThrowUtils.throwIf(!user.getPassword().equals(encryptPassword), ErrorCode.PARAMS_ERROR, "密码错误");
        String token = jwtUtils.generateToken(user.getId());
        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setToken(token);
        userLoginVO.setUserInfo(this.getUserVO(user));
        return userLoginVO;
    }

    @Override
    public User getLoginUser() {
        User currentUser = LoginUserHolder.getUser();
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

}




