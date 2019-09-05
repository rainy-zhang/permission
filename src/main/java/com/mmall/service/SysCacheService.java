package com.mmall.service;

import com.google.common.base.Joiner;
import com.mmall.common.CacheKeyConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;

/**
 * @Author: zhangyu
 * @Description: 处理缓存的Service
 * @Date: in 2019/9/5 17:01
 */
@Service
@Slf4j
public class SysCacheService {

    @Resource(name = "redisPoll")
    private RedisPoll redisPoll;

    /**
     * 保存缓存
     * @param toSavedValue 要保存的内容
     * @param timeoutSeconds 过期时间
     * @param prefix redis中key的前缀
     */
    public void saveCache(String toSavedValue, int timeoutSeconds, CacheKeyConstants prefix) {
        saveCache(toSavedValue, timeoutSeconds, prefix, null);
    }

    /**
     * 保存缓存
     * @param toSavedValue 要保存的内容
     * @param timeoutSeconds 过期时间
     * @param prefix redis中key的前缀
     * @param keys redis中的key
     */
    public void saveCache(String toSavedValue, int timeoutSeconds, CacheKeyConstants prefix, String... keys) {
        if(toSavedValue == null){
            return;
        }
        ShardedJedis shardedJedis = null;
        try {
            String generateKey = generateCacheKey(prefix, keys);
            shardedJedis = redisPoll.instance();
            shardedJedis.setex(generateKey, timeoutSeconds, toSavedValue);
        }catch (Exception e){
            log.error("save cache exception, prefix:{}, keys:{} ", prefix, keys, e);
        }finally {
            redisPoll.safeClose(shardedJedis);
        }
    }

    /**
     * 获取redis中缓存的内容
     */
    public String getFromCache(CacheKeyConstants prefix, String... keys) {
        ShardedJedis shardedJedis = null;
        String cacheKey = generateCacheKey(prefix, keys);
        try {
            shardedJedis = redisPoll.instance();
            String value = shardedJedis.get(cacheKey);
            return value;
        }catch (Exception e){
            log.error("get from cache exception, prefix:{}, keys:{}", prefix, keys, e);
            return null;
        }finally {
            redisPoll.safeClose(shardedJedis);
        }
    }

    /**
     * 生成key
     */
    private String generateCacheKey(CacheKeyConstants prefix, String... keys) {
        String key = prefix.name();
        if (key != null && key.length() > 0) {
            key += "_" + Joiner.on("_").join(keys);
        }
        return key;
    }

}
