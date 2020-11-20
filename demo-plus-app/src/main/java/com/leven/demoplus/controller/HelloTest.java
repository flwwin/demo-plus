package com.leven.demoplus.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloTest {

  @RequestMapping("/hello")
  @ResponseBody
  public void hello(@RequestBody User user) {
    System.out.println(user);
  }
}
