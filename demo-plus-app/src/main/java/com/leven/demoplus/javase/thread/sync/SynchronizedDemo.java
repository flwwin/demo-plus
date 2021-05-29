package com.leven.demoplus.javase.thread.sync;

import org.junit.Test;

public class SynchronizedDemo {

  private final Object object = new Object();
  @Test
  public void test01() {
    synchronized (object) {
      System.out.println("true = " + true);
    }
  }
}
