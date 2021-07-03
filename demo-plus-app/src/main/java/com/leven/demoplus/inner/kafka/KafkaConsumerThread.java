package com.leven.demoplus.inner.kafka;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * kafka clients 消费线程
 */
public class KafkaConsumerThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerThread.class);

    private AtomicBoolean isRunning = new AtomicBoolean(true);

    private CountDownLatch shutdownLatch = new CountDownLatch(1);

    private boolean isInterruptible;

    /**
     * 如果一直没有数据,阻塞的时间.单位 毫秒
     */
    private long timeout;

    /**
     * kafka 消费 consumer
     */
    private KafkaConsumer<String, String> consumer;

    /**
     * 数据处理器
     */
    private ConsumerRecordsProcessor processor;


    public KafkaConsumerThread(String name, String topic, String groupId, String brokers, long timeout,
                               ConsumerRecordsProcessor processor) {
        this(name, topic, groupId, brokers, timeout, false, processor);
    }

    public KafkaConsumerThread(String name, String topic, String groupId, String brokers, long timeout,
                               boolean isInterruptible, ConsumerRecordsProcessor processor) {
        this(name, topic, groupId, brokers, timeout, isInterruptible, processor,
                "", "", "");
    }

    public KafkaConsumerThread(String name, String topic, String groupId, String brokers, long timeout,
                               boolean isInterruptible, ConsumerRecordsProcessor processor, String saslJaasConfig,
                               String securityProtocol, String saslMechanism) {
        super(name);
        this.isInterruptible = isInterruptible;
        this.timeout = timeout;
        this.processor = processor;
        this.consumer = new KafkaConsumer<String, String>(createConsumerProperties(brokers, groupId, saslJaasConfig, securityProtocol, saslMechanism));
        this.consumer.subscribe(Collections.singletonList(topic));
        LOGGER.info("{} ,init completed ! ", getName());
    }

    /**
     * kafka consumer 配置
     *
     * @param brokers
     * @param groupId
     * @return
     */
    public Properties createConsumerProperties(String brokers, String groupId, String saslJaasConfig, String securityProtocol, String saslMechanism) {
        Properties props = new Properties();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        if (StringUtils.isNotBlank(saslJaasConfig)) {
            props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        }
        if (StringUtils.isNotBlank(securityProtocol)) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        }
        if (StringUtils.isNotBlank(saslMechanism)) {
            props.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        }
        return props;
    }

    @Override
    public void run() {
        LOGGER.info("{} ,starting ", this.getName());
        try {
            while (isRunning.get()) {
                doWork();
            }
        } catch (Throwable e) {
            if (isRunning.get()) {
                LOGGER.error("Error due to ", e);
            }
        }
        consumer.close();
        shutdownLatch.countDown();
        LOGGER.info("{} ,stopped ", this.getName());
    }

    /**
     * 设置为要关闭消费
     *
     * @return
     */
    private boolean initiateShutdown() {

        LOGGER.info("{},initiateShutdown ... ", this.getName());

        if (isRunning.compareAndSet(true, false)) {
            LOGGER.info("{} ,shutting down", this.getName());
            isRunning.set(false);
            if (isInterruptible) {
                interrupt();
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * 等待消费结束退出
     *
     * @throws InterruptedException
     */
    private void awaitShutdown() throws InterruptedException {
        shutdownLatch.await();
        LOGGER.info("{}, shutdown completed", this.getName());
    }

    /**
     * 关闭消费线程
     */
    public void shutdown() {
        try {
            initiateShutdown();
            awaitShutdown();
        } catch (Exception ex) {
            LOGGER.info("{}, shutdown error !", this.getName(), ex);
        }
    }

    /**
     * 处理消费
     */
    public void doWork() {
        LOGGER.info("{} ,doWork start ...", this.getName());
        try {
            ConsumerRecords<String, String> records = consumer.poll(timeout);
            processor.process(records);
        } catch (Exception ex) {
            LOGGER.error("process data error , will sleep one minite !", ex);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
        LOGGER.info("{} ,doWork end ...", this.getName());
    }
}
