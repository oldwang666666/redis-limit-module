package com.wang.limit.annotation;

import java.lang.annotation.*;

/**
 * 控制层拦截注解
 * Description:
 * Created by longzhang.wang
 * Date: 2019-09-29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ControllerLimit {

	/**
     * 错误码500
     * @return
     * code
     */
    int errorCode() default 500;

    /**
     * 错误描述
     * @return
     */
    String errorMessage() default "请求次数超过限制,请稍后重试";

    /**
     * 限流最大值
     * @return
     */
    int limit() default 500;
}
