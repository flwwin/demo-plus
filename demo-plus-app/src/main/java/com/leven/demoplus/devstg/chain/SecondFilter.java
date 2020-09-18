package com.leven.demoplus.devstg.chain;

import com.leven.demoplus.devstg.chain.enity.RequextContext;
import com.leven.demoplus.devstg.chain.enity.RespondContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order
public class SecondFilter extends AbstractFilter {
    @Override
    public void doFilter(RequextContext req, RespondContext rep, IFilterChain chain) {
        System.out.println("secondfilter");
    }
}
