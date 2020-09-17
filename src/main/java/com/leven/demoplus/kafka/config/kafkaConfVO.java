package com.leven.demoplus.kafka.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Data
public class kafkaConfVO {
    //@Value("${kafka.topic}") 从配置文件中读取配置
    //
    @Value("${kafka.topic}")
    private String topic;
    private String groupId;
    private Map<String,String> proMap;
    private int treadCount;
    private int kafkaVersion;

    private int batchSize;
    private int queueSize;
    private int maxWaitMills;

}
