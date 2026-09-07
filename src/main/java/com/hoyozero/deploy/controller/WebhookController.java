package com.hoyozero.deploy.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoyozero.deploy.common.Result;
import com.hoyozero.deploy.entity.Project;
import com.hoyozero.deploy.service.BuildService;
import com.hoyozero.deploy.service.ProjectService;
import com.hoyozero.deploy.service.RunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BuildService buildService;

    @Autowired
    private RunnerService runnerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${hoyozero.webhook.secret:}")
    private String webhookSecret;

    @PostMapping({"/trigger", "/gitlab"})
    public Result<Map<String, Object>> trigger(@RequestHeader Map<String, String> headers,
                                              @RequestBody String rawBody) {
        try {
            if (!isValidSignature(headers, rawBody)) {
                return Result.error("webhook 签名校验失败");
            }

            Map<String, Object> payload = objectMapper.readValue(rawBody, new TypeReference<Map<String, Object>>() {});
            String source = detectSource(headers, payload);
            String projectName = resolveProjectName(payload, source);
            String branch = runnerService.resolveBranch(payload);

            if (projectName == null || projectName.trim().isEmpty()) {
                return Result.error("无法从 webhook 中识别项目名称");
            }

            Project project = resolveProject(projectName, payload, source);
            if (project == null) {
                return Result.error("未找到对应项目: " + projectName);
            }
            if ("gitlab".equals(source) && project.getWebhookToken() != null && !project.getWebhookToken().isBlank()) {
                String token = headerIgnoreCase(headers, "X-Gitlab-Token");
                if (token == null || !constantTimeEquals(token, project.getWebhookToken())) {
                    return Result.error(401, "GitLab Webhook Token 校验失败");
                }
            }

            if (branch != null && !runnerService.matchesBranch(project.getBranch(), branch)) {
                return Result.success(Map.of(
                        "message", "分支未匹配，忽略 webhook",
                        "projectId", project.getId(),
                        "source", source,
                        "branch", branch
                ));
            }

            String sha = payload.get("checkout_sha") == null ? null : payload.get("checkout_sha").toString();
            if (sha != null && buildService.lambdaQuery().eq(com.hoyozero.deploy.entity.Build::getProjectId, project.getId()).eq(com.hoyozero.deploy.entity.Build::getGitCommit, sha).count() > 0) {
                return Result.success(Map.of("message", "相同 Commit 已触发过构建", "projectId", project.getId()));
            }

            Long buildId = buildService.triggerBuild(project.getId());
            return Result.success(Map.of(
                    "buildId", buildId,
                    "projectId", project.getId(),
                    "source", source,
                    "branch", branch,
                    "message", "构建已触发"
            ));
        } catch (Exception e) {
            return Result.error("webhook 处理失败: " + e.getMessage());
        }
    }

    private boolean isValidSignature(Map<String, String> headers, String rawBody) {
        if (webhookSecret == null || webhookSecret.trim().isEmpty()) {
            return true;
        }

        String source = detectSource(headers, null);
        String signature = null;
        if ("github".equals(source)) {
            signature = headerIgnoreCase(headers, "X-Hub-Signature-256");
            if (signature != null && signature.startsWith("sha256=")) {
                return constantTimeEquals(signature.substring(7), toHex(hmacSha256(rawBody, webhookSecret)));
            }
            signature = headerIgnoreCase(headers, "X-Hub-Signature");
            if (signature != null && signature.startsWith("sha1=")) {
                return constantTimeEquals(signature.substring(5), toHex(hmacSha1(rawBody, webhookSecret)));
            }
        }
        if ("gitlab".equals(source)) {
            signature = headerIgnoreCase(headers, "X-Gitlab-Token");
            if (signature != null) {
                return constantTimeEquals(signature, webhookSecret);
            }
        }
        if ("gitee".equals(source)) {
            signature = headerIgnoreCase(headers, "X-Gitee-Token");
            if (signature != null) {
                return constantTimeEquals(signature, webhookSecret);
            }
            String timestamp = headerIgnoreCase(headers, "X-Gitee-Timestamp");
            if (timestamp != null) {
                String raw = timestamp + "\n" + rawBody;
                return constantTimeEquals(toBase64(hmacSha256(raw, webhookSecret)), signature);
            }
        }

        return true;
    }

    private String headerIgnoreCase(Map<String, String> headers, String name) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey())) return entry.getValue();
        }
        return null;
    }

    private String detectSource(Map<String, String> headers, Map<String, Object> payload) {
        if (headers != null) {
            if (headers.containsKey("X-Gitlab-Event") || headers.containsKey("X-Gitlab-Token")) {
                return "gitlab";
            }
            if (headers.containsKey("X-Gitee-Event") || headers.containsKey("X-Gitee-Token") || headers.containsKey("X-Gitee-Timestamp")) {
                return "gitee";
            }
            if (headers.containsKey("X-GitHub-Event")) {
                return "github";
            }
        }
        if (payload == null) {
            return "unknown";
        }
        Object repository = payload.get("repository");
        Object project = payload.get("project");
        if (repository != null || project != null) {
            return "github";
        }
        if (payload.containsKey("object_kind") || payload.containsKey("project") && payload.get("project") instanceof Map) {
            return "gitlab";
        }
        return "unknown";
    }

    private String resolveProjectName(Map<String, Object> payload, String source) {
        Object projectName = payload.get("projectName");
        if (projectName != null && !projectName.toString().trim().isEmpty()) {
            return projectName.toString();
        }

        if ("github".equals(source)) {
            Map<String, Object> repo = asMap(payload.get("repository"));
            if (repo != null) {
                Object fullName = repo.get("full_name");
                if (fullName != null) {
                    return fullName.toString();
                }
            }
        }

        if ("gitlab".equals(source)) {
            Map<String, Object> project = asMap(payload.get("project"));
            if (project != null) {
                Object pathWithNamespace = project.get("path_with_namespace");
                if (pathWithNamespace != null) {
                    return pathWithNamespace.toString();
                }
                Object name = project.get("name");
                if (name != null) {
                    return name.toString();
                }
            }
        }

        if ("gitee".equals(source)) {
            Map<String, Object> repo = asMap(payload.get("repository"));
            if (repo != null) {
                Object fullName = repo.get("full_name");
                if (fullName != null) {
                    return fullName.toString();
                }
                Object name = repo.get("name");
                if (name != null) {
                    return name.toString();
                }
            }
        }

        return null;
    }

    private Project resolveProject(String projectName, Map<String, Object> payload, String source) {
        String normalized = projectName.trim();

        Project project = projectService.lambdaQuery()
                .eq(Project::getName, normalized)
                .one();
        if (project != null) {
            return project;
        }

        String repoName = null;
        if ("github".equals(source) || "gitee".equals(source)) {
            Map<String, Object> repo = asMap(payload.get("repository"));
            if (repo != null) {
                Object fullName = repo.get("full_name");
                if (fullName != null) {
                    repoName = fullName.toString();
                }
            }
        }
        if (repoName != null && !repoName.isEmpty()) {
            return projectService.lambdaQuery()
                    .like(Project::getGitUrl, repoName)
                    .one();
        }

        if ("gitlab".equals(source)) {
            Map<String, Object> projectInfo = asMap(payload.get("project"));
            if (projectInfo != null) {
                Object path = projectInfo.get("path_with_namespace");
                if (path != null) {
                    return projectService.lambdaQuery()
                            .like(Project::getGitUrl, path.toString())
                            .one();
                }
            }
        }

        return project;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }

    private boolean constantTimeEquals(String expected, String actual) {
        if (expected == null || actual == null) {
            return false;
        }
        if (expected.length() != actual.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < expected.length(); i++) {
            result |= expected.charAt(i) ^ actual.charAt(i);
        }
        return result == 0;
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format(Locale.ROOT, "%02x", b));
        }
        return sb.toString();
    }

    private byte[] hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("HMAC-SHA256 计算失败", e);
        }
    }

    private byte[] hmacSha1(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));
            return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("HMAC-SHA1 计算失败", e);
        }
    }

    private String toBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
