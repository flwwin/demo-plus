package com.leven.demo.plus.design.chain;


public interface IFilterChain<ReqT,repD> {
    void doFilter(ReqT req, repD rep);

}
