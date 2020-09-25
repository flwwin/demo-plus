package com.leven.demoplus.ThreadDemo;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class CallableDemo {
    public static void main(String[] args) {
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
            return 10 * 10;
        });
        futureTask.run();

        //isDone 方法在futureTask没有返回结果之前都是会阻塞的
        while (!futureTask.isDone()){

        }
        try {
            futureTask.get();
            System.out.println(futureTask.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
