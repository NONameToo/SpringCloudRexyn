package com.rexyn.user.service;

import com.rexyn.user.dao.UserDao;
import com.rexyn.user.entity.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdGenerator;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName fushaokai
 * @Description TODO
 * @Author Administrator
 * @Date 2019/8/13 0013 15:57
 * @Version 1.0
 **/
@Service
@Transactional

public class UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private BCryptPasswordEncoder encoder;


    public void sendMsg(String mobile) {
        // 使用lang3包中的工具生成6位数的验证码
        String vCode = RandomStringUtils.randomNumeric(6);
        // 向缓存中放一份
        redisTemplate.opsForValue().set("vCode"+mobile,vCode,300, TimeUnit.SECONDS);

        // 向用户发一分(放到消息队列里面）
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile",mobile);
        map.put("vCode",vCode);
        rabbitTemplate.convertAndSend("msg",map);

        // 控制台显示一份（测试）
        System.out.println("验证码是:"+vCode);
    }

    /**
     * 查询全部列表
     * @return
     */
    public List<User> findAll() {
        return userDao.findAll();
    }



    /**
     * 根据ID查询实体
     * @param id
     * @return
     */
    public User findById(String id) {
        return userDao.findById(id).get();
    }

    /**
     * 增加
     * @param user
     */
    public void add(User user) {
        user.setId( idGenerator.nextId()+"" );
        //密码加密
        user.setPassword(encoder.encode(user.getPassword()));
        user.setFollowcount(0);//关注数
        user.setFanscount(0);//粉丝数
        user.setOnline(0L);//在线时长
        user.setRegdate(new Date());//注册日期
        user.setUpdatedate(new Date());//更新日期
        user.setLastdate(new Date());//最后登陆日期
        userDao.save(user);
    }

    /**
     * 修改
     * @param user
     */
    public void update(User user) {
        userDao.save(user);
    }

    /**
     * 删除 必须有admin角色才能删除
     * @param id
     */
    public void deleteById(String id) {
        String token = (String) request.getAttribute("admin_claim");
        if (token==null || "".equals(token)){
            throw new RuntimeException("权限不足！");
        }
        userDao.deleteById(id);
    }

    public User login(String mobile, String password) {
        User user = userDao.findByMobile(mobile);
        if(user!=null && encoder.matches(password,user.getPassword())){
            return user;
        }
        return null;
    }
}
