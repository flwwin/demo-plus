package com.leven.demoplus.ThreadDemo.ProdAndConsumer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者消费者阻塞队列版
 */
class shareResource {
    private Boolean FLAG = true;
    BlockingQueue blockingDeque = null;
    AtomicInteger i = new AtomicInteger();

    public shareResource(BlockingQueue blockingDeque) {
        this.blockingDeque = blockingDeque;
    }

    public void myProd() {
        while (FLAG) {
            String data = i.incrementAndGet() + "";
            try {
                boolean offer = blockingDeque.offer(data, 2L, TimeUnit.SECONDS);
                if (offer) {
                    System.out.println(Thread.currentThread().getName() + "\t" + "成功生产一个");
                } else {
                    System.out.println(Thread.currentThread().getName() + "\t" + "插入失败");
                }
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
       }
        System.out.println("FLAG = " + FLAG + "大老板叫停");
    }

    public void myconsum() {
        while (FLAG) {
            String data = i.incrementAndGet() + "";
            try {
                String poll = (String) blockingDeque.poll(2L, TimeUnit.SECONDS);
                if (poll == null || poll.equalsIgnoreCase("")) {
                    System.out.println(Thread.currentThread().getName() + "获取失败");
                } else {
                    System.out.println(Thread.currentThread().getName() + "成功获得消息");
                }
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {

            }

        }
        System.out.println("FLAG = " + FLAG + "大老板叫停");
    }

    public void stop() {
        this.FLAG = false;
    }
}

public class ProdAndConQueue {
    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(1);

        shareResource shareResource = new shareResource(queue);

        new Thread(new Runnable() {
            @Override
            public void run() {
                shareResource.myProd();
            }
        },"prod").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                shareResource.myconsum();
            }
        },"consumer").start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        shareResource.stop();
    }
}
