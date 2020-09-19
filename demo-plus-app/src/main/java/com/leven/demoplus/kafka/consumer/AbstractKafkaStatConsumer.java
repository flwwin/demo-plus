package com.leven.demoplus.kafka.consumer;

import com.leven.demoplus.devstg.dataconsumer.AbstractBatchDataSync;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractKafkaStatConsumer extends KafkaConsumerRunable {

    private AbstractBatchDataSync<String> dataSync;

    @Override
    void handMsg(byte[] data) {
        //提交数据
        try {
            String s = new String(data, StandardCharsets.UTF_8);
            if (null != s){
                dataSync.submit(s);
            }
        } catch (Exception e) {
            log.error("hand kafka msg got error",e);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void run() {
        KafkaConsumer<byte[], byte[]> consumer = super.getConsumer();
        ConsumerRecords<byte[], byte[]> recordData = consumer.poll(2000);
        for (ConsumerRecord<byte[], byte[]> record : recordData) {
            byte[] value = record.value();
            if (value == null || value.length<=0){
                continue;
            }
            handMsg(value);
        }
    }

    public AbstractBatchDataSync<String> getDataSync() {
        return dataSync;
    }

    public void setDataSync(AbstractBatchDataSync<String> dataSync) {
        this.dataSync = dataSync;
    }
}
