package com.leven.demoplus.java8api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
public class CompletableTest {

    @Test
    void test01() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();

        Executors.newFixedThreadPool(10).submit(() -> {
            //future.complete("hello");
        });

        String s = future.get();
        System.out.println(s);
    }

    //组合
    @Test
    void test02() throws Exception {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "hello")
                .thenCompose(res -> CompletableFuture.supplyAsync(() -> res + "hello"))
                .thenCombine(CompletableFuture.supplyAsync(() -> "CompleteFuture"), (a, b) -> a + b);
        String s1 = f1.get();
        System.out.println(s1);
    }

    /**
     * 异常处理，方便多线程之间的异常处理
     */
    @Test
    void test03() throws Exception {
        // 异常处理
        CompletableFuture<Object> f = CompletableFuture.supplyAsync(() -> "Hello")
                .thenApplyAsync(res -> res + "World")
                .thenApplyAsync(res -> {
                    throw new RuntimeException(" test has error");
                })
                .exceptionally(e -> {
                    //handle exception  in here
                    e.printStackTrace();
                    return "出异常了。。";
                });
        System.out.println(f.get());
    }

    //异步结果处理
    @Test
    void test04() throws Exception {
        CompletableFuture<Object> f2 = CompletableFuture.supplyAsync(() -> "Hello")
                .thenApplyAsync(res -> res + "World")
                .thenApplyAsync(res -> {
                   // throw new RuntimeException("error");
                    return res;
                })
                .handleAsync((res, err) -> {
                    if (err != null) {
                        //handle exception here
                        return null;
                    } else {
                        return res;
                    }
                });

        String result = (String) f2.get();

        System.out.println(result);
    }

    //无关联性的多个结果处理
    @Test
    void test05() throws Exception {
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "hello");
        CompletableFuture<String> f4 = CompletableFuture.supplyAsync(() -> "world");
        CompletableFuture<String> f5 = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "!";
        });

        // 使用allOf方法 f3 f4 f5 都执行结束之前一直阻塞
        CompletableFuture.allOf(f3, f4, f5).join();

        System.out.println(f3.get());
        System.out.println(f4.get());
        System.out.println(f5.get());

        List<String> r = Stream.of(f3, f4, f5)
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        System.out.println(r);
    }

}
