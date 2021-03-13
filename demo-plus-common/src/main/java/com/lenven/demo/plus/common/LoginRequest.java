package com.lenven.demo.plus.common;

import lombok.Data;

@Data
public class LoginRequest {

    private LoginType loginType;

    private Long userId;

}