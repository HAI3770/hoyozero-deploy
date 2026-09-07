package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hoyozero.deploy.entity.Build;
import com.hoyozero.deploy.entity.Project;
import com.hoyozero.deploy.entity.Release;
import com.hoyozero.deploy.entity.ReleaseStage;
import com.hoyozero.deploy.entity.ReleaseTask;
import com.hoyozero.deploy.mapper.ReleaseMapper;
import com.hoyozero.deploy.mapper.ReleaseStageMapper;
import com.hoyozero.deploy.mapper.ReleaseTaskMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Service
public class ReleaseService extends ServiceImpl<ReleaseMapper, Release> {
    private final com.hoyozero.deploy.mapper.BuildMapper buildMapper;
    private final ProjectService projects;
    private final ReleaseStageMapper stages;
    private final ReleaseTaskMapper tasks;
    @Autowired @Lazy private BuildExecutorService executor;
    @Autowired @Lazy private ReleaseDeploymentService deploymentService;

    public ReleaseService(com.hoyozero.deploy.mapper.BuildMapper buildMapper, ProjectService projects, ReleaseStageMapper stages, ReleaseTaskMapper tasks) {
        this.buildMapper = buildMapper; this.projects = projects; this.stages = stages; this.tasks = tasks;
    }

    public Release createFromBuild(Long buildId, String triggerBy) {
        Build build = buildMapper.selectById(buildId);
        if (build == null) throw new IllegalArgumentException("构建记录不存在");
        Project project = projects.getById(build.getProjectId());
        if (project == null) throw new IllegalArgumentException("项目不存在");
        Release release = new Release();
        release.setProjectId(project.getId()); release.setBuildId(buildId);
        release.setEnvironment(project.getEnv()); release.setTargetImage(build.getImage());
        release.setStatus("CREATED"); release.setTriggerBy(triggerBy); save(release);
        String[][] definitions = {{"BUILD", "构建", "1"}, {"DEPLOY", "部署", "2"}, {"HEALTH_CHECK", "健康检查", "3"}};
        for (String[] d : definitions) {
            ReleaseStage stage = new ReleaseStage(); stage.setReleaseId(release.getId());
            stage.setStageKey(d[0]); stage.setStageName(d[1]); stage.setStageOrder(Integer.valueOf(d[2])); stage.setStatus("PENDING"); stages.insert(stage);
        }
        return release;
    }

    public List<ReleaseStage> stages(Long releaseId) { return stages.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReleaseStage>().eq(ReleaseStage::getReleaseId, releaseId).orderByAsc(ReleaseStage::getStageOrder)); }
    public List<ReleaseTask> tasks(Long releaseId) { return tasks.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReleaseTask>().eq(ReleaseTask::getReleaseId, releaseId).orderByAsc(ReleaseTask::getId)); }

    public void markBuildFinished(Long buildId, boolean success, String failureReason) {
        Release release = lambdaQuery().eq(Release::getBuildId, buildId).one();
        if (release == null) return;
        if ("SUCCEEDED".equals(release.getStatus()) || "FAILED".equals(release.getStatus()) || "ROLLED_BACK".equals(release.getStatus())) return;
        release.setStatus(success ? "SUCCEEDED" : "FAILED");
        release.setFailureReason(failureReason);
        release.setEndTime(java.time.LocalDateTime.now());
        updateById(release);
        ReleaseStage stage = stages.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReleaseStage>()
                .eq(ReleaseStage::getReleaseId, release.getId()).eq(ReleaseStage::getStageKey, "BUILD"));
        if (stage != null) { stage.setStatus(success ? "SUCCESS" : "FAILED"); stage.setEndTime(java.time.LocalDateTime.now()); stages.updateById(stage); }
        if (success) {
            Project project = projects.getById(release.getProjectId());
            boolean deployed = project != null && Integer.valueOf(1).equals(project.getAutoDeploy());
            for (String key : new String[]{"DEPLOY", "HEALTH_CHECK"}) {
                ReleaseStage next = stages.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReleaseStage>()
                        .eq(ReleaseStage::getReleaseId, release.getId()).eq(ReleaseStage::getStageKey, key));
                if (next != null) { next.setStatus(deployed ? "PENDING" : "SKIPPED"); if (!deployed) { next.setStartTime(java.time.LocalDateTime.now()); next.setEndTime(java.time.LocalDateTime.now()); } stages.updateById(next); }
            }
            if (deployed) { release.setStatus("DEPLOYING"); updateById(release); deploymentService.deploy(release, getById(release.getId()).getTargetImage()); }
        } else {
            ReleaseStage deploy = stages.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReleaseStage>()
                    .eq(ReleaseStage::getReleaseId, release.getId()).eq(ReleaseStage::getStageKey, "DEPLOY"));
            if (deploy != null) { deploy.setStatus("SKIPPED"); stages.updateById(deploy); }
            scheduleAutoRollback(release, failureReason);
        }
    }

    private void scheduleAutoRollback(Release release, String reason) {
        Project project = projects.getById(release.getProjectId());
        if (project == null || !Integer.valueOf(1).equals(project.getAutoDeploy())) return;
        Build target = buildMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Build>()
                .eq(Build::getProjectId, release.getProjectId()).eq(Build::getStatus, "SUCCESS")
                .ne(Build::getId, release.getBuildId()).orderByDesc(Build::getCreateTime).last("LIMIT 1")).stream().findFirst().orElse(null);
        if (target == null) return;
        ReleaseStage stage = stages.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReleaseStage>()
                .eq(ReleaseStage::getReleaseId, release.getId()).eq(ReleaseStage::getStageKey, "DEPLOY"));
        ReleaseTask existing = tasks.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReleaseTask>()
                .eq(ReleaseTask::getReleaseId, release.getId()).eq(ReleaseTask::getTaskType, "ROLLBACK"));
        if (existing != null) return;
        ReleaseTask task = new ReleaseTask(); task.setReleaseId(release.getId()); task.setStageId(stage == null ? null : stage.getId());
        task.setTaskType("ROLLBACK"); task.setStatus("PENDING"); task.setAttempt(0); task.setFailureReason(reason); tasks.insert(task);
        Build rollback = new Build(); rollback.setProjectId(release.getProjectId()); rollback.setStatus("PENDING"); rollback.setTriggerBy("AUTO_ROLLBACK"); buildMapper.insert(rollback);
        task.setRollbackBuildId(rollback.getId());
        task.setStatus("RUNNING"); task.setAttempt(1); task.setStartTime(LocalDateTime.now()); tasks.updateById(task);
        executor.executeRollback(rollback.getId(), target.getId());
    }

    /** Runner 与本地执行器共用的构建完成回调。 */
    public void onBuildFinished(Long buildId, String status, String failureReason, String image) {
        Release release = lambdaQuery().eq(Release::getBuildId, buildId).one();
        if (release == null) return;
        Build build = buildMapper.selectById(buildId);
        String targetImage = image;
        if (build != null && image != null && !image.isBlank() && build.getImageTag() != null && !build.getImageTag().isBlank()) {
            targetImage = image + ":" + build.getImageTag();
        }
        if (targetImage != null && !targetImage.isBlank()) release.setTargetImage(targetImage);
        if (targetImage != null && !targetImage.isBlank()) updateById(release);
        markBuildFinished(buildId, "SUCCESS".equalsIgnoreCase(status), failureReason);
        if (targetImage != null && !targetImage.isBlank()) {
            Release refreshed = getById(release.getId());
            if (refreshed != null) { refreshed.setTargetImage(targetImage); updateById(refreshed); }
        }
    }

    public void finishAutoRollback(Long rollbackBuildId, String status, String log, String reason) {
        Build rollback = buildMapper.selectById(rollbackBuildId);
        if (rollback == null || !"AUTO_ROLLBACK".equals(rollback.getTriggerBy())) return;
        ReleaseTask task = tasks.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReleaseTask>()
                .eq(ReleaseTask::getRollbackBuildId, rollbackBuildId).eq(ReleaseTask::getTaskType, "ROLLBACK").last("LIMIT 1"));
        if (task == null || !"RUNNING".equals(task.getStatus())) return;
        Release release = getById(task.getReleaseId());
        if (release == null) return;
        task.setStatus("SUCCESS".equalsIgnoreCase(status) ? "SUCCESS" : "FAILED"); task.setLog(log); task.setFailureReason(reason); task.setEndTime(LocalDateTime.now()); tasks.updateById(task);
        if ("SUCCESS".equalsIgnoreCase(status)) { release.setStatus("ROLLED_BACK"); release.setEndTime(LocalDateTime.now()); updateById(release); }
    }
}
