
package com.leven.demoplus.inner.sentry.stat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * ClassName: CustomizedStatIndicator <br />
 * Function: 自定义监控指标 <br>
 * @author
 * @version  
 * @since    Ver 1.1
 * @Date	 2019年5月23日  下午3:26:43
 */
public class CustomizedStatIndicator implements Serializable, Cloneable {
	private static final long serialVersionUID = 547964735433316074L;

	public static final String STAT_COUNT 	= "cnt";
	public static final String STAT_AVG 	= "avg";
	public static final String STAT_MAX 	= "max";
	public static final String STAT_MIN 	= "min";
	public static final String STAT_SUM 	= "sum";


	public static final char COLUMN_SPLIT_CHAR = ' ';
	public static final char SEC_SPLIT_CHAR = '|';
	public static final char IND_SPLIT_CHAR = ':';

	private static final Logger logger = LoggerFactory.getLogger(CustomizedStatPool.class);	//日志记录对象

	private String indicatorName;		//当前统计指标的标识

	private long startTime;			//统计开始时间
	private long outputTime;		//统计输出时间

	private long cnt;		//执行次数
	private long sum;		//总执

	private long max;
	private long min;

	private boolean hasStatInfo;		//是否加入了统计信息

	private static final NumberFormat DECIMAL_FORMAT = new DecimalFormat("#.###");

	/**
	 *
	 * Creates a new instance of ExecutorStat.
	 *
	 * @param identifier 统计节点标识
	 */
	public CustomizedStatIndicator(String indicatorName) {
		this.indicatorName = indicatorName;

		reset();
	}

	/**
	 * 添加一个统计数据
	 * @param isSuccess	是否执行成功
	 * @param executeTime	执行时间，单位为纳秒
	 * @return
	 */
	public synchronized void addStat(long num) {
		cnt++; 						//执行次数
		sum += num;					//汇总总数
		max = Math.max(num, max);	//计算最大时间
		min = Math.min(num, min);	//最小时间
		hasStatInfo = true;
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
		
		CustomizedStatIndicator imgStat = this.getDataImage();
		//重置数据
		if(reset) {
			reset();
		}
		
		StringBuilder statBuilder = new StringBuilder(256 );
		//输出标记及开始结束时间
		statBuilder.append(indicatorName).append('\t');
			
		//输出信息
		statBuilder.append(SEC_SPLIT_CHAR);
		statBuilder.append(STAT_COUNT).append(IND_SPLIT_CHAR).append(imgStat.cnt).append(COLUMN_SPLIT_CHAR);//总次数
		statBuilder.append(STAT_SUM).append(IND_SPLIT_CHAR).append(imgStat.sum).append(COLUMN_SPLIT_CHAR);
		statBuilder.append(STAT_AVG).append(IND_SPLIT_CHAR).append(imgStat.getAvg()).append(COLUMN_SPLIT_CHAR);
		statBuilder.append(STAT_MAX).append(IND_SPLIT_CHAR).append(imgStat.max).append(COLUMN_SPLIT_CHAR);
		statBuilder.append(STAT_MIN).append(IND_SPLIT_CHAR).append(imgStat.min).append(COLUMN_SPLIT_CHAR);

		return statBuilder.toString();
	}

	private String getFormatNumber(double number) {
		return DECIMAL_FORMAT.format(number);
	}

	public synchronized CustomizedStatIndicator getAndReset()
	{
		CustomizedStatIndicator stat = getDataImage();
		reset();
		return stat;
	}

	/**
	 * 重置数据
	 * @param
	 * @return
	 */
	public synchronized void reset() {
		startTime = System.currentTimeMillis();	//统计开始
		sum = 0;
		cnt = 0;

		max = 0;				//最大时间
		min = Long.MAX_VALUE;	//最小时间

		hasStatInfo = false;
	}
	
	/**
	 * 获取数据镜像
	 * @param 
	 * @return
	 */
	private synchronized CustomizedStatIndicator getDataImage() {
		CustomizedStatIndicator stat = null;
		try {
			stat = (CustomizedStatIndicator)this.clone();
			adaptMinVal(stat);
		} catch (CloneNotSupportedException e) {
			// never here
		}
		return stat;
	}
	
	public static CustomizedStatIndicator adaptMinVal(CustomizedStatIndicator stat) {
		if (stat.getMin() == Long.MAX_VALUE) {
			stat.setMin(0);
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
	 * 获取当前统计指标的标识
	 * @return  the identifier
	 * @since   Ver 1.0
	 */
	public String getIndicatorName() {
		return indicatorName;
	}
	/**
	 * 设置当前统计指标的标识
	 * @param   indicatorName
	 * @since   Ver 1.0
	 */
	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
	}
	/**
	 * 获取平均值
	 * @return  the avg
	 * @since   Ver 1.0
	 */
	public double getAvg() {
		return (cnt == 0) ? 0 : sum * 1.0 / cnt;
	}


	/**
	 * 获取是否有加入的统计信息
	 * @return  the hasStat
	 * @since   Ver 1.0
	 */
	public boolean hasStatInfo() {
		return hasStatInfo;
	}

	public long getCnt() {
		return cnt;
	}

	public void setCnt(long cnt) {
		this.cnt = cnt;
	}

	public long getSum() {
		return sum;
	}

	public void setSum(long sum) {
		this.sum = sum;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public static void main(String[] args) {
		CustomizedStatIndicator stat = new CustomizedStatIndicator("test");
		stat.addStat(1000);
		stat.addStat(1500);
		stat.addStat(2500);

		System.out.println(stat.toStatString(false));

		CustomizedStatIndicator stat2 = stat.getDataImage();
		System.out.println(stat2.min + "\t");
	}
}

