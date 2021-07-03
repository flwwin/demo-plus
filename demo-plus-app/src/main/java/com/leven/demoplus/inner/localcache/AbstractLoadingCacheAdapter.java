package com.leven.demoplus.inner.localcache;//package com.leven.demoplus.inner.localcache;
//
//import com.github.benmanes.caffeine.cache.Cache;
//import com.github.benmanes.caffeine.cache.CacheLoader;
//import com.github.benmanes.caffeine.cache.Caffeine;
//import com.github.benmanes.caffeine.cache.LoadingCache;
//import io.micrometer.core.instrument.Metrics;
//import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
//
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.ThreadLocalRandom;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author quzijing
// * @date 2019-12-09 18:51
// */
//public abstract class AbstractLoadingCacheAdapter<K, V> implements CaffeineCacheLoader<K, V> {
//    /**
//     * access 永不过期
//     */
//    private static final int EXPIRE_AFTER_ACCESS_SECOND = Integer.MAX_VALUE;
//
//    /**
//     * 定期refresh
//     */
//    private static final int REFRESH_AFTER_WRITE_SECOND = 60;
//
//    protected final LoadingCache<K, V> loadingCache;
//
//    public AbstractLoadingCacheAdapter(ExecutorService executorService, CacheLoader<K, V> cacheLoader, int maxSize) {
//        this(executorService, EXPIRE_AFTER_ACCESS_SECOND, REFRESH_AFTER_WRITE_SECOND, cacheLoader, maxSize);
//    }
//
//    public AbstractLoadingCacheAdapter(ExecutorService executorService, int refreshAfterWriteSecond, CacheLoader<K, V> cacheLoader, int maxSize) {
//        this(executorService, EXPIRE_AFTER_ACCESS_SECOND, refreshAfterWriteSecond, cacheLoader, maxSize);
//    }
//
//
//    public AbstractLoadingCacheAdapter(ExecutorService executorService, int expireAfterAccessSecond, int refreshAfterWriteSecond, CacheLoader<K, V> cacheLoader, int maxSize) {
//        this.loadingCache = Caffeine.newBuilder()
//                .recordStats()
//                .maximumSize(maxSize)
//                .expireAfterAccess(expireAfterAccessSecond, TimeUnit.SECONDS)
//                .refreshAfterWrite(refreshAfterWriteSecond, TimeUnit.SECONDS)
//                .executor(executorService)
//                .build(cacheLoader);
//        // 注册缓存监控
//        CaffeineCacheMetrics.monitor(Metrics.globalRegistry, this.loadingCache, this.getClass().getSimpleName());
//    }
//
//    @Override
//    public Map<K, V> getAllPresent(Iterable<K> keys) {
//        return this.loadingCache.getAllPresent(keys);
//    }
//
//    @Override
//    public V getIfPresent(K key) {
//        return this.loadingCache.getIfPresent(key);
//    }
//
//
//    @Override
//    public Map<K, V> getAllOrLoad(Iterable<K> keys) {
//        return this.loadingCache.getAll(keys);
//    }
//
//    @Override
//    public V getOrLoad(K key) {
//        return this.loadingCache.get(key);
//    }
//
//    @Override
//    public Cache<K, V> getNativeCache() {
//        return this.loadingCache;
//    }
//
//    protected static int randomInterval(int interval) {
//        return ThreadLocalRandom.current().nextInt(60) + interval;
//    }
//}
