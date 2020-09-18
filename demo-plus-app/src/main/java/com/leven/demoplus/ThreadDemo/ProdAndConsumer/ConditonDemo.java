package com.leven.demoplus.ThreadDemo.ProdAndConsumer;
/*
题目:多线程之间按顺序调用，实现A->B->C三 个线程启动，要求如下:
        AA打印5次，BB打印/10次，CC打E/15次
        紧接着
        AA打E15次，BB打印10次，CC打印15次
        来10轮
*/

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class ShareResource {
    private int num = 0;
    private ReentrantLock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

    public void doWork(int count) {
        lock.lock();
        try {
            if (count == 5) {
                while (num != 0) {
                    c1.await();
                }
                for (int i = 1; i <= count; i++) {
                    System.out.println(i);
                }
                num++;
                c2.signal();
            } else if (count == 10) {
                while (num != 1) {
                    c2.await();
                }
                for (int i = 1; i <= count; i++) {
                    System.out.println(i);
                }
                num++;
                c3.signal();
            } else {
                while (num != 2) {
                    c3.await();
                }
                for (int i = 1; i <= count; i++) {
                    System.out.println(i);
                }
                num++;
                c1.signal();
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }
}

public class ConditonDemo {

    public static void main(String[] args) {
        ShareResource resource = new ShareResource();
        new Thread(new Runnable() {
            @Override
            public void run() {
                resource.doWork(5);
            }
        }, "AA").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                resource.doWork(10);
            }
        }, "BB").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                resource.doWork(15);
            }
        }, "CC").start();
    }
}
