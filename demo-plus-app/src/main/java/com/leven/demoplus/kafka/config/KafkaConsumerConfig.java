package com.leven.demoplus.kafka.config;

import com.google.common.collect.Maps;
import com.lenven.demo.plus.common.queue.IHandBatchData;
import com.leven.demoplus.enity.DataLine;
import com.leven.demoplus.kafka.consumer.data.BatchDataSync;
import com.leven.demoplus.kafka.consumer.thread.BizKafkaConsumerRunnable;
import com.leven.demoplus.kafka.consumer.thread.KafkaLocalConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * 1: 通过配置类，对象注入到IOC中
 */
@Configuration
@ConditionalOnExpression(value = "false")
public class KafkaConsumerConfig {

    @Autowired
    private KafkaConfSetting setting;

    @Bean(value = "kafkaConsumer", initMethod = "init",destroyMethod = "close")
    public KafkaLocalConsumer creatKafkaConsumerOperator() {
        KafkaLocalConsumer consumer = new KafkaLocalConsumer();
        consumer.setGroupId(setting.getGroupId());
        consumer.setTopic(setting.getTopic());
        consumer.setPropMap(setting.getProMap());
        consumer.setTreadCount(setting.getTreadCount());
        consumer.setKafkaVersion(setting.getKafkaVersion());
        consumer.setKafkaConsumerStream(creatActualKafkaConsumer());
        consumer.setInitSwitch(setting.isInitSwitch());
        return consumer;
    }

    public BizKafkaConsumerRunnable creatActualKafkaConsumer() {
        BizKafkaConsumerRunnable consumer = new BizKafkaConsumerRunnable();
        HashMap<String, IHandBatchData<DataLine>> map = Maps.newHashMap();
        IHandBatchData batchDataSync = creatRealBatchDataSync();
        // 这里也可以用其他map作为key，创建三个队列，用topic作为key保证进入同一个队列
        map.put(consumer.dataSyncKey(),batchDataSync);
        consumer.setDataSyncMap(map);
        return consumer;
    }

    @Bean(value = "batchDataSync",initMethod = "init",destroyMethod = "close")
    public BatchDataSync creatRealBatchDataSync() {
        BatchDataSync dataSync = new BatchDataSync();
        dataSync.setBatchSize(setting.getBatchSize());
        dataSync.setQueueSize(setting.getQueueSize());
        dataSync.setMaxWaitMills(setting.getMaxWaitMills());
        return dataSync;
    }
}
