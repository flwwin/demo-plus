
package com.leven.demoplus.inner.sentry.stat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class DefaultStatPool implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CustomizedStatPool.class);	//日志记录对象

	private AtomicBoolean isStart = new AtomicBoolean(false);

	private volatile long interval;	//记录线程执行间隔
	private volatile boolean resetOld; //每次输出后是否重置数据
	private volatile Thread thread;

	/**
	 * 监测地址
	 */
	private String monitorUrl = "";

	/**
	 * 0:开关关闭 1:开关全开 2:【包含】identifierList的全部打开 3:【不包含】identifierList的全部打开
	 */
	private Integer monitorSwitch = 1;
	private List<String> identifierList;

	public DefaultStatPool() {
		interval = 60 * 1000;	//默认1分钟执行一次
		resetOld = true;			//默认每次统计完后重置

		init();
	}

	public void run() {
		while(isStart.get()) {
			// 防止设置错误，输出周期最小为1秒
			if (interval < 1000) {
				interval = 1000;
			}
			
			//每隔一定时间生成统计日志
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
			}
			output();
		}
	}
	
	/**
	 * 将统计信息输出，如果要改变其输出方式，可以重写此方法
	 * @param execStat
	 * @return
	 */
	 public abstract void output() ;

	/**
	 * 参数初始化
	 * @param 
	 * @return
	 */
	private void init() {
		if (!isStart.compareAndSet(false, true)) {
			throw new IllegalStateException("already start, do not call init() twice!");
		}
		initThread();
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				DefaultStatPool.this.stop();
			}
		}));
	}
	
	//开启数据记录的线程
	private void initThread() {
		Thread logThread = new Thread(this);
		logThread.setName(this.getClass().getSimpleName() + logThread.getId());
		//设置为后台运行
		logThread.setDaemon(true);
		//设置为低优先级
		logThread.setPriority(4);
		//开始执行现场
		logThread.start();
		
		thread = logThread;
	}

	public boolean getMonitorSwith(String identifier) {
		if (monitorSwitch == null || monitorSwitch == 2) {
			if (CollectionUtils.isEmpty(identifierList)) {
				return false;
			}
			return identifierList.contains(identifier);
		} else if (monitorSwitch == 3) {
			if (CollectionUtils.isEmpty(identifierList)) {
				return true;
			}
			return !identifierList.contains(identifier);
		} else if (monitorSwitch == 0) {
			return false;
		} else if (monitorSwitch == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取记录统计信息的频率
	 * @return  the interval
	 * @since   Ver 1.0
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * 设置记录统计信息的频率
	 * @param   interval    
	 * @since   Ver 1.0
	 */
	public void setInterval(long interval) {
		if(interval <= 0) {
			return;
		}
		
		this.interval = interval;
	}

	/**
	 * 获取每次输出后是否重置数据
	 * @return  the resetOld
	 * @since   Ver 1.0
	 */
	public boolean isResetOld() {
		return resetOld;
	}

	/**
	 * 设置每次输出后是否重置数据
	 * @param   resetOld    
	 * @since   Ver 1.0
	 */
	public void setResetOld(boolean resetOld) {
		this.resetOld = resetOld;
	}
	
	public void stop() {
		if(!this.isStart.compareAndSet(true, false)) {
			return;
		}
		
		if (null != thread) {
			thread.interrupt();
			thread = null;
		}
	}
	
	public Integer getMonitorSwitch() {
		return monitorSwitch;
	}

	public void setMonitorSwitch(Integer monitorSwitch) {
		this.monitorSwitch = monitorSwitch;
	}

	public List<String> getIdentifierList() {
		return identifierList;
	}

	public void setIdentifierList(List<String> identifierList) {
		this.identifierList = identifierList;
	}

	public String getMonitorUrl() {
		return monitorUrl;
	}

	public void setMonitorUrl(String monitorUrl) {
		this.monitorUrl = monitorUrl;
	}
}



