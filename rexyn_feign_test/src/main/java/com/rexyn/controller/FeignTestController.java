package com.rexyn.controller;

import com.rexyn.feign.BaseFeign;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/29 0029 17:25
 * @Version 1.0
 **/
@RestController
@CrossOrigin
@RequestMapping("/feign")
public class FeignTestController {

    @Autowired
    private BaseFeign baseFeign;

    @RequestMapping(value = "/label/{labelId}", method = RequestMethod.GET)
    public Result findLabelById(@PathVariable("labelId")String labelId){

        Result result = baseFeign.findByid(labelId);
        return result;
    }
}
