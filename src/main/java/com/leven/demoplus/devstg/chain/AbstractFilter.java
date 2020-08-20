package com.leven.demoplus.devstg.chain;

import com.leven.demoplus.devstg.chain.enity.RequextContext;
import com.leven.demoplus.devstg.chain.enity.RespondContext;

public abstract class AbstractFilter implements IFilter<RequextContext, RespondContext> {
    @Override
    public void doFilter(RequextContext req, RespondContext rep, IFilterChain chain) {

    }
}
