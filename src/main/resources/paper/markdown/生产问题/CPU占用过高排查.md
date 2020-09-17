#  cpu占用过高排查：

####  1.先用top命令找出CPU占用过高的进程

-load average 系统的负载情况

按数字键 1 可以看每个逻辑cpu的负载情况

![](..\..\image\CPU占用过高\top命令查看cpu占用.png)

####  2. ps -ef |grep java|grep -v grep或者jps 命令找出那个进程的问题

#### 3.定位到具体的线程或者代码,命令： ps -mp 进程号 -o Thread,tid,time,找出一个占用率最高的线程

![](..\..\image\CPU占用过高\定位到线程.png)

####  4.将需要的线程ID转换成16进制格式（英文小写格式），可以通过计算器转换

####  5.jistack 进程ID | grep tid（16进制的线程ID小写英文）

![](..\..\image\CPU占用过高\jstack 定位到代码.png)

