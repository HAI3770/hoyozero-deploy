package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.Project;
import com.hoyozero.deploy.mapper.ProjectMapper;
import org.springframework.stereotype.Service;

@Service
public class ProjectService extends ServiceImpl<ProjectMapper, Project> {
}
