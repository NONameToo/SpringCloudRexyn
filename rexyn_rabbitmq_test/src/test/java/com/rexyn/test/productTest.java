package com.rexyn.test;

import com.rexyn.rabbit.RabbitMQTestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName fushaokai
        * @Description 生产者测试
        * @Author Administrator
        * @Date 2019/8/12 0012 17:15
        * @Version 1.0
        **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RabbitMQTestApplication.class)
public class productTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void sendMessage(){
        rabbitTemplate.convertAndSend("directTest","直接模式测试");
    }


    /**
     * 分裂模式测试
     */
    @Test
    public void sendMessage1(){
        rabbitTemplate.convertAndSend("fanOut","","分裂模式测试");
    }


    /**
     * 主题模式测试
     */
    @Test
    public void sendMessage2(){
        rabbitTemplate.convertAndSend("topic","com.rexyn","主题模式测试");
    }
}
