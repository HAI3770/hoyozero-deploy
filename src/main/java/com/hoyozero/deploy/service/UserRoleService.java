package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.Role;
import com.hoyozero.deploy.entity.User;
import com.hoyozero.deploy.entity.UserRole;
import com.hoyozero.deploy.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserRoleService extends ServiceImpl<UserRoleMapper, UserRole> {
    
    @Autowired
    private RoleService roleService;
    
    /**
     * 获取用户的所有角色
     */
    public List<Role> getUserRoles(Long userId) {
        List<UserRole> userRoles = list(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId));
        
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        
        if (roleIds.isEmpty()) {
            return List.of();
        }
        
        return roleService.listByIds(roleIds);
    }
    
    /**
     * 为用户分配角色
     */
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 删除原有角色
        remove(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId));
        
        // 添加新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<UserRole> userRoles = roleIds.stream()
                    .map(roleId -> {
                        UserRole userRole = new UserRole();
                        userRole.setUserId(userId);
                        userRole.setRoleId(roleId);
                        return userRole;
                    })
                    .collect(Collectors.toList());
            
            saveBatch(userRoles);
        }
    }
    
    /**
     * 检查用户是否有指定角色
     */
    public boolean hasRole(Long userId, String roleCode) {
        List<Role> roles = getUserRoles(userId);
        return roles.stream()
                .anyMatch(role -> role.getCode().equals(roleCode));
    }
}
