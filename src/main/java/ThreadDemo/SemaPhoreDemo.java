package ThreadDemo;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 每次只能三个
 * semaphore.release(); 释放一个位置
 * semaphore.acquire(); 占用一个位置 带参数就是占用多久
 */
public class SemaPhoreDemo {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);//模拟三个停车位
        for (int i = 1; i < 7; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println(Thread.currentThread().getName() + "\t抢到车位");
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println(Thread.currentThread().getName() + "\t停车3秒后离开车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }, String.valueOf(i)).start();
        }

    }
}
