package com.leven.demoplus.devstg.config;

import com.leven.demoplus.kafka.config.kafkaConfVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean(value = "kafkaConf")
    public kafkaConfVO createKakfaConfBean(){
        kafkaConfVO kafkaConfVO = new kafkaConfVO();
        kafkaConfVO.setTopic("lm_click");
        return kafkaConfVO;
    }
}
