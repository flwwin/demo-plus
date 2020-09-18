package com.leven.demoplus.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Iterator;


public abstract class KafkaConsumerRunable implements Runnable, Cloneable, Closeable {

    public KafkaConsumer<Byte[],Byte[]> consumer;

    @Override
    protected KafkaConsumerRunable clone() {
        try {
            return (KafkaConsumerRunable) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    abstract void handMsg(Byte[] data);

    public KafkaConsumer<Byte[],Byte[]> getConsumer() {
        return consumer;
    }

    public void setConsumer(KafkaConsumer<Byte[],Byte[]> consumer) {
        this.consumer = consumer;
    }
}
