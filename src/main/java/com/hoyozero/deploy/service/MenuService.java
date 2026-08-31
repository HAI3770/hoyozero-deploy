package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.Menu;
import com.hoyozero.deploy.entity.RoleMenu;
import com.hoyozero.deploy.mapper.MenuMapper;
import com.hoyozero.deploy.mapper.RoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService extends ServiceImpl<MenuMapper, Menu> {
    
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    
    /**
     * 获取用户菜单树（根据角色）
     */
    public List<Menu> getUserMenuTree(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询角色对应的菜单ID
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>()
                        .in(RoleMenu::getRoleId, roleIds)
        );
        
        if (roleMenus.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .distinct()
                .collect(Collectors.toList());
        
        // 查询菜单列表
        List<Menu> allMenus = list(new LambdaQueryWrapper<Menu>()
                .in(Menu::getId, menuIds)
                .eq(Menu::getVisible, 1)
                .eq(Menu::getMenuType, "MENU")
                .orderByAsc(Menu::getSortOrder));
        
        // 构建树形结构
        return buildMenuTree(allMenus, 0L);
    }
    
    /**
     * 获取所有菜单树
     */
    public List<Menu> getAllMenuTree() {
        List<Menu> allMenus = list(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getVisible, 1)
                .orderByAsc(Menu::getSortOrder));
        
        return buildMenuTree(allMenus, 0L);
    }
    
    /**
     * 构建菜单树
     */
    private List<Menu> buildMenuTree(List<Menu> menus, Long parentId) {
        List<Menu> tree = new ArrayList<>();
        
        for (Menu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                List<Menu> children = buildMenuTree(menus, menu.getId());
                if (!children.isEmpty()) {
                    menu.setChildren(children);
                }
                tree.add(menu);
            }
        }
        
        return tree;
    }
    
    /**
     * 获取角色的菜单ID列表
     */
    public List<Long> getRoleMenuIds(Long roleId) {
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(
                new LambdaQueryWrapper<RoleMenu>()
                        .eq(RoleMenu::getRoleId, roleId)
        );
        
        return roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());
    }
}
