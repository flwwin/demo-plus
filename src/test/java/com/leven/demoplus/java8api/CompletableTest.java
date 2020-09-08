package com.leven.demoplus.java8api;

import com.google.common.collect.Lists;
import com.sun.xml.internal.ws.util.CompletedFuture;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
@Slf4j
public class CompletableTest {

    private static final Executor ES = new ThreadPoolExecutor(10
            , 10
            , 0L
            , TimeUnit.MILLISECONDS
            , new LinkedBlockingQueue<>(10)
            , Executors.defaultThreadFactory()
            , new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.debug("has thread rejectedExecution");
        }
    });

    @Test
    void test01() throws Exception {

        CopyOnWriteArrayList<CompletableFuture<String>> list = Lists.newCopyOnWriteArrayList();
        for (int i = 0; i < 10; i++) {
            CompletableFuture<String> future = new CompletableFuture<>();

            int finalI = i;
            CompletableFuture.runAsync(() -> {
                if (finalI == 5) {
                    try {
                        TimeUnit.SECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                future.complete("hello");
                list.add(future);
            }, ES);
        }

        for (CompletableFuture<String> f : list) {
            System.out.println(f.get());
        }
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

    /**
     * 实现多个线程异步，然后等待多线程处理结果。
     */
    @Test
    void test06(){
        List<Integer> asList = Arrays.asList(1, 2, 3, 4, 5);
        List<CompletableFuture<String>> resList = asList.stream()
                .map(s -> CompletableFuture.supplyAsync(() -> sayHello(s), ES))
                .collect(Collectors.toList());


        CompletableFuture.allOf(resList.toArray(new CompletableFuture[0])).join()  ;
        resList.stream().forEach(s-> {
            try {
                System.out.println(s.get());
            } catch (InterruptedException e) {

            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    private String sayHello(int i){
        if (i==5){
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(i);
    }

    //  flatMap的操作

    //合并两个list
    @Test
    void test07(){
           String[] a = {"h","e","e","l","o"};
        String[] b = {"w","o","r","l","d"};

        final List<String[]> strings = Arrays.asList(a, b);

        System.out.println("strings before = "+strings);

        List<String> collect = strings.stream().flatMap(Arrays::stream).collect(Collectors.toList());

        System.out.println("collect after = " + collect);

        /*合并多个list*/
        //List<AClass> aClassListResult = map.values().stream().flatMap(listContainer -> listContainer.getLst().stream()).collect(Collectors.toList());

    }

}
