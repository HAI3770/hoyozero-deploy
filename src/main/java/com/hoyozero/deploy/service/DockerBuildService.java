package com.hoyozero.deploy.service;

import com.hoyozero.deploy.entity.Build;
import com.hoyozero.deploy.entity.Project;
import com.hoyozero.deploy.utils.GitUtils;
import com.hoyozero.deploy.websocket.BuildLogSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/** 独立的容器构建执行器：不依赖 Jenkins。生产环境建议放入 runner 容器运行。 */
@Slf4j
@Service
public class DockerBuildService {
    private final BuildService builds;
    private final ProjectService projects;
    private final ReleaseService releases;
    @Value("${hoyozero.workspace:/workspace}") private String workspace;
    @Value("${hoyozero.build-timeout:600}") private long timeoutSeconds;
    @Value("${RUNNER_NAME:local-runner}") private String runnerName;

    public DockerBuildService(BuildService builds, ProjectService projects, ReleaseService releases) {
        this.builds = builds; this.projects = projects; this.releases = releases;
    }

    public boolean execute(Long id) {
        Build b = builds.getById(id); if (b == null) return true;
        Project p = projects.getById(b.getProjectId());
        if (p == null || p.getImageName() == null || p.getImageName().isBlank()) return false;
        b.setStatus("RUNNING"); b.setStage("准备构建"); b.setRunnerName(runnerName); b.setStartTime(LocalDateTime.now());
        b.setGitUrl(p.getGitUrl()); b.setGitBranch(p.getBranch()); builds.updateById(b);
        StringBuilder all = new StringBuilder(); Path dir = null;
        Consumer<String> out = s -> { all.append(s); if (!s.endsWith("\n")) all.append('\n'); BuildLogSocket.sendMessage(id.toString(), s); };
        try {
            int number = Math.toIntExact(id); b.setBuildNumber(number);
            dir = Files.createDirectories(Path.of(workspace, "build-" + id + "-" + UUID.randomUUID()));
            out.accept("[Git] 拉取 " + p.getGitUrl() + " @ " + p.getBranch() + "\n");
            GitUtils.cloneOrPull(p.getGitUrl(), p.getBranch(), p.getGitUsername(), p.getGitPassword(), dir.toFile(), out);
            String commit = run(dir, out, "git", "rev-parse", "--short=12", "HEAD").trim(); b.setGitCommit(commit);
            String registry = trimSlash(p.getRegistryUrl());
            String image = (registry == null ? "" : registry + "/") + (p.getRegistryNamespace() == null ? "" : p.getRegistryNamespace() + "/") + p.getImageName();
            String tag = expand(p.getImageTagRule(), number, commit, p.getEnv());
            b.setImage(image); b.setImageTag(tag); b.setStage("镜像构建与推送"); b.setStatus("PUSHING"); builds.updateById(b);
            String token = p.getRegistryToken();
            if (token == null || token.isBlank()) token = System.getenv("REGISTRY_TOKEN");
            if (registry != null && p.getRegistryUsername() != null && token != null) login(registry, p.getRegistryUsername(), token, out);
            String dockerfile = p.getDockerfilePath() == null ? "Dockerfile" : p.getDockerfilePath();
            String context = p.getDockerContext() == null ? "." : p.getDockerContext();
            run(dir, out, "docker", "buildx", "build", "--progress=plain", "--platform", p.getBuildPlatform() == null ? "linux/amd64" : p.getBuildPlatform(), "-f", dockerfile, "-t", image + ":" + tag, "-t", image + ":" + p.getEnv() + "-latest", "--push", context);
            String digest = run(dir, out, "docker", "buildx", "imagetools", "inspect", image + ":" + tag, "--format", "{{.Manifest.Digest}}").trim();
            b.setImageDigest(digest); b.setStage("完成"); b.setStatus("SUCCESS"); out.accept("[完成] 镜像: " + image + ":" + tag + "\nDigest: " + digest);
        } catch (Exception e) { b.setStatus("FAILED"); b.setStage("失败"); b.setFailureReason(e.getMessage()); out.accept("[错误] " + e.getMessage()); log.error("docker build {} failed", id, e); }
        finally { b.setEndTime(LocalDateTime.now()); if (b.getStartTime() != null) b.setDuration(ChronoUnit.SECONDS.between(b.getStartTime(), b.getEndTime())); b.setLog(all.toString()); builds.updateById(b); releases.onBuildFinished(id, b.getStatus(), b.getFailureReason(), b.getImage()); if (dir != null) delete(dir); }
        return true;
    }
    private void login(String registry, String user, String token, Consumer<String> out) throws Exception {
        Process q = new ProcessBuilder("docker", "login", registry, "--username", user, "--password-stdin").redirectErrorStream(true).start();
        try (OutputStream os = q.getOutputStream()) { os.write(token.getBytes(StandardCharsets.UTF_8)); os.write('\n'); }
        String result = new String(q.getInputStream().readAllBytes(), StandardCharsets.UTF_8); out.accept("[Registry] " + result);
        if (!q.waitFor(timeoutSeconds, TimeUnit.SECONDS) || q.exitValue() != 0) throw new IOException("Registry 登录失败");
    }
    private String run(Path dir, Consumer<String> out, String... command) throws Exception {
        Process q = new ProcessBuilder(command).directory(dir.toFile()).redirectErrorStream(true).start();
        StringBuilder captured = new StringBuilder();
        try (BufferedReader r = q.inputReader(StandardCharsets.UTF_8)) { String s; while ((s = r.readLine()) != null) { captured.append(s).append('\n'); out.accept(s + "\n"); } }
        if (!q.waitFor(timeoutSeconds, TimeUnit.SECONDS)) { q.destroyForcibly(); throw new IOException("命令超时: " + command[0]); }
        if (q.exitValue() != 0) throw new IOException("命令失败(" + q.exitValue() + "): " + String.join(" ", command));
        return captured.toString();
    }
    private String expand(String rule, int n, String commit, String env) { String r = rule == null ? "build-{buildNumber}" : rule.split(",")[0].trim(); return r.replace("{buildNumber}", String.valueOf(n)).replace("{commit}", commit).replace("{env}", env == null ? "dev" : env); }
    private String trimSlash(String s) { return s == null ? null : s.replaceAll("/+$", ""); }
    private void delete(Path p) { try { Files.walk(p).sorted(Comparator.reverseOrder()).forEach(x -> { try { Files.deleteIfExists(x); } catch (IOException ignored) {} }); } catch (IOException ignored) {} }
}
