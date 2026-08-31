package com.hoyozero.deploy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hoyozero.deploy.entity.Build;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BuildMapper extends BaseMapper<Build> {
}
