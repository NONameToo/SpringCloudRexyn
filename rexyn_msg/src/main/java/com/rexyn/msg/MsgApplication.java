package com.rexyn.msg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @ClassName fushaokai
 * @Description 短信发送模块启动类
 * @Author Administrator
 * @Date 2019/8/13 0013 17:38
 * @Version 1.0
 **/

@SpringBootApplication
@EnableEurekaClient
public class MsgApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsgApplication.class,args);
    }
}
