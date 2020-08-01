package com.leven.demoplus.reference;

import java.lang.ref.ReferenceQueue;

/**
 * 弱引用：内存不管是否充足，gc就回收
 * 软引用：内存充足不回收，内存不充足就回收，在缓存中用的多
 * 虚引用：内存不管是否充足，gc就回收，回收前会加入一个队列中，主要是记录一下回收前这个动作
 */
public class WeakReference {
    //弱引用
    public static void main(String[] args) {
        Object o1 = new Object();
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        java.lang.ref.WeakReference<Object> weakReference = new java.lang.ref.WeakReference<>(o1,referenceQueue);
        System.out.println(01);
        System.out.println(weakReference.get());
        System.out.println(referenceQueue.poll());
        o1 = null;
        System.gc();
        System.out.println("=================");
        System.out.println(01);
        System.out.println(weakReference.get());
        //回收之前在队列中
        System.out.println(referenceQueue.poll());
    }
}
