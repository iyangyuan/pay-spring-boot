package org.yangyuan.pay.core.common;

import redis.clients.jedis.JedisPool;

/**
 * Redis资源工厂
 * @Auther: yangyuan
 * @Date: 2019/2/22 18:19
 */
public interface RedisResourceFactory {

    /**
     * 获取redis连接池
     * @return
     */
    JedisPool getJedisPool();

}
