package com.leven.demoplus.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean(value = "kafkaConf")
    public kafkaConfSetting createKakfaConfBean(){
        kafkaConfSetting kafkaConfSetting = new kafkaConfSetting();
        kafkaConfSetting.setTopic("lm_click");
        return kafkaConfSetting;
    }
}
