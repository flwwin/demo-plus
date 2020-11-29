package com.leven.demoplus.javase.api;

import com.google.common.collect.Lists;
import lombok.Data;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StreamAPI中集合类的流式编程
 */
public class StreamApiDemo {

    public static void main(String[] args) {
        // filter 过滤元素
        /*Integer[] strs = {1, 2, 3, 4};
        List<Integer> strings = Arrays.asList(strs);
        final long count = strings.stream().count();
        System.out.println("count = " + count); //结果 4*/
        //strings.stream().filter(s -> !s.isEmpty()).forEach(System.out::println);

        //map 计算元素
        //strings.stream().map(s -> s + 1).forEach(strings::add);

        //limit 获取钱N个元素 skip  抛弃钱N个元素
        // strings = strings.stream().limit(2).collect(Collectors.toList());
        //System.out.println(strings.toString());
       /* strings = strings.stream().skip(5).collect(Collectors.toList());
        System.out.println(strings);*/

        //sorted 排序

       /* strings.stream().sorted((String o1, String o2) -> {
            if (Integer.valueOf(o1) < Integer.valueOf(o2))
            {
                return 1;
            }else {
                return -1;
            }
        }).forEach(System.out::println);*/

        //distinct  消除重复
        Integer[] strs = {1, 2, 3,3, 4};
        List<Integer> strings = Arrays.asList(strs);
        //去冲后的结果转为list
        strings = strings.stream().distinct().collect(Collectors.toList());
        System.out.println(strings.toString());


       stream();
    }

    public static void  stream(){
        System.out.println("test");
    }

    /**
     * 创建Stream
     */
    @Test
    public void test01(){

    }

    /**
     * reduce 归于
     */
    @Test
    public void test02(){
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 55, 66);
        Integer reduce = list.stream().reduce(0, Integer::sum);
        System.out.println("reduce = " + reduce);
    }


    /**
     * 收集 collect 将流转换成其他形式，接收一个Collector接口实现，用于给Stream中元素做汇总的方法
     * Collectors提供常用的收集器实例
     */
    @Test
    public void test03(){
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4, 55, 66);
        list.stream().collect(Collectors.toCollection(HashSet::new));

        //总数
        Long collect = list.stream().collect(Collectors.counting());

        //平均值
        Double collect1 = list.stream().collect(Collectors.averagingLong(Integer::intValue));

        //最大值

        //最小值

        //分组

        //多级分组

        //分片

        //连接

        String collect2 = list.stream().map(String::valueOf).collect(Collectors.joining());
        System.out.println("collect2 = " + collect2);
        String collect3 = list.stream().map(String::valueOf).collect(Collectors.joining(","));
        System.out.println("collect3 = " + collect3);

    }


    @Data
    public class Employee{
        private String name;
        private int age;
    }


}
