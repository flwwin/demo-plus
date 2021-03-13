package com.leven.demo.plus.design.chain;

public interface IWorkFilter<ReqT,RepD> extends Cloneable {
    void doFilter(ReqT req, RepD rep);

}
