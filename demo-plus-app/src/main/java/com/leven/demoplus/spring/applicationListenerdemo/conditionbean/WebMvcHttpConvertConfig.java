package com.leven.demoplus.spring.applicationListenerdemo.conditionbean;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class WebMvcHttpConvertConfig  {

    @Bean
    public DefineHttpMessageConvert defineHttpMessageConvert(){
        DefineHttpMessageConvert defineHttpMessageConvert = new DefineHttpMessageConvert();
        return defineHttpMessageConvert;
    }

    /**
     * FastJsonHttpMessageConverter 是fastjson适配spirngmvc的json对象转java对象的适配器
     * springboot默认采用的时jackson
     */
    @Bean
    public FastJsonHttpMessageConverter fastJsonHttpMessageConverter(){
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        converter.setFastJsonConfig(fastJsonConfig);
        converter.setDefaultCharset(StandardCharsets.UTF_8);
        return converter;
    }
}
