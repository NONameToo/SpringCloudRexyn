package com.rexyn.user.interceptor;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/16 0016 15:47
 * @Version 1.0
 **/
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;
    // 把HandlerInterceptor中的preHandle方法的default改为public
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("请求经过了拦截器");

        // 这里无论如何都放行，具体要怎么操作还是到具体的操作中去判断
        // 这里的拦截器只是对请求头中带有token的令牌进行解析
        String header = request.getHeader("Authorization");
        if(header!=null && !"".equals(header)){
            // 如果带了Authorization请求头就进行解析
            if(header.startsWith("rexyn ")){
                // 得到token
                String token = header.substring(6);
                // 对令牌进行验证
                try{
                    Claims claims = jwtUtil.parseJWT(token);
                    String roles = (String)claims.get("roles");
                    if(roles!=null && roles.equals("admin")){
                        request.setAttribute("admin_claim",token);
                    }
                    if(roles!=null && roles.equals("user")){
                        request.setAttribute("user_claim",token);
                    }
                }catch (Exception e){
                    throw new RuntimeException("错误的令牌");
                }
            }
        }
        return true;
    }
}
