package com.leven.demoplus.kafka.config;

import com.leven.demoplus.kafka.consumer.KafkaConsumerOperator;
import com.leven.demoplus.kafka.consumer.RealBatchDataSync;
import com.leven.demoplus.kafka.consumer.RealKafkaConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Value("${kafkaConfig}")
    private kafkaConfVO kafkaConf;

    @Bean(value = "kafkaConsumer", initMethod = "init"/*,destroyMethod = "close"*/)
    public KafkaConsumerOperator creatKafkaConsumerOperator() {
        KafkaConsumerOperator consumer = new KafkaConsumerOperator();
        consumer.setGroupId(kafkaConf.getGroupId());
        consumer.setTopic(kafkaConf.getTopic());
        consumer.setPropMap(kafkaConf.getProMap());
        consumer.setTreadCount(kafkaConf.getTreadCount());
        consumer.setKafkaVersion(kafkaConf.getKafkaVersion());
        consumer.setKafkaConsumerStream(creatActualKafkaConsumer());
        return consumer;
    }

    @Bean(value = "kafkaConsumer")
    public RealKafkaConsumer creatActualKafkaConsumer() {
        RealKafkaConsumer consumer = new RealKafkaConsumer();
        consumer.setDataSync(creatRealBatchDataSync());
        return consumer;
    }

    @Bean(value = "realBatchDataSync")
    public RealBatchDataSync creatRealBatchDataSync() {
        RealBatchDataSync dataSync = new RealBatchDataSync();
        dataSync.setBatchSize(kafkaConf.getBatchSize());
        dataSync.setQueueSize(kafkaConf.getQueueSize());
        dataSync.setMaxWaitMills(kafkaConf.getMaxWaitMills());
        return dataSync;
    }
}
