package com.rexyn.feign;

import com.rexyn.feign.feignImpl.BaseFeignImpl;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "rexyn-base", fallback = BaseFeignImpl.class)    // 被调用的服务的名字 .yml中配置的spring application name
public interface BaseFeign {

    // 这里直接粘贴被调用方的方法，因为是接口，所以内容不用粘
    // 注意要把被调用方的类上面的url路径带上，不然访问不到
    // 这里的url里面的变量两个一定要写对应，例如labelId 和下面的labelId

    @RequestMapping(value = "/label/{labelId}",method = RequestMethod.GET)
    public Result findByid(@PathVariable("labelId") String labelId);
}


