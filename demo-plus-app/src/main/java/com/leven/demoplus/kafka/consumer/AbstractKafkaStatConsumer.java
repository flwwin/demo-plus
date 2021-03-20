package com.leven.demoplus.kafka.consumer;

import com.leven.demoplus.enity.DataLine;
import com.leven.demoplus.kafka.IHandBatchData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Data
public abstract class AbstractKafkaStatConsumer extends KafkaConsumerRunable {

    public String[] eventKeyConf;
    public Map<String, IHandBatchData<DataLine>>  dataSyncMap;

    @Override
    void handMsg(byte[] data) {
        //提交数据
        try {
            String s = new String(data, StandardCharsets.UTF_8);
            IHandBatchData<DataLine> dataSync = dataSyncMap.get(dataSyncKey());
            if (null == dataSync){
                return;
            }
            DataLine dataLine = parseStrToDataLine(s);
            dataSync.submit(dataLine);
        } catch (Exception e) {
            log.error("hand kafka msg got error", e);
        }
    }

    protected DataLine parseStrToDataLine(String s){
        //todo
        return new DataLine();
    };

    @Override
    public void close() {

    }

    @Override
    public void run() {
        KafkaConsumer<byte[], byte[]> consumer = super.getConsumer();
        ConsumerRecords<byte[], byte[]> recordData = consumer.poll(2000);
        for (ConsumerRecord<byte[], byte[]> record : recordData) {
            byte[] value = record.value();
            if (value == null || value.length <= 0) {
                continue;
            }
            handMsg(value);
        }
    }

   public abstract String dataSyncKey();
}
