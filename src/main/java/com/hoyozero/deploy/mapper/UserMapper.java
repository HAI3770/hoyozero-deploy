package com.hoyozero.deploy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hoyozero.deploy.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
