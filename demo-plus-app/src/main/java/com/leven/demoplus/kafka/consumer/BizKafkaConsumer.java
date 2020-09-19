package com.leven.demoplus.kafka.consumer;

import com.leven.demoplus.kafka.BatchSyncConstants;

/**
 * 具体业务的消费处理类
 * 1：按照自己业务逻辑处理数据，封装到实体类
 *
 */
public class BizKafkaConsumer extends AbstractKafkaStatConsumer {
    @Override
    public String dataSyncKey() {
        return BatchSyncConstants.BatchSyncInstance.DATA_SYNC_KEY;
    }
}