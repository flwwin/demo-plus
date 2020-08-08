  ##  几种常见的OOM异常

​    oom异常就是Out Of Memory Error 内存溢出异常，是我们开发中常见的异常。oom异常也分成多种。

> **java.lang.OutOfMemoryError: Java heap space  堆空间溢出，最常见的**	

在创建大对象的时候特别注意堆内存的使用，避免产生堆的内存溢出

模拟一下

```java
/**
 * 堆内存溢出，这是工作中最常见的OOM故障
 * 1：在JVM启动参数的时候将堆内存设置成10M  -Xmx10m -Xms10m
 */
public class HeapSpaceOomDemo {
    public static void main(String[] args) {
        //创建一个20M的对象
        Byte[] b = new Byte[20*1024*1024];
    }
}
```

控制台打印的结果：

![](https://img2020.cnblogs.com/blog/1376820/202006/1376820-20200621140003732-177463861.png)



> **stackoverflowError  栈空间溢出**

模拟：

```java
/**
 * OMM 之 java.lang. StackOverflowError 栈空间溢出，栈管运行，每个方法就是一个栈帧，循环调用方法，会出现这种问题
 */
public class StackOverFlowDemo {
    public static void main(String[] args) {
        stackoverflowError();
    }

    private static void stackoverflowError() {
        stackoverflowError();
    }
}

```

控制台结果：

![](https://img2020.cnblogs.com/blog/1376820/202006/1376820-20200621140417992-1117377978.png)



> **GC overhead limit exceeded  GC时间太长引发的异常**

​		GC回收时间过长时会抛出outOfMemroyError，过长的定义是：超过98%的时间用来做GC并且回收不到2%的堆内存。
​		连续多次GC都只回收了不到2%的极端情况下会抛出。假如不抛出GC overhead limit 错误会发生什么信况呢?
​		那就是GC清理的这么点内存很快会再次填满，迫使GC 再次执行。这样就形成恶性循环,
CPU使用率一直是100%， 两GC 却没有任何效果

​	模拟：

```java
/**
 * GC overhead limit exceeded
 * JVM参数设置：8U
 * -Xms10m -Xmx10m -XX:+PrintGCDetails -XX:MaxDirectMemorySize=5m
 */
public class OverheadLimitExceededDemo {
    public static void main(String[] args) {
        int i =0;
        final ArrayList list = new ArrayList();

        while (true){
            list.add(String.valueOf(++i).intern());
        }
    }
}
```

结果：

![](https://img2020.cnblogs.com/blog/1376820/202006/1376820-20200621142949908-336222567.png)



>  **Directbuffer memory  buffer 内存溢出，在**

导致原因：

​		写NIO程序经常使ByteBuffer读取或者写入数据，这是一种基于通道(Channel)与缓冲区(Buffer)的I/0方式，

​	它可以使用Native函数库直接分配堆外内存，然后通过一个存储在Java堆里面的DirectByteBuffer对象作为这块内存的引用进行操作。

​	这样能在一些场景中显著提高性能， 因为避免了在Java堆和Native堆中来回复制数据。
ByteBuffer. allocate(capability) 第一种方式是分配JVM堆内存， 属于GC 营结范围，由于需要拷贝所以速度相对较慢
​	ByteBuffer. allocteDirect(capability)第2种方式是分配操作系统本地内存，不属于GC 管辖范围，由于不需要内存拷贝所以速度相对较快。
​	但如果不断分配本地内存，堆内存很少使用，那么JVM就不需要执行CG, DirectByteBuffer对象们就不会被回收，
​	这时候堆内存充足，但本地内存可能已经使用光了，再次尝试分配本地内存就会出OutOfMemoryError,程序就直接崩溃。

模拟：

```java
/**
 *  java.lang.OutOfMemoryError: Direct buffer memory 演示
 * JVM参数配置：-Xms10m -Xmx10m -XX:+PrintGCDetails  -XX:MaxDirectMemorySize=5m 
 */
public class DirectBufferMemoryDemo {
    public static void main(String[] args) {
        System.out.println("配置的maxDirectMemory: " + (sun.misc.VM.maxDirectMemory() / (double) 1024 / 1024) + "MB");
        //最大5M 申请6M
        ByteBuffer bb = ByteBuffer . allocateDirect(6* 1024*1024);

    }
}

```

结果：

![](https://img2020.cnblogs.com/blog/1376820/202006/1376820-20200621144641116-524115488.png)



> **unable to create new native thread  无法创建线程**

​	高并发请求服务器时,经常出现如下异常:java.Lang.OutOfMemoryError: unable to create new native thread准确的讲native thread 异常与对应的平台有关

导致原因:

​	你的应用创建了太多线程了,一个应用进程创建多个线程,超过系统承裁极限。
你的服务器并不允许你的应用程序创建这么多线程linux系统默认允许单个进程可以创建的线程数是1024个,你的应用创建超过这个数量，就会报java. lang. OutOfMemoryError: unable to create new native thread

解决办法:

​	1.想办法降低你应用程序创建线程的数量，分析应用是否真的需要创建这么多线程如果不是,改代码将线程数量降到最低
​	2.对于有的应用,确实需要创建很多线程,远超过Linux系统的默认1024个线程的限制,可以通过修改Linux服务器配置,扩大linux默认限制

模拟：这个类拷贝到linux下执行

```java
/**
 * java.Lang.OutOfMemoryError: unable to create new native thread
 */
public class UnableCreateNewThreadDemo {
    public static void main(String[] args) {
        for (int i = 1;; i++) {
            System.out.println("i = " + i);
            new Thread(()->{
                try {
                    TimeUnit.SECONDS.sleep(MAX_VALUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            },""+i).start();
        }
    }
}

```



> **java.lang.OutOfMemoryError: Metaspace  元空间溢出**

​	Java 8及之后的版本使用Metaspace来替代永久化。Metaspace.是方法区在Hotspot中的实现，它与持久代最大的区别在于: Metaspace 不在虚拟机内存中而是使用本地内存
也即在java8中classe metadata(the virtual machines internal presentation of Java class), 被存储在叫做Metaspace的native memory

永久代(java8后被原空间Metaspace取代了)存放了以下信息: 
虚拟机加载的类信息
常量池
静态变量
即时编译后的代码

模拟Metaspace空间溢出，我们不断生成类往元空间灌，类占据的空间总是会超过Metaspace指定的空间大小的



```java

/**
 * 异常演示： java.lang.OutOfMemoryError: Metaspace
 * -XX:MetaspaceSize=8m -XX:MaxMetaspaceSize=8m
 */
class OomTest{}
public class MetaspaceDemo {
    public static void main(String[] args) {
        int i = 0;
        try {
            while (true){
                i++;
                final Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(OomTest.class);
                enhancer.setUseCache(false);
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                        return methodProxy.invoke(o,args);
                    }
                });

                enhancer.create();
            }
        }catch (Exception e){
            System.out.println("*****多少次发生异常 " + i);
            e.printStackTrace();
        }
    }
}

```

执行后的截图：

![](https://img2020.cnblogs.com/blog/1376820/202006/1376820-20200621155754612-1374001100.png)