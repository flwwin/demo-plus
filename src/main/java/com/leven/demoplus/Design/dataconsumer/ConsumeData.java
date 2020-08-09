package com.leven.demoplus.Design.dataconsumer;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/** 消费数据 */
@Component
public class ConsumeData extends AbstractBatchDataSync {

  @PostConstruct
  @Override
  public void init() {
    super.init();
  }

  @Override
  public void handMultiData(List datas) {
    // 处理数据逻辑
    System.out.println(datas);
  }

}
