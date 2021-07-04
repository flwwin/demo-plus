
package com.leven.demoplus.inner.sentry.http;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.leven.demoplus.inner.sentry.stat.ExecutorStat;
import com.leven.demoplus.inner.sentry.stat.ExecutorStatPool;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HttpUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
	
	public static final String HEADER_X_REAL_IP = "X-Real-IP";
	public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
	
	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader(HEADER_X_FORWARDED_FOR); 
    	if (StringUtils.isNotEmpty(ip)) {
    		// 取X-Forwarded-For中的第一个逗号前的数据
    		int dotIndex = ip.indexOf(',');
    		if (dotIndex == -1) {
    			return ip;
    		}
    		
    		return ip.substring(0, dotIndex);
    	}
    	
    	ip = request.getHeader(HEADER_X_REAL_IP);
    	if (StringUtils.isNotEmpty(ip)) {
    		return ip;
    	}
    	
    	return request.getRemoteAddr();
    }

	/**
	 * 根据httpClient和request发起请求，获取返回数据
	 * @param httpClient
	 * @param request
	 * @param timeoutInMills 小于等于0表示按默认值
	 * @param tag 用于统计
	 * @return
	 */
	public static byte[] getDataFromHttpRequest(CloseableHttpClient httpClient, HttpUriRequest request, int timeoutInMills, String tag) {
		return getDataFromHttpRequest(httpClient, request, createHttpContext(httpClient, timeoutInMills), tag);
	}
	
	/**
	 * 根据httpClient和request发起请求，获取返回数据
	 * @param httpClient
	 * @param request
	 * @param context 
	 * @param tag 用于统计
	 * @return
	 */
	public static byte[] getDataFromHttpRequest(CloseableHttpClient httpClient, HttpUriRequest request, HttpContext context, String tag) {
		ExecutorStat stat = null;
		if (null != tag) {
			stat = ExecutorStatPool.getInstance().getExecutorStat(tag);
		}
		
		long start = System.nanoTime();
		CloseableHttpResponse resp = null;
		try {
			resp = httpClient.execute(request, (org.apache.http.protocol.HttpContext) context);
			
			byte[] data = getDataFromHttpResponse(resp, request.getURI().toString());
			// execute stat
			if (null != stat) {
				boolean isSucc = resp.getStatusLine().getStatusCode() < HttpStatus.SC_BAD_REQUEST;
				
				stat.addStat(isSucc, System.nanoTime() - start);
			}
			
			return data;
		} catch (IOException e) {
			// execute stat
			if (null != stat) {
				stat.addStat(false, System.nanoTime() - start);
			}
			LOGGER.error("request {} error: {}", request.getURI(), e.toString());
			
			return null;
		} finally {
			try {
				Closeables.close(resp, true);
			} catch (IOException e) {
				// ignore
			}
		}
	}
	
	public static byte[] getDataFromHttpResponse(HttpResponse resp, String url) throws IOException {
		try {
			int statusCode = resp.getStatusLine().getStatusCode();
			if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
				LOGGER.warn("request {} fail:{}", url, resp.getStatusLine());
				return null;
			} else if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			
			HttpEntity httpEntity = resp.getEntity();
			if (null == httpEntity || !httpEntity.isStreaming()) {
				return null;
			}
			
			return ByteStreams.toByteArray(httpEntity.getContent());
		} finally {
			EntityUtils.consume(resp.getEntity());
		}
	}
	
	public static HttpContext createHttpContext(CloseableHttpClient httpClient, int timeoutInMills) {
		if (timeoutInMills <= 0) {
			return null;
		}
		
		HttpClientContext context = HttpClientContext.create();
		
		RequestConfig defaultRequestConfig = null;
		if (httpClient instanceof Configurable) {
			Configurable configurableHttpClient = (Configurable)httpClient;
			defaultRequestConfig = configurableHttpClient.getConfig();
		}
		
		RequestConfig.Builder builder = null == defaultRequestConfig ? RequestConfig.custom() : RequestConfig.copy(defaultRequestConfig);
		builder.setSocketTimeout(timeoutInMills);
		
		context.setRequestConfig(builder.build());
		
		return context;
	}
}

