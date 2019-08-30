package com.rexyn.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 安全配置类
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * .authorizeRequests()   表示开始配置认证拦截
         * .antMatchers("/**")    表示拦截的路径
         * .permitAll()           表示任何权限都可以访问，直接放行
         * .anyRequest().authenticated()  表示任何的请求都必须要认证
         * .and().csrf().disable();    固定写法，表示关闭Spring Security的csrf
         */

        http
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();
    }
}
