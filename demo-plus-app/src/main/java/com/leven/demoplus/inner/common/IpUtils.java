package com.leven.demoplus.inner.common;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtils {

	private static final long LOCALHOST = 0x7f000001; // 127.0.0.1

	private static final String IP_SPLIT = ",";

	/**
	 * 
	 * 校验IP是否被接受
	 * 
	 * @param ip
	 *            待校验IP
	 * @param acceptedIps
	 *            可接受IP
	 * @return
	 */
	public static boolean checkIpAccept( String ip, String acceptedIps ) {

		// 可接受IP为空,默认为接受所有
		if( StringUtils.isEmpty( acceptedIps ) ) {
			return true;
		}

		long ipInt = ip2long( ip );

		// localhost默认接收
		if( LOCALHOST == ipInt ) {
			return true;
		}

		String[] ipArr = acceptedIps.split(IP_SPLIT);
		for( String acceptedIp : ipArr ) {
			if( ipInt == ip2long( acceptedIp ) ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * ip地址转Long值
	 */
	public static long ip2long( String ip ) {
		InetAddress add;
		try {
			add = InetAddress.getByName( ip );
		} catch( UnknownHostException e ) {
			throw new RuntimeException(String.format("Unknown Host [%s]", ip), e);
		}
		int l = add.hashCode();
		if( l < 0 ) {
			return ( long )l & ( 0xFFFFFFFFL );
		}
		return l;
	}

	/**
	 * 获取远程访问IP
	 */
	public static String getRemoteIp( HttpServletRequest request ) {
		String ip = request.getHeader( "x-forwarded-for" );
		if( ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase( ip ) ) {
			ip = request.getHeader( "Proxy-Client-IP" );
		}
		if( ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase( ip ) ) {
			ip = request.getHeader( "WL-Proxy-Client-IP" );
		}

		if (StringUtils.isEmpty(ip)) {
			return request.getRemoteAddr();
		}
		return ip.split(",")[0].trim();
	}
	
	public static String getLocalIPAddress() {
		String host = null;
		try {
			host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
		}

		if (NetUtils.isInvalidLocalHost(host)) {
			return "127.0.0.1";
		}

		return host;
	}
}
