package com.leven.demoplus.kafka.consumer;

import com.leven.demoplus.devstg.dataconsumer.AbstractBatchDataSync;

import java.io.IOException;

public abstract class AbstractKafkaStatConsumer extends KafkaConsumerRunable {

    private AbstractBatchDataSync dataSync;

    @Override
    void handMsg() {
        //提交数据
        dataSync.submit("");
    }

    @Override
    public void close() throws IOException {

    }

    public AbstractBatchDataSync getDataSync() {
        return dataSync;
    }

    public void setDataSync(AbstractBatchDataSync dataSync) {
        this.dataSync = dataSync;
    }
}
