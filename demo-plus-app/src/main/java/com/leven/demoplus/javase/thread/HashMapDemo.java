package com.leven.demoplus.javase.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HashMapDemo {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        //ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
        //Collections.synchronizedMap()
        for (int i = 1; i <= 30; i++)
            new Thread(() -> {
                map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0, 8));
                System.out.println(map);
            }, String.valueOf(i)).start();
    }
}
