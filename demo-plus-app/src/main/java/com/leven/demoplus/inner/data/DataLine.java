
package com.leven.demoplus.inner.data;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.Map;

public class DataLine {
	private String originalLine;	// 原始的日志行内人
	
	private String serverTime;
	private String ip;
	private String imei;
	private String ssoId;
	private String model;
	private String osVersion;
	private String romVersion;
	private String androidVersion;
	private String sdkVersion;
	private String channel;
	private String systemId;
	private String category;
	private String appVersion;
	private String networkId;
	private String clientTime;
	private String seqId;
	private String sessionId;
	private String dataType;
	private String eventValue;
	private String count;
	private Map<String, String> eventKeyMap;
	private List<String> eventKeyList;
	
	private int retryCnt = 0;	// 重试次数
	
	/**
	 * 获取指定位置上的eventKey
	 * @param 
	 * @return
	 */
	public String getEventKey(int index) {
		if (index >= eventKeyList.size()) {
			return "";
		}
		
		return eventKeyList.get(index);
	}
	
	public String getEventKey(String key) {
		String val = eventKeyMap.get(key);
		
		return null == val ? "" : val;
	}
	
	public int getEventKeyInt(String key, int defaultVal) {
		String val = eventKeyMap.get(key);
		if (StringUtils.isEmpty(val)) {
			return defaultVal;
		}
		
		return NumberUtils.toInt(val, defaultVal);
	}
	
	public long getEventKeyLong(String key, long defaultVal) {
		String val = eventKeyMap.get(key);
		if (StringUtils.isEmpty(val)) {
			return defaultVal;
		}
		
		return NumberUtils.toLong(val, defaultVal);
	}
	
	/**
	 * 获取originalLine
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getOriginalLine() {
		return originalLine;
	}
	/**
	 * 设置originalLine  
	 * @param   originalLine	sth.   
	 * @since   Ver 1.0
	 */
	public void setOriginalLine(String originalLine) {
		this.originalLine = originalLine;
	}
	/**
	 * 获取serverTime
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getServerTime() {
		return serverTime;
	}
	/**
	 * 设置serverTime  
	 * @param   serverTime	sth.   
	 * @since   Ver 1.0
	 */
	public void setServerTime(String serverTime) {
		this.serverTime = serverTime;
	}
	/**
	 * 获取ip
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getIp() {
		return ip;
	}
	/**
	 * 设置ip  
	 * @param   ip	sth.   
	 * @since   Ver 1.0
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * 获取imei
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getImei() {
		return imei;
	}
	/**
	 * 设置imei  
	 * @param   imei	sth.   
	 * @since   Ver 1.0
	 */
	public void setImei(String imei) {
		this.imei = imei;
	}
	/**
	 * 获取ssoId
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getSsoId() {
		return ssoId;
	}
	/**
	 * 设置ssoId  
	 * @param   ssoId	sth.   
	 * @since   Ver 1.0
	 */
	public void setSsoId(String ssoId) {
		this.ssoId = ssoId;
	}
	/**
	 * 获取model
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getModel() {
		return model;
	}
	/**
	 * 设置model  
	 * @param   model	sth.   
	 * @since   Ver 1.0
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * 获取osVersion
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getOsVersion() {
		return osVersion;
	}
	/**
	 * 设置osVersion  
	 * @param   osVersion	sth.   
	 * @since   Ver 1.0
	 */
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	/**
	 * 获取romVersion
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getRomVersion() {
		return romVersion;
	}
	/**
	 * 设置romVersion  
	 * @param   romVersion	sth.   
	 * @since   Ver 1.0
	 */
	public void setRomVersion(String romVersion) {
		this.romVersion = romVersion;
	}
	/**
	 * 获取androidVersion
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getAndroidVersion() {
		return androidVersion;
	}
	/**
	 * 设置androidVersion  
	 * @param   androidVersion	sth.   
	 * @since   Ver 1.0
	 */
	public void setAndroidVersion(String androidVersion) {
		this.androidVersion = androidVersion;
	}
	/**
	 * 获取sdkVersion
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getSdkVersion() {
		return sdkVersion;
	}
	/**
	 * 设置sdkVersion  
	 * @param   sdkVersion	sth.   
	 * @since   Ver 1.0
	 */
	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}
	/**
	 * 获取channel
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getChannel() {
		return channel;
	}
	/**
	 * 设置channel  
	 * @param   channel	sth.   
	 * @since   Ver 1.0
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}
	/**
	 * 获取systemId
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getSystemId() {
		return systemId;
	}
	/**
	 * 设置systemId  
	 * @param   systemId	sth.   
	 * @since   Ver 1.0
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	/**
	 * 获取category
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * 设置category  
	 * @param   category	sth.   
	 * @since   Ver 1.0
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * 获取appVersion
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getAppVersion() {
		return appVersion;
	}
	/**
	 * 设置appVersion  
	 * @param   appVersion	sth.   
	 * @since   Ver 1.0
	 */
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	/**
	 * 获取networkId
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getNetworkId() {
		return networkId;
	}
	/**
	 * 设置networkId  
	 * @param   networkId	sth.   
	 * @since   Ver 1.0
	 */
	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	/**
	 * 获取clientTime
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getClientTime() {
		return clientTime;
	}
	/**
	 * 设置clientTime  
	 * @param   clientTime	sth.   
	 * @since   Ver 1.0
	 */
	public void setClientTime(String clientTime) {
		this.clientTime = clientTime;
	}
	/**
	 * 获取seqId
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getSeqId() {
		return seqId;
	}
	/**
	 * 设置seqId  
	 * @param   seqId	sth.   
	 * @since   Ver 1.0
	 */
	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}
	/**
	 * 获取sessionId
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getSessionId() {
		return sessionId;
	}
	/**
	 * 设置sessionId  
	 * @param   sessionId	sth.   
	 * @since   Ver 1.0
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	/**
	 * 获取dataType
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getDataType() {
		return dataType;
	}
	/**
	 * 设置dataType  
	 * @param   dataType	sth.   
	 * @since   Ver 1.0
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	/**
	 * 获取eventValue
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getEventValue() {
		return eventValue;
	}
	/**
	 * 设置eventValue  
	 * @param   eventValue	sth.   
	 * @since   Ver 1.0
	 */
	public void setEventValue(String eventValue) {
		this.eventValue = eventValue;
	}
	/**
	 * 获取count
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getCount() {
		return count;
	}
	/**
	 * 设置count  
	 * @param   count	sth.   
	 * @since   Ver 1.0
	 */
	public void setCount(String count) {
		this.count = count;
	}
	/**
	 * 获取eventKeyMap
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public Map<String, String> getEventKeyMap() {
		return eventKeyMap;
	}
	/**
	 * 设置eventKeyMap  
	 * @param   eventKeyMap	sth.   
	 * @since   Ver 1.0
	 */
	public void setEventKeyMap(Map<String, String> eventKeyMap) {
		this.eventKeyMap = eventKeyMap;
	}
	/**
	 * 获取eventKeyList
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public List<String> getEventKeyList() {
		return eventKeyList;
	}
	/**
	 * 设置eventKeyList  
	 * @param   eventKeyList	sth.   
	 * @since   Ver 1.0
	 */
	public void setEventKeyList(List<String> eventKeyList) {
		this.eventKeyList = eventKeyList;
	}

	/**
	 * 获取retryCnt
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public int getRetryCnt() {
		return retryCnt;
	}

	/**
	 * 设置retryCnt  
	 * @param   retryCnt	sth.   
	 * @since   Ver 1.0
	 */
	public void setRetryCnt(int retryCnt) {
		this.retryCnt = retryCnt;
	}
	/**
	 * UDID->GUID->ssoId
	 * @return
	 */
	public String getDataLineGuId() {
		return ssoId;
	}
	/**
	 * OAID->OUID->category
	 * @return
	 */
	public String getDataLineOuId() {
		return category;
	}
	/**
	 * VAID->DUID->seqId
	 * @return
	 */
	public String getDataLineDuId() {
		return seqId;
	}
	
}

