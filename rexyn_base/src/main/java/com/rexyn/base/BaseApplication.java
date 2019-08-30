package com.rexyn.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import util.IdGenerator;

/**
 * @ClassName fushaokai
 * @Description base的启动类
 * @Author Administrator
 * @Date 2019/8/9 0009 11:36
 * @Version 1.0
 **/
@SpringBootApplication
@EnableEurekaClient
public class BaseApplication {
    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class,args);
    }

    // 把common模块中的id生成注入进来
    @Bean
    public IdGenerator idGenerator(){
        return new IdGenerator(1,1);
    }

}
