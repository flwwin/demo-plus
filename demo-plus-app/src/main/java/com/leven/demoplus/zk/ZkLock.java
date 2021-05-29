package com.leven.demoplus.zk;

import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZkLock  implements Lock{

    public static ThreadLocal<CuratorZookeeperClient>  zkClient = null;

    public static String LOCK_NAME = "/lock";

    public static ThreadLocal<String> CURRENT_NODE = new ThreadLocal<>();

    static {
        zkClient.set(new CuratorZookeeperClient("localhost:8080",2000,2000,null,new RetryOneTime(1)));
    }


    @Override
    public boolean tryLock()  {
        String nodeName = LOCK_NAME + "/zk_";
        try {
            CURRENT_NODE.set(zkClient.get().getZooKeeper().create(nodeName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL));

            List<String> children = zkClient.get().getZooKeeper().getChildren(nodeName, false);

            Collections.sort(children);

            // 判断是否获得锁
            String minNode = children.get(0);
            if ((LOCK_NAME +"/"+minNode).equals(CURRENT_NODE.get())){
                // 获取到锁
                return true;
            }
            // 没有获取到锁,监听前一个节点是否存在
            // 获取前一个节点的index
          int i = children.indexOf(CURRENT_NODE.get().substring(CURRENT_NODE.get().lastIndexOf("/") + 1));
            // 获取前一个节点
          String preNodeName = children.get(i);
          // 增加countDownLatch 同步线程
          CountDownLatch countDownLatch = new CountDownLatch(1);
          zkClient.get().getZooKeeper().exists(LOCK_NAME + "/" + preNodeName, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
              // 监听前一个节点，前一个临时节点消失就是代表当前线程为最小的那个节点，可以获取到锁
              if (Event.EventType.NodeDeleted.equals(watchedEvent.getType())){
                countDownLatch.countDown();
              }
            }
          });
           countDownLatch.await();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void lock() {

    }

    @Override
    public void unlock() {
      try {
        zkClient.get().getZooKeeper().delete(CURRENT_NODE.get(),-1);
        CURRENT_NODE.remove();
      } catch (Exception e) {
        e.printStackTrace();
      }finally{
        zkClient.get().close();
      }
    }
}
