package com.leven.demoplus.mybatis;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/*
  在springboot中想通过配置文件配置我们的mybaits
 */
@Configuration
@ImportResource(locations = {"classpath:spring/application-bean.xml"})
public class Mybatisconfig {

}
