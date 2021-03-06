package com.leven.demo.plus.design.strategy;

import com.lenven.demo.plus.common.LoginRequest;
import com.lenven.demo.plus.common.LoginResponse;
import com.lenven.demo.plus.common.LoginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private LoginHandlerFactory loginHandlerFactory;

    @Override
    public LoginResponse<String, Serializable> login(LoginRequest request) {
        LoginType loginType = request.getLoginType();
        // 根据 loginType 找到对应的登录处理器
        LoginHandler<Serializable> loginHandler =
                loginHandlerFactory.getHandler(loginType);
        // 处理登录
        return loginHandler.handleLogin(request);
    }
}