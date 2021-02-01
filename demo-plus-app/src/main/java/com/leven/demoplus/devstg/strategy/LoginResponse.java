package com.leven.demoplus.devstg.strategy;

import lombok.Data;

@Data
public class LoginResponse<T,R> {
    T success;
    R code;
}
