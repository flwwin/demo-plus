package com.leven.demoplus.devstg.chain;


public interface IFilterChain<ReqT,repD> {
    void doFilter(ReqT req,repD rep);

}
