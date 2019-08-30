package com.rexyn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/29 0029 15:59
 * @Version 1.0
 **/
@SpringBootApplication
@EnableEurekaServer

public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class,args);
}
}