package reference;

import java.lang.ref.SoftReference;

/**
 * 软引用：内存够就不回收，内存不够就回收
 * 内存这是为 5M  打印GC日志 -Xms5m -Xmx5m -XX:+PrintGCDetails
 */
public class SoftReferenceDemo {
    public static void main(String[] args) {
        Object o1 = new Object();
        SoftReference<Object> softReference = new SoftReference<>(o1);
        System.out.println(o1);
        System.out.println(softReference.get());
        o1 = null;
        try {
            byte[] bytes = new byte[30 * 1024 * 1024]; //30M的数组
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.println(01);
            System.out.println(softReference.get());
        }

    }
}
