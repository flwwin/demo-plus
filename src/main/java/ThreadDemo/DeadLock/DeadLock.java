package ThreadDemo.DeadLock;

import java.util.concurrent.TimeUnit;

/**
 * 手写一个死锁
 */
public class DeadLock {

    public static void main(String[] args) {
        String str1 = "1";
        String str2 = "2";

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (str1) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (str2) {

                    }
                }
            }
        }, "AA").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (str2) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (str1) {

                    }
                }
            }
        }, "BB").start();
    }
}
