package com.leven.demoplus.devstg.strategy;

import java.io.Serializable;

public interface LoginService {
     LoginResponse<String, Serializable> login(LoginRequest request) ;
}
