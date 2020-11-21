package com.leven.demoplus.javase.api;


/**
 * 关键字：volititle
 * 1：保证可见性
 * 2：不保证原子性
 * 3：禁止指令重排
 */

//如果volatile保证原子性的话预期结果是20000
//多次运行结果都不是两万，对incrNum进行同步后结果为20000
class MyData{
    //AtomicInteger num = new AtomicInteger(0); 原子类可以保证线程的安全
    int num = 0;

    void incrNum() {
        this.num++;
    }
}
public class VolaititleDemo {

    public static void main(String[] args) {
        final MyData myData = new MyData();


        for (int i = 0; i <20 ; i++) {
            new Thread(()->{
                for (int j = 0; j <1000 ; j++) {
                    myData.incrNum();
                }
            }).start();
        }
        //2代表GC线程和main线程
        while (Thread.activeCount()>2){
            Thread.yield();
        }
        System.out.println("myData.num = " + myData.num);
    }

}
