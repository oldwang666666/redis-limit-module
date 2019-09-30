package com.wang.limit.intercept;

import com.wang.limit.fuse.RedisCurrentLimit;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * redis限流切面
 * Description:
 * Created by longzhang.wang
 * Date: 2019-09-29
 */
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class CommonAspect {

    private static Logger logger = LoggerFactory.getLogger(CommonAspect.class);

    @Autowired
    private RedisCurrentLimit redisCurrentLimit;

    @Pointcut("@annotation(com.wang.limit.annotation.AspectLimit)")
    private void check(){}

    @Before("check()")
    public void before(JoinPoint joinPoint) throws Exception {

        if (redisCurrentLimit == null) {
            throw new NullPointerException(Message.get("message.redisCurentLimit.isnull"));
        }

        boolean limit = redisCurrentLimit.currentLimitHandle();
        if (limit) {
            logger.warn(Message.get("message.intercept.request.limited"));
            throw new RuntimeException(Message.get("message.intercept.request.limited")) ;
        }
    }
}
