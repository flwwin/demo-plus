package com.leven.demoplus.Design.observe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ObserveService {

    @Autowired
    EventManager eventManager;

    public void executeService(String eventName, Map<String, Object> mapParam) {
        eventManager.dispatchEvent(eventName, mapParam);
    }
}
