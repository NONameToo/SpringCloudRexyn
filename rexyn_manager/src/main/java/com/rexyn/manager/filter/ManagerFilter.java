package com.rexyn.manager.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName fushaokai
 * @Description 网关过滤器
 * @Author Administrator
 * @Date 2019/9/2 0002 15:30
 * @Version 1.0
 **/
@Component
public class ManagerFilter extends ZuulFilter {

    @Autowired
    private JwtUtil jwtUtil;



    /**
     *设置是在请求之前（pre）还是之后(post)执行
     * @return
     */

    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 设置过滤器的执行顺序，当有多个过滤器时
     * 数字越小越先执行
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 设置是否开启过滤器
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * 过滤器要执行的操作
     * 返回任意的Object都代表着继续执行
     * 只有设置setsendzullResponse(false)的时候才不会继续执行
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        /**
         *  我们在这里验证权限，并且转发请求头
         */
        // System.out.println("请求经过了过滤器!");

        // 得到Request上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        // 得到Request域
        HttpServletRequest request = currentContext.getRequest();
        // 如果时内部分发的请求，就放行
        if(request.getMethod().equals("OPTIONS")){
            return null;
        }
        // 如果是登录请求就放行
        if(request.getRequestURI().indexOf("login")>0){
            return null;
        }

        // 得到请求头
        String header = request.getHeader("Authorization");
        // 判断请求头是否为空
        if(header!=null && !"".equals(header)){
            if(header.startsWith("rexyn ")){
                // 取出token进行验证
                String token = header.substring(6);
                // 解析token
                try{
                    Claims claims = jwtUtil.parseJWT(token);
                    String roles = (String)claims.get("roles");
                    if(roles != null && roles.equals("admin")){
                        // 转发请求
                        currentContext.addZuulRequestHeader("Authorization",header);
                        return null;
                    }
                }catch (Exception e){
                    // 捕获到异常不放行
                    e.printStackTrace();
                    currentContext.setSendZuulResponse(false);
                }
            }
        }
        // 如果都没有满足上面的条件进行解析token 那么我们这里就不进行放行
        currentContext.setSendZuulResponse(false);  // 终止运行
        currentContext.setResponseStatusCode(403);
        currentContext.setResponseBody("权限不足");
        currentContext.getResponse().setContentType("text/html;charset=utf-8");
        return null;
    }
}
