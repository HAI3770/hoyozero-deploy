package com.hoyozero.deploy.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hoyozero.deploy.annotation.OperationLog;
import com.hoyozero.deploy.annotation.RepeatSubmit;
import com.hoyozero.deploy.common.Result;
import com.hoyozero.deploy.entity.Menu;
import com.hoyozero.deploy.entity.Role;
import com.hoyozero.deploy.service.MenuService;
import com.hoyozero.deploy.service.RoleMenuService;
import com.hoyozero.deploy.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private UserRoleService userRoleService;
    
    @Autowired
    private RoleMenuService roleMenuService;
    
    /**
     * 获取当前用户的菜单树
     */
    @GetMapping("/user")
    public Result<List<Menu>> getUserMenus() {
        String userId = StpUtil.getLoginIdAsString();
        List<Role> roles = userRoleService.getUserRoles(Long.parseLong(userId));
        
        List<Long> roleIds = roles.stream()
                .map(Role::getId)
                .collect(Collectors.toList());
        
        List<Menu> menuTree = menuService.getUserMenuTree(roleIds);
        return Result.success(menuTree);
    }
    
    /**
     * 获取所有菜单（树形结构）
     */
    @GetMapping("/tree")
    public Result<List<Menu>> getMenuTree() {
        List<Menu> menuTree = menuService.getAllMenuTree();
        return Result.success(menuTree);
    }
    
    /**
     * 获取菜单列表（分页）
     */
    @GetMapping("/list")
    public Result<Page<Menu>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        
        Page<Menu> page = new Page<>(current, size);
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.trim().isEmpty()) {
            wrapper.like(Menu::getName, name);
        }
        
        wrapper.orderByAsc(Menu::getSortOrder);
        
        return Result.success(menuService.page(page, wrapper));
    }
    
    /**
     * 获取菜单详情
     */
    @GetMapping("/{id}")
    public Result<Menu> getById(@PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }
    
    /**
     * 添加菜单
     */
    @RepeatSubmit
    @OperationLog(module = "菜单管理", type = "新增", description = "添加菜单")
    @PostMapping
    public Result<String> add(@RequestBody Menu menu) {
        menuService.save(menu);
        return Result.success("添加成功");
    }
    
    /**
     * 更新菜单
     */
    @RepeatSubmit
    @OperationLog(module = "菜单管理", type = "编辑", description = "更新菜单")
    @PutMapping
    public Result<String> update(@RequestBody Menu menu) {
        menuService.updateById(menu);
        return Result.success("更新成功");
    }
    
    /**
     * 删除菜单
     */
    @OperationLog(module = "菜单管理", type = "删除", description = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        menuService.removeById(id);
        return Result.success("删除成功");
    }
    
    /**
     * 获取角色的菜单ID列表
     */
    @GetMapping("/role/{roleId}")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        List<Long> menuIds = menuService.getRoleMenuIds(roleId);
        return Result.success(menuIds);
    }
    
    /**
     * 为角色分配菜单
     */
    @RepeatSubmit
    @OperationLog(module = "菜单管理", type = "分配权限", description = "为角色分配菜单权限")
    @PostMapping("/role/{roleId}")
    public Result<String> assignRoleMenus(
            @PathVariable Long roleId,
            @RequestBody Map<String, Object> params) {
        
        @SuppressWarnings("unchecked")
        List<Object> menuIdsObj = (List<Object>) params.get("menuIds");
        
        if (menuIdsObj != null) {
            List<Long> menuIds = menuIdsObj.stream()
                    .map(id -> Long.valueOf(id.toString()))
                    .collect(Collectors.toList());
            
            roleMenuService.assignMenus(roleId, menuIds);
        }
        
        return Result.success("分配成功");
    }
}
