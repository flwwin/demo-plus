package com.leven.demoplus.javase.newfeature;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** 四大函数式接口： 消费型接口： 供给型接口： 函数型接口： 断言型接口： */
public class FunctionInterfaceDemo {

  // 消费型接口Consumer:没有返回值的，需要要给入参
  @Test
  public void test01() {
    // hello("hello world", System.out::println);
    hello("hello", s -> System.out.println("s = " + s + "word"));
  }

  void hello(String str, Consumer<String> con) {
    con.accept(str);
  }

  // 供给型接口：Supplier  返回一个处理后的对象，不需要入参
  @Test
  void test02() {
    List list = getRandomNum(10, () -> new Random().nextInt(10));
    System.out.println(list.toString());
  }

  List getRandomNum(int num, Supplier<Integer> sup) {
    List list = new ArrayList();
    for (int i = 0; i < num; i++) {
      list.add(sup.get());
    }
    return list;
  }

  // 函数型接口Function  有入参也有出参

  @Test
  void test03() {
    String s = handle("牛逼哄哄", (str) -> str.substring(0, 2));
    System.out.println(s);
  }

  String handle(String str, Function<String, String> fun) {
    return fun.apply(str);
  }

  // 断言型接口Predicate  返回一个boolean
  @Test
  void test04() {
    List<String> asList = Arrays.asList("123", "12345");
    List list = handle02(asList, (str) -> str.length() > 3);
    System.out.println(list.toString());
  }

  List handle02(List<String> list, Predicate<String> pre) {

    List<String> strList = new ArrayList();

    for (String i : list) {
      if (pre.test(i)) {
        strList.add(i);
      }
    }
    return strList;
  }
}
