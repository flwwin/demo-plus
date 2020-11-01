package com.leven.demoplus.design.chain;

import org.springframework.stereotype.Component;

@Component
public class UserFilter implements IReqFilter {
    @Override
    public void doFilter() {
        System.out.println("用户过滤。。。");
    }
}
