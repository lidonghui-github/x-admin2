package com.lantu.sys.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lantu.sys.entity.User;
import com.lantu.sys.mapper.UserMapper;
import com.lantu.sys.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lidonghui
 * @since 2023-03-16
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Map<String, Object> login(User user) {
        //根据用户名和密码查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        //wrapper.eq(User::getPassword, user.getPassword());
        User loginUser = this.baseMapper.selectOne(wrapper);
        //结果不为空，则生成token，并且用户信息存入redis
        if (loginUser != null && passwordEncoder.matches(user.getPassword(),loginUser.getPassword())) {

            //生成token
            String key = "user:" + UUID.randomUUID();
            //存入redis
            loginUser.setPassword(null);
            redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);
            //返回数据
            Map<String,Object> data = new HashMap<>();
            data.put("token",key);
            return data;

        }
        return null;
    }

   /* @Override
    public Map<String, Object> login(User user) {
        //根据用户名和密码查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        wrapper.eq(User::getPassword, user.getPassword());
        User loginUser = this.baseMapper.selectOne(wrapper);
        //结果不为空，则生成token，并且用户信息存入redis
        if (loginUser != null) {
            //生成token
            String key = "user:" + UUID.randomUUID();
            //存入redis
            loginUser.setPassword(null);
            redisTemplate.opsForValue().set(key,loginUser,30, TimeUnit.MINUTES);
            //返回数据
            Map<String,Object> data = new HashMap<>();
            data.put("token",key);
            return data;

        }
        return null;
    }*/


    @Override
    public Map<String, Object> getUserInfo(String token) {
        //根据token获取用户信息,redis
        Object o = redisTemplate.opsForValue().get(token);
        if(o!=null){
            User loginUser = JSON.parseObject(JSON.toJSONString(o), User.class);
            Map<String, Object> data = new HashMap<>();
            data.put("name",loginUser.getUsername());
            data.put("avatar",loginUser.getAvatar());

            //角色
            List<String> roleList = this.baseMapper.getRoleNameByUserid(loginUser.getId());
            data.put("roles",roleList);

            return data;
        }
        return null;
    }

    @Override
    public void logout(String token) {
        redisTemplate.delete(token);
    }

}
