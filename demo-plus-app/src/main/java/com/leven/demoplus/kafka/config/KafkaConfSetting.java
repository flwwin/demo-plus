package com.leven.demoplus.kafka.config;

import lombok.Data;

import java.util.Map;

@Data
public class KafkaConfSetting {
    /**
     * topic 消费主题 可以通过动态配置中心动态注入
     */
    private String topic="click";

    /**
     * 消费的groupId
     */
    private String groupId="consumdata";

    /**
     * kafka其他的一些配置项
     */
    private Map<String,String> proMap;

    /**
     * 通过几个线程去消费
     */
    private int treadCount=1;

    /**
     * kafka的版本配置，8以后的版本配置不太一样
     */
    private int kafkaVersion=9;

    /**
     * 异步从队列中处理消息的每次获取大小
     */
    private int batchSize=100;

    /**
     * 队列初始化大小
     */
    private int queueSize=10000;

    /**
     * 间隔多久去队列中获取消费数据
     * 1：batchSize 和 maxWaitMills 那个达到都会触发批量消费
     */
    private int maxWaitMills=3000;

    /**
     * 初始化开关
     */
    private boolean initSwitch;

}
