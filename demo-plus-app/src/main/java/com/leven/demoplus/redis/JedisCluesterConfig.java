package com.leven.demoplus.redis;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class JedisCluesterConfig {

    @Bean
    public JedisCluster bizJedisCluster(){
      return new JedisCluster(parseNodes("127.0.0.1:6380"),parseJedisPoolConfig());
    }

    public Set<HostAndPort> parseNodes(String nodesStr){
        String[] nodeArr = nodesStr.split(",");
        HashSet<HostAndPort> nodeSet = Sets.newHashSet();
        for (String node : nodeArr) {
            String[] split = node.split(":");
            HostAndPort hostAndPort = new HostAndPort(split[0], NumberUtils.toInt(split[1]));
            nodeSet.add(hostAndPort);
        }
        return nodeSet;
    }

    public JedisPoolConfig parseJedisPoolConfig(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(64);
        jedisPoolConfig.setMaxTotal(64);
        jedisPoolConfig.setMinIdle(64);
        return jedisPoolConfig;
    }
}
