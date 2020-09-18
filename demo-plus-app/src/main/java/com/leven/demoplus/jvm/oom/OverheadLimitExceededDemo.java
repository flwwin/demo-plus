package com.leven.demoplus.jvm.oom;

import java.util.ArrayList;

/**
 * GC overhead limit exceeded
 * JVM参数设置：8U
 * -Xms10m -Xmx10m -XX:+PrintGCDetails -XX:MaxDirectMemorySize=5m
 */
public class OverheadLimitExceededDemo {
    public static void main(String[] args) {
        int i =0;
        final ArrayList list = new ArrayList();

        while (true){
            list.add(String.valueOf(++i).intern());
        }
    }
}
