package javase;


import com.sun.xml.internal.ws.util.CompletedFuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 异步编程：CompletableFuture
 */
public class CompleteFutrueDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1:把CompleteFutrue 当Future使用
        //CompletableFuture<String> future = new CompletableFuture<>();
      /*  Executors.newFixedThreadPool(10).submit(() -> {
            future.complete("hello");
        });

        String s = future.get();
        System.out.println(s);
        // 通过CompletetableFuture来控制多个异步操作
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

        future.complete("hello");*/

       /* future.supplyAsync(() -> "hello")
                .thenApplyAsync(res -> res + "world")
                .thenAcceptAsync(System.out::println);

        future.get();*/

        //组合
       /* CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "hello")
                .thenCompose(res -> CompletableFuture.supplyAsync(() -> res + "hello"))
                .thenCombine(CompletableFuture.supplyAsync(() -> "CompleteFuture"), (a, b) -> a + b);
        String s = future.get();
        System.out.println(s);*/

        // 异常处理
  /*      CompletableFuture<Object> f = CompletableFuture.supplyAsync(() -> "Hello")
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

        System.out.println(result);*/

        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "hello");
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "world");
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "!");

// 使用allOf方法
        CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2, f3);
        all.get();

        System.out.println(f1.get());
        System.out.println(f2.get());
        System.out.println(f3.get());

// 结合StreamAPI
        List<String> result = Stream.of(f1, f2, f3)
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        System.out.println(result);
    }
}
