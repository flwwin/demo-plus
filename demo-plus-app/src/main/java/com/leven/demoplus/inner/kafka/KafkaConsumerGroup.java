package com.leven.demoplus.inner.kafka;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

/**
 * kafka 多线程消费组
 * 使用此类 需要 自己手动 引入 kafka-clients 及其版本
 * 
 * 原因 : kafka 0.10.0.0 的clients 消费不了 0.9.0.0 的服务 . 
 * 		正式环境存在多个版本的服务.但是client api是一致的,故需使用者自己引入包和对应的版本 
 * 
 * @author 80122172
 * 
 */
public class KafkaConsumerGroup implements Closeable {

	private static final String THREAD_NAME_FORMAT = "%s-%s-%s";//topic-groupId-i
	
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerGroup.class);
	
	/**
	 * 线程数
	 */
	private int nThreads;
	
	/**
	 * 订阅的topic
	 */
	private String topic;

	/**
	 * 消费者组
	 */
	private String groupId;

	/**
	 * kafka 节点逗号分隔
	 */
	private String brokers;

	/**
	 * 鉴权相关字段
	 */
	private String saslJaasConfig;
	/**
	 * 鉴权相关字段
	 */
	private String securityProtocol;
	/**
	 * 鉴权相关字段
	 */
	private String saslMechanism;
	/**
	 * poll 超时时间 ,如果没有数据超过此时间直接返回空 集合
	 */
	private long timeout;

	private ConsumerRecordsProcessor processor;

	private ExecutorService workers;

	private List<KafkaConsumerThread> workerThreads = Lists.newArrayList();
	
	public void init(){
		KafkaConsumerThread workerThread = null;
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("kafka-consumer-%d").build();
		/// workers = Executors.newFixedThreadPool(nThreads);
		workers = new ThreadPoolExecutor(nThreads, nThreads,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(),threadFactory);
		for (int i = 0; i < nThreads; i++) {
			workerThread = new KafkaConsumerThread(
					String.format(THREAD_NAME_FORMAT, topic, groupId, i), topic, groupId, brokers, timeout,
					false, processor.clone(), saslJaasConfig, securityProtocol, saslMechanism);
			workerThreads.add(workerThread);
			workers.submit(workerThread);
		}
	}
	
	@Override
	public void close() throws IOException {
		
		LOGGER.info("closing KafkaConsumerGroup , {},{},{}",groupId,topic,brokers);
		
		for(KafkaConsumerThread workerThread:workerThreads){
			workerThread.shutdown();
		}
		
		if(!workers.isShutdown()) {
			workers.shutdownNow();
		}
		
		LOGGER.info("KafkaConsumerGroup closed, {},{},{}",groupId,topic,brokers);
	}

	public int getnThreads() {
		return nThreads;
	}

	public String getTopic() {
		return topic;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getBrokers() {
		return brokers;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setnThreads(int nThreads) {
		this.nThreads = nThreads;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setBrokers(String brokers) {
		this.brokers = brokers;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public String getSaslJaasConfig() {
		return saslJaasConfig;
	}

	public void setSaslJaasConfig(String saslJaasConfig) {
		this.saslJaasConfig = saslJaasConfig;
	}

	public String getSecurityProtocol() {
		return securityProtocol;
	}

	public void setSecurityProtocol(String securityProtocol) {
		this.securityProtocol = securityProtocol;
	}

	public String getSaslMechanism() {
		return saslMechanism;
	}

	public void setSaslMechanism(String saslMechanism) {
		this.saslMechanism = saslMechanism;
	}

	public ConsumerRecordsProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(ConsumerRecordsProcessor processor) {
		this.processor = processor;
	}
}
