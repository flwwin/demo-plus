package com.leven.demoplus.ThreadDemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * ArrayList是线程不安全的,为什么不安全呢？
 * 因为add 方法没有同步
 * 那怎么获得一个线程安全的list呢
 * 1：通过Vector  这个类是在1.1版本就出来了，比ArrayList还要早，线程是安全的，但是用的是synchronized同步，性能差，不建议用
 * 2：通过Collections
 * 3: CopyonwriteArrayList
 */
public class ArrayListDemo {
    public static void main(String[] args) {
        List list = new ArrayList();
        List synchronizedList = Collections.synchronizedList(list);

        //
        for (int i = 0; i <30 ; i++) {
            new Thread(() -> {
                String str = UUID.randomUUID().toString().substring(0, 8);
                synchronizedList.add(str);
                System.out.println(synchronizedList);
            },String.valueOf(i)).start();
        }
    }
}
