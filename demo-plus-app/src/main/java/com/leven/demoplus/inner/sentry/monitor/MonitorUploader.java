package com.leven.demoplus.inner.sentry.monitor;


import com.alibaba.fastjson.JSON;
import com.leven.demoplus.inner.sentry.http.HttpPoolClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.util.List;

public class MonitorUploader {

	private static final Logger LOGGER = LoggerFactory.getLogger(MonitorUploader.class);

	private static HttpPoolClient httpClient = new HttpPoolClient(3000, 3000, 3000, 5, 5);

	public static <T> void upload(String monitorUrl, List<T> itemList) {
		LOGGER.debug("MonitorUploader upload, url ={}, size = {}", monitorUrl, itemList.size());
		String data = null;
		try {
			if (StringUtils.isEmpty(monitorUrl)) {
				LOGGER.error("monitor url is empty,please check it!");
				return;
			}
			if (CollectionUtils.isEmpty(itemList)) {
				LOGGER.error("monitor data is empty,need not to upload");
				return;
			}
			DataEvent dataEvent = new DataEvent();
			// 选择自定义采集方式为push
			dataEvent.addHeader("logType", "push");
			// 添加上报数据体
			for (T item : itemList) {
				dataEvent.addBody(item);
			}

			// 上报数据
			data = JSON.toJSONString(dataEvent);
			HttpPoolClient.DefaultHttpResponse response = httpClient.doHttpPost(monitorUrl, null, data, false);
			LOGGER.info("exectorstat upload to monitor,code:{},itemSize:{}", response.getStatusCode(), itemList.size());
		} catch (SocketTimeoutException et) {
			LOGGER.error("exectorstat upload to snake monitor time out|{}", data);
		} catch (Exception e) {
			LOGGER.error("exectorstat upload to snake monitor error|{}", data, e);
		}
	}
}
