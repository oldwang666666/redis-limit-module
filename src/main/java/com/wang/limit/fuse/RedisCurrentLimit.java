package com.wang.limit.fuse;

import com.wang.limit.intercept.Message;
import com.wang.limit.util.ScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Collections;

/**
 * redis限流实现类
 * Description:
 * Created by longzhang.wang
 * Date: 2019-09-29
 */
public class RedisCurrentLimit {

    private static final Logger logger= LoggerFactory.getLogger(RedisCurrentLimit.class);

    /**
     * 默认为单机模式
     */
    private boolean isCluster = false;
    /**
     * 用于单机模式进行redis连接池管理
     */
    private JedisPool jedisPool = null;
    /**
     * 用于单机模式进行redis连接池管理
     */
    private JedisConnectionFactory jedisConnectionFactory = null;
    /**
     * 获取限流lua脚本
     */
    private final static String script = ScriptUtil.getLuaScript("currentLimit.lua");
    /**
     * 限流标志，如果值为0则被限流
     */
    private final static int LIMIT_FALG = 0;
    /**
     * 默认qps最大值
     */
    private int limitNum = 500;

    private RedisCurrentLimit() {}

    protected RedisCurrentLimit(boolean isCluster, int limitNum, JedisPool jedisPool, JedisConnectionFactory jedisConnectionFactory) {
        this.isCluster = isCluster;
        this.limitNum = limitNum;
        this.jedisPool = jedisPool;
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    /**
     * 限流方法
     * true 限流， false 不限流
     * @param methodName 方法名
     * @return
     */
    public boolean currentLimitHandle(String methodName) {
        return currentLimitHandle(limitNum, methodName);
    }

    /**
     * 限流方法 - 带限制次数
     * true 限流， false 不限流
     * @param limitNum 每秒限制次数
     * @param methodName 方法名
     * @return
     */
    public boolean currentLimitHandle(Integer limitNum, String methodName) {
        //计算失败默认通过
        Long result = -1L;
        //以秒为时间单位,此处的key实际使用需要用方法名 + 时间 用于做方法的唯一识别
        String key = methodName + String.valueOf(System.currentTimeMillis() / 1000);
        if (!isCluster){
            //已测试，可用
            result = this.standAloneCurrentLimitHandle(key, limitNum);
        }else {
            //未测试
            result = this.clusterCurrentLimitHandle(key, limitNum);
        }

        return LIMIT_FALG == result.intValue() ? true : false;
    }

    /**
     * 单机版本限流计算
     * @param key
     * @param limitNum
     * @return
     */
    private Long standAloneCurrentLimitHandle(String key ,int limitNum) {
        Jedis jedis = jedisPool.getResource();
        try {
            Object result = jedis.eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limitNum)));
            return (Long) result;
        } catch (Exception e){
            logger.error(Message.get("message.limit.verification.fail"), e);
        }finally {
            jedis.close();
        }
        return -1L;
    }

    /**
     * 集群版本限流计算
     * @param key
     * @param limitNum
     * @return
     */
    private Long clusterCurrentLimitHandle(String key ,int limitNum) {

        RedisClusterConnection redisClusterConnection = jedisConnectionFactory.getClusterConnection();
        Object conn = redisClusterConnection.getNativeConnection();
        JedisCluster jedisCluster = (JedisCluster)conn;
        try {
            Object result = jedisCluster.eval(script, Collections.singletonList(key), Collections.singletonList(String.valueOf(limitNum)));
            return (Long) result;
        } catch (Exception e){
            logger.error(Message.get("message.limit.verification.fail"), e);
        }finally {
            try {
                jedisCluster.close();
            } catch (IOException e) {
                logger.error(Message.get("message.jedisCluster.close.fail"), e);
            }
        }
        return -1L;
    }
}
