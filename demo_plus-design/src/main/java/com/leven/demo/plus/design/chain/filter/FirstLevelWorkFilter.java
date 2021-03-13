package com.leven.demo.plus.design.chain.filter;

import com.leven.demo.plus.design.chain.IWorkFilter;
import com.leven.demo.plus.design.chain.enity.RequestContext;
import com.leven.demo.plus.design.chain.enity.RespondContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@Slf4j
public class FirstLevelWorkFilter implements IWorkFilter<RequestContext,RespondContext> {
    @Override
    public void doFilter(RequestContext req, RespondContext rep) {
        log.info("一级处理器执行。。。");
    }
}
