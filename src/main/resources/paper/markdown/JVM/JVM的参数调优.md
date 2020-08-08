# JVM的参数及调优
#### jvm的参数类型
    1.标配参数
      java -help
      java -version
    2.x参数（了解）
      -xint 解释执行
      -xcomp 第一次使用就变异成本地代码
      -xmixed 混合模式
    3.xx参数
       Boolean类型
       1.-xx：+或者 -某个值
       2.+表示开启
         -表示关闭
      + KV类型
      + jinfo举例，如何查看当前运行的程序配置

#### 盘点家底查看JVM默认值
    1. 查看一个正在运行的java程序，他的某个JVM的值，具体是多少
       0. JVM启动参数中打开参数打印
          -xx:+PrintGCDetails
       1. jinfo -l 查看java进程列表  ps:jinfo -v
       2. jinfo -flag PrintGCDetails 13632  查看 PrintGCDetails 参数是否打开

####  kv参数的设置值

- jps -l 查看java进程的值
- jinfo -flag MetaspaceSize 4300 查看元空间的值
- 配置元空间大小： -XX：MetaspaceSize = 1024
- jps - l 
- jinfo -flag MetaspaceSize xxx进程号  查看元空间大小



####  查看默认所有配置

- jps -l 找到Java的进程编号

- jinfo -flags  xxx进程号  查看所有的默认参数

   ![](..\..\image\查看JVM默认所有参数.png)

-  comand line 就是用户定义的
- 解释 -xms = -xx:initialHeapSize  -xmx   =  -xx: MaxHeapSize

####  查看所有的默认值

- java -xx: +PrintFlagsInitial

  ![](..\..\image\默认所有启动参数jvm.png)

- := 就是我们修改过得  -xx: + PrintFlagsFinal

  ![](..\..\image\PrintFlagsFinal -version.png)

    ####  JVM 查看修改变更值，修改并查看

![](..\..\image\修改jvm参数并查看.png)

-  -xx: +PrintCommandLineFlags -version



####  常用的JVM参数

-  通过程序查看对内存大小

  ![](..\..\image\通过代码查看堆内存大小.png)

- -xms  等价于：-Xx:InitalHealSize

- -xmx 等价于：-Xx：MaxHeapSize

- -Xss   等价于：-Xx:ThreadStackSize  设置单个线程栈的大小 默认：512K-1024K

- -Xmn  等价于：  设置新生区年轻代 的大小  一般用默认

- -xx:MetaspaceSize  重要  查看GC回收

- -xx：+PrintGCDetails

  - gc日志解析

    ![](..\..\image\gc日志解读.png)

  - full  gc 日志解读

    ![](..\..\image\Full gc 日志解读.png)

- -xx:SuivivoRatio

  - 设置新生代中eden 和s0/s1空间的比例  默认 -xx:SurvivorRatio = 8

    ![](..\..\image\Eden,s1,s2 内存占比.png)

- -xx: newRatio

  ![](..\..\image\新生代和老年代内存占比.png)

- -xx:MaxTenuringThreaShold  对象在新生代可以接受多少次gc次数，取值在0-15之间
