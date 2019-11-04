package com.rexyn.activiti.controller;

import entity.Result;
import entity.StatusCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName fushaokai
 * @Description Base模块统一异常捕获
 * @Author Administrator
 * @Date 2019/8/12 0012 10:08
 * @Version 1.0
 **/

@RestControllerAdvice
public class BaseControllerExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result exception(Exception e){
        e.printStackTrace();
        return new Result(false, StatusCode.ERROR,e.getMessage());
    }
}
