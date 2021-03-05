package com.leven.demoplus.devstg.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class QQLoginHandler implements LoginHandler<Serializable> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取登录类型
     *
     * @return
     */
    @Override
    public LoginType getLoginType() {
        return LoginType.QQ;
    }

    /**
     * 登录
     *
     * @param request
     * @return
     */
    @Override
    public LoginResponse<String, Serializable> handleLogin(LoginRequest request) {
        logger.info("QQ登录：userId：{}", request.getUserId());
        //return LoginResponse.success("QQ登录成功", null);
        return null;
    }
}