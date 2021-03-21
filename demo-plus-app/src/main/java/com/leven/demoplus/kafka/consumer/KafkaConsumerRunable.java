package com.leven.demoplus.kafka.consumer;

import lombok.Data;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.Closeable;

/**
 * kafka消费线程类
 * 1：consumer对象在KafkaLocalConsumer初始化的时候赋值
 * 2：具体业务子类实现handMag方法，按照业务逻辑处理数据
 */
@Data
public abstract class KafkaConsumerRunable implements Runnable, Cloneable, Closeable {

    public KafkaConsumer<byte[],byte[]> consumer;


    @Override
    public KafkaConsumerRunable clone() {
        try {
            return (KafkaConsumerRunable) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    abstract void handMsg(byte[] data);
}
