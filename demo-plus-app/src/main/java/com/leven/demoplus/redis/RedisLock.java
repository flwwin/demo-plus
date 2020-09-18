package com.leven.demoplus.redis;


import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁
 * 1：互斥性：只能有一个线程可以获取到锁
 * 2：锁超时：在超过规定时间自动释放锁，防止死锁
 * 3：支持阻塞和非阻塞：在一次没有获取锁的时候回重复尝试获得锁，而不是直接返回失败
 * 4：可重入性：在一个锁中可以再去获得锁
 * 5：高可用：在超过我们设定的锁过期的时间，但是我们的线程还没有执行完操作，还没有释放锁，就被过期释放了。所以要提供
 *           可以自动续期的机制
 */

public class RedisLock {

    private StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();

    private ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     * 加锁
     */
    public Boolean lock(String key,Long timeOut,TimeUnit unit) {

        //加个判断  在同一个线程中threadLock中有值得话，就是表明该该线程已经获取过锁了，我们直接返回true ，保证锁的可重入性
        if (threadLocal.get() == null) {
            //新启动一个线程，没个10s给锁加过期时间，防止业务时间超过过期时间
            Thread addThread =  new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        stringRedisTemplate.expire(key,timeOut,unit);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            addThread.start();


            String uuid = addThread.getId() +":"+ UUID.randomUUID().toString();
            threadLocal.set(uuid);

            //在redis中插入一条key  超时时间为30s  把添加过期时间的线程id也作为key  后面我们通过id 在释放锁的时候停止该线程
            Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(key, uuid, timeOut, unit);

            //支持阻塞性  获取锁失败 重复去获取锁
            if (!isLock) {
                while (true) {
                    isLock = stringRedisTemplate.opsForValue().setIfAbsent(key, uuid, timeOut, unit);
                    if (isLock) {
                        break;
                    }
                }
            }

            return isLock;
        } else {
            return true;
        }


    }

    /**
     * 解锁
     */
    public Boolean release(String key) {

        //加一个线程id 判断，防止其他线程业务锁释放掉其他业务的锁
        if (threadLocal.get().equals(stringRedisTemplate.opsForValue().get(key))) {
            //获取延期的线程id  停止该线程
            String s = stringRedisTemplate.opsForValue().get(key);
            String[] split = s.split(":");
            Thread thread = findThread(Long.parseLong(split[0]));
            thread.interrupt();
            //释放锁
            stringRedisTemplate.delete(key);
            //释放线程id
            threadLocal.remove();//防止线程复用  释放锁
        }
        return null;
    }

    /**
     * 通过线程id获得线程
     *
     * @param threadId
     * @return
     */
    public static Thread findThread(long threadId) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while(group != null) {
            Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
            int count = group.enumerate(threads, true);
            for(int i = 0; i < count; i++) {
                if(threadId == threads[i].getId()) {
                    return threads[i];
                }
            }
            group = group.getParent();
        }
        return null;
    }
}
