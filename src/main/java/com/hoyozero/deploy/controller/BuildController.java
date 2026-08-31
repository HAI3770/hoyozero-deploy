package com.hoyozero.deploy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hoyozero.deploy.annotation.OperationLog;
import com.hoyozero.deploy.common.Result;
import com.hoyozero.deploy.entity.Build;
import com.hoyozero.deploy.entity.Project;
import com.hoyozero.deploy.entity.Server;
import com.hoyozero.deploy.entity.User;
import com.hoyozero.deploy.service.BuildService;
import com.hoyozero.deploy.service.ProjectService;
import com.hoyozero.deploy.service.ServerService;
import com.hoyozero.deploy.service.UserService;
import com.hoyozero.deploy.service.ProjectMemberService;
import com.hoyozero.deploy.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/build")
public class BuildController {
    
    @Autowired
    private BuildService buildService;
    
    @Autowired
    private ProjectService projectService;
    
    @Autowired
    private ServerService serverService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProjectMemberService projectMemberService;
    
    @Autowired
    private UserRoleService userRoleService;
    
    @GetMapping("/list")
    public Result<Page<Build>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long projectId) {
        
        Page<Build> page = new Page<>(current, size);
        LambdaQueryWrapper<Build> wrapper = new LambdaQueryWrapper<>();
        
        if (projectId != null) {
            wrapper.eq(Build::getProjectId, projectId);
        }
        
        wrapper.orderByDesc(Build::getCreateTime);
        
        Page<Build> buildPage = buildService.page(page, wrapper);
        
        // 填充项目名称、环境和触发人名称
        buildPage.getRecords().forEach(build -> {
            // 填充项目名称和环境
            if (build.getProjectId() != null) {
                Project project = projectService.getById(build.getProjectId());
                if (project != null) {
                    build.setProjectName(project.getName());
                    build.setEnv(project.getEnv());
                }
            }
            
            // 填充触发人名称
            if (build.getTriggerBy() != null) {
                try {
                    Long userId = Long.parseLong(build.getTriggerBy());
                    User user = userService.getById(userId);
                    if (user != null) {
                        build.setTriggerByName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                    }
                } catch (NumberFormatException e) {
                    // 如果不是数字，使用原值
                    build.setTriggerByName(build.getTriggerBy());
                }
            }
        });
        
        return Result.success(buildPage);
    }
    
    @GetMapping("/{id}")
    public Result<Build> getById(@PathVariable Long id) {
        Build build = buildService.getById(id);
        
        if (build != null) {
            // 填充项目名称和环境
            if (build.getProjectId() != null) {
                Project project = projectService.getById(build.getProjectId());
                if (project != null) {
                    build.setProjectName(project.getName());
                    build.setEnv(project.getEnv());
                }
            }
            
            // 填充触发人名称
            if (build.getTriggerBy() != null) {
                try {
                    Long userId = Long.parseLong(build.getTriggerBy());
                    User user = userService.getById(userId);
                    if (user != null) {
                        build.setTriggerByName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                    }
                } catch (NumberFormatException e) {
                    build.setTriggerByName(build.getTriggerBy());
                }
            }
        }
        
        return Result.success(build);
    }
    
    @OperationLog(module = "构建管理", type = "触发构建", description = "触发项目构建")
    @PostMapping("/trigger")
    public Result<Map<String, Object>> trigger(@RequestBody Map<String, Long> params) {
        Long projectId = params.get("projectId");

        Long buildId = buildService.triggerBuild(projectId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("buildId", buildId);
        
        return Result.success(data);
    }
    

    @OperationLog(module = "构建管理", type = "删除", description = "删除构建记录")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        buildService.removeById(id);
        return Result.success("删除成功");
    }
    
    /**
     * 版本回退
     */
    @OperationLog(module = "构建管理", type = "版本回退", description = "回退到指定版本")
    @PostMapping("/rollback")
    public Result<Map<String, Object>> rollback(@RequestBody Map<String, Object> params) {
        Long userId = Long.parseLong(cn.dev33.satoken.stp.StpUtil.getLoginIdAsString());
        Long projectId = Long.parseLong(params.get("projectId").toString());
        String backupFileName = (String) params.get("backupFileName");
        
        // 触发回退操作
        Long newBuildId = buildService.rollbackToBackup(projectId, backupFileName);
        
        Map<String, Object> data = new HashMap<>();
        data.put("buildId", newBuildId);
        
        return Result.success(data);
    }
    
    /**
     * 获取项目的备份文件列表
     */
    @GetMapping("/backups/{projectId}")
    public Result<List<Map<String, Object>>> getBackups(@PathVariable Long projectId) {
        List<Map<String, Object>> backups = buildService.getBackupList(projectId);
        return Result.success(backups);
    }
    
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 项目总数
        long projectCount = projectService.count();
        stats.put("projectCount", projectCount);
        
        // 服务器总数
        long serverCount = serverService.count();
        stats.put("serverCount", serverCount);
        
        // 构建总数
        long buildCount = buildService.count();
        stats.put("buildCount", buildCount);
        
        // 成功率计算
        if (buildCount > 0) {
            long successCount = buildService.count(
                new LambdaQueryWrapper<Build>().eq(Build::getStatus, "SUCCESS")
            );
            double successRate = (double) successCount / buildCount * 100;
            stats.put("successRate", Math.round(successRate));
        } else {
            stats.put("successRate", 0);
        }
        
        return Result.success(stats);
    }
    
    /**
     * 获取构建趋势数据（最近7天）
     */
    @GetMapping("/trend")
    public Result<Map<String, Object>> getTrend() {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Integer> successData = new ArrayList<>();
        List<Integer> failedData = new ArrayList<>();
        
        // 最近7天
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            
            LocalDateTime startTime = date.atStartOfDay();
            LocalDateTime endTime = date.plusDays(1).atStartOfDay();
            
            // 统计成功的构建
            long successCount = buildService.count(
                new LambdaQueryWrapper<Build>()
                    .eq(Build::getStatus, "SUCCESS")
                    .ge(Build::getCreateTime, startTime)
                    .lt(Build::getCreateTime, endTime)
            );
            successData.add((int) successCount);
            
            // 统计失败的构建
            long failedCount = buildService.count(
                new LambdaQueryWrapper<Build>()
                    .eq(Build::getStatus, "FAILED")
                    .ge(Build::getCreateTime, startTime)
                    .lt(Build::getCreateTime, endTime)
            );
            failedData.add((int) failedCount);
        }
        
        result.put("dates", dates);
        result.put("successData", successData);
        result.put("failedData", failedData);
        
        return Result.success(result);
    }
    
    /**
     * 获取构建状态分布数据
     */
    @GetMapping("/status-distribution")
    public Result<List<Map<String, Object>>> getStatusDistribution() {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 统计各状态的数量
        String[] statuses = {"SUCCESS", "FAILED", "RUNNING", "PENDING"};
        String[] statusNames = {"成功", "失败", "运行中", "等待中"};
        
        for (int i = 0; i < statuses.length; i++) {
            long count = buildService.count(
                new LambdaQueryWrapper<Build>().eq(Build::getStatus, statuses[i])
            );
            
            if (count > 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", statusNames[i]);
                item.put("value", count);
                result.add(item);
            }
        }
        
        return Result.success(result);
    }
}
