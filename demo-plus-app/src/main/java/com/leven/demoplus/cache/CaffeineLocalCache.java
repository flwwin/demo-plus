package com.leven.demoplus.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** 缓存框架 coffeine */
@Component
@Slf4j
public class CaffeineLocalCache {

  private static ExecutorService ES = Executors.newSingleThreadExecutor();
  private Cache<String, String> cache;

  @PostConstruct
  public void init() {

    try {
      CacheLoader<String, String> loader = initLoader();
      cache =
          Caffeine.newBuilder()
              .expireAfterWrite(10, TimeUnit.MINUTES) // 个元素在上一次读写操作后一段时间之后，在指定的时间后没有被再次访问将会被认定为过期项
              .maximumSize(10000)
              .refreshAfterWrite(1, TimeUnit.MINUTES) //
              .executor(ES) // 线程池去刷新
              .build(loader); // 实现CacheLoader

      // 项目启动的时候加载一次缓存
      loader.load("key");
    } catch (Exception e) {
      log.error("load local cache got error", e);
    }
  }

  private String createExpensiveGraph(String key) {
    // todo  去加载缓存
    return "缓存。。。。";
  };

  private CacheLoader<String, String> initLoader() {
    return new CacheLoader<String, String>() {
      @Nullable
      @Override
      public String load(@NonNull String s) {
        // 添加缓存
        cache.put(s, "123");
        return null;
      }

      @Nullable
      @Override
      public String reload(@NonNull String key, @NonNull String oldValue) {
        // 刷新缓存
        return null;
      }
    };
  }
}
