package com.leven.demoplus.java8api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class OptionsDemo {
    /**
     * Options类 java8新推出来的类，防止空指针的
     * 1：ofNullable(T value) 返回一个 Optional指定值的Optional，如果非空，则返回一个空的 Optional
     *   一般搭配orElse(T other) 方法，防止空指针
     * 2:
     */
    @Test
    void test01(){
        List list = Arrays.asList(1,2,3);

        //这样防止空指针
        List list1 = Optional.of(list).orElse(Collections.singletonList(1));
        System.out.println(list1);
    }
}
