package com.leven.demoplus.javase;

import java.util.Arrays;
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
}
