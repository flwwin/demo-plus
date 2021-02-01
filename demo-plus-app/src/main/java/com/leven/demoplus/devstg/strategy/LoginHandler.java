package com.leven.demoplus.devstg.strategy;


import java.io.Serializable;

public interface LoginHandler<T extends Serializable> {

    /**
     * 获取登录类型
     *
     * @return
     */
    LoginType getLoginType();

    /**
     * 登录
     *
     * @param request
     * @return
     */
    LoginResponse<String, T> handleLogin(LoginRequest request);
}