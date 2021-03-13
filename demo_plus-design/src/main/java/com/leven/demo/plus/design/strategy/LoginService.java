package com.leven.demo.plus.design.strategy;

import com.lenven.demo.plus.common.LoginRequest;
import com.lenven.demo.plus.common.LoginResponse;

import java.io.Serializable;

public interface LoginService {
     LoginResponse<String, Serializable> login(LoginRequest request) ;
}
