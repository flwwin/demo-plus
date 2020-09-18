package com.leven.demoplus.ThreadDemo;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * CyclicBarrier的字面意思是可循环(Cyclic) 使用的屏障(Barrier) 。它要做的事情是，
 * 让一组线程到达一个屏障(也可以叫同步点)时被阻塞，直到最后一个线程到达屏障时，
 * 屏障才会开门，所有被屏障拦截的线程才会继续干活，线程进入屏障通过CyclicBarrier的wait0方法。
 * 最后一个线程达到屏障的时候，就是回调第二个参数的方法中的线程，屏障就是await方法
 * 通俗的说：CountDownLatch就是减少
 * CyclicBarrier就是增加 CyclicBarrier 可以重新使用的
 */
public class CycliBarrierDemo {
    //收集七颗龙珠 召唤神龙
    public static void main(String[] args) {
        //CyclicBarrier(int parties, Runable barrierAction)
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7, () -> {
            //屏障跳闸时执行给定的屏障动作，由最后一个进入屏障的线程执行
            System.out.println("*******召唤神龙");
        });
        for (int i = 1; i <= 7; i++) {
            final int tempInt = i;
            new Thread(() -> {
                //System.out.println(Thread.currentThread().getName() + "\t收集到第: " + tempInt + " 龙珠");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();

        }


    }
}
