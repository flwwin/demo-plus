package com.leven.demoplus.redis;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

import java.util.Iterator;
import java.util.Set;

/**
 * redis
 */
@SpringBootTest
public class CreateRedisData {

    public static Jedis jedis = new Jedis("127.0.0.1", 6379);

    /**
     * redis得keys命令查出相应得key得set集合
     *
     */
    @Test
    void test01(){
        Set<String> keys = jedis.keys("*");
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
