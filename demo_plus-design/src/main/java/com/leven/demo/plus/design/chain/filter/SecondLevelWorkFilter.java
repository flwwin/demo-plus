package com.leven.demo.plus.design.chain.filter;

import com.leven.demo.plus.design.chain.IWorkFilter;
import com.leven.demo.plus.design.chain.enity.RequestContext;
import com.leven.demo.plus.design.chain.enity.RespondContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2) // 可以通过order对注入责任链的顺序控制
@Slf4j
public class SecondLevelWorkFilter implements IWorkFilter<RequestContext,RespondContext> {
    @Override
    public void doFilter(RequestContext req, RespondContext rep) {
        log.info("执行二级过滤器。。。。");
    }

}
