package com.flw.demo.consumer.data;

import com.lenven.demo.plus.common.User;
import com.lenven.demo.plus.common.queue.AbstractBatchDataSync;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 队列同步消费数据 数据和处理数据解耦
 */
@Component
public class ConsumeData extends AbstractBatchDataSync<User> {

  @PostConstruct
  @Override
  public void init() {
    super.init();
  }

  /**
   * 数据批量处理
   *
   * @param datas 批量数据
   */
  @Override
  public void handMultiData(List<User> datas) {
    // 处理数据逻辑
    if (datas.size() != 0) {

      System.out.println(datas);
    }
  }

  /**
   * 处理单个数据
   *
   * @param data 数据
   */
  @Override
  public void handData(User data) {}
}
