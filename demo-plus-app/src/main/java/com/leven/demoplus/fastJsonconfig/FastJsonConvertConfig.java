package com.leven.demoplus.fastJsonconfig;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * springboot默认采用jackjson作为httpMessage的解析框架
 * 修改配置支持通过FastJSON进行序列化
 */
public class FastJsonConvertConfig extends WebMvcConfigurationSupport {
    @Override
    protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        converters.add(fastJsonHttpMessageConverter);
        super.configureMessageConverters(converters);
    }
}
