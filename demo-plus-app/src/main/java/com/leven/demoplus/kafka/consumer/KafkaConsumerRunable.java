package com.leven.demoplus.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Iterator;

/**
 * kafka消费线程类
 * 1：consumer对象在KafkaLocalConsumer初始化的时候赋值
 * 2：具体业务子类实现handMag方法，按照业务逻辑处理数据
 */
public abstract class KafkaConsumerRunable implements Runnable, Cloneable, Closeable {

    public KafkaConsumer<byte[],byte[]> consumer;

    @Override
    protected KafkaConsumerRunable clone() throws CloneNotSupportedException {
        try {
            return (KafkaConsumerRunable) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    abstract void handMsg(byte[] data);

    public KafkaConsumer<byte[],byte[]> getConsumer() {
        return consumer;
    }

    public void setConsumer(KafkaConsumer<byte[],byte[]> consumer) {
        this.consumer = consumer;
    }
}
