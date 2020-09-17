package com.leven.demoplus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
//@ImportResource(locations = {"classpath:/srping/xxx"}) 支持导入spirng的配置文件，在开发中常用于导入旧项目的xml文件，导入bean
@MapperScan("com.leven.demoplus.mybatis.dao")
public class DemoplusApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoplusApplication.class, args);
    }

}
