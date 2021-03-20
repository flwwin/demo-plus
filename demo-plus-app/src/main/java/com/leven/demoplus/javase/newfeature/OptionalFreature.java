package com.leven.demoplus.javase.newfeature;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Optional;

/**
 * Optional特性
 */
@Slf4j
public class OptionalFreature {

    /**
     optional容器类的常用方法:
     optional.of(T t) :创建一个Optional实例
     Optional.empty() :创建一个空的Optional实例
     optional.ofNullable(T t):若t不为null,创建Optional 实例,否则创建空实例
     isPresent() :判断是否包含值
     orElse(T t) :如果调用对象包含值,返回该值,否则返回t
     orElseGet(Supplier s) :如果调用对象包含值，返回该值，否则返回s获取的值
     map(Function f):如果有值对其处理，并返回处理后的Optional，否则返回Optional.empty()fatMap(Function mapper):与map 类似，要求返回值必须是Optional
     */

    @Test
    public void test01(){
        Optional<String> optional = Optional.of("456");
        log.info(optional.get());

        Optional<String> optional1 = Optional.ofNullable(null);
        log.info(optional1.get());
    }
}
