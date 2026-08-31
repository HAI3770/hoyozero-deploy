package com.hoyozero.deploy.service;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.User;
import com.hoyozero.deploy.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    public User login(String username, String password) {
        String md5Password = MD5.create().digestHex(password);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username)
               .eq(User::getPassword, md5Password);
        return this.getOne(wrapper);
    }
}
