package com.leven.demoplus.kafka.consumer;

import com.leven.demoplus.devstg.dataconsumer.AbstractBatchDataSync;
import com.leven.demoplus.enity.KafkaConsumeData;

import java.util.List;

/**
 * kafka消费批处理
 */
public class BatchDataSync extends AbstractBatchDataSync<KafkaConsumeData> {
    @Override
    public void handMultiData(List<KafkaConsumeData> datas) {
        /*
          1：datas为从kafka消费的数据
          2：该方法在父类中新启动一个线程持续处理消费数据
          3：消费数据批次的大小,初始化的时候可以定义（最好动态配置起来，按照实际需求调节）
         */
        //todo  处理kafka消费数据
    }

    @Override
    public void handData(KafkaConsumeData data) {

    }
}
