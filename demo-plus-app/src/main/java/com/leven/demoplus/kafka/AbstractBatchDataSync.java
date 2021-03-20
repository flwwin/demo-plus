package com.leven.demoplus.kafka;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 数据消费类:
 * 1: 批量异步数据处理，提升性能
 * 2：实现优雅停机，停机数据不丢失
 * 3：继承该抽象类（记得初始化）
 * 4: 生产
 */
@Slf4j
public abstract class AbstractBatchDataSync<T> implements IHandBatchData<T> {
    private LinkedBlockingQueue<T> dataQueue;
    private int batchSize; // 数据大小
    private int queueSize; // 队列大小
    private int maxWaitMills; // 等待时长

    private volatile boolean isStop = false;

    private ExecutorService taskExecutor;

    public AbstractBatchDataSync() {
        super();

        this.batchSize = 100;
        this.queueSize = 10;
        this.maxWaitMills = 3000;
    }

    public void init() {
        if (batchSize <= 0) {
            batchSize = 1;
        }

        if (queueSize <= 0) {
            queueSize = 1;
            return;
        }

        dataQueue = new LinkedBlockingQueue<T>(2000); //队列大小必须根据业务合理设置，Offer不然超过容量就不丢掉数据

        // new thread to consume
        taskExecutor =
                new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        Thread th =
                new Thread(new FetchStatLineRunable(System.currentTimeMillis(), batchSize, maxWaitMills));
        th.setName(this.getClass().getSimpleName());
        th.start();
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getMaxWaitMills() {
        return maxWaitMills;
    }

    public void setMaxWaitMills(int maxWaitMills) {
        this.maxWaitMills = maxWaitMills;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public abstract void handData(T data);

    public abstract void handMultiData(List<T> datas);

    @Override
    public void submit(T data) {
        if (isStop){
            //日志
            log.info("application is stop");
        }

       /*
        1)add(anObject):把anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,则返回true,否则报异常
        2)offer(anObject):表示如果可能的话,将anObject加到BlockingQueue里,即如果BlockingQueue可以容纳,则返回true,否则返回false.
        3)put(anObject):把anObject加到BlockingQueue里,如果BlockQueue没有空间,则调用此方法的线程被阻断直到BlockingQueue里面有空间再继续.
        */
        try {
            this.dataQueue.put(data);
        } catch (InterruptedException e) {
           //日志 日志打点监控
        }
    }

    @Override
    public void close() throws IOException {
        log.info("停机中。。。，batchSync stoping");
        this.isStop = true;
        if (null != taskExecutor) {
            taskExecutor.shutdown();
        }
    }

    private class FetchStatLineRunable implements Runnable {
        private long lastTime;
        private int batchSize;
        private int maxWaitMills;

        FetchStatLineRunable(long lastTime, int batchSize, int maxWaitMills) {
            this.lastTime = lastTime;
            this.batchSize = batchSize;
            this.maxWaitMills = maxWaitMills;
        }

        @Override
        public void run() {
            try {
                List<T> list = new ArrayList<T>(batchSize);
                // consumer dadta
                while (!isStop) {

                    T data = dataQueue.poll(100, TimeUnit.MILLISECONDS); //poll不会阻塞(可以计时)，take会阻塞
                    if (null != data) {
                        list.add(data);
                    }
                    // 超时或者数据达到指定大小开始处理数据
                    long interval = System.currentTimeMillis() - lastTime;
                    if (interval >= maxWaitMills || list.size() >= batchSize) {
                        try {
                            handMultiData(list);
                        } finally {
                            list.clear();
                        }
                        this.lastTime = System.currentTimeMillis();
                    }
                }

                // 停机前消费剩余数据
                T data = null;
                while ((data = dataQueue.poll()) != null) {
                    list.add(data);
                }
                if (list.size() > 0) {
                    handMultiData(list);
                }
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
