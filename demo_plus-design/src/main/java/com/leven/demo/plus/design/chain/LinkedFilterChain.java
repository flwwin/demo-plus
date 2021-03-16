package com.leven.demo.plus.design.chain;

import com.leven.demo.plus.design.chain.enity.RequestContext;
import com.leven.demo.plus.design.chain.enity.RespondContext;

import java.util.List;

/**
 * 过滤链
 */
public class LinkedFilterChain implements IFilterChain<RequestContext, RespondContext> ,Cloneable {

    List<IWorkFilter<RequestContext,RespondContext>> filterChain;

    @Override
    public void doFilter(RequestContext req, RespondContext rep) {
        // 循环执行责任链
        for (IWorkFilter<RequestContext, RespondContext> filterChain : filterChain) {
             filterChain.doFilter(req,rep);
        }
    }


    public List<IWorkFilter<RequestContext, RespondContext>> getFilterChains() {
        return filterChain;
    }

    public void setFilterChains(List<IWorkFilter<RequestContext, RespondContext>> filterChains) {
        this.filterChain = filterChains;
    }

    @Override
    protected LinkedFilterChain clone() throws CloneNotSupportedException {
        return (LinkedFilterChain) super.clone();
    }
}
