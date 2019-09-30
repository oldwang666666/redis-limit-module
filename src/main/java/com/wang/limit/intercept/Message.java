package com.wang.limit.intercept;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 提示语初始化、获取
 * Description:
 * Created by longzhang.wang
 * Date: 2019-09-29
 */
@Component
public class Message {

    public static Properties props = new Properties();

    private Message() {};

    @Bean
    public void getProperties() {

        try {
//            InputStream in = Message.class.getClassLoader().getResourceAsStream("message.properties");
            InputStreamReader in = new InputStreamReader(Message.class.getClassLoader().getResourceAsStream("message.properties"), "UTF-8");
            props.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String params){
        return props.getProperty(params);
    }

}
