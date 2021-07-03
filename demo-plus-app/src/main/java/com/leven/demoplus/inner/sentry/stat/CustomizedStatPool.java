
package com.leven.demoplus.inner.sentry.stat;

import com.leven.demoplus.inner.sentry.monitor.MonitorUploader;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomizedStatPool extends DefaultStatPool implements Runnable {
	public static final String STAT_POOL_START = "=====================stat output at ";
	public static final String STAT_POOL_END = "=====================";

	private static final Logger logger = LoggerFactory.getLogger(CustomizedStatPool.class);	//日志记录对象

	private static String host = "";
	static {
		try {
			host = getLocalHostLANAddress().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 存放已有实体的map
	 */
	private ConcurrentHashMap<String, CustomizedStat> customizedStatMap = new ConcurrentHashMap<String, CustomizedStat>();

	private static final CustomizedStatPool instance = new CustomizedStatPool();

	public static CustomizedStatPool getInstance() {
		return instance;
	}

	public CustomizedStatPool() {
		super();
	}
	
	/**
	 * 根据标识符获取执行统计实体
	 * @param 
	 * @return
	 */
	public CustomizedStat getCustomizedStat(String identifier) {
		CustomizedStat existStat = customizedStatMap.get(identifier);
		if(null == existStat) {
			existStat = new CustomizedStat(identifier);
			CustomizedStat oldExecStat = customizedStatMap.putIfAbsent(identifier, existStat);
			if(null != oldExecStat) {
				existStat = oldExecStat;
			}
		}
		return existStat;
	}
	
	/**
	 * 将统计信息输出，如果要改变其输出方式，可以重写此方法
	 * @param execStat
	 * @return
	 */
	public void output() {
		logger.debug("start customized stat output()" );
		if (null == logger || !logger.isInfoEnabled()
				|| customizedStatMap.isEmpty()) {
			logger.debug("output() isInfoEnabled{},customizedStatMap size{}", logger.isInfoEnabled(),customizedStatMap.isEmpty());
			return;
		}

		// 当前时间
		String now = null;
		try {
			now =new Timestamp(System.currentTimeMillis()).toString();
			//TODO
			//now = DateUtils.date2Str(new Date(System.currentTimeMillis()), "yyyy-MM-dd HH:mm:ss");
		} catch (Exception ex) {
			now = new Timestamp(System.currentTimeMillis()).toString();
		}

		try {
			StringBuilder statBuilder = new StringBuilder();

			// header
			statBuilder.append(STAT_POOL_START).append(now).append(STAT_POOL_END);

			// body
			boolean hasData = false;

			// monitor Data
			List<HashMap> monitorList = new ArrayList<HashMap>();
			for (CustomizedStat stat : customizedStatMap.values()) {
				// 加入监控数据上报
				if (getMonitorSwith(stat.getIdentifier())) {
					HashMap monitorItem = CustomizedMonitorItem.convert2Item(stat, host);
					monitorList.add(monitorItem);
				}

				String statInfo = stat.toString();
				if (null != statInfo) {
					hasData = true;
					statBuilder.append("\r\n").append(now).append(" ").append(statInfo);
				}
			}

			if (monitorList.size() > 0) {
				logger.info(monitorList.toString());
			}

			if (!CollectionUtils.isEmpty(monitorList)) {
				MonitorUploader.upload(this.getMonitorUrl(), monitorList);
			}
		} catch (Throwable e) {
			logger.error("upload data to snake monitor exception {}", e);
		}
	}


	/**
	 * 相当准确的IP，即优先拿site-local地址
	 *
	 * @return
	 * @throws UnknownHostException
	 */
	private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
		try {
			InetAddress candidateAddress = null;
			// 遍历所有的网络接口
			for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				// 在所有的接口下再遍历IP
				for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
						if (inetAddr.isSiteLocalAddress()) {
							// 如果是site-local地址，就是它了
							return inetAddr;
						} else if (candidateAddress == null) {
							// site-local类型的地址未被发现，先记录候选地址
							candidateAddress = inetAddr;
						}
					}
				}
			}
			if (candidateAddress != null) {
				return candidateAddress;
			}
			// 如果没有发现 non-loopback地址.只能用最次选的方案
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress;
		} catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}

	public static void main(String[] args) {
		CustomizedStatPool pool = new CustomizedStatPool();
		pool.setMonitorSwitch(1);
		pool.setMonitorUrl("http://172.17.161.62:20006");
		//pool.setLogger(LoggerFactory.getLogger("test"));

		CustomizedStat es0 = pool.getCustomizedStat("test0");
		CustomizedStatIndicator indicator_0 = es0.getStatIndicator("indicator_0");
		indicator_0.addStat(1000);
		indicator_0.addStat(1023);
		indicator_0.addStat(1010);
		indicator_0.addStat(1020);
		indicator_0.addStat(1111);

		CustomizedStatIndicator indicator_1 = es0.getStatIndicator("indicator_1");
		indicator_1.addStat(1000);
		indicator_1.addStat(1023);
		indicator_1.addStat(1010);
		indicator_1.addStat(1020);
		indicator_1.addStat(1111);
		
		pool.output();
	}

}



