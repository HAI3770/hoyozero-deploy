package com.hoyozero.deploy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hoyozero.deploy.entity.Server;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServerMapper extends BaseMapper<Server> {
}
