package com.leven.demoplus.devstg.chain;

public interface IFilter<ReqT,RepD> extends Cloneable {
    void doFilter(ReqT req,RepD rep,IFilterChain chain);
}
