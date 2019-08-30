package com.rexyn.msg.listener;

import com.aliyuncs.exceptions.ClientException;
import com.rexyn.msg.util.MessageUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.rexyn.msg.util.SmsUtil;

import java.util.HashMap;

/**
 * @ClassName fushaokai
 * @Description 监听短信发送队列
 * @Author Administrator
 * @Date 2019/8/13 0013 17:40
 * @Version 1.0
 **/
@Component
@RabbitListener(queues = "msg")
public class MsgListener {
    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private MessageUtil messageUtil;

    @Value("${aliyun.sms.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.sms.template_code}")
    private String template_code;

    @Value("${aliyun.sms.sign_name}")
    private String sign_name;



    @RabbitHandler
    public void executeSendMsg(HashMap<String,String> map){
        // 取出消息中的手机号和验证码
        String mobile = map.get("mobile");
        String vCode = map.get("vCode");
        System.out.println("customer得到的手机号是:" + mobile);
        System.out.println("customer得到的验证码是:" + vCode);
//        // 旧版阿里云发送短信
//        try {
//            smsUtil.sendSms(mobile,template_code,sign_name,"{\"vCode\":\""+vCode+"\"}");
//        } catch (ClientException e) {
//            e.printStackTrace();
//        }

        // 新版发送短信
        messageUtil.sendMsg(mobile,sign_name,template_code,"{\"vCode\":\""+vCode+"\"}");
    }
}
