package com.rexyn.feign.feignImpl;

import com.rexyn.feign.BaseFeign;
import entity.Result;
import entity.StatusCode;
import org.springframework.stereotype.Component;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author Administrator
 * @Date 2019/9/2 0002 11:21
 * @Version 1.0
 **/
@Component
public class BaseFeignImpl implements BaseFeign {
    @Override
    public Result findByid(String labelId) {
        return  new Result(false,StatusCode.ERROR,"调用失败，熔断器触发");
    }
}
