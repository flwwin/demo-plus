package com.leven.demoplus.Design.chain;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FilterFactoryBuilder {
    //可以动态配置一些链

    public List<IReqFilter> filterChain;

    public FilterFactoryBuilder(List<IReqFilter> filterChains) {
        this.filterChain = filterChains;
    }

    public List<IReqFilter> getFilterChain(){
        return this.filterChain;
    }
}
