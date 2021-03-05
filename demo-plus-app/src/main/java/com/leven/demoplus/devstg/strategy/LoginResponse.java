package com.leven.demoplus.devstg.strategy;

import lombok.Data;

@Data
public class LoginResponse<T,R> {
    public T success;
    public R code;
}
