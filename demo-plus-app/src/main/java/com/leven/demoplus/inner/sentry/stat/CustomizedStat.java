package com.leven.demoplus.inner.sentry.stat;

import java.util.concurrent.ConcurrentHashMap;

public class CustomizedStat {

	private static final long serialVersionUID = 655116151189689537L;

	//监控项
	private String identifier = "";

	//监控项下指标列表
	private ConcurrentHashMap<String, CustomizedStatIndicator> indicatorMap = new ConcurrentHashMap<String, CustomizedStatIndicator>();

	public CustomizedStat(String identifier)
	{
		this.identifier = identifier;
	}

	public synchronized CustomizedStatIndicator getStatIndicator(String indicator) {
		CustomizedStatIndicator existStat = indicatorMap.get(indicator);
		if(null == existStat) {
			existStat = new CustomizedStatIndicator(indicator);
			CustomizedStatIndicator oldExecStat = indicatorMap.putIfAbsent(indicator, existStat);
			if(null != oldExecStat) {
				existStat = oldExecStat;
			}
		}
		return existStat;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public ConcurrentHashMap<String, CustomizedStatIndicator> getIndicatorMap() {
		return indicatorMap;
	}

	@Override
	public String toString() {
		String str = "CustomizedStat target: " + identifier + "\n";
		str += "[\n";
		for(CustomizedStatIndicator stat:indicatorMap.values())
		{
			str += stat.toStatString(false) + "\n";
		}
		str += "]\n";
		return str;
	}

	public static void main(String[] args) {
		CustomizedStat item = new CustomizedStat("mytest");


		CustomizedStatIndicator stat = item.getStatIndicator("indicator_0");

		stat.addStat(1000);
		stat.addStat(1500);
		stat.addStat(2500);

		System.out.println(item.toString());
	}

}
