package com.leven.demoplus.Design.observe;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class ObserverAction extends AbstractObserver {

    @PostConstruct
    public void init(){
        this.asyn=true;
    }

    @Override
    public void execute(Map<String, Object> param) {
        //具体业务代码逻辑
        System.out.println("action params is " + param);
        System.out.println("thread name Observer:" + Thread.currentThread().getName());
    }
}
