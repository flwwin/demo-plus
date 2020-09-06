package com.leven.demoplus.jvm.oom;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 异常演示： java.lang.OutOfMemoryError: Metaspace
 * -XX:MetaspaceSize=8m -XX:MaxMetaspaceSize=8m
 */
class OomTest{}
public class MetaspaceDemo {
    public static void main(String[] args) {
        int i = 0;
        try {
            while (true){
                i++;
                final Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(OomTest.class);
                enhancer.setUseCache(false);
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                        return methodProxy.invoke(o,args);
                    }
                });

                enhancer.create();
            }
        }catch (Exception e){
            System.out.println("*****多少次发生异常 " + i);
            e.printStackTrace();
        }
    }
}
