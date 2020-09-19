package com.leven.demoplus.kafka.config;

import com.google.common.collect.Maps;
import com.leven.demoplus.devstg.dataconsumer.IHandBatchData;
import com.leven.demoplus.enity.DataLine;
import com.leven.demoplus.kafka.consumer.AbstractKafkaStatConsumer;
import com.leven.demoplus.kafka.consumer.BatchDataSync;
import com.leven.demoplus.kafka.consumer.BizKafkaConsumer;
import com.leven.demoplus.kafka.consumer.KafkaLocalConsumer;
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
public class KafkaConfig {

    @Autowired
    private kafkaConfVO kafkaConf;

    @Bean(value = "kafkaConsumer", initMethod = "init",destroyMethod = "close")
    public KafkaLocalConsumer creatKafkaConsumerOperator() {
        KafkaLocalConsumer consumer = new KafkaLocalConsumer();
        consumer.setGroupId(kafkaConf.getGroupId());
        consumer.setTopic(kafkaConf.getTopic());
        consumer.setPropMap(kafkaConf.getProMap());
        consumer.setTreadCount(kafkaConf.getTreadCount());
        consumer.setKafkaVersion(kafkaConf.getKafkaVersion());
        consumer.setKafkaConsumerStream(creatActualKafkaConsumer());
        consumer.setInitSwitch(kafkaConf.isInitSwitch());
        return consumer;
    }

    @Bean(value = "kafkaConsumer")
    public AbstractKafkaStatConsumer creatActualKafkaConsumer() {
        BizKafkaConsumer consumer = new BizKafkaConsumer();
        HashMap<String, IHandBatchData<DataLine>> map = Maps.newHashMap();
        IHandBatchData batchDataSync = creatRealBatchDataSync();

        map.put(consumer.dataSyncKey(),batchDataSync);
        consumer.setDataSyncMap(map);

        return consumer;
    }

    @Bean(value = "batchDataSync",initMethod = "init",destroyMethod = "close")
    public BatchDataSync creatRealBatchDataSync() {
        BatchDataSync dataSync = new BatchDataSync();
        dataSync.setBatchSize(kafkaConf.getBatchSize());
        dataSync.setQueueSize(kafkaConf.getQueueSize());
        dataSync.setMaxWaitMills(kafkaConf.getMaxWaitMills());
        return dataSync;
    }
}
