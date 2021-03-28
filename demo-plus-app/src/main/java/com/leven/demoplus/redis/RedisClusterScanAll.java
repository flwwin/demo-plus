package com.leven.demoplus.redis;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.*;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/** 学习JedisCluster的Scan命令使用 */
@Component
@Slf4j
public class RedisClusterScanAll {

  @Resource(name = "bizJedisCluster")
  JedisCluster jedisCluster;

  public void scanAllCache() {
    Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();

    if (CollectionUtils.isEmpty(clusterNodes)) {
      log.warn("clusterNodes is empty");
    }
    List<String> caches = Lists.newArrayList();
    Collection<JedisPool> values = clusterNodes.values();
    for (JedisPool jedisPool : values) {
      Jedis resource = jedisPool.getResource();
      caches.addAll(doScanAll(resource));
    }
    log.debug("redis scan result {}", caches);
  }

  private List<String> doScanAll(Jedis resource) {
    String cursor = ScanParams.SCAN_POINTER_START;
    ScanParams scanParams = new ScanParams().count(100).match("uf*");
    List<String> result = Lists.newArrayList();
    while (true) {
      ScanResult<String> scan = resource.scan(cursor, scanParams);
      cursor = scan.getCursor();
      result = scan.getResult();
      if (String.valueOf(Integer.MAX_VALUE).equals(cursor)) {
        break;
      }
    }
    return result;
  }
}
