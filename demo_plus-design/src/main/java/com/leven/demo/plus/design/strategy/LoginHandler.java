package com.leven.demo.plus.design.strategy;


import com.lenven.demo.plus.common.LoginRequest;
import com.lenven.demo.plus.common.LoginResponse;
import com.lenven.demo.plus.common.LoginType;

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