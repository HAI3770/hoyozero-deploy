package com.hoyozero.deploy.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoyozero.deploy.annotation.OperationLog;
import com.hoyozero.deploy.common.Result;
import com.hoyozero.deploy.entity.Release;
import com.hoyozero.deploy.service.ReleaseService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/release")
public class ReleaseController {
    private final ReleaseService releases;
    public ReleaseController(ReleaseService releases) { this.releases = releases; }

    @OperationLog(module = "发布管理", type = "创建发布", description = "从构建创建发布单")
    @PostMapping
    public Result<Release> create(@RequestBody Map<String, Object> body) {
        Long buildId = Long.valueOf(String.valueOf(body.get("buildId")));
        return Result.success(releases.createFromBuild(buildId, StpUtil.getLoginIdAsString()));
    }

    @GetMapping("/list")
    public Result<Object> list(@RequestParam(required = false) Long projectId) {
        LambdaQueryWrapper<Release> q = new LambdaQueryWrapper<Release>().orderByDesc(Release::getCreateTime);
        if (projectId != null) q.eq(Release::getProjectId, projectId);
        return Result.success(releases.list(q));
    }

    @GetMapping("/{id}")
    public Result<Release> detail(@PathVariable Long id) { return Result.success(releases.getById(id)); }

    @GetMapping("/{id}/stages")
    public Result<Object> stages(@PathVariable Long id) { return Result.success(releases.stages(id)); }

    @GetMapping("/{id}/tasks")
    public Result<Object> tasks(@PathVariable Long id) { return Result.success(releases.tasks(id)); }
}
