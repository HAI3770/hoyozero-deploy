package com.hoyozero.deploy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hoyozero.deploy.annotation.OperationLog;
import com.hoyozero.deploy.annotation.RepeatSubmit;
import com.hoyozero.deploy.common.Result;
import com.hoyozero.deploy.entity.Server;
import com.hoyozero.deploy.service.ProjectServerService;
import com.hoyozero.deploy.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/server")
public class ServerController {
    
    @Autowired
    private ServerService serverService;
    
    @Autowired
    private ProjectServerService projectServerService;
    
    @GetMapping("/list")
    public Result<Page<Server>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        
        Page<Server> page = new Page<>(current, size);
        LambdaQueryWrapper<Server> wrapper = new LambdaQueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like(Server::getName, name);
        }
        
        wrapper.orderByDesc(Server::getCreateTime);
        
        return Result.success(serverService.page(page, wrapper));
    }
    
    @GetMapping("/{id}")
    public Result<Server> getById(@PathVariable Long id) {
        return Result.success(serverService.getById(id));
    }
    
    @RepeatSubmit
    @OperationLog(module = "服务器管理", type = "新增", description = "添加服务器")
    @PostMapping
    public Result<String> save(@RequestBody Server server) {
        serverService.saveOrUpdateServer(server);
        return Result.success("添加成功");
    }
    
    @RepeatSubmit
    @OperationLog(module = "服务器管理", type = "编辑", description = "更新服务器")
    @PutMapping
    public Result<String> update(@RequestBody Server server) {
        serverService.saveOrUpdateServer(server);
        return Result.success("更新成功");
    }
    
    @OperationLog(module = "服务器管理", type = "删除", description = "删除服务器")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        // 检查服务器是否关联了项目
        boolean hasProjects = serverService.hasRelatedProjects(id);
        if (hasProjects) {
            return Result.error("该服务器已关联项目，无法删除");
        }
        
        serverService.removeById(id);
        return Result.success("删除成功");
    }
    
    @PostMapping("/test/{id}")
    public Result<String> testConnection(@PathVariable Long id) {
        Server server = serverService.getById(id);
        if (server == null) {
            return Result.error("服务器不存在");
        }
        
        boolean connected = serverService.testConnection(server);
        return connected ? Result.success("连接成功") : Result.error("连接失败");
    }
    
    @GetMapping("/detail/{id}")
    public Result<Server> getDetail(@PathVariable Long id) {
        Server server = serverService.getById(id);
        if (server == null) {
            return Result.error("服务器不存在");
        }
        return Result.success(server);
    }
    
    /**
     * 获取服务器监控信息
     */
    @GetMapping("/monitor/{id}")
    public Result<?> getMonitorInfo(@PathVariable Long id) {
        Server server = serverService.getById(id);
        if (server == null) {
            return Result.error("服务器不存在");
        }
        
        try {
            return Result.success(serverService.getMonitorInfo(server));
        } catch (Exception e) {
            return Result.error("获取监控信息失败: " + e.getMessage());
        }
    }
}
