package com.leven.demoplus.kafka.config;

import java.util.Map;

public class kafkaConfVO {
    private String topic;
    private String groupId;
    private Map<String,String> proMap;
    private int treadCount;
    private int kafkaVersion;

    private int batchSize;
    private int queueSize;
    private int maxWaitMills;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getMaxWaitMills() {
        return maxWaitMills;
    }

    public void setMaxWaitMills(int maxWaitMills) {
        this.maxWaitMills = maxWaitMills;
    }

    public int getKafkaVersion() {
        return kafkaVersion;
    }

    public void setKafkaVersion(int kafkaVersion) {
        this.kafkaVersion = kafkaVersion;
    }

    public int getTreadCount() {
        return treadCount;
    }

    public void setTreadCount(int treadCount) {
        this.treadCount = treadCount;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Map<String, String> getProMap() {
        return proMap;
    }

    public void setProMap(Map<String, String> proMap) {
        this.proMap = proMap;
    }
}
