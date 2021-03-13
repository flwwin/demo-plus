package com.leven.demoplus.kafka.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Data
public class kafkaConfSetting {
    //@Value("${kafka.topic}") 从配置文件中读取配置
    //
    @Value("${kafka.topic}")
    private String topic="click";
    private String groupId="consumdata";
    private Map<String,String> proMap;
    private int treadCount=1;
    private int kafkaVersion=9;

    private int batchSize=100;
    private int queueSize=100;
    private int maxWaitMills=3000;
    private boolean initSwitch;

}
