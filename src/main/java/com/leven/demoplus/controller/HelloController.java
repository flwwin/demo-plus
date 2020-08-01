package com.leven.demoplus.controller;

import com.leven.demoplus.dataconsumer.ConsumeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {
  @Autowired
  ConsumeData consumeData;

  @RequestMapping("/hello")
  public void sayHello() {
    List list = new ArrayList();
    list.add(111);
    consumeData.submit(list);
  }
}
