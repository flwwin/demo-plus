package com.lenven.demo.plus.common;

import lombok.Data;

@Data
public class LoginResponse<T,R> {
    public T success;
    public R code;
}
