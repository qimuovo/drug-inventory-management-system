package com.hxl.inventory.config;

import cn.hutool.core.util.StrUtil;
import com.hxl.inventory.exception.BusinessException;
import com.hxl.inventory.exception.ErrorCode;
import com.hxl.inventory.model.entity.User;
import com.hxl.inventory.service.UserService;
import com.hxl.inventory.utils.JwtUtils;
import com.hxl.inventory.utils.LoginUserHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录拦截器：解析 token 并写入 ThreadLocal
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestUri = request.getRequestURI();
        if (isWhitePath(requestUri)) {
            return true;
        }

        String authorization = request.getHeader("Authorization");
        if (StrUtil.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "token 不存在");
        }

        String token = authorization.substring("Bearer ".length());
        Long userId;
        try {
            userId = jwtUtils.parseUserId(token);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "token 无效或已过期");
        }

        User currentUser = userService.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        LoginUserHolder.setUser(currentUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理，避免线程复用导致内存泄漏
        LoginUserHolder.clear();
    }

    private boolean isWhitePath(String path) {
        return path.endsWith("/user/login")
                || path.contains("/doc.html")
                || path.contains("/v2/api-docs")
                || path.contains("/swagger-resources")
                || path.contains("/webjars")
                || path.endsWith("/error");
    }
}
