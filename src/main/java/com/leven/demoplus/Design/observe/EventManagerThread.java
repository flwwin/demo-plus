package com.leven.demoplus.Design.observe;

import java.util.Map;

/**
 * 多线程执行类
 */
public class EventManagerThread implements Runnable {
    private Observer observer;
    private Map<String, Object> paras;

    EventManagerThread(Observer observer, Map<String, Object> paras) {
        this.observer = observer;
        this.paras = paras;
    }

    @Override
    public void run() {
        this.observer.execute(paras);
    }
}
