package com.rexyn.msg.util;


import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @ClassName fushaokai
 * @Description 阿里云短信接口调用
 * @Author Administrator
 * @Date 2019/8/14 0014 17:01
 * @Version 1.0
 **/

/*
pom.xml
<dependency>
  <groupId>com.aliyun</groupId>
  <artifactId>aliyun-java-sdk-core</artifactId>
  <version>4.0.3</version>
</dependency>
*/
@Component
public class MessageUtil {

    @Autowired
    private Environment env;

        public void sendMsg(String phoneNumbers,String signName, String templateCode,String param){
            String accessKeyId =env.getProperty("aliyun.sms.accessKeyId");
            String accessKeySecret = env.getProperty("aliyun.sms.accessKeySecret");
            DefaultProfile profile = DefaultProfile.getProfile("default", accessKeyId, accessKeySecret);
            IAcsClient client = new DefaultAcsClient(profile);

            CommonRequest request = new CommonRequest();
            request.setMethod(MethodType.POST);
            request.setDomain("dysmsapi.aliyuncs.com");
            request.setVersion("2017-05-25");
            request.setAction("SendSms");
            request.putQueryParameter("RegionId", "default");
            request.putQueryParameter("PhoneNumbers", phoneNumbers);
            request.putQueryParameter("SignName", signName);
            request.putQueryParameter("TemplateCode", templateCode);
            request.putQueryParameter("TemplateParam", param);
            try {
                CommonResponse response = client.getCommonResponse(request);
                System.out.println(response.getData());
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (ClientException e) {
                e.printStackTrace();
            }
        }
    }
