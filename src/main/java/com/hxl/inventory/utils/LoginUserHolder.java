package com.hxl.inventory.utils;

import com.hxl.inventory.model.entity.User;

/**
 * 当前登录用户 ThreadLocal 容器
 */
public final class LoginUserHolder {

    private static final ThreadLocal<User> USER_THREAD_LOCAL = new ThreadLocal<>();

    private LoginUserHolder() {
    }

    public static void setUser(User user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static User getUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}
