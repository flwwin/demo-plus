package com.leven.demoplus.Design.observe;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
class Test1Test {
    @Autowired
    ObserveService observeService;

    @Test
    void test01() {
        HashMap<String, Object> map = Maps.newHashMap();
        map.put("eventParam","参数");
        observeService.executeService("eventChain", map);
    }
}