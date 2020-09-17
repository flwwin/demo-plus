package com.leven.demoplus.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Iterator;


public abstract class KafkaConsumerRunable implements Runnable, Cloneable, Closeable {

    private KafkaConsumer consumer;


    @Override
    public void run() {
        ConsumerRecords poll = consumer.poll(Duration.ofMillis(100));
        Iterator iterator = poll.iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            handMsg();
        }
    }

    @Override
    protected KafkaConsumerRunable clone() {
        try {
            return (KafkaConsumerRunable) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    abstract void handMsg();

    public KafkaConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(KafkaConsumer consumer) {
        this.consumer = consumer;
    }
}
