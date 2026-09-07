package com.hoyozero.deploy.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hoyozero.deploy.common.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoyozero.deploy.entity.Build;
import com.hoyozero.deploy.entity.Project;
import com.hoyozero.deploy.service.BuildService;
import com.hoyozero.deploy.service.ProjectService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

/** Server-side Dify proxy. The Dify API key never reaches the browser. */
@RestController
@RequestMapping("/api/ai")
public class DifyAiController {
    private final ObjectMapper objectMapper;
    private final BuildService buildService;
    private final ProjectService projectService;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();

    @Value("${hoyozero.dify.base-url:}")
    private String baseUrl;
    @Value("${hoyozero.dify.api-key:}")
    private String apiKey;
    @Value("${hoyozero.dify.app-id:}")
    private String appId;

    public DifyAiController(ObjectMapper objectMapper, BuildService buildService, ProjectService projectService) {
        this.objectMapper = objectMapper;
        this.buildService = buildService;
        this.projectService = projectService;
    }

    @PostMapping("/chat")
    public Result<JsonNode> chat(@RequestBody Map<String, Object> input) throws Exception {
        StpUtil.checkLogin();
        if (baseUrl.isBlank() || apiKey.isBlank()) {
            throw new IllegalStateException("Dify 尚未配置，请设置 DIFY_BASE_URL 和 DIFY_API_KEY");
        }
        ObjectNode body = objectMapper.createObjectNode();
        body.put("query", String.valueOf(input.getOrDefault("query", "")) + "\n\n" + buildContext());
        body.put("response_mode", "streaming");
        body.put("user", "hoyozero-" + StpUtil.getLoginIdAsString());
        body.set("inputs", objectMapper.createObjectNode());
        if (input.get("conversation_id") != null) body.put("conversation_id", String.valueOf(input.get("conversation_id")));

        String endpoint = baseUrl.replaceAll("/+$", "") + "/v1/chat-messages";
        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .timeout(Duration.ofSeconds(300)).header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString())).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            String detail = response.body();
            if (detail.length() > 500) detail = detail.substring(0, 500);
            throw new IllegalStateException("Dify 请求失败，HTTP " + response.statusCode() + ": " + detail);
        }
        ObjectNode result = objectMapper.createObjectNode();
        StringBuilder answer = new StringBuilder();
        String conversationId = "";
        boolean hasAgentMessage = false;
        boolean hasMessage = false;
        for (String line : response.body().split("\\R")) {
            if (!line.startsWith("data:")) continue;
            String payload = line.substring(5).trim();
            if (payload.isEmpty() || "[DONE]".equals(payload)) continue;
            JsonNode event = objectMapper.readTree(payload);
            String eventType = event.path("event").asText();
            if (event.path("conversation_id").isTextual()) conversationId = event.path("conversation_id").asText();
            // Agent App 通常同时发送 agent_message 和 message，只取一种，避免答案重复。
            if ("agent_message".equals(eventType) && !hasAgentMessage) {
                answer.append(event.path("answer").asText(""));
                hasAgentMessage = true;
            } else if ("message".equals(eventType) && !hasAgentMessage && !hasMessage) {
                answer.append(event.path("answer").asText(""));
                hasMessage = true;
            }
        }
        result.put("answer", answer.toString());
        result.put("conversation_id", conversationId);
        return Result.success(result);
    }

    private String buildContext() {
        StringBuilder context = new StringBuilder("以下是 HoyoZero 当前可用的构建系统上下文，请优先据此回答：\n");
        List<Build> builds = buildService.list(new LambdaQueryWrapper<Build>()
                .orderByDesc(Build::getCreateTime).last("LIMIT 5"));
        if (builds.isEmpty()) return context.append("暂无构建记录。\n").toString();
        for (Build build : builds) {
            Project project = build.getProjectId() == null ? null : projectService.getById(build.getProjectId());
            context.append("- 构建ID=").append(build.getId())
                    .append(", 项目=").append(project == null ? build.getProjectId() : project.getName())
                    .append(", 状态=").append(build.getStatus())
                    .append(", 分支=").append(build.getGitBranch())
                    .append(", 构建号=").append(build.getBuildNumber())
                    .append(", 失败原因=").append(build.getFailureReason()).append('\n');
            if (build.getLog() != null && !build.getLog().isBlank()) {
                String log = build.getLog();
                if (log.length() > 6000) log = log.substring(log.length() - 6000);
                context.append("最近日志：\n").append(log).append('\n');
            }
        }
        return context.toString();
    }
}
