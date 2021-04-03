package com.flw.demo.config;

import com.flw.demo.consumer.data.BatchDataSync;
import com.flw.demo.consumer.thread.BizKafkaConsumerRunnable;
import com.flw.demo.consumer.thread.KafkaLocalConsumer;
import com.google.common.collect.Maps;
import com.lenven.demo.plus.common.data.DataLine;
import com.lenven.demo.plus.common.kafka.KafkaSetting;
import com.lenven.demo.plus.common.queue.IHandBatchData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
public class kafakaConfig {

    private static final KafkaSetting setting = new KafkaSetting();

    /**
     *
     */
    static {
        setting.setTopic("expose");
        setting.setGroupId("dev-test");
        setting.setInitSwitch(true);
        setting.setBatchSize(500);
        setting.setMaxWaitMills(3000);
        setting.setKafkaVersion(9);
        setting.setQueueSize(10000);
        setting.setBatchSize(500);
    }


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
        Map<String, IHandBatchData<DataLine>> map = Maps.newHashMap();
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
