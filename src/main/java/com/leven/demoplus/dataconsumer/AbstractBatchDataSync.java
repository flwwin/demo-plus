package com.leven.demoplus.dataconsumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** 数据消费类:
 * 1:批量异步数据处理，提升性能
 * 2：实现优雅停机，停机数据不丢失
 * 3：继承该抽象类（记得初始化）
 * 4: 生产 */
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

    dataQueue = new LinkedBlockingQueue<T>(batchSize);

    // new thread to consume
    taskExecutor =
        new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    Thread th =
        new Thread(new FetchStatLineRunable(System.currentTimeMillis(), batchSize, maxWaitMills));
    th.setName(this.getClass().getSimpleName());
    th.start();
  }

  public LinkedBlockingQueue getDataQueue() {
    return dataQueue;
  }

  public void setDataQueue(LinkedBlockingQueue dataQueue) {
    this.dataQueue = dataQueue;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  @Override
  public void handData(T data) {}

  @Override
  public void handMultiData(List<T> datas) {}

  @Override
  public void submit(T data) {
    this.dataQueue.offer(data);
  }

  @Override
  public void close() throws IOException {
    System.out.println("关闭中。。。");
    this.isStop = true;
    taskExecutor.shutdown();
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

          T data = dataQueue.poll(100, TimeUnit.MILLISECONDS);
          if (null != data) {
            list.add(data);
          }
          // 超时或者线程达到指定大小开始处理数据
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

        // 优雅停机
        if (dataQueue.size() > 0) {
          list.add(dataQueue.poll());
        }
        TimeUnit.SECONDS.sleep(3);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
