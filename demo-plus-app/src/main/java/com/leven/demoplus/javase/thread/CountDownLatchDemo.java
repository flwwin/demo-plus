package com.leven.demoplus.javase.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 让一些线程阻塞直到另一些线程完成一系列操作后才被唤醒
 CountDownLatch主要有两个方法， 当一个或多个线程调用await方法时， 调用线程会被阻塞。
 其它线程调用countDown方法会将计数器减1(调用countDown方法的线程不会阻塞)，
 当计数器的值变为零时，因调用await方法被阻塞的线程会被唤醒，继续执行。
 */
public class CountDownLatchDemo {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(9);

        for (int i = 1; i <= countDownLatch.getCount(); i++) {

            new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("第\t" + Thread.currentThread().getName() + "自习结束。。。。");
                countDownLatch.countDown();
            }, String.valueOf(i)).start();
        }

        try {
           /* 1：countDownLatch没有减为0的时候，await一直都会阻塞线程的,发生异常，没有执行countDown也是阻塞。
              countDownLatch.await();
              2：还可以计时等待
            */
            countDownLatch.await(3,TimeUnit.SECONDS);
            System.out.println("countDownLatch = " + countDownLatch.getCount()+"工作做完了，下班了！！");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
