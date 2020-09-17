##  linux操作

1：查看ip  ip  addr

2：查看当前登录用户：pwd

3：切换目录 cd

- . 表示当前目录
- .. 表示当前目录的上一级目录
- -

4：创建文件夹 mkdir

5：创建文件 touch

5：查看文件 cat

6：编辑文件

ifconfig          查看网络情况

ping                测试网络连通

netstat          显示网络状态信息

kill            杀死进程，通过ps 或者top 查看进程号

telnet      是Internet提供的一项基本服务，用来将本地计算机作为远程计算机的终端机使用。

###  vim程序编辑器

vi分为三种模式：一般模式，编辑模式和指令列命令模式 

vi hello.c //进入hello.c文件，此时为一般模式，若hello.c不存在则自动新建 

del为删除光标上字符，

dd为删除一整列，退格键及上下左右键移动光标，在删除操作前要esc退出编辑模式再按dd 或者del 才会删除，不让当做就是字符输入了 

按esc 然后再输入 :wq! 保存并退出。



###  文件打包压缩

常用压缩命令

- `gzip filename`
- `bzip2 filename`
- `tar -czvf filename`



常用的解压命令

- `gzip -d filename.gz`
- `bzip2 -d filename.bz2`
- `tar -xzvf filename.tar.gz`

###  grep命令

grep(global search regular expression)是一个**强大的文本搜索工具**。grep 使用正则表达式搜索文本，并把匹配的行打印出来。

　　格式：`grep [options] PATTERN [FILE...]`

- PATTERN 是查找条件：**可以是普通字符串、可以是正则表达式**，通常用单引号将RE括起来。
- FILE 是要查找的文件，可以是用空格间隔的多个文件，也可是使用Shell的通配符在多个文件中查找PATTERN，省略时表示在标准输入中查找。
- grep命令不会对输入文件进行任何修改或影响，可以使用输出重定向将结果存为文件

例子：

- 在文件 myfile 中查找包含字符串 mystr的行 

```
　　　　grep -n mystr myfile
```

- 显示 myfile 中第一个字符为字母的所有行 

```
　　　　grep  '^[a-zA-Z]'  myfile
```

- 在文件 myfile 中查找首字符不是 # 的行（**即过滤掉注释行**） 

```
　　　　grep -v '^#' myfile
```

- 列出/etc目录（包括子目录）下所有文件内容中包含字符串“root”的文件名

```
　　　　grep -lr root /etc/*
```

###   ps命令

###  管道符

top命令  -p 监控进程id

整机：top  负载

内存 free -m

磁盘 df -h

网络 iostat

cpu  vmstat -n 2 3

 top 





