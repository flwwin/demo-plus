package com.leven.demoplus.inner.data;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public abstract class AbstractBatchDataSync<T> implements IBatchDataSync<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBatchDataSync.class);

	private int queueSize;	// 队列大小
	private int batchSize;	// 每批数据
	private int maxWaitMills;
	
	private volatile boolean isStop = false;
	private LinkedBlockingQueue<T> dataLineQueue;
	private ExecutorService taskExecutor;
	
	public AbstractBatchDataSync() {
		super();
		
		this.queueSize = 100;
		this.batchSize = 10;
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
		
		dataLineQueue = new LinkedBlockingQueue<>(queueSize);
		// using thread factory instead of executors
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("batch-data-sync-%d").build();
		taskExecutor = new ThreadPoolExecutor(1, 1,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(),threadFactory);
		
		Thread th = new Thread(new FetchStatLineRunnable(this.getBatchSize(), this.getMaxWaitMills()));
		th.setName(this.getClass().getSimpleName());
		th.start();
	}

	@Override
	public void submit(T data) {
		if (isStop) {
			LOGGER.warn("source is stop, current line is ignore! data: {}", data);
			
			return;
		}
		
		// 多个消息处理
		try {
			dataLineQueue.put(data);
		} catch (InterruptedException e) {
			LOGGER.error("submit data line error:{}", data, e);
		}
	}
	
	/**
	 * 将数据重新提交到队列中，内部重试时调用此方法
	 * @param 
	 * @return
	 */
	protected void innerSubmit(final T data) {
		if (isStop) {
			LOGGER.warn("source is stop, current line is ignore! line: {}", data);
			
			return;
		}
		
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				// 多个消息处理
				try {
					dataLineQueue.offer(data, maxWaitMills, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					LOGGER.warn("async submit data line error:{}", data, e);
				}
			}
		});
	}
	
	@Override
	public void close() {
		this.isStop = true;
		
		if (null != taskExecutor) {
			taskExecutor.shutdown();
		}
	}

	/**
	 * 获取queueSize
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public int getQueueSize() {
		return queueSize;
	}

	/**
	 * 设置queueSize  
	 * @param   queueSize	sth.   
	 * @since   Ver 1.0
	 */
	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	/**
	 * 获取batchSize
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public int getBatchSize() {
		return batchSize;
	}

	/**
	 * 设置batchSize  
	 * @param   batchSize	sth.   
	 * @since   Ver 1.0
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	/**
	 * 获取maxWaitMills
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public int getMaxWaitMills() {
		return maxWaitMills;
	}

	/**
	 * 设置maxWaitMills  
	 * @param   maxWaitMills	sth.   
	 * @since   Ver 1.0
	 */
	public void setMaxWaitMills(int maxWaitMills) {
		this.maxWaitMills = maxWaitMills;
	}

	private class FetchStatLineRunnable implements Runnable {
		private long lastHandle;
		private int batchSize;
		private int maxWaitMills;
		private FetchStatLineRunnable(int batchSize, int maxWaitMills) {
			this.batchSize = batchSize;
			this.maxWaitMills = maxWaitMills;
			this.lastHandle = System.currentTimeMillis();
		}
		@Override
		public void run() {
			LOGGER.info("data sync start...");
			List<T> dataList = new ArrayList<>(this.batchSize);
			while (!isStop) {
				try {
					// 拉取数据并缓存到list中
					T data = dataLineQueue.poll(100, TimeUnit.MILLISECONDS);
					if (null != data) {
						dataList.add(data);
					}
					
					// 超时或者list达到指定大小则开始处理数据
					long interval = System.currentTimeMillis() - lastHandle;
					if (interval >= maxWaitMills || dataList.size() >= batchSize) {
						this.lastHandle = System.currentTimeMillis();
						
						// 处理数据
						if (!dataList.isEmpty()) {
							try {
								handleMulti(dataList);
							} finally {
								dataList.clear();
							}
						}
					}
				} catch (Throwable ex) {
					LOGGER.error("handle data error !", ex);
				}
			}
			
			// 停止前清理剩下的
			T data = null;
			while ((data = dataLineQueue.poll()) != null) {
				dataList.add(data);
			}
			
			if (dataList.size() > 0) {
				LOGGER.info("handle rest {} records.", dataList.size());
				handleMulti(dataList);
			}
			
			LOGGER.info("data sync stop!");
		}
	}
}

