package com.leven.demoplus.kafka.consumer.data;


import com.lenven.demo.plus.common.queue.AbstractBatchDataSync;

import javax.sound.sampled.DataLine;
import java.util.List;

/**
 * kafka消费批处理
 */
public class BatchDataSync extends AbstractBatchDataSync<DataLine> {
    @Override
    public void handMultiData(List<DataLine> datas) {
        /*
          1：datas为从kafka消费的数据
          2：该方法在父类中新启动一个线程持续处理消费数据
          3：消费数据批次的大小,初始化的时候可以定义（最好动态配置起来，按照实际需求调节）
         */
        //todo  处理kafka消费数据
    }

    @Override
    public void handData(DataLine data) {

    }
}
