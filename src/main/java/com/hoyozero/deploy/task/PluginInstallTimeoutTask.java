package com.hoyozero.deploy.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoyozero.deploy.entity.PluginInstall;
import com.hoyozero.deploy.service.PluginInstallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 插件安装超时检测任务
 * 定期检查并清理超时的安装任务
 */
@Slf4j
@Component
public class PluginInstallTimeoutTask {
    
    @Autowired
    private PluginInstallService pluginInstallService;
    
    /**
     * 插件安装超时时间(分钟)
     */
    private static final int TIMEOUT_MINUTES = 30;
    
    /**
     * 每5分钟执行一次超时检测
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void checkTimeout() {
        log.info("开始检测插件安装超时任务...");
        
        try {
            // 查询所有安装中或卸载中的记录
            LambdaQueryWrapper<PluginInstall> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(PluginInstall::getStatus, "INSTALLING", "UNINSTALLING");
            List<PluginInstall> installingList = pluginInstallService.list(wrapper);
            
            if (installingList.isEmpty()) {
                log.info("没有进行中的安装任务");
                return;
            }
            
            LocalDateTime now = LocalDateTime.now();
            int timeoutCount = 0;
            
            for (PluginInstall install : installingList) {
                // 计算已经过去的时间
                LocalDateTime createTime = install.getCreateTime();
                if (createTime == null) {
                    continue;
                }
                
                long minutesPassed = java.time.Duration.between(createTime, now).toMinutes();
                
                // 如果超过超时时间,标记为失败
                if (minutesPassed > TIMEOUT_MINUTES) {
                    String originalStatus = install.getStatus();
                    install.setStatus("FAILED");
                    
                    String timeoutLog = String.format(
                        "\n\n========================================\n" +
                        "[系统] 任务超时自动标记为失败\n" +
                        "[系统] 原状态: %s\n" +
                        "[系统] 开始时间: %s\n" +
                        "[系统] 已运行: %d 分钟\n" +
                        "[系统] 超时限制: %d 分钟\n" +
                        "========================================\n",
                        originalStatus, createTime, minutesPassed, TIMEOUT_MINUTES
                    );
                    
                    String currentLog = install.getLog() != null ? install.getLog() : "";
                    install.setLog(currentLog + timeoutLog);
                    
                    pluginInstallService.updateById(install);
                    timeoutCount++;
                    
                    log.warn("插件安装任务超时: installId={}, status={}, 已运行{}分钟", 
                            install.getId(), originalStatus, minutesPassed);
                }
            }
            
            if (timeoutCount > 0) {
                log.info("检测完成,共处理 {} 个超时任务", timeoutCount);
            } else {
                log.info("检测完成,没有超时任务");
            }
            
        } catch (Exception e) {
            log.error("检测插件安装超时任务失败", e);
        }
    }
}
