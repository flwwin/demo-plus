

package com.leven.demoplus.inner.sentry.stat;

import com.google.common.collect.Maps;
import com.leven.demoplus.inner.sentry.monitor.MonitorItem;
import com.leven.demoplus.inner.sentry.monitor.MonitorUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExecutorStatPool extends DefaultStatPool implements Runnable {
	
	public static final String LOGGER_NAME = "common.stat.ExecutorStat";
	public static final String STAT_POOL_START = "=====================stat output at ";
	public static final String STAT_POOL_END = "=====================";

	//日志记录对象
	private Logger logger = LoggerFactory.getLogger(ExecutorStatPool.class.getSimpleName());

	/**
	 * 存放已有实体的map
	 */
	private ConcurrentHashMap<String, ExecutorStat> executorStatMap = new ConcurrentHashMap<String, ExecutorStat>();
	
	private static final ExecutorStatPool INSTANCE = new ExecutorStatPool();
	
	public static ExecutorStatPool getInstance() {
		return INSTANCE;
	}
	
	public ExecutorStatPool() {
		super();
	}
	
	/**
	 * 根据标识符获取执行统计实体
	 * @param 
	 * @return
	 */
	public ExecutorStat getExecutorStat(String identifier) {
		return getExecutorStat(identifier, 0);
	}
	
	/**
	 * 根据标识符获取执行统计实体
	 * @param 
	 * @return
	 */
	public ExecutorStat getExecutorStat(String identifier, int childSize) {
		String key = identifier + "_" + childSize;
		ExecutorStat existStat = executorStatMap.get(key);
		if(null == existStat) {
			existStat = new ExecutorStat(identifier, childSize);
			ExecutorStat oldExecStat = executorStatMap.putIfAbsent(key, existStat);
			if(null != oldExecStat) {
				existStat = oldExecStat;
			}
		}
		
		return existStat;
	}
	
	/**
	 * 将统计信息输出，如果要改变其输出方式，可以重写此方法
	 */
	@Override
	public void output() {
		if (null == logger || !logger.isInfoEnabled()
				|| executorStatMap.isEmpty()) {
			return;
		}

		// 当前时间
		String now = null;
		try {
			//todo
			//now = DateUtils.date2Str(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss");
		} catch (Exception ex) {
			now = new Timestamp(System.currentTimeMillis()).toString();
		}
		
		StringBuilder statBuilder = new StringBuilder();
		
		// header
		statBuilder.append(STAT_POOL_START).append(now).append(STAT_POOL_END);
		
		// body
		boolean hasData = false;

		// monitor Data
		List<MonitorItem> monitorList = new ArrayList<MonitorItem>();
		for(ExecutorStat execStat : executorStatMap.values()) {
			// 加入监控数据上报
			if (execStat.hasStatInfo() && getMonitorSwith(execStat.getIdentifier())) {
				ExecutorMonitorItem monitorItem = ExecutorMonitorItem.convert2Item(
						ExecutorStat.adaptMinVal(execStat));
				if (shouldUpload(monitorItem)) {
					monitorList.add(monitorItem);
				}
			}
			String statInfo = execStat.toStatString(isResetOld());
			if(null != statInfo) {
				hasData = true;
				statBuilder.append("\r\n").append(now).append(" ").append(statInfo);
			}
		}
		
		if (hasData) {
			logger.info(statBuilder.toString());
		}

		if (!CollectionUtils.isEmpty(monitorList)) {
			MonitorUploader.upload(this.getMonitorUrl(), monitorList);
		}
	}

	//todo
	//private static final DynamicJsonToMapProperty<Integer> EXECUTOR_STAT_UPLOAD_CONF = new DynamicJsonToMapProperty<Integer>("executor_stat_upload_conf", (String)null, Integer.class);

	private boolean shouldUpload(ExecutorMonitorItem monitorItem) {
		//todo
		//Map<String, Integer> dataMap = EXECUTOR_STAT_UPLOAD_CONF.getDataMap();
		Map<String, Integer> dataMap = Maps.newHashMap();
		if (CollectionUtils.isEmpty(dataMap)) {
			return true;
		}

		Integer minTtl = dataMap.get(monitorItem.getTarget());
		if (null == minTtl) {
			return true;
		}

		return monitorItem.getTttl() >= minTtl;
	}

	/**
	 * 获取日志处理类
	 * @return  the logger
	 * @since   Ver 1.0
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * 设置日志处理类
	 * @param   logger    
	 * @since   Ver 1.0
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public static void main(String[] args) {
		ExecutorStatPool pool = new ExecutorStatPool();
		pool.setMonitorSwitch(1);
		pool.setMonitorUrl("xx");
		pool.setLogger(LoggerFactory.getLogger("test"));

		ExecutorStat es0 = pool.getExecutorStat("test0");
		es0.addStat(true, 1000);
		es0.addStat(true, 1023);
		es0.addStat(true, 1010);
		es0.addStat(true, 1020);
		es0.addStat(false, 1111);
		
		ExecutorStat es1 = pool.getExecutorStat("test1");
		es1.addStat(true, 1000);
		es1.addStat(true, 1023);
		es1.addStat(true, 1010);
		es1.addStat(true, 1020);
		es1.addStat(false, 1111);
		
		pool.output();
	}

}



