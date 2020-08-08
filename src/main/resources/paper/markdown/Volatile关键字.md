## 关于volatile关键字的解析

> #### 什么是volatile

​	volatile是Java虚拟机提供的轻量级的同步机制

> 三大特性

- 不保证原子性
- 禁止指令重排
- 保证可见性



>####  不保证原子性

对于volatile不保证原子性，我们可以通过一段代码来验证

```java
package javase;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 关键字：volititle
 * 1：保证可见性
 * 2：不保证原子性
 * 3：禁止指令重排
 */
class MyData{
    volatile int num = 0;

    void incrNum() {
        this.num++;
    }
}
public class VolitileDemo {

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
        System.out.println("myData = " + myData.num);
    }

}
//如果volatile保证原子性的话预期结果是20000
//多次运行结果都不是两万，对incrNum进行同步后结果为20000

```

对于volatile不保证原则性，我们可以通过原子类来保证原子性

```java
class MyData{
    volatile AtomicInteger num = new AtomicInteger(0);
   
    void incrNum() {
        this.num.incrementAndGet();
    }
}
```



**对于可见性，需要了解一下java虚拟机的JMM模型**

![](https://img2020.cnblogs.com/blog/1376820/202006/1376820-20200620162540748-96526762.png)

​	各个线程对主内存中共享变量的操作都是各个线程各自拷贝到自己的工作内存进行操作后再写回到主内存中的。
这就可能存在一个线程A修改了共享变量X的值但还未写回主内存时，另外-一个线程B又对主内存中
同一个共享变量X进行操作，但此时A线程工作内存中共享变量x对线程B来说并不可见，
这种工作内存与主内存同步延迟现象就造成了可见性问题

​	通过`volititle` 关键字修饰变量X就可以解决这个问题。



> #### 验证可见性

```java
/**
 * 验证volatitle的可见性
 */
class Mydate{
    int num = 0;
    public void incrNum(){
        this.num = 60;
    }
}
public class VolatileDemo2
{
    public static void main(String[] args) {
        final Mydate mydate = new Mydate();
        new Thread(()->{
            System.out.println(Thread.currentThread().getName()+"\t"+"come in");
            try { TimeUnit.SECONDS.sleep(3); } catch (InterruptedException e) { e.printStackTrace(); }
            //三秒后修改num为60
            mydate.incrNum();
            System.out.println(Thread.currentThread().getName()+"\t"+"update after"+ mydate.num);

        }).start();

        //其他线程修改后通知主内存后 num 为60 不会阻塞
        while (mydate.num == 0){
            //num为0时 一直自旋
        }

        System.out.println("VolatitleDemo2.main\t"+mydate.num);
    }
}
```

​	num在没有volatile修饰的情况下，结果是一直阻塞

![](https://img2020.cnblogs.com/blog/1376820/202006/1376820-20200620171925242-765009923.png)

   加上volatile后，结果为

![](https://img2020.cnblogs.com/blog/1376820/202006/1376820-20200620172024532-647037390.png)

  main线程执行结束



> #### 禁止指令重排

​	计算机在执行程序时，为了提高性能，编译器和处理器的常常会对指令做重排，一般分以下3种

![](https://img2020.cnblogs.com/blog/1376820/202006/1376820-20200620172615455-502971227.png)

单线程环境里面确保程序最终执行结果和代码顺序执行的结果- 致。
处理器在进行重排序时必须要考虑指令之间的数据依赖性
多线程环境中线程交替执行,由于编译器优化重排的存在，两个线程中使用的变量能否保证一致性是无法确定的,结果无法预测

  ```java
public void mySort(){
    intx=11;//语句1.
    int y = 12; //语句2
    x=x+5; //语够3
    y=x*x;//语句4
}
//语句1，2，3，4 可以修改顺序 1234 2134 1324 都是可以的，不影响程序执行结果
//jvm在编译的时候就可能对代码指令的顺序进行重排

  ```

下面的例子：可以看出指令重排可能导致线程的不安全

![](https://img2020.cnblogs.com/blog/1376820/202006/1376820-20200620173522073-2040532504.png)

volatile修饰变量可以禁止指令重排，从而可以避免指令重排导致的线程不安全问题

