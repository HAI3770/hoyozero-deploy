package com.hoyozero.deploy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hoyozero.deploy.common.Result;
import com.hoyozero.deploy.entity.LoginLog;
import com.hoyozero.deploy.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log/login")
public class LoginLogController {
    
    @Autowired
    private LoginLogService loginLogService;
    
    /**
     * 分页查询登录日志
     */
    @GetMapping("/list")
    public Result<Page<LoginLog>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        
        Page<LoginLog> page = new Page<>(current, size);
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(LoginLog::getUsername, username);
        }
        
        if (status != null) {
            wrapper.eq(LoginLog::getStatus, status);
        }
        
        wrapper.orderByDesc(LoginLog::getLoginTime);
        
        return Result.success(loginLogService.page(page, wrapper));
    }
    
    /**
     * 删除登录日志
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        loginLogService.removeById(id);
        return Result.success("删除成功");
    }
    
    /**
     * 清空登录日志
     */
    @DeleteMapping("/clear")
    public Result<String> clear() {
        loginLogService.remove(new LambdaQueryWrapper<>());
        return Result.success("清空成功");
    }
}
