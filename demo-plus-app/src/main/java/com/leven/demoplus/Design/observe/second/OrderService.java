package com.leven.demoplus.Design.observe.second;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class OrderService {
    @Autowired
    ApplicationContext applicationContext;


    public void orderDeliver(){
        //生成订单
        HashMap map = Maps.newHashMap();
        map.put("userId",456);
        OrderDeliver deliver = new OrderDeliver(this, map);
        applicationContext.publishEvent(deliver);
    }
}
