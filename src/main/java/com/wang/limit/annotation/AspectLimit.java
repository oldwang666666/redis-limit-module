package com.wang.limit.annotation;

import java.lang.annotation.*;

/**
 * 切面注解
 * Description:
 * Created by longzhang.wang
 * Date: 2019-09-29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AspectLimit {

}
