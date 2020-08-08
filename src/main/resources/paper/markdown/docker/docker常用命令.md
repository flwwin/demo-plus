##  docker 常用命令

1：启动docker

2：

docker images    //显示已安装的image
docker search xx  //查找image
docker pull [OPTIONS] NAME[:TAG|@DIGEST]    
docker ps -a
docker rm xx  //删除容易
docker exec -it 容器id /bin/bash 进入容器
exit 退出容器

1.启动docker,命令:systemctl start docker
2.验证docker是否启动成功,命令:dockers version
3.重启docker,命令:systemctl restart docker
4.关闭docker,命令:systemctl stop docker END
