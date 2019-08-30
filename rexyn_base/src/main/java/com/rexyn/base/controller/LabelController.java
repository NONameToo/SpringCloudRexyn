package com.rexyn.base.controller;

import com.rexyn.base.pojo.Label;
import com.rexyn.base.service.LabelService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/9 0009 14:14
 * @Version 1.0
 **/
@RestController
@CrossOrigin
@RequestMapping("/label")
public class LabelController {

    @Autowired
    private LabelService labelService;


    @RequestMapping(method = RequestMethod.GET)
    public Result findAll(){
        List<Label> allLabel = labelService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",allLabel);
    }

    @RequestMapping(value = "/{labelId}",method = RequestMethod.GET)
    public Result findByid(@PathVariable("labelId") String labelId){
        Label oneLabel = labelService.findById(labelId);
        return new Result(true,StatusCode.OK,"查询成功",oneLabel);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Label label){
        labelService.save(label);
        return new Result(true,StatusCode.OK,"新增成功");

    }

    @RequestMapping(value = "/{labelId}",method = RequestMethod.DELETE)
    public Result delete(@PathVariable("labelId") String labelId){
        labelService.deleteById(labelId);
        return new Result(true,StatusCode.OK,"删除成功");

    }

    @RequestMapping(value = "/{labelId}",method = RequestMethod.PUT)
    public Result update(@PathVariable("labelId") String labelId,@RequestBody Label label){
        label.setId(labelId);
        labelService.update(label);
        return new Result(true,StatusCode.OK,"修改成功");
    }


    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public Result findSearch(@RequestBody Label label ){
        List<Label> list = labelService.findSearch(label);
        return new Result(true,StatusCode.OK,"查询成功",list);

    }
    @RequestMapping(value = "/search/{page}/{size}",method = RequestMethod.POST)
    public Result pageQuery(@RequestBody Label label,@PathVariable Integer page, @PathVariable Integer size){
        Page<Label> pageData = labelService.pageQuery(label,page,size);
        return new Result (true,StatusCode.OK,"查询成功",new PageResult<Label>(pageData.getTotalElements(),pageData.getContent()));
    }

}
