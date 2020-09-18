package com.leven.demoplus.kafka.config;

import com.leven.demoplus.devstg.dataconsumer.AbstractBatchDataSync;
import com.leven.demoplus.kafka.consumer.AbstractKafkaStatConsumer;
import com.leven.demoplus.kafka.consumer.KafkaLocalConsumer;
import com.leven.demoplus.kafka.consumer.BatchDataSync;
import com.leven.demoplus.kafka.consumer.BizKafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 1: 通过配置类，对象注入到IOC中
 */
@Configuration
@ConditionalOnExpression(value = "false")
public class KafkaConfig {

    @Autowired
    private kafkaConfVO kafkaConf;

    @Bean(value = "kafkaConsumer", initMethod = "init"/*,destroyMethod = "close"*/)
    public KafkaLocalConsumer creatKafkaConsumerOperator() {
        KafkaLocalConsumer consumer = new KafkaLocalConsumer();
        consumer.setGroupId(kafkaConf.getGroupId());
        consumer.setTopic(kafkaConf.getTopic());
        consumer.setPropMap(kafkaConf.getProMap());
        consumer.setTreadCount(kafkaConf.getTreadCount());
        consumer.setKafkaVersion(kafkaConf.getKafkaVersion());
        consumer.setKafkaConsumerStream(creatActualKafkaConsumer());
        return consumer;
    }

    @Bean(value = "kafkaConsumer")
    public AbstractKafkaStatConsumer creatActualKafkaConsumer() {
        BizKafkaConsumer consumer = new BizKafkaConsumer();
        consumer.setDataSync(creatRealBatchDataSync());
        return consumer;
    }

    @Bean(value = "realBatchDataSync")
    public AbstractBatchDataSync creatRealBatchDataSync() {
        BatchDataSync dataSync = new BatchDataSync();
        dataSync.setBatchSize(kafkaConf.getBatchSize());
        dataSync.setQueueSize(kafkaConf.getQueueSize());
        dataSync.setMaxWaitMills(kafkaConf.getMaxWaitMills());
        return dataSync;
    }
}
