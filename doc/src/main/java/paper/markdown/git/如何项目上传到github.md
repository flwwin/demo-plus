##  如何把项目上传到github

###  本地git初始化

- `git init`  初始化git环境

- `git add .` 把所有的文件添加到缓存空间



####  本地密钥配置

- 在用户目录下看有没有 `.ssh`  文件

![image-20210227162447538](D:\opensorce\demo-plus\doc\src\main\java\paper\markdown\git\img\image-20210227162447538-1614416883538.png)

没有的话新建一个文件夹

进入`.ssh`文件夹下面，创建这两个文件 ，命令 `ssh-Keygen -t rsa -C "email"`  一直空格就可以了



![image-20210227163217807](.\img\image-20210227163217807.png)



###  进入github,创建一个SSH密钥

![image-20210227163630153](.\img\image-20210227163630153.png)

点击`New SSH key` ,然后把刚才创建的文件 `id_rsa.pub` 里面的公钥加入里面，保存就好了

![img](.\img\format,png)



####  创建github的仓库，获得SSH 连接

![image-20210227164219658](.\img\image-20210227164219658.png)



####  绑定SSH地址

- 在项目文件夹目录下打开git bash 输入命令 `git remote add origin git@github.com:flwwin/spring-framework-reader.git`
- `git pull --rebase origin master`  处理一下冲突
- `git push origin master`  上传代码