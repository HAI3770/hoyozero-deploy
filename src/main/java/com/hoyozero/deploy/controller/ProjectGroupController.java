package com.hoyozero.deploy.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hoyozero.deploy.annotation.OperationLog;
import com.hoyozero.deploy.annotation.RepeatSubmit;
import com.hoyozero.deploy.common.Result;
import com.hoyozero.deploy.entity.Project;
import com.hoyozero.deploy.entity.ProjectGroup;
import com.hoyozero.deploy.service.ProjectGroupService;
import com.hoyozero.deploy.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/project-group")
@SaCheckLogin
public class ProjectGroupController {

    @Autowired
    private ProjectGroupService projectGroupService;

    @Autowired
    private ProjectService projectService;

    @GetMapping("/list")
    public Result<Page<ProjectGroup>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {

        Page<ProjectGroup> page = new Page<>(current, size);
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();

        if (name != null && !name.isEmpty()) {
            wrapper.like(ProjectGroup::getName, name);
        }

        wrapper.orderByDesc(ProjectGroup::getCreateTime);

        Page<ProjectGroup> groupPage = projectGroupService.page(page, wrapper);

        // 填充项目数量
        groupPage.getRecords().forEach(group -> {
            LambdaQueryWrapper<Project> projectWrapper = new LambdaQueryWrapper<>();
            projectWrapper.eq(Project::getGroupId, group.getId());
            long count = projectService.count(projectWrapper);
            group.setProjectCount((int) count);
        });

        return Result.success(groupPage);
    }

    @GetMapping("/{id}")
    public Result<ProjectGroup> getById(@PathVariable Long id) {
        ProjectGroup group = projectGroupService.getById(id);
        return Result.success(group);
    }

    @RepeatSubmit
    @OperationLog(module = "项目组管理", type = "新增", description = "创建项目组")
    @PostMapping
    public Result<String> save(@RequestBody ProjectGroup projectGroup) {
        // 检查项目组名称是否重复
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectGroup::getName, projectGroup.getName());
        if (projectGroupService.count(wrapper) > 0) {
            return Result.error("项目组名称已存在");
        }

        projectGroupService.save(projectGroup);
        return Result.success("添加成功");
    }

    @RepeatSubmit
    @OperationLog(module = "项目组管理", type = "编辑", description = "更新项目组")
    @PutMapping
    public Result<String> update(@RequestBody ProjectGroup projectGroup) {
        // 检查项目组名称是否重复（排除自己）
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectGroup::getName, projectGroup.getName())
               .ne(ProjectGroup::getId, projectGroup.getId());
        if (projectGroupService.count(wrapper) > 0) {
            return Result.error("项目组名称已存在");
        }

        projectGroupService.updateById(projectGroup);
        return Result.success("更新成功");
    }

    @OperationLog(module = "项目组管理", type = "删除", description = "删除项目组")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        // 检查项目组下是否有项目
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getGroupId, id);
        long count = projectService.count(wrapper);
        if (count > 0) {
            return Result.error("该项目组下还有项目，无法删除");
        }

        projectGroupService.removeById(id);
        return Result.success("删除成功");
    }

    @GetMapping("/all")
    public Result<java.util.List<ProjectGroup>> all() {
        LambdaQueryWrapper<ProjectGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ProjectGroup::getCreateTime);
        return Result.success(projectGroupService.list(wrapper));
    }
}
