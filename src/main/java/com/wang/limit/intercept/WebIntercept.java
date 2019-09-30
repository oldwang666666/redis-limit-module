package com.wang.limit.intercept;

import com.wang.limit.annotation.ControllerLimit;
import com.wang.limit.fuse.RedisCurrentLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * redis限流拦截器
 * Description:
 * Created by longzhang.wang
 * Date: 2019-09-29
 */
@Component
public class WebIntercept extends WebMvcConfigurerAdapter {

    private static Logger logger = LoggerFactory.getLogger(WebIntercept.class);

    @Autowired
    private RedisCurrentLimit redisCurrentLimit;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /**
         * addPathPatterns添加符合规则的路径进入CheckControllerLimitInterceptor方法
         * excludePathPatterns排除符合规则的路径进入CheckControllerLimitInterceptor方法
         */
        registry.addInterceptor(new CheckControllerLimitInterceptor())
                .addPathPatterns("/**");
    }

    private class CheckControllerLimitInterceptor extends HandlerInterceptorAdapter {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                 Object handler) throws Exception {

            if (redisCurrentLimit == null) {
                throw new NullPointerException(Message.get("message.redisCurentLimit.isnull"));
            }

            if (handler instanceof HandlerMethod) {
                HandlerMethod method = (HandlerMethod) handler;

                ControllerLimit annotation = method.getMethodAnnotation(ControllerLimit.class);
                if (annotation == null) {
                    //是否存在ControllerLimit注解
                    return true;
                }

                boolean limit = redisCurrentLimit.currentLimitHandle(annotation.limit());
                if (limit) {
                    /*提示语意义不大，不作对比浪费资源
                    logger.warn(StringUtils.isEmpty(annotation.errorMessage())
                            ? Message.get("message.intercept.request.limited")
                            : annotation.errorMessage());*/
                    logger.warn(annotation.errorMessage());
                    response.sendError(annotation.errorCode(), annotation.errorMessage());
                    return false;
                }
            }

            return true;
        }
    }
}
