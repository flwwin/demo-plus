package com.leven.demoplus.inner.sentry.http;

import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.SSLInitializationException;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

public class HttpAsyncClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(HttpAsyncClient.class);

	private CloseableHttpAsyncClient httpClient;

	private int connectTimeout;

	private int socketTimeout;

	private int connectionRequestTimeout;

	private int ioThreadCount;

	public HttpAsyncClient() {
		this(1000, 1000, 1000, 2, 20, 0);
	}

	public HttpAsyncClient(int socketTimeout, int connectTimeout, int connectionRequestTimeout, int defaultMaxPerRoute,
			int maxTotal, int ioThreadCount) {

		this.socketTimeout = socketTimeout;
		this.connectTimeout = connectTimeout;
		this.connectionRequestTimeout = connectionRequestTimeout;
		this.ioThreadCount = ioThreadCount;
		if (this.ioThreadCount <= 0) {
			this.ioThreadCount = Runtime.getRuntime().availableProcessors();
		}

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

		Registry<SchemeIOSessionStrategy> sessionStrategy = RegistryBuilder.<SchemeIOSessionStrategy> create()
				.register("http", NoopIOSessionStrategy.INSTANCE)
				.register("https", new SSLIOSessionStrategy(sslContext, NoopHostnameVerifier.INSTANCE)).build();

		// 设置reactor参数
		IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
				.setIoThreadCount(this.ioThreadCount).setSoKeepAlive(true).build();
		ConnectingIOReactor ioReactor;
		try {
			ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
		} catch (IOReactorException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		// 设置connction参数
		PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor,
				sessionStrategy);
		ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE)
				.setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();
		connManager.setDefaultConnectionConfig(connectionConfig);
		connManager.setMaxTotal(maxTotal);
		connManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

		RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(this.socketTimeout)
				.setConnectTimeout(this.connectTimeout).setConnectionRequestTimeout(this.connectionRequestTimeout)
				.setExpectContinueEnabled(true)
				// .setContentCompressionEnabled(true)
				.build();

		List<String> supportEncodings = Arrays.asList("gzip", "deflate");
		httpClient = HttpAsyncClients.custom()
				.addInterceptorLast(new RequestAcceptEncoding(supportEncodings))
				// FIXME:这个拦截器有点问题,将解压的操作放到了后面Build
				// DefaultHttpResponse/BinaryHttpResponse 里面
				// .addInterceptorLast(new
				// HttpResponseContentEncoding(supportEncodings, true))
				.setConnectionManager(connManager)
				.setDefaultRequestConfig(defaultRequestConfig).build();
		httpClient.start();
	}

	public Future<HttpResponse> doHttpGet(String url, Map<String, String> header, String data,
			FutureCallback<HttpResponse> callback) throws Exception {
		HttpEntity dataEntity = null;
		if (data != null) {
			dataEntity = new ByteArrayEntity(data.getBytes(Consts.UTF_8));
		}
		return this.execute(HttpGet.METHOD_NAME, url, header, dataEntity, callback);
	}

	public Future<HttpResponse> doHttpPost(String url, Map<String, String> header, String data,
			FutureCallback<HttpResponse> callback)
			throws Exception {
		HttpEntity dataEntity = null;
		if (data != null) {
			dataEntity = new ByteArrayEntity(data.getBytes(Consts.UTF_8));
		}
		return this.execute(HttpPost.METHOD_NAME, url, header, dataEntity, callback);
	}

	public Future<HttpResponse> doHttpPostBinary(String url, Map<String, String> header, byte[] data,
												 FutureCallback<HttpResponse> callback)
			throws Exception {
		HttpEntity dataEntity = null;
		if (data != null) {
			dataEntity = new ByteArrayEntity(data);
		}
		return this.execute(HttpPost.METHOD_NAME, url, header, dataEntity, callback);
	}

	public Future<HttpResponse> execute(String methodName, String url, Map<String, String> header, HttpEntity dataEntity,
			FutureCallback<HttpResponse> callback)
			throws Exception {
		Future<HttpResponse> responseFuture = null;
		try {
//			HttpEntity dataEntity = null;
//			if (data != null) {
//				dataEntity = new ByteArrayEntity(data.getBytes(Consts.UTF_8));
//			}
			responseFuture = doRequest(methodName, url, this.socketTimeout, this.connectTimeout,
					this.connectionRequestTimeout, header, dataEntity, callback);
		} catch (Exception ex) {
			throw ex;
		}
		return responseFuture;
	}

	private Future<HttpResponse> doRequest(String methodName, String url, int socketTimeout, int connectTimeout,
			int connectionRequestTimeout, Map<String, String> header, HttpEntity data, FutureCallback<HttpResponse> callback)
			throws Exception {
		if (org.apache.commons.lang3.StringUtils.isBlank(url)) {
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
		Future<HttpResponse> responseFuture = null;
		try {
			responseFuture = httpClient.execute(httpRequest, callback);
		} catch (Exception ex) {
			httpRequest.abort();
			throw ex;
		}
		return responseFuture;
	}

	public static class BinaryHttpResponse {

		private int statusCode;

		private byte[] binaryBody;

		public static BinaryHttpResponse build(HttpResponse response) {
			if (response == null) {
				LOGGER.error("BinaryHttpResponse build but httpResponse is null");
				return null;
			}

			BinaryHttpResponse binaryResponse = null;
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			try {
				binaryResponse = new BinaryHttpResponse();
				binaryResponse.setStatusCode(statusLine.getStatusCode());
				if (statusLine.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES || entity == null) {
					EntityUtils.consume(entity);
					return binaryResponse;
				}

				byte[] body = null;
				final Header ceheader = entity.getContentEncoding();
				if (ceheader != null) {
					final HeaderElement[] codecs = ceheader.getElements();
					for (final HeaderElement codec : codecs) {
						final String codecname = codec.getName().toLowerCase(Locale.ROOT);
						if ("gzip".equals(codecname) || "x-gzip".equals(codecname)) {
							body = EntityUtils.toByteArray(new GzipDecompressingEntity(response.getEntity()));
						} else if ("deflate".equals(codecname)) {
							body = EntityUtils.toByteArray(new DeflateDecompressingEntity(response.getEntity()));
						} else {
							body = EntityUtils.toByteArray(entity);
						}
					}
				}
				binaryResponse.setBinaryBody(body);
			} catch (Exception e) {
				LOGGER.error("BinaryHttpResponse build error|{}", statusLine.getStatusCode());
			}
			return binaryResponse;
		}

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

		public static DefaultHttpResponse build(HttpResponse response) {
			if (response == null) {
				LOGGER.error("DefaultHttpResponse build but httpResponse is null");
				return null;
			}

			DefaultHttpResponse defaultResponse = null;
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			try {
				defaultResponse = new DefaultHttpResponse();
				defaultResponse.setStatusCode(statusLine.getStatusCode());
				if (statusLine.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES || entity == null) {
					EntityUtils.consume(entity);
					return defaultResponse;
				}

				String body = null;
				final Header ceheader = entity.getContentEncoding();
				if (ceheader != null) {
					final HeaderElement[] codecs = ceheader.getElements();
					for (final HeaderElement codec : codecs) {
						final String codecname = codec.getName().toLowerCase(Locale.ROOT);
						if ("gzip".equals(codecname) || "x-gzip".equals(codecname)) {
							body = EntityUtils.toString(new GzipDecompressingEntity(response.getEntity()),
									Consts.UTF_8);
						} else if ("deflate".equals(codecname)) {
							body = EntityUtils.toString(new DeflateDecompressingEntity(response.getEntity()),
									Consts.UTF_8);
						} else {
							body = EntityUtils.toString(entity, Consts.UTF_8);
						}
					}
				} else {
					body = EntityUtils.toString(entity, Consts.UTF_8);
				}
				defaultResponse.setBody(body);
			} catch (Exception e) {
				LOGGER.error("DefaultHttpResponse build error|{}", statusLine.getStatusCode());
			}
			return defaultResponse;
		}

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

	public static class DefaultHttpResponseBinary {

		private int statusCode;

		private byte[] body;

		public static DefaultHttpResponseBinary build(HttpResponse response) {
			if (response == null) {
				LOGGER.error("DefaultHttpResponseBinary build but httpResponse is null");
				return null;
			}

			DefaultHttpResponseBinary defaultResponse = null;
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			try {
				defaultResponse = new DefaultHttpResponseBinary();
				defaultResponse.setStatusCode(statusLine.getStatusCode());
				if (statusLine.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES || entity == null) {
					EntityUtils.consume(entity);
					return defaultResponse;
				}

				byte[] body = null;
				final Header ceheader = entity.getContentEncoding();
				if (ceheader != null) {
					final HeaderElement[] codecs = ceheader.getElements();
					for (final HeaderElement codec : codecs) {
						final String codecname = codec.getName().toLowerCase(Locale.ROOT);
						if ("gzip".equals(codecname) || "x-gzip".equals(codecname)) {
							body = EntityUtils.toByteArray(new GzipDecompressingEntity(response.getEntity()));
						} else if ("deflate".equals(codecname)) {
							body = EntityUtils.toByteArray(new DeflateDecompressingEntity(response.getEntity()));
						} else {
							body = EntityUtils.toByteArray(entity);
						}
					}
				} else {
					body = EntityUtils.toByteArray(entity);
				}
				defaultResponse.setBody(body);
			} catch (Exception e) {
				LOGGER.error("DefaultHttpResponse build error|{}", statusLine.getStatusCode());
			}
			return defaultResponse;
		}

		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public byte[] getBody() {
			return body;
		}

		public void setBody(byte[] body) {
			this.body = body;
		}
	}

	public void close() {
		if (null != httpClient) {
			try {
				httpClient.close();
			} catch (IOException ex) {
			}
		}
	}

}
