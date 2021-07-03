package com.leven.demoplus.javase.newfeature;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
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

    /**
     * Options类 java8新推出来的类，防止空指针的
     * 1：ofNullable(T value) 返回一个 Optional指定值的Optional，如果非空，则返回一个空的 Optional
     *   一般搭配orElse(T other) 方法，防止空指针
     * 2: of  返回一个非空的Optional 如果是null 爆空指针
     */
    @Test
    public void test02(){
        List list = null;

        //这样防止空指针
        List list1 = Optional.ofNullable(list).orElse(Collections.singletonList(1));
        System.out.println(list1);
    }
}
