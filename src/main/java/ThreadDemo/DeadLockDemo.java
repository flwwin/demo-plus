package ThreadDemo;

import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 死锁
 */
public class DeadLockDemo {

    public static void main(String[] args) {
        String s1 = "1";
        String s2 = "2";
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (s1) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (s2) {

                    }

                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (s2) {
                    synchronized (s1) {

                    }
                }
            }
        }).start();
    }
}
