package com.leven.demoplus.inner.sentry.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.SSLInitializationException;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class HttpPoolClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpPoolClient.class);

	private CloseableHttpClient httpClient;

	private int connectTimeout;

	private int socketTimeout;

	private int connectionRequestTimeout;

	public HttpPoolClient() {
		this(1000, 1000, 1000, 2, 20);
	}

	public HttpPoolClient(int socketTimeout, int connectTimeout, int connectionRequestTimeout, int defaultMaxPerRoute,
			int maxTotal) {

		this.socketTimeout = socketTimeout;
		this.connectTimeout = connectTimeout;
		this.connectionRequestTimeout = connectionRequestTimeout;

		SSLContext sslContext = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			TrustStrategy noopTrustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			};
			sslContext = SSLContexts.custom().useProtocol(SSLConnectionSocketFactory.TLS)
					.loadTrustMaterial(trustStore, noopTrustStrategy).build();
		} catch (NoSuchAlgorithmException ex) {
			throw new SSLInitializationException(ex.getMessage(), ex);
		} catch (KeyManagementException ex) {
			throw new SSLInitializationException(ex.getMessage(), ex);
		} catch (KeyStoreException ex) {
			throw new SSLInitializationException(ex.getMessage(), ex);
		}

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)).build();

		PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

		SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(this.socketTimeout).setTcpNoDelay(true)
				.setSoKeepAlive(true).build();
		connManager.setDefaultSocketConfig(socketConfig);

		ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();
		connManager.setDefaultConnectionConfig(connectionConfig);
		connManager.setMaxTotal(maxTotal);
		connManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
		connManager.setValidateAfterInactivity(1000);

		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(this.socketTimeout)
				.setConnectTimeout(this.connectTimeout).setConnectionRequestTimeout(this.connectionRequestTimeout)
				.setExpectContinueEnabled(true)
				// .setContentCompressionEnabled(false)
				.build();

		httpClient = HttpClients.custom().addInterceptorLast(new RequestAcceptEncoding())
				.addInterceptorLast(new ResponseContentEncoding()).setConnectionManager(connManager)
				.setDefaultRequestConfig(defaultRequestConfig).build();
	}

	public DefaultHttpResponse doHttpGet(String url, Map<String, String> header, String data) throws Exception {
		return this.execute(HttpGet.METHOD_NAME, url, header, data, false);
	}

	public DefaultHttpResponse doHttpPost(String url, Map<String, String> header, String data, boolean gzip)
			throws Exception {
		return this.execute(HttpPost.METHOD_NAME, url, header, data, gzip);
	}

	public DefaultHttpResponse execute(String methodName, String url, Map<String, String> header, String data,
			boolean gzip) throws Exception {
		CloseableHttpResponse response = null;
		try {
			HttpEntity dataEntity = null;
			if (data != null) {
				dataEntity = new ByteArrayEntity(data.getBytes(Consts.UTF_8));
				if (gzip) {
					dataEntity = new GzipCompressingEntity(dataEntity);
				}
			}
			response = doRequest(methodName, url, this.socketTimeout, this.connectTimeout,
					this.connectionRequestTimeout, header, dataEntity);
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			if (statusLine.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
				EntityUtils.consume(entity);
				LOGGER.error("=========>responseCode:{},responseBody:{}", statusLine.getStatusCode(),
						entity.getContent());
				throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
			} else {
				DefaultHttpResponse httpResponse = new DefaultHttpResponse();
				httpResponse.setStatusCode(response.getStatusLine().getStatusCode());
				httpResponse.setBody(null == entity ? null : EntityUtils.toString(entity, Consts.UTF_8));
				return httpResponse;
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (null != response) {
				response.close();
			}
		}
	}

	private CloseableHttpResponse doRequest(String methodName, String url, int socketTimeout, int connectTimeout,
			int connectionRequestTimeout, Map<String, String> header, HttpEntity data) throws Exception {
		if (StringUtils.isBlank(url)) {
			throw new HttpResponseException(HttpURLConnection.HTTP_BAD_REQUEST, "url is empty");
		}

		HttpRequestBase httpRequest = null;
		if (HttpGet.METHOD_NAME.equalsIgnoreCase(methodName)) {
			httpRequest = new HttpGet(url);
		} else if (HttpPost.METHOD_NAME.equalsIgnoreCase(methodName)) {
			httpRequest = new HttpPost(url);
		} else {
			throw new HttpResponseException(HttpURLConnection.HTTP_BAD_REQUEST,
					String.format("method[%s] not supported", methodName));
		}

		if (null != header && !header.isEmpty()) {
			for (Map.Entry<String, String> item : header.entrySet()) {
				httpRequest.setHeader(item.getKey(), item.getValue());
			}
		}
		if (null != data && httpRequest instanceof HttpPost) {
			((HttpPost) httpRequest).setEntity(data);
		}

		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout).build();
		httpRequest.setConfig(requestConfig);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpRequest);
		} catch (Exception ex) {
			httpRequest.abort();
			throw ex;
		}
		return response;
	}

	public void close() {
		if (null != httpClient) {
			try {
				httpClient.close();
			} catch (IOException ex) {
			}
		}
	}

	public DefaultHttpResponse download(String url, String destFileName) throws Exception {
		if (StringUtils.isBlank(url)) {
			throw new HttpResponseException(HttpURLConnection.HTTP_BAD_REQUEST, "url is empty");
		}

		File destFile = new File(destFileName);
		if (!destFile.getParentFile().exists()) {
			destFile.getParentFile().mkdirs();
		}

		HttpRequestBase httpRequest = new HttpGet(url);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout)
				.setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectTimeout).build();
		httpRequest.setConfig(requestConfig);

		CloseableHttpResponse response = null;
		BufferedInputStream buffInStream = null;
		BufferedOutputStream buffOutStream = null;
		try {
			response = httpClient.execute(httpRequest);
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			if (statusLine.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
				EntityUtils.consume(entity);
				throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
			} else {
				buffInStream = new BufferedInputStream(entity.getContent());
				buffOutStream = new BufferedOutputStream(new FileOutputStream(destFileName));
				int len = -1;
				byte[] tmp = new byte[1024];
				while ((len = buffInStream.read(tmp)) != -1) {
					buffOutStream.write(tmp, 0, len);
				}

				DefaultHttpResponse httpResponse = new DefaultHttpResponse();
				httpResponse.setStatusCode(response.getStatusLine().getStatusCode());
				return httpResponse;
			}
		} catch (Exception ex) {
			httpRequest.abort();
			throw ex;
		} finally {
			if (null != response) {
				response.close();
			}
			if (null != buffInStream) {
				buffInStream.close();
			}
			if (null != buffOutStream) {
				buffOutStream.close();
			}
		}
	}

	public static class BinaryHttpResponse {

		private int statusCode;

		private byte[] binaryBody;

		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public byte[] getBinaryBody() {
			return binaryBody;
		}

		public void setBinaryBody(byte[] binaryBody) {
			this.binaryBody = binaryBody;
		}

	}

	public static class DefaultHttpResponse {

		private int statusCode;

		private String body;

		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}
	}
}
