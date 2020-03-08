package oom;

import java.util.Random;

/**
 * 堆内存溢出，这是工作中最常见的OOM故障
 * 1：在JVM启动参数的时候将堆内存设置成10M
 */
public class HeapSpaceOomDemo {
    public static void main(String[] args) {
        Byte[] b = new Byte[20*1024*1024];
    }
}
