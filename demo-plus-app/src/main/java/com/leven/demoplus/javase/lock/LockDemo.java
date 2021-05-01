package com.leven.demoplus.javase.lock;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.locks.ReentrantLock;

/** LockDemo */
@Slf4j
public class LockDemo {

  @Test
  public void test() {
    ReentrantLock lock = new ReentrantLock();

    try {
      lock.lock();
      log.info("hello 01...");
      lock.lock();
      log.info("hello 02...");
    } catch (Exception e) {
    } finally {
      lock.unlock();
      lock.unlock();
    }
  }
}
