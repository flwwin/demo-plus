package com.leven.demoplus.inner.sentry.stat;


import com.leven.demoplus.inner.sentry.monitor.MonitorItem;
import org.apache.commons.lang3.time.DateUtils;

public class ExecutorMonitorItem extends MonitorItem {

	private static final long serialVersionUID = 6128232623117809767L;

	private long tttl; // 执行次数
	private double tavg; // 平均耗时
	private double ettl; // 执行总耗时

	private long sttl; // 执行成功次数
	private double savg; // 执行成功平均耗时
	private double smax; // 执行成功最大耗时
	private double smin; // 执行成功最小耗时
	private double settl; // 执行成功总耗时

	private long fttl; // 执行失败次数
	private double favg; // 执行失败平均耗时
	private double fmax; // 执行失败的最大时间
	private double fmin; // 执行失败的最小时间
	private double fettl; // 执行失败的总时间
	
	private double srate;	// 成功率

	public long getTttl() {
		return tttl;
	}

	public void setTttl(long tttl) {
		this.tttl = tttl;
	}

	public double getTavg() {
		return tavg;
	}

	public void setTavg(double tavg) {
		this.tavg = tavg;
	}

	public long getSttl() {
		return sttl;
	}

	public void setSttl(long sttl) {
		this.sttl = sttl;
	}

	public double getSavg() {
		return savg;
	}

	public void setSavg(double savg) {
		this.savg = savg;
	}

	public double getSmax() {
		return smax;
	}

	public void setSmax(double smax) {
		this.smax = smax;
	}

	public double getSmin() {
		return smin;
	}

	public void setSmin(double smin) {
		this.smin = smin;
	}

	public long getFttl() {
		return fttl;
	}

	public void setFttl(long fttl) {
		this.fttl = fttl;
	}

	public double getFavg() {
		return favg;
	}

	public void setFavg(double favg) {
		this.favg = favg;
	}

	public double getFmax() {
		return fmax;
	}

	public void setFmax(double fmax) {
		this.fmax = fmax;
	}

	public double getFmin() {
		return fmin;
	}

	public void setFmin(double fmin) {
		this.fmin = fmin;
	}

	public double getEttl() {
		return ettl;
	}

	public void setEttl(double ettl) {
		this.ettl = ettl;
	}

	public double getSettl() {
		return settl;
	}

	public void setSettl(double settl) {
		this.settl = settl;
	}

	public double getFettl() {
		return fettl;
	}

	public void setFettl(double fettl) {
		this.fettl = fettl;
	}

	/**
	 * 获取srate
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public double getSrate() {
		return srate;
	}

	/**
	 * 设置srate  
	 * @param   srate	sth.   
	 * @since   Ver 1.0
	 */
	public void setSrate(double srate) {
		this.srate = srate;
	}

	public static ExecutorMonitorItem convert2Item(ExecutorStat stat) {
		ExecutorMonitorItem monitorItem = new ExecutorMonitorItem();
		monitorItem.setTarget(stat.getIdentifier());
		monitorItem.setRecordTime(System.currentTimeMillis());

		monitorItem.setTttl(stat.getExecutorTimes());
		// TODO
		/*monitorItem.setTavg(DateUtils.nanoToMilli(stat.getAvgExecutorTime()));
		monitorItem.setEttl(DateUtils.nanoToMilli(stat.getTotalExecutorTime()));

		monitorItem.setSttl(stat.getSuccessTimes());
		monitorItem.setSavg(DateUtils.nanoToMilli(stat.getAvgSuccessTime()));
		monitorItem.setSmax(DateUtils.nanoToMilli(stat.getMaxSuccessTime()));
		monitorItem.setSmin(DateUtils.nanoToMilli(stat.getMinSuccessTime()));
		monitorItem.setSettl(DateUtils.nanoToMilli(stat.getTotalSuccessTime()));

		monitorItem.setFttl(stat.getFailTimes());
		monitorItem.setFavg(DateUtils.nanoToMilli(stat.getAvgFailTime()));
		monitorItem.setFmax(DateUtils.nanoToMilli(stat.getMaxFailTime()));
		monitorItem.setFmin(DateUtils.nanoToMilli(stat.getMinFailTime()));
		monitorItem.setFettl(DateUtils.nanoToMilli(stat.getTotalFailTime()));*/
		
		if (stat.getExecutorTimes() > 0) {
			monitorItem.setSrate(stat.getSuccessTimes() * 1.0 / stat.getExecutorTimes());
		}
		
		return monitorItem;
	}


	@Override
	public String toString() {
		return "LogMonitorItem [tttl=" + tttl + ", tavg=" + tavg + ", ettl=" + ettl
				+", sttl=" + sttl + ", savg=" + savg + ", smax=" + smax + ", smin=" + smin + ", settl=" + settl
				+ ", fttl=" + fttl + ", favg=" + favg + ", fmax=" + fmax + ", fmin=" + fmin + ", fettl=" + fettl
				+ "]";
	}

}
