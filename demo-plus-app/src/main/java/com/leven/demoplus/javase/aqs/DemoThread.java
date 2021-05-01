package com.leven.demoplus.javase.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DemoThread implements Runnable {

  private final ReentrantLock lock;

  private final boolean wait;

  public DemoThread(ReentrantLock lock, boolean wait) {
    this.lock = lock;
    this.wait = wait;
  }

  @Override
  public void run() {
    lock.lock();
    try {
      if (wait) {
        TimeUnit.SECONDS.sleep(10);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
  }
}
