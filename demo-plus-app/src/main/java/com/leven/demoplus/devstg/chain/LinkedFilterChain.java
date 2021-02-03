package com.leven.demoplus.devstg.chain;

import java.util.List;


public class LinkedFilterChain<T, R> implements IFilterChain<T, R> {

    List<IFilter<T, R>> filterChain;

    @Override
    public void doFilter(T req, R rep) {
        for (IFilter<T, R> filterChain : filterChain) {
            //filterChain.doFilter(req,rep);
        }
    }

    @Override
    protected LinkedFilterChain<T, R> clone() throws CloneNotSupportedException {
        LinkedFilterChain<T, R> cloneChain = new LinkedFilterChain<T, R>();
        cloneChain.setFilterChains(filterChain);
        return cloneChain;
    }

    public List<IFilter<T, R>> getFilterChains() {
        return filterChain;
    }

    public void setFilterChains(List<IFilter<T, R>> filterChains) {
        this.filterChain = filterChains;
    }
}
