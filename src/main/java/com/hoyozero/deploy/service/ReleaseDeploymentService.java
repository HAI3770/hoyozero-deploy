package com.hoyozero.deploy.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoyozero.deploy.entity.Release;
import com.hoyozero.deploy.entity.ReleaseStage;
import com.hoyozero.deploy.entity.ReleaseTask;
import com.hoyozero.deploy.entity.Server;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReleaseDeploymentService {
    private final ReleaseService releases;
    private final ProjectServerService bindings;
    private final ServerService servers;
    private final ComposeDeploymentService compose;
    private final com.hoyozero.deploy.mapper.ReleaseStageMapper stageMapper;
    private final com.hoyozero.deploy.mapper.ReleaseTaskMapper taskMapper;

    public ReleaseDeploymentService(ReleaseService releases, ProjectServerService bindings, ServerService servers,
                                    ComposeDeploymentService compose,
                                    com.hoyozero.deploy.mapper.ReleaseStageMapper stageMapper,
                                    com.hoyozero.deploy.mapper.ReleaseTaskMapper taskMapper) {
        this.releases = releases; this.bindings = bindings; this.servers = servers;
        this.compose = compose; this.stageMapper = stageMapper; this.taskMapper = taskMapper;
    }

    @Async("buildExecutor")
    public void deploy(Release release, String image) {
        ReleaseStage deployStage = stageMapper.selectOne(new LambdaQueryWrapper<ReleaseStage>()
                .eq(ReleaseStage::getReleaseId, release.getId()).eq(ReleaseStage::getStageKey, "DEPLOY"));
        ReleaseStage healthStage = stageMapper.selectOne(new LambdaQueryWrapper<ReleaseStage>()
                .eq(ReleaseStage::getReleaseId, release.getId()).eq(ReleaseStage::getStageKey, "HEALTH_CHECK"));
        List<Long> ids = bindings.getProjectServerIds(release.getProjectId());
        if (deployStage != null) { deployStage.setStatus("RUNNING"); deployStage.setStartTime(LocalDateTime.now()); stageMapper.updateById(deployStage); }
        if (ids == null || ids.isEmpty()) { finishFailure(release, deployStage, healthStage, "项目未绑定部署服务器"); return; }
        boolean allSuccess = true;
        for (Long serverId : ids) {
            Server server = servers.getById(serverId);
            ReleaseTask task = new ReleaseTask(); task.setReleaseId(release.getId());
            task.setStageId(deployStage == null ? null : deployStage.getId()); task.setServerId(serverId);
            task.setTaskType("DEPLOY"); task.setStatus("RUNNING"); task.setAttempt(1); task.setStartTime(LocalDateTime.now()); taskMapper.insert(task);
            StringBuilder log = new StringBuilder();
            try {
                if (server == null) throw new IllegalArgumentException("服务器不存在");
                compose.deploy(release.getProjectId(), serverId, image, msg -> log.append(msg));
                task.setStatus("SUCCESS");
            } catch (Exception e) {
                allSuccess = false; task.setStatus("FAILED"); task.setFailureReason(e.getMessage());
            }
            task.setLog(log.toString()); task.setEndTime(LocalDateTime.now()); taskMapper.updateById(task);
        }
        if (!allSuccess) { finishFailure(release, deployStage, healthStage, "至少一台服务器部署失败"); return; }
        if (deployStage != null) { deployStage.setStatus("SUCCESS"); deployStage.setEndTime(LocalDateTime.now()); stageMapper.updateById(deployStage); }
        if (healthStage != null) { healthStage.setStatus("SUCCESS"); healthStage.setStartTime(LocalDateTime.now()); healthStage.setEndTime(LocalDateTime.now()); stageMapper.updateById(healthStage); }
        Release current = releases.getById(release.getId());
        if (current != null) { current.setStatus("SUCCEEDED"); current.setEndTime(LocalDateTime.now()); releases.updateById(current); }
    }

    private void finishFailure(Release release, ReleaseStage deployStage, ReleaseStage healthStage, String reason) {
        if (deployStage != null) { deployStage.setStatus("FAILED"); deployStage.setFailureReason(reason); deployStage.setEndTime(LocalDateTime.now()); stageMapper.updateById(deployStage); }
        if (healthStage != null) { healthStage.setStatus("SKIPPED"); healthStage.setEndTime(LocalDateTime.now()); stageMapper.updateById(healthStage); }
        Release current = releases.getById(release.getId());
        if (current != null) { current.setStatus("FAILED"); current.setFailureReason(reason); current.setEndTime(LocalDateTime.now()); releases.updateById(current); }
    }
}
