package com.leven.demoplus.devstg.dataconsumer;

import com.leven.demoplus.enity.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/** 消费数据 */
@Component
public class ConsumeData extends AbstractBatchDataSync<User> {

  @PostConstruct
  @Override
  public void init() {
    super.init();
  }

  @Override
  public void handMultiData(List<User> datas) {
    // 处理数据逻辑
    if (datas.size()!=0){

      System.out.println(datas);
    }
  }

  @Override
  public void handData(User data) {

  }


}
