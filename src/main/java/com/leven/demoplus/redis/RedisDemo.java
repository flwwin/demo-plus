package com.leven.demoplus.redis;


import redis.clients.jedis.Jedis;

import java.util.*;

public class RedisDemo {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);

        //key
        Set<String> keys = jedis.keys("*"); //查出所有的key，参数可以用过正则匹配key的名称
        for (Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            System.out.println(key);
        }

        System.out.println("jedis.exists====>" + jedis.exists("k2")); //判断一个key是否存在
        System.out.println(jedis.ttl("k1")); //还有多少过期时间 返回-2 不存在 -1没有过期时间
        jedis.append("k1", "myreids"); //后面拼接
        System.out.println(jedis.get("k1"));
        jedis.set("k4", "k4_redis"); //设置一个key
        System.out.println("----------------------------------------");
        jedis.mset("str1", "v1", "str2", "v2", "str3", "v3"); //批量插入
        System.out.println(jedis.mget("str1", "str2", "str3"));//批量获取

        //list
        System.out.println("-------------------list---------------------");

        jedis.lpush("llist","v5","v4","v3","v2","v1"); //从左边push
        jedis.rpush("rlist","v5","v4","v3","v2","v1");//从右边push
        List<String> list1 = jedis.lrange("llist", 0, 2);//-1 is the last element of the list, -2 the penultimate
        List<String> list2 = jedis.lrange("rlist", 0, 2);//-1 is the last element of the list, -2 the penultimate
        System.out.println("list1="+list1);
        System.out.println("list2="+list2);

        System.out.println("-------------------set---------------------");

        //set
        jedis.sadd("orders", "jd001");
        jedis.sadd("orders", "jd003");
        jedis.sadd("orders", "jd002");
        Set<String> set1 = jedis.smembers("orders");//Returns all the members of the set value stored
        System.out.println("set1 = " + set1);

        jedis.srem("orders", "jd002");//Remove the specified members from the set stored
        System.out.println(jedis.smembers("orders"));

        System.out.println("-------------------hash---------------------");

        //hash
        jedis.hset("hash1", "userName", "list");//将哈希表 key 中的字段 field 的值设为 value 。
        System.out.println(jedis.hget("hash1", "userName"));//获取哈希表中的字段

        Map<String, String> map = new HashMap<String, String>();
        map.put("telphone", "13811814763");
        map.put("address", "atguigu");
        map.put("email", "abc@163.com");
        jedis.hmset("hash2", map);//同时将多个 field-value (域-值)对设置到哈希表 key 中。
        List<String> result = jedis.hmget("hash2", "telphone", "email");
        System.out.println("result = " + result);

        System.out.println("-------------------zset---------------------");

        /**
         * zset：
         *
         * Redis 有序集合和集合一样也是string类型元素的集合,且不允许重复的成员。
         *
         * 不同的是每个元素都会关联一个double类型的分数。redis正是通过分数来为集合中的成员进行从小到大的排序。
         *
         * 有序集合的成员是唯一的,但分数(score)却可以重复。
         *
         * 集合是通过哈希表实现的，所以添加，删除，查找的复杂度都是O(1)。
         */
        jedis.zadd("zset01", 60d, "v1");
        jedis.zadd("zset01", 70d, "v2");
        jedis.zadd("zset01", 80d, "v3");
        jedis.zadd("zset01", 90d, "v4");

        Set<String> s1 = jedis.zrange("zset01", 0, -1);
        System.out.println("s1 = " + s1);

    }
}
