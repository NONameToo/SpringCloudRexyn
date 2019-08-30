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
@RabbitListener(queues = "topic3")
public class Customer6 {

    @RabbitHandler
    public void getMsg(String msg){
        System.out.println("[主题模式] 消费者6---消费消息:" + msg);
    }
}

