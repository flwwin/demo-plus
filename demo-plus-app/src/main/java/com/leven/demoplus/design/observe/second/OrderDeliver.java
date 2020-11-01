package com.leven.demoplus.design.observe.second;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * 需求场景：1：订单生产的时候，发送短信，邮件给用户
 * 采用，监听这模式，对代码解耦。
 */

/**
 * 定义一个事件类
 */

public class OrderDeliver extends ApplicationEvent {

    public Map param;

    public OrderDeliver(Object source,Map param) {
        super(source);
        this.param=param;
    }

    public Map getParam() {
        return param;
    }

    public void setParam(Map param) {
        this.param = param;
    }
}
