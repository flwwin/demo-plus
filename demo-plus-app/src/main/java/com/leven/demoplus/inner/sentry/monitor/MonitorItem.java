package com.leven.demoplus.inner.sentry.monitor;


import com.leven.demoplus.inner.common.IpUtils;

import java.io.Serializable;

public abstract class MonitorItem implements Serializable {

	private static final long serialVersionUID = 9020572528898929856L;

	/**
	 * 主机字段用于识别不同主机上报同一个
	 */
	private static final String host = IpUtils.getLocalIPAddress();

	/*** 监控目标 */
	private String target;

	/**
	 * 上报的时间点
	 */
	private long recordTime;


	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public long getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(long recordTime) {
		this.recordTime = recordTime;
	}

	public String getHost() {
		return host;
	}

	@Override
	public String toString() {
		return "MonitorItem [target=" + target + ", recordTime=" + recordTime + ", getHost()=" + getHost() + "]";
	}

}
