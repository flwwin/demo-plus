package com.leven.demoplus.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 本地缓存：
 * 1：用
 */
public class LocalCache {

    public static ExecutorService ES = Executors.newSingleThreadExecutor();
    public LoadingCache<String, List<String>> cacheList;

    @PostConstruct
    public void init() {

        CacheLoader<String, List<String>> cache = initLoader();
        this.cacheList = CacheBuilder.newBuilder().initialCapacity(1).maximumSize(1)
                .expireAfterAccess(5, TimeUnit.SECONDS)
                .refreshAfterWrite(5, TimeUnit.SECONDS)
                .build(CacheLoader.asyncReloading(cache, ES));//异步刷新
    }

    private CacheLoader<String, List<String>> initLoader() {
        return new CacheLoader<String, List<String>>() {
            @Override
            public List<String> load(String s) throws Exception {
                ArrayList<String> cacheList = Lists.newArrayList();
                cacheList.add("123");
                return cacheList;
            }
        };
    }
}
