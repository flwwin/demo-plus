package com.leven.demoplus.javase.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JVM动态代理
 * 1:必须是实现接口（应为JVM动态代理继承了Proxy类，java只能是单继承）
 * 2：通过反射调用
 * 3：操作步骤  1：定义接口 2：实现InvocationHandler接口，实现invoke方法，增强原生方法调用原生方法
 *             3：通过Proxy.newProxyInstance 生产代理类，
 * 原理：通过反射调用实际方法
 * 实践：AOP，Mybatis中通过Mapper接口实现对DAO的操作
 */
interface ISayHello {
  void sayHello(String str);
}

class HelloImpl implements ISayHello {
  @Override
  public void sayHello(String str) {
    System.out.println("JVM say Hello..."+str);
  }
}

class JVMProxyHello implements InvocationHandler{
    private ISayHello sayHello;

    public JVMProxyHello(ISayHello sayHello) {
        this.sayHello = sayHello;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    System.out.println("start ....");
        Object invoke = method.invoke(sayHello, args);
        System.out.println("end ...");
        return invoke;
    }
}

public class JVMProxy {
  public static void main(String[] args) {
    //
      ISayHello hello = new HelloImpl();
      JVMProxyHello jvmProxyHello = new JVMProxyHello(hello);
      ISayHello o = (ISayHello) Proxy.newProxyInstance(JVMProxy.class.getClassLoader(), new Class[]{ISayHello.class}, jvmProxyHello);
      o.sayHello("str");
  }
}
