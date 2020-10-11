package com.leven.demoplus.javase.proxy;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.Method;

/**
 * 回调过滤器：在CGLib回调时可以设置对不同方法执行不同的回调逻辑，或者根本不执行回调。
 *
 * <p>在JDK动态代理中并没有类似的功能，对InvocationHandler接口方法的调用对代理类内的所以方法都有效。
 */
class CallBackFilterTarget implements CallbackFilter {

  /**
   * 返回的值为数字，代表了Callback数组中的索引位置，要到用的Callback
   *
   * @param method 代理的方法
   * @return
   */
  @Override
  public int accept(Method method) {
    if (method.getName().equals("method01")) {
      System.out.println("filter method01");
      return 0;
    }
    return 0;
  }
}

/** */
class TargetObject {
  public void method01(String s) {
    System.out.println("method 01");
  }

    public void method02(String s) {
    System.out.println("method 02");
  }
}

public class CallbackFilterDemo {
  public static void main(String[] args) {
    //
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(TargetObject.class);
    CallbackFilter callBackFilterTarget = new CallBackFilterTarget();
    enhancer.setCallbackFilter(callBackFilterTarget);
    Callback noopCb = NoOp.INSTANCE;
    /*Callback callback1=new TargetInterceptor();
    Callback fixedValue=new TargetResultFixed();*/
    Callback[] cbarray = new Callback[] {noopCb};
    enhancer.setCallbacks(cbarray);
    TargetObject o = (TargetObject) enhancer.create();
    o.method01("100");
    o.method02("200");
  }
}
