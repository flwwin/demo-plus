package com.leven.demoplus.inner.sentry.monitor;

import org.apache.commons.lang3.StringUtils;

public class MonitorSetTargetUtils {

	private static final String SET_BJ1_NAME="set_bj1";
	private static final String SET_BJ2_NAME="set_bj1";
	private static final String SPLIT = "-";

	public static String generateTarget(String prefix, String identify) {
		//String setName = SetHelper.getCurrentSetName();
		String setName="set";
		StringBuilder targetUrl = new StringBuilder();
		if (StringUtils.isEmpty(setName) || SET_BJ1_NAME.equalsIgnoreCase(setName)
				|| SET_BJ2_NAME.equalsIgnoreCase(setName)) {
			return targetUrl.append(prefix).append(identify).toString();
		}
		return targetUrl.append(prefix).append(identify).append(SPLIT).append(setName).toString();
	}
}
