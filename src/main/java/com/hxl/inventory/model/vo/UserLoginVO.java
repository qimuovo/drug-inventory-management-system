package com.hxl.inventory.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录返回
 */
@Data
public class UserLoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * JWT token
     */
    private String token;

    /**
     * 当前登录用户信息
     */
    private UserVO userInfo;
}
