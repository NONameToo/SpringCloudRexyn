package com.rexyn.rabbit.customer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName fushaokai
 * @Description 测试消费者
 * @Author Administrator
 * @Date 2019/8/12 0012 17:38
 * @Version 1.0
 **/

@Component
@RabbitListener(queues = "directTest")
public class Customer1 {

    @RabbitHandler
    public void getMsg(String msg){
        System.out.println("[直接模式] 消费者1---消费消息:" + msg);
    }
}

