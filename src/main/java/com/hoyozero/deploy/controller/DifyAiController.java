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
import com.hoyozero.deploy.service.ServerService;
import com.hoyozero.deploy.service.DeploymentService;
import com.hoyozero.deploy.entity.Server;
import com.hoyozero.deploy.entity.Deployment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

/** Server-side Dify proxy. The Dify API key never reaches the browser. */
@RestController
@RequestMapping("/api/ai")
public class DifyAiController {
    private final ObjectMapper objectMapper;
    private final BuildService buildService;
    private final ProjectService projectService;
    private final ServerService serverService;
    private final DeploymentService deploymentService;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();

    @Value("${hoyozero.dify.base-url:}")
    private String baseUrl;
    @Value("${hoyozero.dify.api-key:}")
    private String apiKey;
    public DifyAiController(ObjectMapper objectMapper, BuildService buildService, ProjectService projectService,
                            ServerService serverService, DeploymentService deploymentService) {
        this.objectMapper = objectMapper;
        this.buildService = buildService;
        this.projectService = projectService;
        this.serverService = serverService;
        this.deploymentService = deploymentService;
    }

    @PostMapping("/chat")
    public Result<JsonNode> chat(@RequestBody Map<String, Object> input) throws Exception {
        StpUtil.checkLogin();
        if (baseUrl.isBlank() || apiKey.isBlank()) {
            throw new IllegalStateException("Dify 尚未配置，请设置 DIFY_BASE_URL 和 DIFY_API_KEY");
        }
        String query = String.valueOf(input.getOrDefault("query", "")).trim();
        if (query.isBlank()) throw new IllegalArgumentException("问题不能为空");
        if (query.length() > 2000) throw new IllegalArgumentException("问题不能超过 2000 个字符");
        ObjectNode body = objectMapper.createObjectNode();
        body.put("query", query + "\n\n" + buildContext());
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
        String answerEventType = null;
        for (String line : response.body().split("\\R")) {
            if (!line.startsWith("data:")) continue;
            String payload = line.substring(5).trim();
            if (payload.isEmpty() || "[DONE]".equals(payload)) continue;
            JsonNode event = objectMapper.readTree(payload);
            String eventType = event.path("event").asText();
            if ("error".equals(eventType) || eventType.endsWith("_error")) {
                String detail = event.path("message").asText("Dify 返回未知错误");
                throw new IllegalStateException("Dify 智能体执行失败：" + detail);
            }
            if (event.path("conversation_id").isTextual()) conversationId = event.path("conversation_id").asText();
            // Agent App 通常同时发送 agent_message 和 message，只取一种，避免答案重复。
            if (("agent_message".equals(eventType) || "message".equals(eventType))
                    && (answerEventType == null || answerEventType.equals(eventType))) {
                answer.append(event.path("answer").asText(""));
                answerEventType = eventType;
            } else if (answerEventType == null && event.path("answer").isTextual()) {
                // 兼容不同 Dify 应用类型返回的事件名，只要事件携带答案即可展示。
                answer.append(event.path("answer").asText());
                answerEventType = eventType;
            }
        }
        result.put("answer", answer.toString());
        result.put("conversation_id", conversationId);
        return Result.success(result);
    }

    /** Read-only health snapshot for the dashboard assistant. */
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        StpUtil.checkLogin();
        List<Build> builds = buildService.list(new LambdaQueryWrapper<Build>()
                .orderByDesc(Build::getCreateTime).last("LIMIT 5"));
        List<Server> servers = serverService.list(new LambdaQueryWrapper<Server>()
                .orderByDesc(Server::getUpdateTime).last("LIMIT 20"));
        List<Deployment> deployments = deploymentService.list(new LambdaQueryWrapper<Deployment>()
                .orderByDesc(Deployment::getDeploymentTime).last("LIMIT 5"));
        Map<String, Object> data = new java.util.LinkedHashMap<>();
        data.put("difyConfigured", !baseUrl.isBlank() && !apiKey.isBlank());
        data.put("buildCount", builds.size());
        data.put("serverCount", servers.size());
        data.put("onlineServerCount", servers.stream().filter(s -> "ONLINE".equalsIgnoreCase(s.getStatus())).count());
        data.put("deploymentCount", deployments.size());
        data.put("latestBuildStatus", builds.isEmpty() ? null : builds.get(0).getStatus());
        data.put("latestDeploymentStatus", deployments.isEmpty() ? null : deployments.get(0).getStatus());
        return Result.success(data);
    }

    private String buildContext() {
        StringBuilder context = new StringBuilder("以下是 hoyozero（宏宇）当前可用的 CI/CD 上下文，仅用于分析和答复；不要假设可以直接执行操作：\n");
        appendServerContext(context);
        appendDeploymentContext(context);
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
                context.append("最近日志（已脱敏）：\n").append(sanitizeLog(log)).append('\n');
            }
        }
        return context.toString();
    }

    private void appendServerContext(StringBuilder context) {
        List<Server> servers = serverService.list(new LambdaQueryWrapper<Server>()
                .orderByDesc(Server::getUpdateTime).last("LIMIT 20"));
        context.append("服务器状态：\n");
        if (servers.isEmpty()) {
            context.append("暂无服务器记录。\n");
            return;
        }
        for (Server server : servers) {
            context.append("- 服务器=").append(server.getName())
                    .append(", 状态=").append(server.getStatus()).append('\n');
        }
    }

    private void appendDeploymentContext(StringBuilder context) {
        List<Deployment> deployments = deploymentService.list(new LambdaQueryWrapper<Deployment>()
                .orderByDesc(Deployment::getDeploymentTime).last("LIMIT 5"));
        context.append("最近部署：\n");
        if (deployments.isEmpty()) {
            context.append("暂无部署记录。\n");
            return;
        }
        for (Deployment deployment : deployments) {
            context.append("- 项目ID=").append(deployment.getProjectId())
                    .append(", 服务器ID=").append(deployment.getServerId())
                    .append(", 状态=").append(deployment.getStatus())
                    .append(", 时间=").append(deployment.getDeploymentTime()).append('\n');
        }
    }

    private String sanitizeLog(String log) {
        String sanitized = log.replaceAll("(?i)(password|passwd|token|api[-_]?key|secret|private[-_]?key)(\\s*[=:]\\s*)[^\\s,;]+", "$1$2[已脱敏]");
        sanitized = sanitized.replaceAll("(?i)Bearer\\s+[A-Za-z0-9._-]+", "Bearer [已脱敏]");
        sanitized = sanitized.replaceAll("https?://[^\\s:@]+:[^\\s@]+@", "https://[已脱敏]@");
        if (sanitized.length() > 6000) sanitized = sanitized.substring(sanitized.length() - 6000);
        return sanitized;
    }
}
