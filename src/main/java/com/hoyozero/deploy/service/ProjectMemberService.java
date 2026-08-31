package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.ProjectMember;
import com.hoyozero.deploy.entity.User;
import com.hoyozero.deploy.mapper.ProjectMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectMemberService extends ServiceImpl<ProjectMemberMapper, ProjectMember> {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取用户有权限的项目ID列表
     */
    public List<Long> getUserProjectIds(Long userId) {
        List<ProjectMember> members = list(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getUserId, userId));
        
        return members.stream()
                .map(ProjectMember::getProjectId)
                .collect(Collectors.toList());
    }
    
    /**
     * 添加项目成员
     */
    public void addMember(Long projectId, Long userId, String roleType) {
        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(userId);
        member.setRoleType(roleType);
        save(member);
    }
    
    /**
     * 检查用户是否有项目权限
     */
    public boolean hasProjectPermission(Long userId, Long projectId) {
        return count(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getUserId, userId)
                .eq(ProjectMember::getProjectId, projectId)) > 0;
    }
    
    /**
     * 检查用户对项目的权限类型
     */
    public String getProjectRole(Long userId, Long projectId) {
        ProjectMember member = getOne(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getUserId, userId)
                .eq(ProjectMember::getProjectId, projectId));
        
        return member != null ? member.getRoleType() : null;
    }
    
    /**
     * 移除项目成员
     */
    @Transactional
    public void removeMember(Long projectId, Long userId) {
        remove(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getProjectId, projectId)
                .eq(ProjectMember::getUserId, userId));
    }
    
    /**
     * 获取项目的所有成员（包含用户信息）
     */
    public List<ProjectMember> getProjectMembers(Long projectId) {
        List<ProjectMember> members = list(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getProjectId, projectId));
        
        // 填充用户信息
        members.forEach(member -> {
            User user = userService.getById(member.getUserId());
            if (user != null) {
                user.setPassword(null);
                member.setUser(user);
            }
        });
        
        return members;
    }
    
    /**
     * 批量分配项目成员
     */
    @Transactional
    public void batchAssignMembers(Long projectId, List<ProjectMember> members) {
        // 先删除非拥有者的成员（保留拥有者）
        remove(new LambdaQueryWrapper<ProjectMember>()
                .eq(ProjectMember::getProjectId, projectId)
                .ne(ProjectMember::getRoleType, "OWNER"));
        
        // 添加新成员
        if (members != null && !members.isEmpty()) {
            members.forEach(member -> {
                member.setProjectId(projectId);
                // 检查是否已存在（避免重复）
                long count = count(new LambdaQueryWrapper<ProjectMember>()
                        .eq(ProjectMember::getProjectId, projectId)
                        .eq(ProjectMember::getUserId, member.getUserId()));
                
                if (count == 0) {
                    save(member);
                }
            });
        }
    }
}
