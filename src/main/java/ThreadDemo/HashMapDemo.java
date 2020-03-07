package ThreadDemo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapDemo {
    public static void main(String[] args) {
        //Map<String, String> map = new HashMap<>();
        ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
        //Collections.synchronizedMap()
        for (int i = 1; i <= 30; i++)
            new Thread(() -> {
                concurrentHashMap.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0, 8));
                System.out.println(concurrentHashMap);
            }, String.valueOf(i)).start();
    }
}
