package com.mmall.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;

/**
 * @Author: zhangyu
 * @Description: Redis连接池
 * @Date: in 2019/9/5 16:55
 */
@Service("redisPoll")
@Slf4j
public class RedisPoll {

    @Resource(name = "shardedJedisPool")
    private ShardedJedisPool shardedJedisPool;

    public ShardedJedis instance() {
        return shardedJedisPool.getResource();
    }

    public void safeClose(ShardedJedis shardedJedis) {
        try {
            if(shardedJedis != null) {
                shardedJedis.close();
            }
        } catch (Exception e){
            RedisPoll.log.error("return redis resource exception ", e);
        }
    }

}