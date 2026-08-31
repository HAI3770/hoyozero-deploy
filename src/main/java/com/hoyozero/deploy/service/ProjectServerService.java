package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.ProjectServer;
import com.hoyozero.deploy.mapper.ProjectServerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectServerService extends ServiceImpl<ProjectServerMapper, ProjectServer> {
    
    /**
     * 获取项目的服务器ID列表
     */
    public List<Long> getProjectServerIds(Long projectId) {
        LambdaQueryWrapper<ProjectServer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectServer::getProjectId, projectId);
        return list(wrapper).stream()
                .map(ProjectServer::getServerId)
                .toList();
    }
    
    /**
     * 为项目分配服务器
     */
    @Transactional
    public void assignServers(Long projectId, List<Long> serverIds) {
        // 删除旧的关联
        LambdaQueryWrapper<ProjectServer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectServer::getProjectId, projectId);
        remove(wrapper);
        
        // 添加新的关联
        if (serverIds != null && !serverIds.isEmpty()) {
            for (Long serverId : serverIds) {
                ProjectServer projectServer = new ProjectServer();
                projectServer.setProjectId(projectId);
                projectServer.setServerId(serverId);
                save(projectServer);
            }
        }
    }
}
