

package com.leven.demoplus.inner.sentry.stat;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ExecutorStat implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	public static final String STAT_IDENTIFIER = "";
	public static final String STAT_START = "start at ";
	public static final String STAT_OUTPUT = "output at ";
	public static final String STAT_EXEC_TIMES = "tttl:"; //执行次数
	public static final String STAT_EXEC_AVG = "tavg:";   //平均耗时
	public static final String STAT_EXEC_TOTAL_TIME = "ettl:"; //执行总时间

	public static final String STAT_SUCC_TIMES = "sttl:"; //成功次数
	public static final String STAT_SUCC_AVG = "savg:";	  //成功平均耗时
	public static final String STAT_SUCC_MAX = "smax:";   //成功最大耗时
	public static final String STAT_SUCC_MIN = "smin:";   //成功最小耗时
	public static final String STAT_SUCC_EXEC_TOTAL_TIME = "settl:"; //成功执行总时间

	public static final String STAT_FAIL_TIMES = "fttl:";
	public static final String STAT_FAIL_AVG = "favg:";
	public static final String STAT_FAIL_MAX = "fmax:";
	public static final String STAT_FAIL_MIN = "fmin:";
	public static final String STAT_FAIL_EXEC_TOTAL_TIME = "fettl:";

	public static final char COLUMN_SPLIT_CHAR = ' ';
	public static final char SEC_SPLIT_CHAR = '|';
	public static final String CHILD_START = "\n{\n";
	public static final String CHILD_END = "\n}\n";

	private String identifier;		//当前统计项的标识
	private final int childNodeSize;		//子节点个数
	private List<ExecutorStat> childNodeList;	//子节点list
	private long startTime;			//统计开始时间
	private long outputTime;		//统计输出时间

	private long executorTimes;		//执行次数
	private long totalExecutorTime;	//总执行时间，以纳秒为单位

	private long successTimes;		//执行成功次数
	private long totalSuccessTime;	//总成功时间，以纳秒为单位
	private long maxSuccessTime;	//执行成功的最大时间，以纳秒为单位
	private long minSuccessTime;	//执行成功的最小时间，以纳秒为单位

	private long maxFailTime;		//执行失败的最大时间，以纳秒为单位
	private long minFailTime;		//执行失败的最小时间，以纳秒为单位

	private boolean hasStatInfo;		//是否加入了统计信息

	private static final NumberFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

	/**
	 *
	 * Creates a new instance of ExecutorStat.
	 *
	 * @param identifier 统计节点标识
	 */
	public ExecutorStat(String identifier) {
		this(identifier, 0);
	}

	/**
	 *
	 * Creates a new instance of ExecutorStat.
	 *
	 * @param identifier 统计节点标识
	 * @param childNodeSize 子节点个数
	 */
	public ExecutorStat(String identifier, int childNodeSize) {
		if(null == identifier) {
			throw new NullPointerException("identifier can not be null.");
		}

		this.identifier = identifier;
		this.childNodeSize = childNodeSize;
		this.changeChildSize(this.childNodeSize);

		reset();
	}

	//修改子节点数目
	private void changeChildSize(int size) {
		if(size > 0) {
			//如果数目未变则不重新设置
			if(null != childNodeList && size == childNodeList.size()) {
				return;
			}

			childNodeList = new ArrayList<ExecutorStat>(size);
			for(int i = 0; i < size; i++) {
				//字节点命名规则为 父节点‘identifier_子节点位置’,如 'identifier_0'
				childNodeList.add(new ExecutorStat(this.identifier + "_" + size));
			}
		} else {
			childNodeList = null;
		}
	}

	/**
	 * 添加子节点统计信息
	 * @param index 子节点位置，从0开始，该值不能大于等于初始设置的childNodeSize
	 * @param isSuccess	是否执行成功
	 * @param executorTime	执行时间
	 * @return
	 */
	public void addChildStat(int index, boolean isSuccess, long executorTime) {
		if(index < 0) {
			return;
		}

		if(index >= this.childNodeSize) {
			throw new IndexOutOfBoundsException("child size is " + childNodeSize + ", input index too large: " + index);
		}

		this.childNodeList.get(index).addStat(isSuccess, executorTime);
		hasStatInfo = true;
	}

	/**
	 * 添加一个统计数据
	 * @param isSuccess	是否执行成功
	 * @param executeTime	执行时间，单位为纳秒
	 * @return
	 */
	public synchronized void addStat(boolean isSuccess, long executeNanoTime) {
		//执行次数，总时间及平均执行时间
		executorTimes++;
		totalExecutorTime += executeNanoTime;
			
		if(isSuccess) {
			//成功
			successTimes++;
			totalSuccessTime += executeNanoTime;
				

			//计算最大时间
			maxSuccessTime = Math.max(executeNanoTime, maxSuccessTime);
			//最小时间
			minSuccessTime = Math.min(executeNanoTime, minSuccessTime);
		} else {
			//最大时间
			maxFailTime = Math.max(executeNanoTime, maxFailTime);
			//最小时间
			minFailTime = Math.min(executeNanoTime, minFailTime);
		}

		hasStatInfo = true;
	}

	/**
	 * 添加一个统计数据
	 * @param isSuccess	是否执行成功
	 * @param num	叠加值
	 * @return
	 */
	public synchronized void addStatistic(boolean isSuccess, long num) {
		addStat(isSuccess, num * 1000000);
	}
	/**
	 * 获得统计信息的文字格式
	 * @param
	 * @return
	 */
	public String toStatString(boolean reset) {
		return toStatString(true, reset);
	}

	/**
	 * 获得统计信息的文字格式
	 * @param
	 * @return
	 */
	public String toStatString(boolean emptyJudge, boolean reset) {
		if(emptyJudge) {
			if(this.hasStatInfo()) {
				outputTime = System.currentTimeMillis();
			} else {
				return null;
			}
		}
		
		ExecutorStat imgStat = this.getDataImage();
		//重置数据
		if(reset) {
			reset();
		}
		
		StringBuilder statBuilder = new StringBuilder(256 * (1 + childNodeSize));
		//输出标记及开始结束时间
		statBuilder.append(identifier).append('\t');
			
		//输出总信息
		statBuilder.append(SEC_SPLIT_CHAR);
		statBuilder.append(STAT_EXEC_TIMES).append(imgStat.executorTimes).append(COLUMN_SPLIT_CHAR);//总次数
		statBuilder.append(STAT_EXEC_AVG).append(nanoToMilli(imgStat.getAvgExecutorTime())).append(COLUMN_SPLIT_CHAR);//总平均时间
		statBuilder.append(STAT_EXEC_TOTAL_TIME).append(nanoToMilli(imgStat.getTotalExecutorTime())).append(COLUMN_SPLIT_CHAR);//总时间
	
		//输出成功信息
		//statBuilder.append(COLUMN_SPLIT_CHAR);
		statBuilder.append(SEC_SPLIT_CHAR);
		statBuilder.append(STAT_SUCC_TIMES).append(imgStat.successTimes).append(COLUMN_SPLIT_CHAR);//成功次数
		statBuilder.append(STAT_SUCC_AVG).append(nanoToMilli(imgStat.getAvgSuccessTime())).append(COLUMN_SPLIT_CHAR);//成功平均时间
		statBuilder.append(STAT_SUCC_MAX).append(nanoToMilli(imgStat.maxSuccessTime)).append(COLUMN_SPLIT_CHAR);	//最大成功执行时间
		statBuilder.append(STAT_SUCC_MIN).append(nanoToMilli(imgStat.minSuccessTime)).append(COLUMN_SPLIT_CHAR);	//最小成功执行时间
		statBuilder.append(STAT_SUCC_EXEC_TOTAL_TIME).append(nanoToMilli(imgStat.getTotalSuccessTime())).append(COLUMN_SPLIT_CHAR);//总时间
	
		//输出失败信息
		//statBuilder.append(COLUMN_SPLIT_CHAR);
		statBuilder.append(SEC_SPLIT_CHAR);
		statBuilder.append(STAT_FAIL_TIMES).append(imgStat.getFailTimes()).append(COLUMN_SPLIT_CHAR);//失败次数
		statBuilder.append(STAT_FAIL_AVG).append(nanoToMilli(imgStat.getAvgFailTime())).append(COLUMN_SPLIT_CHAR);//失败平均时间
		statBuilder.append(STAT_FAIL_MAX).append(nanoToMilli(imgStat.maxFailTime)).append(COLUMN_SPLIT_CHAR);	//最大失败执行时间
		statBuilder.append(STAT_FAIL_MIN).append(nanoToMilli(imgStat.minFailTime)).append(COLUMN_SPLIT_CHAR);	//最小失败执行时间
		statBuilder.append(STAT_FAIL_EXEC_TOTAL_TIME).append(nanoToMilli(imgStat.getTotalFailTime())).append(COLUMN_SPLIT_CHAR);//总时间
			
		//输出子节点
		if(this.childNodeSize > 0) {
			statBuilder.append(CHILD_START);
			for(int i = 0; i < this.childNodeSize; i++) {
				String statInfo = this.childNodeList.get(i).toStatString(emptyJudge, reset);
				if(null != statInfo) {
					statBuilder.append(statInfo);
				}
			}
	
			statBuilder.append(CHILD_END);
		}
			
		return statBuilder.toString();
	}

	
	
	/**
	 * 从纳秒转为毫秒
	 * @param 
	 * @return
	 */
	private String nanoToMilli(long nanoTime) {
		return getFormatNumber(nanoTime / 1000000.0);
	}
	
	/**
	 * 从纳秒转为毫秒
	 * @param 
	 * @return
	 */
	private String nanoToMilli(double nanoTime) {
		return getFormatNumber(nanoTime / 1000000.0);
	}
	
	private String getFormatNumber(double number) {
		return DECIMAL_FORMAT.format(number);
	}

	/**
	 * 重置数据
	 * @param
	 * @return
	 */
	public synchronized void reset() {
		startTime = System.currentTimeMillis();	//统计开始
		totalExecutorTime = 0;	//总执行时间，以纳秒为单位
		executorTimes = 0;		//执行次数

		successTimes = 0;		//执行成功次数
		totalSuccessTime = 0;	//总成功时间，以纳秒为单位
		maxSuccessTime = 0;		//执行成功的最大时间
		minSuccessTime = Long.MAX_VALUE;	//执行成功的最小时间

		maxFailTime = 0;		//执行失败的最大时间
		minFailTime = Long.MAX_VALUE;		//执行失败的最小时间

		hasStatInfo = false;
	}
	
	/**
	 * 获取数据镜像
	 * @param 
	 * @return
	 */
	private synchronized ExecutorStat getDataImage() {
		ExecutorStat stat = null;
		try {
			stat = (ExecutorStat)this.clone();
			adaptMinVal(stat);
		} catch (CloneNotSupportedException e) {
			// never here
		}
		
		return stat;
	}
	
	public static ExecutorStat adaptMinVal(ExecutorStat stat) {
		if (stat.getMinFailTime() == Long.MAX_VALUE) {
			stat.setMinFailTime(0);
		}
		
		if (stat.getMinSuccessTime() == Long.MAX_VALUE) {
			stat.setMinSuccessTime(0);
		}
		
		return stat;
	}
	
	/**
	 * 设置开始时间（在分析统计日志等情景使用）
	 * @param
	 * @return
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getStartTime() {
		return this.startTime;
	}

	/**
	 * 设置输出时间（在分析统计日志等情景使用）
	 * @param
	 * @return
	 */
	public void setOutputTime(long outputTime) {
		this.outputTime = outputTime;
	}

	public long getOutputTime() {
		return this.outputTime;
	}

	/**
	 * 获取当前统计项的标识
	 * @return  the identifier
	 * @since   Ver 1.0
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * 设置当前统计项的标识
	 * @param   identifier
	 * @since   Ver 1.0
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	/**
	 * 获取平均执行时间，以纳秒为单位
	 * @return  the avgExecutorTime
	 * @since   Ver 1.0
	 */
	public double getAvgExecutorTime() {
		return (executorTimes == 0) ? 0 : totalExecutorTime * 1.0 / executorTimes;
	}

	/**
	 * 获取执行次数
	 * @return  the executorTimes
	 * @since   Ver 1.0
	 */
	public long getExecutorTimes() {
		return executorTimes;
	}
	/**
	 * 设置执行次数
	 * @param   executorTimes
	 * @since   Ver 1.0
	 */
	public void setExecutorTimes(long executorTimes) {
		this.executorTimes = executorTimes;
	}
	/**
	 * 获取执行成功次数
	 * @return  the successTimes
	 * @since   Ver 1.0
	 */
	public long getSuccessTimes() {
		return successTimes;
	}
	/**
	 * 设置执行成功次数
	 * @param   successTimes
	 * @since   Ver 1.0
	 */
	public void setSuccessTimes(long successTimes) {
		this.successTimes = successTimes;
	}
	/**
	 * 获取执行成功的平均时间，以纳秒为单位
	 * @return  the avgSuccessTime
	 * @since   Ver 1.0
	 */
	public double getAvgSuccessTime() {
		return (successTimes == 0) ? 0 : totalSuccessTime * 1.0 / successTimes;
	}

	/**
	 * 获取执行成功的最大时间，以纳秒为单位
	 * @return  the maxSuccessTime
	 * @since   Ver 1.0
	 */
	public long getMaxSuccessTime() {
		return maxSuccessTime;
	}
	/**
	 * 设置执行成功的最大时间，以纳秒为单位
	 * @param   maxSuccessTime
	 * @since   Ver 1.0
	 */
	public void setMaxSuccessTime(long maxSuccessTime) {
		this.maxSuccessTime = maxSuccessTime;
	}
	/**
	 * 获取执行成功的最小时间，以纳秒为单位
	 * @return  the minSuccessTime
	 * @since   Ver 1.0
	 */
	public long getMinSuccessTime() {
		return minSuccessTime;
	}
	/**
	 * 设置执行成功的最小时间，以纳秒为单位
	 * @param   minSuccessTime
	 * @since   Ver 1.0
	 */
	public void setMinSuccessTime(long minSuccessTime) {
		this.minSuccessTime = minSuccessTime;
	}
	/**
	 * 获取执行失败次数
	 * @return  the failTimes
	 * @since   Ver 1.0
	 */
	public long getFailTimes() {
		return executorTimes - successTimes;
	}

	/**
	 * 获取执行失败的平均时间，以纳秒为单位
	 * @return  the avgFailTime
	 * @since   Ver 1.0
	 */
	public double getAvgFailTime() {
		long failTimes = getFailTimes();
		return (failTimes == 0) ? 0 : getTotalFailTime() * 1.0 / failTimes;
	}

	/**
	 * 获取执行失败的最大时间，以纳秒为单位
	 * @return  the maxFailTime
	 * @since   Ver 1.0
	 */
	public long getMaxFailTime() {
		return maxFailTime;
	}
	/**
	 * 设置执行失败的最大时间，以纳秒为单位
	 * @param   maxFailTime
	 * @since   Ver 1.0
	 */
	public void setMaxFailTime(long maxFailTime) {
		this.maxFailTime = maxFailTime;
	}
	/**
	 * 获取执行失败的最小时间，以纳秒为单位
	 * @return  the minFailTime
	 * @since   Ver 1.0
	 */
	public long getMinFailTime() {
		return minFailTime;
	}
	/**
	 * 设置执行失败的最小时间，以纳秒为单位
	 * @param   minFailTime
	 * @since   Ver 1.0
	 */
	public void setMinFailTime(long minFailTime) {
		this.minFailTime = minFailTime;
	}
	/**
	 * 获取总执行时间，以纳秒为单位
	 * @return  the totalExecutorTime
	 * @since   Ver 1.0
	 */
	public long getTotalExecutorTime() {
		return totalExecutorTime;
	}

	/**
	 * 设置总执行时间，以纳秒为单位
	 * @param   totalExecutorTime
	 * @since   Ver 1.0
	 */
	public void setTotalExecutorTime(long totalExecutorTime) {
		this.totalExecutorTime = totalExecutorTime;
	}

	/**
	 * 获取总成功时间，以纳秒为单位
	 * @return  the totalSuccessTime
	 * @since   Ver 1.0
	 */
	public long getTotalSuccessTime() {
		return totalSuccessTime;
	}

	/**
	 * 设置总成功时间，以纳秒为单位
	 * @param   totalSuccessTime
	 * @since   Ver 1.0
	 */
	public void setTotalSuccessTime(long totalSuccessTime) {
		this.totalSuccessTime = totalSuccessTime;
	}

	/**
	 * 获取总失败时间，以纳秒为单位
	 * @return  the totalFailTime
	 * @since   Ver 1.0
	 */
	public long getTotalFailTime() {
		return totalExecutorTime - totalSuccessTime;
	}

	/**
	 * 获取是否有加入的统计信息
	 * @return  the hasStat
	 * @since   Ver 1.0
	 */
	public boolean hasStatInfo() {
		return hasStatInfo;
	}

	public static void main(String[] args) {
		ExecutorStat es = new ExecutorStat("my test",1);
		es.addStat(true, 1000);
		es.addStat(true, 1023);
		es.addStat(false, 10110);
		es.addStat(true, 1020);
//		es.addStat(false, 1111);
		es.addChildStat(0, true, 1000);
		es.addChildStat(0, true, 1030);
		es.addChildStat(0, false, 1000);
		System.out.println(es.toStatString(false));
		
		ExecutorStat es1 = es.getDataImage();
		System.out.println(es1.maxFailTime + "\t" + es1.maxSuccessTime);
	}
}

