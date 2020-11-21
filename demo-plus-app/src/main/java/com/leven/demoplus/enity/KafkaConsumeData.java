package com.leven.demoplus.enity;

import lombok.Data;

/**
 * kafka消费数据实体类
 */
public class KafkaConsumeData {
    @Data
    public static class User {
      private String name;
      private int age;
    }
}
