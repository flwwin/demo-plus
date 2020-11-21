package com.leven.demoplus.javase.collection;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CurrentHashMapDemo {
    public static void main(String[] args) {
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        HashMap<Object, Object> hashMap = new HashMap<>(0);
        //currentHashMap是不可以为null的 value 和key都是
        map.put("1","123");
        hashMap.put("1","123");
        hashMap.put("1","345");
        System.out.println(hashMap.get(null));

    }
}
