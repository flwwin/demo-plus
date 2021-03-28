package com.leven.demoplus.mybatis.source;

import org.apache.ibatis.annotations.Select;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

interface UserMapper {
  @Select("select * from dual where id=#{id}")
  void selectOne(int id);
}

public class Demo01 {
  public static void main(String[] args) {
    // 这是Mapper文件执行的大概原理
    UserMapper userMapper =
        (UserMapper)
            Proxy.newProxyInstance(
                Demo01.class.getClassLoader(),
                new Class<?>[] {UserMapper.class},
                new InvocationHandler() {
                  @Override
                  public Object invoke(Object proxy, Method method, Object[] args)
                      throws Throwable {
                    Select annotation = method.getAnnotation(Select.class);
                    if (null != annotation) {
                      String[] value = annotation.value();
                      System.out.println(Arrays.toString(value));
                    }
                    return null;
                  }
                });
    userMapper.selectOne(1);
  }
}
