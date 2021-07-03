
package com.leven.demoplus.inner.kafka;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import kafka.consumer.KafkaStream;

import java.io.Closeable;

public abstract class KafkaStreamRunnable implements Runnable, Cloneable, Closeable {
	private KafkaStream<byte[], byte[]> stream;		// 0.8之前的版本
	private KafkaConsumer<byte[], byte[]> consumer;	// 0.8之后的版本
	private int threadNum;
	private volatile boolean isStart = true;
	
	@Override
	public KafkaStreamRunnable clone() {
		try {
			return (KafkaStreamRunnable)super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void close() {
		isStart = false;
		if (null != consumer) {
			consumer.close();
			consumer = null;
		}
	}
	
	public boolean isStart() {
		return isStart;
	}
	
	/**
	 * 获取stream
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public KafkaStream<byte[], byte[]> getStream() {
		return stream;
	}
	/**
	 * 设置stream  
	 * @param   stream	sth.   
	 * @since   Ver 1.0
	 */
	public void setStream(KafkaStream<byte[], byte[]> stream) {
		this.stream = stream;
	}
	
	/**
	 * 获取consumer
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public KafkaConsumer<byte[], byte[]> getConsumer() {
		return consumer;
	}

	/**
	 * 设置consumer  
	 * @param   consumer	sth.   
	 * @since   Ver 1.0
	 */
	public void setConsumer(KafkaConsumer<byte[], byte[]> consumer) {
		this.consumer = consumer;
	}

	/**
	 * 获取threadNum
	 * @return  sth.
	 * @since   Ver 1.0
	 */
	public int getThreadNum() {
		return threadNum;
	}
	/**
	 * 设置threadNum  
	 * @param   threadNum	sth.   
	 * @since   Ver 1.0
	 */
	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}
}

