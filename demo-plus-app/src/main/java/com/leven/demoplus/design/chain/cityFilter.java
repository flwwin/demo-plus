package com.leven.demoplus.design.chain;

import org.springframework.stereotype.Component;

@Component
public class cityFilter implements IReqFilter {
    @Override
    public void doFilter() {
        System.out.println("城市过滤");
    }
}
