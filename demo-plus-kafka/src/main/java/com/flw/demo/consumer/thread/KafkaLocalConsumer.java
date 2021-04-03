package com.flw.demo.consumer.thread;

import com.flw.demo.consumer.KafkaConsumerRunable;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

@Slf4j
@Data
public class KafkaLocalConsumer implements Closeable {

    private int treadCount = 10;

    private String groupId;
    private String topic;
    private ExecutorService executor;
    private KafkaConsumerRunable kafkaConsumerStream; //创建时候传入

    private Map<String, String> propMap; //补充配置
    private int kafkaVersion; //kafka的版本8和8以后的版本不一样
    private boolean initSwitch; //初始化开关

    //初始化方法
    public void init() {
        if (initSwitch){
            log.info("kafka inint switch is close|{}",initSwitch);
        }
        if (kafkaVersion>8){
            initKafkaSetting();//针对9以上版本
        }
    }

    @Override
    public void close() throws IOException {
        if (kafkaConsumerStream != null){
            kafkaConsumerStream.close();
        }
        if (null != executor){
            executor.shutdown();
            try {
                if (!executor.awaitTermination(2000,TimeUnit.MILLISECONDS)){

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void initKafkaSetting() {
        //创建一个properties
        Properties properties = createKafkaConfProperties();
        initThreadExecutor();
        //多线程批量处理
        for (int i = 0; i < treadCount; i++) {
            KafkaConsumer<byte[],byte[]> kafkaConsumer = new KafkaConsumer<byte[],byte[]>(properties);
            kafkaConsumer.subscribe(Collections.singletonList(topic));
            KafkaConsumerRunable runable = null;
            try {
                runable = kafkaConsumerStream.clone();
            } catch (Exception e) {
                log.error("create kafka run thread got error",e);
            }
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
}
