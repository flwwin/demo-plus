package com.leven.demoplus.devstg.observer;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * 实现：ApplicationListener  //https://www.jianshu.com/p/e450cded3306
 *  1：event listener 是observer模式一种体现
 */
public class ApplicationListerDemo implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }
}
