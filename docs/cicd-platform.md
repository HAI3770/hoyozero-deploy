# HoyoZero 独立 CI/CD

本版本不调用 Jenkins。构建由 `DockerBuildService` 执行，流程为：

`PENDING -> RUNNING -> PUSHING -> SUCCESS/FAILED`

执行器为每个构建创建 `/workspace/build-{id}-{uuid}`，通过 JGit 拉取指定分支，再执行：

```text
docker login <registry> --username <user> --password-stdin
docker buildx build --progress=plain --platform linux/amd64 -f <Dockerfile> \
  -t <image>:<tag> -t <image>:<env>-latest --push <context>
docker buildx imagetools inspect <image>:<tag>
```

Registry 凭据优先使用项目配置中的 Token；没有配置时读取 `REGISTRY_TOKEN` 环境变量。生产环境应将 Token 放到 Secret/环境变量，并启用 Docker Socket 访问审计。当前 Docker Socket 方案等价于授予构建服务宿主机 Docker 控制权，建议把 runner 放在隔离节点，限制工作目录、项目权限和并发数。

## Compose

```bash
export HOYOZERO_DB_ROOT_PASSWORD='通过 Secret 注入'
docker compose up -d --build
docker compose ps
```

`backend` 需要 Docker Socket 执行构建；`runner` 是隔离的 Docker CLI/BuildKit 节点占位服务，后续可将队列消费协议迁移到该容器。构建工作目录挂载到 `/data/hoyozero-deploy/workspace`，任务结束自动清理。

## 项目配置字段

创建/编辑项目时传入 `dockerfilePath`、`dockerContext`、`registryUrl`、`registryUsername`、`registryToken`、`registryNamespace`、`imageName`、`imageTagRule`、`buildPlatform`、`autoPush`、`webhookToken`、`deployEnabled`、`composePath`、`composeService` 和 `healthCheckUrl`。

`imageTagRule` 支持逗号分隔规则，例如 `build-{buildNumber},git-{commit},latest,{env}-latest`；当前构建记录保存第一条精确 Tag，并始终推送环境 Tag。精确回滚必须使用 `build-{buildNumber}` 或 Digest，不能依赖 `latest`。

## API

- `POST /api/build/trigger`：创建 PENDING 构建并异步执行
- `GET /api/build/list`、`GET /api/build/{id}`：构建记录、日志、Digest
- `POST /api/webhook/gitlab`：GitLab Push/Tag Webhook（兼容 `/api/webhook/trigger`）
- `GET /ws/build/{buildId}`：实时构建日志
- 项目创建/编辑：`POST /api/project`、`PUT /api/project`

## 数据库

已有数据库执行 `doc/migration-cicd-mysql.sql`。新部署应在初始化脚本之后执行该迁移。迁移增加 Docker 构建配置、构建 Git/镜像元数据和失败原因字段。

## 当前限制

部署/回滚的 Docker Compose SSH 记录模型和前端页面仍需继续接入本次新增的镜像字段；现有旧 JAR 部署逻辑保留为兼容路径。由于本次执行环境的 Maven/JDK 工具链在 javac 初始化阶段失败，尚未完成应用启动、Compose healthy、真实 Registry、目标服务器和浏览器联调验证。
