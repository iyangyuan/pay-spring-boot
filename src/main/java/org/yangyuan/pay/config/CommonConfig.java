package org.yangyuan.pay.config;

import redis.clients.jedis.JedisPool;

/**
 *
 * 公共配置
 *
 * @Auther: yangyuan
 * @Date: 2019/1/3 19:33
 */
public class CommonConfig{
    /**
     * 统一编码
     */
    public static final String UNIFY_CHARSET = "UTF-8";

    /**
     * redis连接池
     */
    private static JedisPool JEDIS_POOL;

    public static void setJedisPool(JedisPool jedisPool){
        JEDIS_POOL = jedisPool;
    }

    public static JedisPool getJedisPool(){
        return JEDIS_POOL;
    }



}
