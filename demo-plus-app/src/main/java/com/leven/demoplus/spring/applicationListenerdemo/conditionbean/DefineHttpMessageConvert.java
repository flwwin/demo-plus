package com.leven.demoplus.spring.applicationListenerdemo.conditionbean;

import com.google.protobuf.Message;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * 自定义消息转换器
 */
public class DefineHttpMessageConvert extends AbstractHttpMessageConverter<Message> {
    @Override
    protected boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    protected Message readInternal(Class<? extends Message> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    protected void writeInternal(Message message, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

    }
}
