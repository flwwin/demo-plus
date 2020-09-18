package com.leven.demoplus.devstg.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class HandService {
    @Autowired
    Handlevelfactory handlevelfactory;

    public void consumerData(){
        handlevelfactory.getInstance(1).handData();
    }
}
