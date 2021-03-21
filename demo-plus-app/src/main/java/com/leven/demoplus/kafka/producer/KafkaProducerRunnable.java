package com.leven.demoplus.kafka.producer;

import lombok.Data;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.io.Closeable;

@Data
public abstract class KafkaProducerRunnable implements Runnable, Cloneable, Closeable {

    public KafkaProducer<byte[],byte[]> producer;


    @Override
    public KafkaProducerRunnable clone() {
        try {
            return (KafkaProducerRunnable) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    abstract void handMsg(byte[] data);

    @Override
    public void run() {
        //ProducerRecord producerRecord = new ProducerRecord();
        //producer.send()
    }
}
