package com.rexyn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/29 0029 16:52
 * @Version 1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient  // 发现服务
@EnableFeignClients     // 以feign的方式去发现服务
public class FeignTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(FeignTestApplication.class,args);
    }
}
