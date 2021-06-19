package com.leven.demoplus.javase.algorithm;

import java.util.LinkedHashMap;
import java.util.Map;

/** 基于LinkHashMap实现一个LRU缓存 */
public class LRUCacheDemo<k, v> extends LinkedHashMap<k, v> {

  private int capacity;

  public LRUCacheDemo(int capacity) {
    // assesOrder  我们就清楚了当accessOrder设置为false时，会按照插入顺序进行排序，当accessOrder为true是，会按照访问顺序
    super(capacity, 0.75f, true);
    this.capacity = capacity;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<k, v> eldest) {
    return super.size() > capacity;
  }

  public static void main(String[] args) {
    LRUCacheDemo lruCacheDemo = new LRUCacheDemo<>(3);
    lruCacheDemo.put(1, "a");
    lruCacheDemo.put(2, "b");
    lruCacheDemo.put(3, "c");

    System.out.println("lruCacheDemo.keySet() = " + lruCacheDemo.keySet());

    lruCacheDemo.put(4, "d");
    System.out.println("lruCacheDemo.keySet() = " + lruCacheDemo.keySet());

    lruCacheDemo.get(3);
    System.out.println("lruCacheDemo.keySet() = " + lruCacheDemo.keySet());

    lruCacheDemo.put(5, "");
    System.out.println("lruCacheDemo.keySet() = " + lruCacheDemo.keySet());

  }
}
