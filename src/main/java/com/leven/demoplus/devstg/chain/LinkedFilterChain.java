package com.leven.demoplus.devstg.chain;

import java.util.List;


public class LinkedFilterChain<ReqT,RepD> implements IFilterChain<ReqT,RepD> {

    List<IFilter<ReqT,RepD>> filterChain;

    @Override
    public void doFilter(ReqT req,RepD rep) {
        for (IFilter<ReqT, RepD> filterChain : filterChain) {
            //filterChain.doFilter(req,rep);
        }
    }

    @Override
    protected LinkedFilterChain<ReqT,RepD> clone() throws CloneNotSupportedException {
        LinkedFilterChain<ReqT,RepD> cloneChain = new LinkedFilterChain<ReqT,RepD>();
        cloneChain.setFilterChains(filterChain);
        return cloneChain;
    }

    public List<IFilter<ReqT, RepD>> getFilterChains() {
        return filterChain;
    }

    public void setFilterChains(List<IFilter<ReqT, RepD>> filterChains) {
        this.filterChain = filterChains;
    }
}
