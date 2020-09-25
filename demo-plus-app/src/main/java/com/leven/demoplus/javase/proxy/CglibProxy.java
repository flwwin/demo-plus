package com.leven.demoplus.javase.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 1：CGlib动态代理（为啥是类？） 2：操作：1：引入cglib的jar 2：创建代理类 3：实现MethodInterceptor接口，对方法拦截 4：创建被代理类 3：原理： 4：应用：
 */
class CgTarget {
  void sayHello(String param) {
    System.out.println("cglib\t" + param);
  }
}

class ProxyClass implements MethodInterceptor {

    /**
     *
     * @param o 目标对象
     * @param method 目标方法
     * @param objects 参数
     * @param methodProxy cilib代理方法
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
    System.out.println("start>>>>>");
        Object res = methodProxy.invokeSuper(o, objects);
        System.out.println("end>>>>>>");
        return res;
    }
}

public class CglibProxy {
  public static void main(String[] args) {
    //
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(CgTarget.class);
    enhancer.setCallback(new ProxyClass());
    CgTarget cgTarget = (CgTarget) enhancer.create();
    cgTarget.sayHello("hello");
  }
}
