#  Spring的源码搭建



**问题一：Kotlin: Language version 1.1 is no longer supported; please, use version 1.2 or greater.**

​	1：这个就是Kotlin语言版本太低了，看看报错的是那个模块在idea中，调整一下编译的等级

![image-20210328132813981.png](https://pic.rmb.bdstatic.com/bjh/dc11f92ef33cabb5fb98dd48477571e7.png)

2：可以设置全局的Kotlin编译等级，然后每个模块设置使用project settings

![image-20210328132745595.png](https://pic.rmb.bdstatic.com/bjh/52f312716f1b66623eda43cec03a4b6a.png)

然后每个模块设置使用project settings

![image-20210328114503454.png](https://pic.rmb.bdstatic.com/bjh/11a80ea6fd25691fca6aaf1102ecdb72.png)