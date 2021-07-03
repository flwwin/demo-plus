package com.leven.demoplus.inner.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecords;

/**
 * kafka消费数据处理器
 * @author 80122172
 *
 */
public abstract class ConsumerRecordsProcessor implements Cloneable {
	
	public abstract void process(ConsumerRecords<String, String> records);
	
	/**
	 * 防止使用人使用了线程不安全的功能
	 */
	public ConsumerRecordsProcessor clone(){
		try {
			return (ConsumerRecordsProcessor)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
}
