package com.leven.demoplus.devstg.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order
public class SecondFilter<ReqT,RepD> extends AbstractFilter<ReqT, RepD> {
    @Override
    public void doFilter(ReqT req, RepD rep, IFilterChain chain) {

    }
}
