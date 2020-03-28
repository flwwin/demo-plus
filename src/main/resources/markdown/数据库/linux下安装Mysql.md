# linux下安装Mysql：RPM安装

####  1.官网下载

####  2.检查当前系统有没有安装过mysql

- 查询命令

  rpm -qa|grep -i mysql

- 删除命令

  rpm -e RPM软件包（前面差出来的名字）

![11](..\..\image\linux下安装mysql\查询是否有安装过mysql.png)

####  安装mysql服务端（注意提示） 注意路径 不要中文，空格

![](..\..\image\linux下安装mysql\安装mysql服务端.png)

注意修改修改root用户密码

####  安装mysql客户端

同服务端

####  查看mysql安装时创建的mysql用户和mysql组

![](..\..\image\linux下安装mysql\查看mysql安装是创建的用户组.png)

####  mysql服务的启+停

![](..\..\image\linux下安装mysql\启动服务.png)

停止：service mysql stop

####  mysql服务启动后，开始连接

1:输入mysql  首次连接成功，不需要账户密码

 所以安装server中的提示修改密码

![](..\..\image\linux下安装mysql\修改密码.png)

####  自启动mysql服务

![](..\..\image\linux下安装mysql\设置开机自启动.png)

####   修改配置文件的位置

####  修改字符集和数据存储路径

####  mysql的安装位置

![](..\..\image\linux下安装mysql\查看安装目录.png)