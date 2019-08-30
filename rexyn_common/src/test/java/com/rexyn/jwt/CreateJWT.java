package com.rexyn.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/16 0016 11:13
 * @Version 1.0
 **/
public class CreateJWT {
    public static void main(String[] args) {
        JwtBuilder jwtBuilder = Jwts.builder()
                .setId("666")                                               // 设置用id
                .setSubject("张三")                                          // 设置用户名
                .setIssuedAt(new Date())                                    // 设置登录时间
                .setExpiration(new Date(new Date().getTime() + 86400000))   //设置一天后过期，过期解析时会抛出一异常
                .signWith(SignatureAlgorithm.HS256,"rexyn")              //设置头部信息（加密的算法，以及secret)
                .claim("role","admin");                              // 自定义属性
        System.out.println(jwtBuilder.compact());                        // 打印密文
    }
}
