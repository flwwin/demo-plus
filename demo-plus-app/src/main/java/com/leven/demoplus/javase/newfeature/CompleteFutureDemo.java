package com.leven.demoplus.javase.newfeature;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 异步编程：CompletableFuture
 */
class CompleteFutrueDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1:把CompleteFutrue 当Future使用
        CompletableFuture<String> future = new CompletableFuture<>();
        Executors.newFixedThreadPool(10).submit(() -> {
            future.complete("hello");
        });

        String s = future.get();
        System.out.println(s);

        // 通过CompletableFuture来控制多个异步操作
        new Thread(() -> {
            try {
                System.out.println("future.get(t1) = " + future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                System.out.println("future.get(t2) = " + future.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        future.complete("hello");

        future.supplyAsync(() -> "hello")
                .thenApplyAsync(res -> res + "world")
                .thenAcceptAsync(System.out::println);

        future.get();

        //组合
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "hello")
                .thenCompose(res -> CompletableFuture.supplyAsync(() -> res + "hello"))
                .thenCombine(CompletableFuture.supplyAsync(() -> "CompleteFuture"), (a, b) -> a + b);
        String s1 = f1.get();
        System.out.println(s1);

        // 异常处理
        CompletableFuture<Object> f = CompletableFuture.supplyAsync(() -> "Hello")
                .thenApplyAsync(res -> res + "World")
                .thenApplyAsync(res -> {
                    throw new RuntimeException("error");
                })
                .exceptionally(e -> {
                    //handle exception here
                    e.printStackTrace();
                    return null;
                });
        f.get();

        // 执行结果处理
        CompletableFuture<Object> f2 = CompletableFuture.supplyAsync(() -> "Hello")
                .thenApplyAsync(res -> res + "World")
                .thenApplyAsync(res -> {
                    throw new RuntimeException("error");
                })
                .handleAsync((res, err) -> {
                    if (err != null) {
                        //handle exception here
                        return null;
                    } else {
                        return res;
                    }
                });

        Object result = f2.get();

        System.out.println(result);

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
        //all.get();

        System.out.println(f3.get());
        System.out.println(f4.get());
        System.out.println(f5.get());

       // 结合StreamAPI
        List<String> r = Stream.of(f3, f4, f5)
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        System.out.println(r);

        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> System.out.println("123"));
        runAsync.get();

        String s2 = CompletableFuture.supplyAsync(() -> "hello").thenApplyAsync(v -> v + "world").join();

        CompletableFuture.supplyAsync(()->"hello").thenAcceptAsync(v-> System.out.println(v+"hello"));

        final String endStr = CompletableFuture.supplyAsync(() -> "hello")
                .thenCombine(CompletableFuture.supplyAsync(() -> "world"), (str1, str2) -> str1 + str2)
                .thenCombineAsync(CompletableFuture.supplyAsync(() -> "!!!"), (str3, str4) -> str3 + str4).join();

        System.out.println("endStr = " + endStr);


        Object errStr = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("error1");
        }).thenApplyAsync((s3) -> {
            throw new RuntimeException("error2");
        }).exceptionally((e) -> {
            return e.getMessage();
        }).join();

        System.out.println(errStr);
    }

}
