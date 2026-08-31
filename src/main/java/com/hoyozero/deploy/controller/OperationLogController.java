package com.hoyozero.deploy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hoyozero.deploy.common.Result;
import com.hoyozero.deploy.entity.OperationLog;
import com.hoyozero.deploy.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log/operation")
public class OperationLogController {
    
    @Autowired
    private OperationLogService operationLogService;
    
    /**
     * 分页查询操作日志
     */
    @GetMapping("/list")
    public Result<Page<OperationLog>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Integer status) {
        
        Page<OperationLog> page = new Page<>(current, size);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        if (username != null && !username.trim().isEmpty()) {
            wrapper.like(OperationLog::getUsername, username);
        }
        
        if (module != null && !module.trim().isEmpty()) {
            wrapper.like(OperationLog::getModule, module);
        }
        
        if (status != null) {
            wrapper.eq(OperationLog::getStatus, status);
        }
        
        wrapper.orderByDesc(OperationLog::getOperationTime);
        
        return Result.success(operationLogService.page(page, wrapper));
    }
    
    /**
     * 删除操作日志
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        operationLogService.removeById(id);
        return Result.success("删除成功");
    }
    
    /**
     * 清空操作日志
     */
    @DeleteMapping("/clear")
    public Result<String> clear() {
        operationLogService.remove(new LambdaQueryWrapper<>());
        return Result.success("清空成功");
    }
}
