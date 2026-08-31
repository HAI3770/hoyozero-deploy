package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.Role;
import com.hoyozero.deploy.mapper.RoleMapper;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {
}
