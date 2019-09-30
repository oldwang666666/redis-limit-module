package com.wang.limit.fuse;

import com.wang.limit.intercept.Message;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPool;

/**
 * redis限流实例工厂
 * Description:
 * Created by longzhang.wang
 * Date: 2019-09-29
 */
public class RedisCurrentLimitFactory {

    protected final static int LIMIT_MAX = 50000000;

    /**
     * 创建单机限流对象
     * @param limitNum   默认限流大小
     * @param jedisPool  jedis连接池
     * @return
     * @throws Exception
     */
    public RedisCurrentLimit standAloneInstance(int limitNum, JedisPool jedisPool) throws Exception {

        this.checkMaximum(limitNum);
        return this.buildRedisCurrentLimit(false, limitNum, jedisPool, null);
    }

    /**
     * 创建集群限流对象
     * @param limitNum  默认限流大小
     * @param jedisConnectionFactory  jedis集群工厂
     * @return
     * @throws Exception
     */
    public RedisCurrentLimit clusterInstance(int limitNum, JedisConnectionFactory jedisConnectionFactory) throws Exception {

        this.checkMaximum(limitNum);
        return this.buildRedisCurrentLimit(true, limitNum, null, jedisConnectionFactory);
    }

    private RedisCurrentLimit buildRedisCurrentLimit(boolean isCluster, int limitNum, JedisPool jedisPool
            , JedisConnectionFactory jedisConnectionFactory) {

        return new RedisCurrentLimit(isCluster, limitNum, jedisPool, jedisConnectionFactory);
    }

    /**
     * 校验初始化时，输入的限流数值是否超过最大值
     * @param limitNum
     * @throws Exception
     */
    private void checkMaximum(int limitNum) throws Exception {
        if(limitNum > LIMIT_MAX) {
            throw new Exception(Message.get("message.limitConfig.over.maximum"));
        }
    }
}
