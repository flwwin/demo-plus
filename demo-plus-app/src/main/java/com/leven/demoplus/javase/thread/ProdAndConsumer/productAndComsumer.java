package com.leven.demoplus.javase.thread.ProdAndConsumer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class resource {
    private  int i = 0;
    private ReentrantLock lock;
    private Condition proCondition;
    private Condition conCondition;

    public resource(ReentrantLock lock, Condition proCondition, Condition conCondition) {
        this.lock = lock;
        this.proCondition = proCondition;
        this.conCondition = conCondition;
    }

    public void incr() {
        lock.lock();
        try {
            while (i != 0) {
                proCondition.await();
            }

            i++;
            System.out.println(Thread.currentThread().getName()+"\t" + i);
            conCondition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void desc() {
        lock.lock();
        try {
            while (i == 0) {
                conCondition.await();
            }

            i--;
            System.out.println(Thread.currentThread().getName()+"\t"+i);
            proCondition.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

public class productAndComsumer {
    public static void main(String[] args) {
        ReentrantLock reentrantLock = new ReentrantLock();
        Condition proCondition = reentrantLock.newCondition();
        Condition conCondition = reentrantLock.newCondition();
        resource resource = new resource(reentrantLock,proCondition,conCondition);



            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        resource.incr();
                    }
                }
            }, String.valueOf("生产------ ")).start();




            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        resource.desc();
                    }
                }
            }, "消费------ ").start();


    }
}
