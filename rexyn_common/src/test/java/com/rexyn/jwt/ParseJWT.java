package com.rexyn.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

import java.text.SimpleDateFormat;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/16 0016 11:27
 * @Version 1.0
 **/
public class ParseJWT {
    public static void main(String[] args) {

        try{
            Claims claims = Jwts.parser().setSigningKey("rexyn")
                    .parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2NjYiLCJzdWIiOiLlvKDkuIkiLCJpYXQiOjE1NjU5MzgxNDgsImV4cCI6MTU2NjAyNDU0OCwicm9sZSI6ImFkbWluIn0.xuiBbo8NwWT2uHrzh_kg1nQmYSMaX0G0YygSUnuUtm0")
                    .getBody();
            System.out.println("用户的id是:"+ claims.getId());
            System.out.println("用户的用户名是:"+ claims.getSubject());
            System.out.println("用户的登录时间是:"+ new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(claims.getIssuedAt()));
            System.out.println("用户的登录过期时间是:"+ new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").format(claims.getExpiration()));
            System.out.println("用户的角色是:"+ claims.get("role"));
        }catch (ExpiredJwtException e){
            System.out.println("登录已经过期，请重新登录");
        }

    }
}
