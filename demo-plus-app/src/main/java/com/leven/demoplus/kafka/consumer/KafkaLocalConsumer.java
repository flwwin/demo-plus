package com.leven.demoplus.kafka.consumer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Value;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

@Slf4j
public class KafkaLocalConsumer implements Closeable {

    @Value("${threadCount}")
    private int treadCount = 10;

    private String groupId;
    private String topic;
    private ExecutorService executor;
    private KafkaConsumerRunable kafkaConsumerStream; //创建时候传入

    private Map<String, String> propMap; //补充配置
    private int kafkaVersion; //kafka的版本
    private boolean kafkaSwitch; //kafka消费开关

    //初始化方法
    public void init() {
        if (kafkaSwitch){
            log.info("kafka inint switch is close|{}",kafkaSwitch);
        }
        if (kafkaVersion>8){
            initKafkaSetting();//针对9以上版本
        }
    }

    @Override
    public void close() throws IOException {
        //todo 关闭资源
    }

    private void initKafkaSetting() {
        //创建一个properties
        Properties properties = createKafkaConfProperties();
        initThreadExecutor();
        //多线程批量处理
        for (int i = 0; i < treadCount; i++) {
            KafkaConsumer<Byte[],Byte[]> kafkaConsumer = new KafkaConsumer<Byte[],Byte[]>(properties);
            kafkaConsumer.subscribe(Collections.singletonList(topic));
            KafkaConsumerRunable runable = kafkaConsumerStream.clone();
            if (null == runable){
                throw new NullPointerException("kafkaConsumerStream is null");
            }
            runable.setConsumer(kafkaConsumer);
            executor.submit(runable);
        }
    }

    private void initThreadExecutor() {
        int curTreadCount = 1;
        if (treadCount > 1) {
            curTreadCount = treadCount;
        }
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(groupId + "." + topic + ".threadCount").build();
        executor = new ThreadPoolExecutor(curTreadCount, curTreadCount, 0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), threadFactory);

    }

    private Properties createKafkaConfProperties() {
        Properties properties = new Properties();
        properties.put("zkAddress", topic);
        properties.put("group.id", groupId);
        //TODO 其他参数
        appendConf(properties);
        return properties;
    }

    /**
     * 补充kafka配置的其他参数
     * @param properties
     */
    private void appendConf(Properties properties) {
        if (null != properties && null != propMap) {
            properties.putAll(propMap);
        }
    }

    public int getTreadCount() {
        return treadCount;
    }

    public void setTreadCount(int treadCount) {
        this.treadCount = treadCount;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public KafkaConsumerRunable getKafkaConsumerStream() {
        return kafkaConsumerStream;
    }

    public void setKafkaConsumerStream(KafkaConsumerRunable kafkaConsumerStream) {
        this.kafkaConsumerStream = kafkaConsumerStream;
    }

    public Map<String, String> getPropMap() {
        return propMap;
    }

    public void setPropMap(Map<String, String> propMap) {
        this.propMap = propMap;
    }

    public int getKafkaVersion() {
        return kafkaVersion;
    }

    public void setKafkaVersion(int kafkaVersion) {
        this.kafkaVersion = kafkaVersion;
    }
}
