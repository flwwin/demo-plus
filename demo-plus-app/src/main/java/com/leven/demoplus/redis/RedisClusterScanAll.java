package com.leven.demoplus.redis;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class RedisClusterScanAll {

    @Resource(name = "bizJedisCluster")
    JedisCluster jedisCluster;

    public void scanAllCache() {
        Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();

        if (CollectionUtils.isEmpty(clusterNodes)){
            log.warn("clusterNodes is empty");
        }
        List<String> caches = Lists.newArrayList();
        for (String jedisKey : clusterNodes.keySet()) {
            caches.addAll(doScanAll(jedisKey));
        }
        System.out.println("caches = " + caches);
    }

    private List<String> doScanAll(String jedisKey) {
        String cursor= ScanParams.SCAN_POINTER_START;
        ScanParams scanParams = new ScanParams().count(100).match("uf*");
        List<String> result = Lists.newArrayList();
        while (true){
            ScanResult<String> scan = jedisCluster.scan(cursor, scanParams);
            cursor = scan.getCursor();
            result = scan.getResult();
            if (ScanParams.SCAN_POINTER_START.equals(cursor)){
                break;
            }
        }
        return result;
    }
}
