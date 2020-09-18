package com.leven.demoplus.kafka.consumer;

import com.leven.demoplus.devstg.dataconsumer.AbstractBatchDataSync;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public abstract class AbstractKafkaStatConsumer extends KafkaConsumerRunable {

    private AbstractBatchDataSync<String> dataSync;

    @Override
    void handMsg(Byte[] data) {
        //提交数据
        dataSync.submit("");
    }

    @Override
    public void close(){

    }

    @Override
    public void run() {
        KafkaConsumer<Byte[],Byte[]> consumer = super.getConsumer();
        ConsumerRecords<Byte[],Byte[]> recordData = consumer.poll(2000);
        for (ConsumerRecord<Byte[], Byte[]> record : recordData) {
            Byte[] value = record.value();
            handMsg(value);
            System.out.println(value);
        }

    }

    public AbstractBatchDataSync<String> getDataSync() {
        return dataSync;
    }

    public void setDataSync(AbstractBatchDataSync<String> dataSync) {
        this.dataSync = dataSync;
    }
}
