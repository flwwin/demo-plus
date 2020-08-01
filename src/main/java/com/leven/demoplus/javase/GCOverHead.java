package com.leven.demoplus.javase;

/**
 * 放障现象:
 * Exception in thread "main" java.Lang.OutOfMemoryError: Direct buffer memory
 * 导效原因:
 * 写NIO程序经常使ByteBuffer来谈取或者写入数据，这是一种基于通道(Channel)与缓冲区(Buffer)的I/0方式，
 * 它可以便sNative函教库直接分配准外内存，然后通过一 个存储在Java维里面e的irectByteBuffer对象作为这统内存的引用进行操作.
 * 这样能在一些杨景 中显者提高性能，因为避免7在Java堆西Native雄中来回复制教据.
 * ByteBuffer. allocate(capability)第- -种方式是升配JM雄内存，属于6C营辖范園，由于嘉夔拷贝所以速度相对较嫂
 * ByteBuffer. allocteDirect(capability)第一种方 式是升配0S本地内存，不属于6C曾辖范围，由于不需要内存拷贝所以速度相对较快.
 * 但如果不断分配本地内办，雄内存很少使用，那么JVM就不需要执行6C, DirectByteBuffer对原们就不会被回收，
 * 过时候堆内存充足，但本地内存可能已经使用光了，再次尝试分配本地内存就会出0utOfMemoryError.都程序就直接腐溃了.
 */
public class GCOverHead {
    public static void main(String[] args) {
       /* int i = 0;
        ArrayList<Object> list = new ArrayList<>();
        try {
            while (true) {
                list.add(String.valueOf(i).intern());
            }
        } catch (Exception e) {
            System.out.println("......" + i);
            e.printStackTrace();
            throw e;
        }*/



 }
}
