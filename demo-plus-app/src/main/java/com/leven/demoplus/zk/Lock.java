package com.leven.demoplus.zk;

public interface Lock {

    boolean tryLock();

    void lock();

    void unlock();
}
