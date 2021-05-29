package com.leven.demoplus.javase.aqs;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class Demo {

  public static void main(String[] args) {

      // 创建两个线程强同一把锁，第一个线程休眠10000s，第二个线程直接尝试获取锁，通过debug来看AQS的源码实现
      ReentrantLock lock = new ReentrantLock(true);
      ExecutorService es = Executors.newFixedThreadPool(10);
      DemoThread t1 = new DemoThread(lock, true);
      DemoThread t2 = new DemoThread(lock, false);

      es.submit(t1);
      es.submit(t2);
  }
}
