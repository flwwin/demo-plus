package com.leven.demoplus.inner.sentry.stat;


import lombok.extern.slf4j.Slf4j;

/**
 * 自定义监控
 * @author 80122172
 */
@Slf4j
public class StatLogUtils {

    public static void addStat(String key, boolean isSuccess, long nanoTime) {
        try {
            ExecutorStatPool.getInstance().getExecutorStat(key).addStat(isSuccess, nanoTime);
        } catch (Exception e) {
            log.error("统计日志错误...", e);
        }
    }

    public static void addStat(String key, long nanoTime) {
        addStat(key, true, nanoTime);
    }
}


