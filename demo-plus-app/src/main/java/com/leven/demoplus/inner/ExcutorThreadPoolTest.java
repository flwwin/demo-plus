package com.leven.demoplus.inner;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @className ExcutorThreadPoolTest
 * @description TODO
 * @author W9007371/flw
 * @date 2021/6/16 10:32
 */
public class ExcutorThreadPoolTest {
    /**
     * 线程池的创建：1：总共七个参数
     *             2：核心线程数量，和最大保持一致（经验值512，1024）
     *             3：keepAliveTime 设置为0，线程空闲及时回收
     */
    private static final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("mix-web-req-%d").build();
    public static Executor ES = new ThreadPoolExecutor(100
            , 100
            , 0L
            , TimeUnit.MILLISECONDS
            , new ArrayBlockingQueue<Runnable>(100)
            , threadFactory
            , new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 加日志监控，或者抛出异常都可以，看业务需要
        }
    });
}
