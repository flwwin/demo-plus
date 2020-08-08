##  java8新特性

####  异步编程

##### `CompletableFuture` 实现异步编程

- ` supplyAsync`  有返回值

  ```java
  CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "hello");
  ```

- `runAsync`参数是Runable没有返回值

  ```java
   CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> System.out.println("123"));
          runAsync.get();
  ```

- `allof`在f1和f2两个任务执行完成前一直阻塞，一般用于多个线程执行后获取执行结果

  ```java
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
  
         // 使用allOf方法
          CompletableFuture.allOf(f3, f4, f5).join();
  ```

  

  #####  变换结果

  ​	这些api用于上一个阶段的计算结果，然后对结果进行操作返回操作后的结果

  `thenApplyAsync`  异步执行

  `thenApply` 同步执行

  ```java
  //计算结果 helloworld
  String s2 = CompletableFuture.supplyAsync(() -> "hello").thenApplyAsync(v -> v + "world").join();
  ```

  

  ######  消费结果

  `thenAccept`  同步消费结果没有返回值

  `thenAcceptAsync` 异步消费结果没有返回值

  ```java
   //打印出helloWord
   CompletableFuture.supplyAsync(()->"hello").thenAcceptAsync(v-> System.out.println(v+"hello"));
  ```



######  		结果转化

​		`thenCombine`  获取多个结果转化	

​		`thenCombineAsync` 

​		`thenAcceptBoth`  当两个CompletionStage都执行完成后，把结果一块交给thenAcceptBoth来进行消耗

```java
//结果：endStr = helloworld!!!
final String endStr = CompletableFuture.supplyAsync(() -> "hello")
        .thenCombine(CompletableFuture.supplyAsync(() -> "world"), (str1, str2) -> str1 + str2)
        .thenCombineAsync(CompletableFuture.supplyAsync(() -> "!!!"), (str3, str4) -> str3 + str4).join();

System.out.println("endStr = " + endStr);
```



######  		谁执行快用谁

`applyToEither` 

```java
private static void applyToEither() throws Exception {
    CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
        @Override
        public Integer get() {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f1="+t);
            return t;
        }
    });
    CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(new Supplier<Integer>() {
        @Override
        public Integer get() {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f2="+t);
            return t;
        }
    });
    
    CompletableFuture<Integer> result = f1.applyToEither(f2, new Function<Integer, Integer>() {
        @Override
        public Integer apply(Integer t) {
            System.out.println(t);
            return t * 2;
        }
    });

    System.out.println(result.get());
}
```



######  异常补偿

`exceptionally` 捕捉异常，相当于try-catch

```java
 Object errStr = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("error1");
        }).thenApplyAsync((s) -> {
            throw new RuntimeException("error2");
        }).exceptionally((e) -> {
            return e.getMessage();
        }).join();
```



#### StreamApi中集合类的流式编程

`filter`过滤

```java
strings.stream().filter(s -> !s.isEmpty()).forEach(System.out::println);
```

`map` 计算

```java
strings.stream().map(s -> s+"1").forEach(System.out::println);

```

`flateMap`  将数组扁平化操作

```java

        String[] a = {"h","e","e","l","o"};
        String[] b = {"w","o","r","l","d"};

        final List<String[]> strings = Arrays.asList(a, b);

        System.out.println("strings before = " + strings);

        List<String> collect = strings.stream().flatMap(Arrays::stream).collect(Collectors.toList());

        System.out.println("collect after = " + collect);
```

执行结果：

```
strings before = [[Ljava.lang.String;@4dc63996, [Ljava.lang.String;@d716361]
collect after = [h, e, e, l, o, w, o, r, l, d]
```

将内层数组全部抽离出来，将集合扁平化



`sorted` 排序

```java
strings.stream().sorted((String o1, String o2) -> {
            if (Integer.valueOf(o1) < Integer.valueOf(o2))
            {
                return 1;
            }else {
                return -1;
            }
        }).forEach(System.out::println);
```



`distinct` 消除重复

```java
strings.stream().distinct().forEach(System.out::println);
```



######  最终操作

`forEach` 迭代每一个元素

```
strings.stream().map(s -> s+"1").forEach(System.out::println);
```

`count` 统计元素的个数

```java
 Integer[] strs = {1, 2, 3, 4};
        List<Integer> strings = Arrays.asList(strs);
        final long count = strings.stream().count();
        System.out.println("count = " + count); //结果 4
```

`collect` 汇总一个结果

```java
 Integer[] strs = {1, 2, 3,3, 4};
        List<Integer> strings = Arrays.asList(strs);
        //去冲后的结果转为list
        strings = strings.stream().distinct().collect(Collectors.toList());
        System.out.println(strings.toString());
```





