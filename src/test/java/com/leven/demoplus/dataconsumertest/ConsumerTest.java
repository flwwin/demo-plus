package com.leven.demoplus.dataconsumertest;

import com.leven.demoplus.Design.dataconsumer.ConsumeData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ConsumerTest {
  @Autowired
  ConsumeData consumeData;
  @Test
  void test01() {
    ConsumeData consumeData = new ConsumeData();
    List list = new ArrayList();
    list.add(1);
    consumeData.submit(list);
  }
}
