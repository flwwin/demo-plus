package com.leven.demoplus.redis;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisCluster;


public class jedisTestHelper {

    @Autowired
    JedisCluster bizJedisCluster;

    @Test
    public void test01(){
        System.out.println(bizJedisCluster);
    }
}
