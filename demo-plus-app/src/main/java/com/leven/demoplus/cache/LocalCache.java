package com.leven.demoplus.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存：
 * 1：用Guava 缓存框架
 */
@Component
public class LocalCache {

    /**
     * 创建单线程的线程池实现对数据的刷新
     */
    public static ExecutorService ES = Executors.newSingleThreadExecutor();
    public LoadingCache<String, List<String>> cacheList;

    @PostConstruct
    public void init() throws Exception {

        CacheLoader<String, List<String>> cache = initLoader();
        this.cacheList = CacheBuilder.newBuilder().initialCapacity(1).maximumSize(1)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .refreshAfterWrite(5, TimeUnit.SECONDS)
                .build(CacheLoader.asyncReloading(cache, ES));//异步刷新
        initLoader().load("");
    }

    /**
     * 初始化缓存刷新
     * @return
     */
    private CacheLoader<String, List<String>> initLoader() {
        return new CacheLoader<String, List<String>>() {
            @Override
            public List<String> load(String s) {
                ArrayList<String> cacheList = Lists.newArrayList();
                cacheList.add("123");
                return cacheList;
            }
        };
    }
}
