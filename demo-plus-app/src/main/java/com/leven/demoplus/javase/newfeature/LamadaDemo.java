package com.leven.demoplus.javase.newfeature;

import java.util.concurrent.*;

/**
 * GCRoot:虚拟机栈中的局部变量引用
 * 方法区 ，静态表里
 * 静态常量
 * 本地方法栈用的
 */

public class LamadaDemo {
    public static void main(String[] args) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5,
                0L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        for (int i = 0; i <9 ; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                     try {TimeUnit.SECONDS.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
                    System.out.println("i = " + Thread.currentThread().getName());
                }
            });
        }
    }
}
