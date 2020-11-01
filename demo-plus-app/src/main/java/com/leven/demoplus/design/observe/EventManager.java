package com.leven.demoplus.design.observe;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EventManager {
    public Map<String, List<Observer>> events;


    @PostConstruct
    //todo events可以动态配置化
    public void init(){
        events = Maps.newHashMap();
        List arrayList = new ArrayList();
        arrayList.add(new ObserverAction());
        events.put("eventChain", arrayList);
    }

    void dispatchEvent(String eventName, Map<String, Object> paras) {
        if (events == null || events.isEmpty()) {
            return;
        }

        List<Observer> observers = events.get(eventName);
        for (Observer observer : observers) {
            if (observer.isAsyn()) {
                //异步
                EventManagerThread eventManagerThread = new EventManagerThread(observer, paras);
                Thread thread = new Thread(eventManagerThread);
                thread.start();
            } else {
                //同步
                observer.execute(paras);
            }
        }
    }

    public void setEvents(Map<String, List<Observer>> events) {
        this.events = events;
    }
}
