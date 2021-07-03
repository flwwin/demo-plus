
package com.leven.demoplus.inner.kafka;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.netflix.config.DynamicIntProperty;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

public class KafkaConsumer implements Closeable {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);
	
	private String zkAddress;
	private String groupId;
	private String topic;
	private int threadCount;	// 线程个数
	private KafkaStreamRunnable kafkaStreamRunnable;	// 源配置
	private List<KafkaStreamRunnable > kafkaStreamRunnables = new ArrayList<>();
	
	private ConsumerConnector consumer;	// 0.8版本的consumer
	private ExecutorService executor;
	private int kafkaVersion;
	/**
	 * KafkaConsumer初始化开关，默认为true，为false时不执行init()方法
	 */
	private boolean initSwitch = true;
	private Map<String, String> propertyMap;

	public KafkaConsumer() {
	}

	public void init() {
		if (!initSwitch) {
			LOGGER.info("[topic:{}][groupId:{}] initSwitch is false, skip init!", topic, groupId);
			return;
		}
		if (isV08()) {
			initV08();
		} else {
			initAfterV08();
		}
	}
	
	private void initV08() {
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig());

		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, initThreadCount());
		
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

		int threadNum = 0;
		for (final KafkaStream<byte[], byte[]> stream : streams) {
			try {
				KafkaStreamRunnable runnable = kafkaStreamRunnable.clone();
				runnable.setStream(stream);
				runnable.setThreadNum(threadNum);

				kafkaStreamRunnables.add(runnable);
				executor.submit(runnable);
				
				threadNum++;
			} catch (Exception e) {
				LOGGER.error("init kafka stream runnable fail!", e);
			}
		}
	}
	
	private void initAfterV08() {
		int threadCount = initThreadCount();
		
		Properties kafkaProperties = createConsumerPropertiesAfterV08();
		Method subscribeMethod = getKafkaSubscribeMethod();
		for(int i = 0; i < threadCount ; i++){
			org.apache.kafka.clients.consumer.KafkaConsumer<byte[], byte[]> consumer = 
					new org.apache.kafka.clients.consumer.KafkaConsumer<>(kafkaProperties);
			List<String> subscribe = Collections.singletonList(topic);
			// 0.9为subscribe(List),0.10为subscribe(Collection), 为了兼容0.9,0.10，用反射调用，DT, -_-||
			try {
				subscribeMethod.invoke(consumer, subscribe);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			
			KafkaStreamRunnable runnable = kafkaStreamRunnable.clone();
			runnable.setConsumer(consumer);
			runnable.setThreadNum(i);
			
			kafkaStreamRunnables.add(runnable);
			executor.submit(runnable);
		}
	}
	
	private Method getKafkaSubscribeMethod() {
		try {
			return org.apache.kafka.clients.consumer.KafkaConsumer.class.getMethod("subscribe", List.class);
		} catch (NoSuchMethodException | SecurityException e) {
			try {
				return org.apache.kafka.clients.consumer.KafkaConsumer.class.getMethod("subscribe", Collection.class);
			} catch (NoSuchMethodException | SecurityException e1) {
				throw new RuntimeException(e1);
			}
		}
	}
	
	private int initThreadCount() {
		// 可以通过配置中心调节线程数
		//DynamicIntProperty dataSyncThreadCount = new DynamicIntProperty(groupId + "." + topic + ".thread_count", 0);
		int useThreadCount = this.threadCount;
		/*if (dataSyncThreadCount.get() > 0) {
		}*/
		useThreadCount = 10;

		ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("kafka-consumer-"+topic+"-%d").build();

		/// executor = Executors.newFixedThreadPool(useThreadCount);

		executor = new ThreadPoolExecutor(useThreadCount, useThreadCount,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(),threadFactory);
		return useThreadCount;
	}
	
	protected boolean isV08() {
		return kafkaVersion <= 8;
	}
	
	protected ConsumerConfig createConsumerConfig() {
		Properties props = createConsumerProperties();

		LOGGER.info("init kafka consumer with properties:{}", props);

		return new ConsumerConfig(props);
	}
	
	
	protected Properties createConsumerProperties() {
		Properties props = new Properties();

		props.put("zookeeper.connect", zkAddress);
		props.put("group.id", groupId);
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		// zk中没有offset记录时使用当前最大的offset
		props.put("auto.offset.reset", "largest");	
		// rebalance.max.retries * rebalance.backoff.ms > zookeeper.session.timeout.ms
		props.put("rebalance.max.retries", "4");  
		props.put("rebalance.backoff.ms", "2600");
		props.put("zookeeper.session.timeout.ms", "10000");
		
		appendProperty(props);
		
		return props;
	}
	
	protected Properties createConsumerPropertiesAfterV08() {
		Properties props = new Properties();
		
		props.put("bootstrap.servers", zkAddress);
		props.put("group.id", groupId);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "10000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        
		appendProperty(props);
		
        return props;
	}
	
	private void appendProperty(Properties prop) {
		if (null == prop || null == propertyMap) {
			return;
		}
		
		prop.putAll(propertyMap);
	}

	@Override
	public void close() {
		if (consumer != null) {
			consumer.shutdown();
		}
		
		for (KafkaStreamRunnable kafkaStreamRunnable : kafkaStreamRunnables) {
			try {
				kafkaStreamRunnable.close();
			} catch (Exception ex) {
			}
		}
		
		if (executor != null) {
			executor.shutdown();
			
			try {
				if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
					LOGGER.error("Timed out waiting for consumer threads to shut down, exiting uncleanly");
				} else {
					LOGGER.info("close kafka consumer complete!");
				}
				
			} catch (InterruptedException e) {
				LOGGER.error("Interrupted during shutdown, exiting uncleanly");
			}
		}
	}

	/**
	 * 获取zkAddress
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getZkAddress() {
		return zkAddress;
	}

	/**
	 * 设置zkAddress  
	 * @param   zkAddress	sth.   
	 * @since   Ver 1.0
	 */
	public void setZkAddress(String zkAddress) {
		this.zkAddress = zkAddress;
	}

	/**
	 * 获取groupId
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * 设置groupId  
	 * @param   groupId	sth.   
	 * @since   Ver 1.0
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
	 * 获取topic
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * 设置topic  
	 * @param   topic	sth.   
	 * @since   Ver 1.0
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}

	/**
	 * 获取threadCount
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public int getThreadCount() {
		return threadCount;
	}

	/**
	 * 设置threadCount  
	 * @param   threadCount	sth.   
	 * @since   Ver 1.0
	 */
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	/**
	 * 获取kafkaStreamRunnable
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public KafkaStreamRunnable getKafkaStreamRunnable() {
		return kafkaStreamRunnable;
	}

	/**
	 * 设置kafkaStreamRunnable  
	 * @param   kafkaStreamRunnable	sth.   
	 * @since   Ver 1.0
	 */
	public void setKafkaStreamRunnable(KafkaStreamRunnable kafkaStreamRunnable) {
		this.kafkaStreamRunnable = kafkaStreamRunnable;
	}

	/**
	 * 获取kafkaVersion
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public int getKafkaVersion() {
		return kafkaVersion;
	}

	/**
	 * 设置kafkaVersion  
	 * @param   kafkaVersion	sth.   
	 * @since   Ver 1.0
	 */
	public void setKafkaVersion(int kafkaVersion) {
		this.kafkaVersion = kafkaVersion;
	}

	public boolean isInitSwitch() {
		return initSwitch;
	}

	public void setInitSwitch(boolean initSwitch) {
		this.initSwitch = initSwitch;
	}

	public Map<String, String> getPropertyMap() {
		return propertyMap;
	}

	public void setPropertyMap(Map<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}
}
