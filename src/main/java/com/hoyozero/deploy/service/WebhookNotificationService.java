package com.hoyozero.deploy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoyozero.deploy.entity.Build;
import com.hoyozero.deploy.entity.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WebhookNotificationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${hoyozero.webhook.callback-url:}")
    private String callbackUrl;

    @Value("${hoyozero.webhook.callback-secret:}")
    private String callbackSecret;

    public void notifyBuildStatus(Build build, Project project) {
        if (build == null || project == null) {
            return;
        }
        if (callbackUrl == null || callbackUrl.trim().isEmpty()) {
            return;
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("projectId", project.getId());
            payload.put("projectName", project.getName());
            payload.put("buildId", build.getId());
            payload.put("status", build.getStatus());
            payload.put("triggerBy", build.getTriggerBy());
            payload.put("startTime", build.getStartTime() == null ? null : build.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            payload.put("endTime", build.getEndTime() == null ? null : build.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            payload.put("duration", build.getDuration());
            payload.put("env", project.getEnv());
            payload.put("branch", project.getBranch());
            payload.put("message", "build status changed");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (callbackSecret != null && !callbackSecret.trim().isEmpty()) {
                headers.set("X-Webhook-Secret", callbackSecret);
            }

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);
            restTemplate.postForEntity(callbackUrl, request, String.class);
            log.info("已发送构建状态回调: projectId={}, buildId={}, status={}", project.getId(), build.getId(), build.getStatus());
        } catch (Exception e) {
            log.warn("发送构建状态回调失败: {}", e.getMessage());
            throw new RuntimeException("发送构建状态回调失败", e);
        }
    }
}
