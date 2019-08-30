package com.rexyn.user.controller;

import com.rexyn.user.entity.User;
import com.rexyn.user.service.UserService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName fushaokai
 * @Description 用户模块
 * @Author Administrator
 * @Date 2019/8/13 0013 15:53
 * @Version 1.0
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 短信验证码发送
     * @param mobile
     * @return
     */
    @RequestMapping(value = "/sendMsg/{mobile}",method= RequestMethod.POST)
    public Result sendMsg(@PathVariable("mobile") String mobile){
        userService.sendMsg(mobile);
        return new Result(true, StatusCode.OK,"短信发送成功");
    }

    /**
     * 用户注册
     * @param code
     * @return
     */
    @RequestMapping(value = "/regist/{code}",method= RequestMethod.POST)
    public Result regist(@PathVariable("code") String code ,@RequestBody User user){
        // 从redis缓存中拿到这个验证码
        String vCodeRedis = (String)redisTemplate.opsForValue().get("vCode" + user.getMobile());
        if(vCodeRedis.isEmpty()){
            return new Result(false, StatusCode.ERROR,"请先获取验证码");
        }
        if(!vCodeRedis.equals(code)){
            return new Result(false, StatusCode.ERROR,"请输入正确的验证码");
        }
        userService.add(user);
        return new Result(true, StatusCode.OK,"注册成功");
    }

    /**
     * 用户登录
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Result login(@RequestBody User user){
        User loginUser = userService.login(user.getMobile(),user.getPassword());
        if(loginUser==null){
            return new Result(false,StatusCode.ERROR,"登录失败");
        }
        return new Result(true, StatusCode.OK,"登录成功");
    }

    /**
     * 查询全部数据
     * @return
     */
    @RequestMapping(method= RequestMethod.GET)
    public Result findAll(){
        return new Result(true,StatusCode.OK,"查询成功",userService.findAll());
    }

    /**
     * 根据ID查询
     * @param id ID
     * @return
     */
    @RequestMapping(value="/{id}",method= RequestMethod.GET)
    public Result findById(@PathVariable String id){
        return new Result(true,StatusCode.OK,"查询成功",userService.findById(id));
    }

    /**
     * 增加
     * @param user
     */
    @RequestMapping(method=RequestMethod.POST)
    public Result add(@RequestBody User user  ){
        userService.add(user);
        return new Result(true,StatusCode.OK,"增加成功");
    }

    /**
     * 修改
     * @param user
     */
    @RequestMapping(value="/{id}",method= RequestMethod.PUT)
    public Result update(@RequestBody User user, @PathVariable String id ){
        user.setId(id);
        userService.update(user);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /**
     * 删除 必须有admin角色才能删除
     * @param id
     */
    @RequestMapping(value="/{id}",method= RequestMethod.DELETE)
    public Result delete(@PathVariable String id ){
        userService.deleteById(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }
}
