package com.leven.demoplus.javase.thread;

import java.util.concurrent.*;

/**
 * 线程池有哪些优点：
 * 线程池做的工作主要是控制运行的线程的数量，处理过程中将任务放入队列，然后在线程创建后启动这些任务，如果线程数量超过了最
 * 大数量超出数量的线程排队等候，等其它线程执行完毕，再从队列中取出任务来执行。
 * 他的主要特点为:线程复用:控制最大并发数:管理线程。
 * 第一:降低资源消耗。通过重复利用己创建的线程降低线程创建和销毁造成的消耗。
 * 第二:提高响应速度。当任务到达时，任务可以不需要的等到线程创建就能立即执行。
 * 第三:提高线程的可管理性。线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进
 * 行统- -的分配，调优和监控
 *
 * 线程池在阿里的规范里面，不可以用Excutors创建的，原因就是默认创建的21亿大小的线程池，太大了，容易引发OOM
 * 在开发中我们都是自己手写一个线程池的，但是线程池的定义大小，的看业务是IO密集型还是CPU密集型的。
 * CPU密集型的话定义为：CPU核数+1
 * IO密集型的话定义为：参考公式: CPU核数11-阻塞系数阻塞系数在0.8~0.9之间比如8核CPU: 81 1-0.9= 80个线程数对于自己创建线程池的5个参数定义：
 * 也可以简单的cpu*2这个公式，让CPU更多的利用，而不是被其他的阻塞。
 * 拒绝策略：
 */
public class ThreadPoolDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Executors.newCachedThreadPool();
        Executors.newFixedThreadPool(10);
        ExecutorService threadPool = new ThreadPoolExecutor(
                2,
                5,
                1L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());


            FutureTask<Integer> futureTask = new FutureTask<>(() -> {
                 try {TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
                return 10 * 10;
            });
            threadPool.submit(futureTask);
            while (!futureTask.isDone()){

            }
            System.out.println(futureTask.get());


    }
}
