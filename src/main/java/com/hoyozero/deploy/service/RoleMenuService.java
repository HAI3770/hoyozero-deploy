package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.RoleMenu;
import com.hoyozero.deploy.mapper.RoleMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleMenuService extends ServiceImpl<RoleMenuMapper, RoleMenu> {
    
    /**
     * 为角色分配菜单
     */
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 删除原有菜单
        remove(new LambdaQueryWrapper<RoleMenu>()
                .eq(RoleMenu::getRoleId, roleId));
        
        // 添加新菜单
        if (menuIds != null && !menuIds.isEmpty()) {
            List<RoleMenu> roleMenus = menuIds.stream()
                    .map(menuId -> {
                        RoleMenu roleMenu = new RoleMenu();
                        roleMenu.setRoleId(roleId);
                        roleMenu.setMenuId(menuId);
                        return roleMenu;
                    })
                    .collect(Collectors.toList());
            
            saveBatch(roleMenus);
        }
    }
}
