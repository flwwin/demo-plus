package com.leven.demoplus.inner.sentry.http;

import org.apache.http.Consts;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.SSLInitializationException;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * http client 工具类
 *
 * @author lihongkun
 * @updator chenyue
 */
public class HttpClientUtil {

    public static Builder customBuilder() {
        return new Builder();
    }

    public static CloseableHttpClient getPoolingHttpClient(Integer httpsClientType, int socketTimeout, int connectTimeout, int connectionRequestTimeout, int maxPerRoute, int maxConnTotal) {
        return HttpClientUtil.customBuilder()
                .setPooling(true)
                .setHttpsClientType(httpsClientType)
                .setSocketTimeout(socketTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setMaxPerRoute(maxPerRoute)
                .setMaxConnTotal(maxConnTotal)
                .build();
    }

    /**
     * 信任所有Https请求(跳过SSL证书检测)
     *
     * @return
     */
    public static SSLConnectionSocketFactory trustAllHttps() {
        //跳过校验
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            SSLContext sslContext = SSLContexts.custom().useProtocol(SSLConnectionSocketFactory.TLS).loadTrustMaterial(trustStore, SKIP_CHECK_TRUST_STRATEGY).build();
            return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        } catch (NoSuchAlgorithmException ex) {
            throw new SSLInitializationException(ex.getMessage(), ex);
        } catch (KeyManagementException ex) {
            throw new SSLInitializationException(ex.getMessage(), ex);
        } catch (KeyStoreException ex) {
            throw new SSLInitializationException(ex.getMessage(), ex);
        }
    }

    public static class Builder {
        private HttpClientBuilder defaultHttpClientBuilder = HttpClientBuilder.create();
        /**
         * 是否使用连接池:默认使用
         */
        private boolean pooling = true;
        /**
         * 0默认1系统默认2跳过证书校验
         */
        private Integer httpsClientType = 0;
        private Integer socketTimeout = 3000;
        private Integer connectTimeout = 3000;
        private Integer connectionRequestTimeout = 3000;
        private Integer maxPerRoute = 8;
        private Integer maxConnTotal = 32;
        private PoolingHttpClientConnectionManager connManager;
        private RequestConfig requestConfig;
        private HttpClientBuilder httpClientBuilder;

        public Builder setPooling(boolean pooling) {
            this.pooling = pooling;
            return this;
        }

        public Builder setHttpsClientType(Integer httpsClientType) {
            this.httpsClientType = httpsClientType;
            return this;
        }

        public Builder setSocketTimeout(Integer socketTimeout) {
            this.socketTimeout = socketTimeout;
            return this;
        }

        public Builder setConnectTimeout(Integer connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setConnectionRequestTimeout(Integer connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
            return this;
        }

        public Builder setMaxPerRoute(Integer maxPerRoute) {
            this.maxPerRoute = maxPerRoute;
            return this;
        }

        public Builder setMaxConnTotal(Integer maxConnTotal) {
            this.maxConnTotal = maxConnTotal;
            return this;
        }

        public Builder disableCookieManagement() {
            defaultHttpClientBuilder.disableCookieManagement();
            return this;
        }

        public Builder disableContentCompression() {
            defaultHttpClientBuilder.disableContentCompression();
            return this;
        }

        public Builder disableAuthCaching() {
            defaultHttpClientBuilder.disableAuthCaching();
            return this;
        }

        public Builder setConnManager(PoolingHttpClientConnectionManager connManager) {
            this.connManager = connManager;
            return this;
        }

        public Builder setRequestConfig(RequestConfig requestConfig) {
            this.requestConfig = requestConfig;
            return this;
        }

        public Builder setHttpClientBuilder(HttpClientBuilder httpClientBuilder) {
            this.httpClientBuilder = httpClientBuilder;
            return this;
        }

        public CloseableHttpClient build() {
            if (httpClientBuilder == null) {
                httpClientBuilder = defaultHttpClientBuilder;
            }

            if (pooling) {
                if (connManager == null) {
                    connManager = getPoolingHttpClientConnectionManager(httpsClientType, socketTimeout, maxPerRoute, maxConnTotal);
                }
                httpClientBuilder.setConnectionManager(connManager);
            } else {
                httpClientBuilder.setMaxConnTotal(maxConnTotal)
                        .setMaxConnPerRoute(maxPerRoute)
                        .setSSLSocketFactory(getSSLConnectionSocketFactory(httpsClientType));
            }

            if (requestConfig == null) {
                requestConfig = getDefaultRequestConfig(socketTimeout, connectTimeout, connectionRequestTimeout);
            }

            return httpClientBuilder.setDefaultRequestConfig(requestConfig).build();
        }

        private static RequestConfig getDefaultRequestConfig(int socketTimeout, int connectTimeout, int connectionRequestTimeout) {
            return RequestConfig.custom()
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setExpectContinueEnabled(true)
                    .build();
        }

        private static PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager(Integer httpsClientType, int socketTimeout, int maxPerRoute, int maxConnTotal) {
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", getSSLConnectionSocketFactory(httpsClientType))
                    .build();

            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            SocketConfig socketConfig = SocketConfig.custom()
                    .setSoTimeout(socketTimeout).setTcpNoDelay(true).setSoKeepAlive(true).build();
            connManager.setDefaultSocketConfig(socketConfig);

            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE)
                    .setCharset(Consts.UTF_8)
                    .build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(maxConnTotal);
            connManager.setDefaultMaxPerRoute(maxPerRoute);
            connManager.setValidateAfterInactivity(1000);
            return connManager;
        }

        private static SSLConnectionSocketFactory getSSLConnectionSocketFactory(Integer httpsClientType) {
            if (HTTPS_CLIENT_DEFAULT.equals(httpsClientType)) {
                return SSLConnectionSocketFactory.getSocketFactory();
            } else if (HTTPS_CLIENT_SYSTEM_DEFAULT.equals(httpsClientType)) {
                return SSLConnectionSocketFactory.getSystemSocketFactory();
            } else if (HTTPS_CLIENT_SKIP_CHECK.equals(httpsClientType)) {
                return trustAllHttps();
            }
            return SSLConnectionSocketFactory.getSocketFactory();
        }
    }

    /**
     * 默认
     */
    public static final Integer HTTPS_CLIENT_DEFAULT = 0;
    /**
     * 系统默认
     */
    public static final Integer HTTPS_CLIENT_SYSTEM_DEFAULT = 1;
    /**
     * 跳过校验
     */
    public static final Integer HTTPS_CLIENT_SKIP_CHECK = 2;


    /**
     * HTTPS信任策略：跳过校验
     */
    private static final TrustStrategy SKIP_CHECK_TRUST_STRATEGY = new TrustStrategy() {
        @Override
        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            return true;
        }
    };

}
