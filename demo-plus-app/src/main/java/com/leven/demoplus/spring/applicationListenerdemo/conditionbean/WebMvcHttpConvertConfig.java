package com.leven.demoplus.spring.applicationListenerdemo.conditionbean;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcHttpConvertConfig extends WebMvcConfigurerAdapter {

    @Bean
    public DefineHttpMessageConvert defineHttpMessageConvert(){
        DefineHttpMessageConvert defineHttpMessageConvert = new DefineHttpMessageConvert();
        return defineHttpMessageConvert;
    }

  /*  *
     * FastJsonHttpMessageConverter 是fastjson适配spirngmvc的json对象转java对象的适配器
     * springboot默认采用的时jackson*/

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        //自定义配置...
        FastJsonConfig config = new FastJsonConfig();
        converter.setFastJsonConfig(config);
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
        converters.add(0, converter);
    }
}
