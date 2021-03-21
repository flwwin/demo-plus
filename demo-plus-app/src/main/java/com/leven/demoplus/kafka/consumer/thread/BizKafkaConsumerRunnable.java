package com.leven.demoplus.kafka.consumer.thread;


import com.leven.demoplus.kafka.config.BizConstants;
import com.leven.demoplus.kafka.consumer.AbstractKafkaConsumerRunnable;

/**
 * 具体业务的消费处理类
 * 1：按照自己业务逻辑处理数据，封装到实体类
 *
 */
public class BizKafkaConsumerRunnable extends AbstractKafkaConsumerRunnable {
    @Override
    public String dataSyncKey() {
        return BizConstants.DataTopic.DATA_SYNC_KEY;
    }
}
