package oom;

import java.util.Random;

/**
 * 堆内存溢出，这是工作中最常见的OOM故障
 * 1：在JVM启动参数的时候将堆内存设置成10M
 */
public class HeapSpaceOomDemo {
    public static void main(String[] args) {
        String str = "123";
        while (true){
            str += str + new Random().nextInt(111111111)+new Random().nextInt(222222);
            str.intern();
        }
    }
}
