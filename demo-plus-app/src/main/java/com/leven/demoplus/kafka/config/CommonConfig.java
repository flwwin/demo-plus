package com.leven.demoplus.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建要给配置类，实际中通过动态配置中心注入动态配置的值，这里只是个范例
 */
@Configuration
public class CommonConfig {

    @Bean(value = "kafkaConf")
    public KafkaConfSetting createKakfaConfBean(){
        KafkaConfSetting kafkaConfSetting = new KafkaConfSetting();
        kafkaConfSetting.setTopic("lm_click");
        return kafkaConfSetting;
    }
}
