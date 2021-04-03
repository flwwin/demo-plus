package com.flw.demo.consumer;

import com.lenven.demo.plus.common.data.DataLine;
import com.lenven.demo.plus.common.queue.IHandBatchData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Slf4j
@Data
/**
 * kafka消费抽象处理类
 * 1：实现dataSyncKey 获取异步队列的key{@link KafkaConsumerConfig#creatActualKafkaConsumer()}
 * 2：dataSyncMap 可以配置的时候配置多个队列
 */
public abstract class AbstractKafkaConsumerRunnable extends KafkaConsumerRunable {

    /**
     * 这个对象更对是解析kafka数据用的，在公司业务中用到，这里忽略这部分业务代码
     */
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
        // todo 这里可以做具体的业务解析
        return new DataLine();
    };

    @Override
    public void close() {

    }

    @Override
    public void run() {
        KafkaConsumer<byte[], byte[]> consumer = super.getConsumer();

        // 这里需要注意定义的超时时间是否合理，是否需要阻塞
        ConsumerRecords<byte[], byte[]> recordData = consumer.poll(Duration.ofMillis(2000));
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
