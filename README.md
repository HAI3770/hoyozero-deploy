# hoyozero-deploy

轻量、可自托管的 CI/CD 自动化部署平台。

hoyozero-deploy 面向需要自行管理代码构建、服务器部署和发布过程的团队。平台提供 Web 管理界面，支持项目配置、构建任务、部署发布、服务器管理、实时日志和权限控制。

## 功能概览

- 项目管理：配置 Git 仓库、分支、构建命令和部署目标
- 自动构建：支持 Java、Vue 等常见项目的构建流程
- 持续交付：管理发布单、部署阶段、任务状态和失败原因
- 服务器管理：SSH 连接测试、Web 控制台、文件管理和系统监控
- 构建日志：查看构建过程、产物和历史记录
- 插件市场：安装常用基础环境和中间件
- 权限管理：用户、角色和菜单权限
- 界面体验：响应式 Web 界面、亮色/暗色主题和全屏操作
- 可选智能助手：通过 Dify 接入 CI/CD 智能体

## 技术栈

- 前端：Vue 3、Vite、Naive UI、ECharts、xterm.js
- 后端：Spring Boot 3、MyBatis-Plus、Sa-Token
- 数据库：MySQL 8.0
- 缓存：Redis 7
- 构建与运行：Docker、Docker Compose、Maven、Node.js
- 通信：HTTP、WebSocket、SSH/SFTP

## 运行要求

- Linux 服务器
- Docker 20 或更高版本
- Docker Compose v2（使用 `docker compose` 命令）
- 至少 2 GB 可用内存；构建大型项目时建议提供更多资源
- 一个可用的 Git 仓库和部署目标服务器

宿主机不需要单独安装 Java、Node.js 或 MySQL；这些运行依赖由构建流程和 Docker 服务负责。部署目标服务器需要根据实际项目安装对应的运行环境。

## 快速部署

### 1. 获取代码

```bash
git clone <你的 GitHub 或 Gitee 仓库地址>
cd hoyozero-deploy
```

如果从 Gitee 获取，请将仓库地址替换为对应的 Gitee 地址。

### 2. 创建本地配置

`.env` 只保存在部署服务器上，不要提交到 Git：

```dotenv
HOYOZERO_DB_ROOT_PASSWORD=请填写强密码
HOYOZERO_RUNNER_TOKEN=请填写随机令牌

# 可选：接入 Dify 智能助手
DIFY_BASE_URL=http://your-dify-host:8060
DIFY_API_KEY=
DIFY_APP_ID=

# 可选：私有镜像仓库
REGISTRY_USERNAME=
REGISTRY_TOKEN=
```

请使用强密码和随机令牌，并通过服务器 Secret、环境变量或权限严格的 `.env` 文件管理敏感配置。

### 3. 构建并启动

```bash
docker compose up --build -d
docker compose ps
```

看到 `mysql`、`redis`、`backend`、`runner`、`frontend` 均为 `running`，且健康检查通过后即可访问：

```text
http://服务器IP:30080/
```

### 4. 常用运维命令

```bash
# 查看全部日志
docker compose logs -f

# 查看单个服务日志
docker compose logs -f backend

# 更新代码并重新构建
git pull
docker compose up --build -d

# 停止服务
docker compose down
```

`docker compose down` 默认不会删除 `/data/hoyozero-deploy` 下的数据库和工作目录数据。删除数据前请先完成备份，并确认删除范围。

## 数据目录

默认数据目录如下：

- `/data/hoyozero-deploy/mysql`：MySQL 数据
- `/data/hoyozero-deploy/redis`：Redis 持久化数据
- `/data/hoyozero-deploy/workspace`：构建工作目录和产物

建议定期备份数据库和重要构建产物。数据库映射端口仅用于维护时访问，生产环境建议移除公网暴露或限制为内网访问。

## 使用流程

1. 登录 Web 管理端并修改管理员密码。
2. 在“服务器管理”中添加部署目标，先执行连接测试。
3. 在“项目管理”中配置 Git 地址、分支、构建命令和产物路径。
4. 关联部署服务器，确认部署目录和启动脚本。
5. 手动触发构建，检查构建日志和产物。
6. 创建发布或执行部署，确认部署后的健康检查结果。

不同项目的构建命令和启动方式不同，请根据项目实际情况填写，不要直接照搬示例配置。

## 智能助手

智能助手为可选功能。启用前需要准备可访问的 Dify 服务，并在 `.env` 中配置 Dify 地址和必要的应用信息。未配置 Dify 时，平台的项目、构建和部署核心功能仍可独立运行。

不要把 Dify API Key、Git Token、镜像仓库 Token、SSH 私钥或服务器密码写入源码、README、截图或提交记录。

## 安全说明

- `.env`、私钥、Token、密码和数据库备份禁止提交到公开仓库。
- 首次登录后立即修改管理员密码。
- 仅向可信网络开放 Web、SSH 和必要的服务端口。
- backend/runner 使用 Docker Socket 时拥有较高宿主机权限，建议部署在隔离节点，并限制可执行项目和用户权限。
- 生产环境建议在前置网关启用 HTTPS、访问控制、审计和备份。
- 发布前请检查 Git 历史；文件后来加入 `.gitignore` 不能自动清除历史中的敏感信息。

## 目录结构

```text
.
├── src/                 # Spring Boot 后端源码
├── web/                 # 管理端前端源码
├── website/             # hoyozero 宏宇项目介绍页
├── runner/              # 构建 runner
├── doc/                 # 数据库脚本和界面截图
├── Dockerfile           # 后端镜像
├── Dockerfile.frontend  # 前端镜像
├── docker-compose.yml   # 服务编排
├── nginx.conf           # 前端 Nginx 配置
└── pom.xml              # Maven 配置
```

## 开发调试

后端：

```bash
mvn spring-boot:run
```

管理端前端：

```bash
cd web
npm install
npm run dev
```

官网：

```bash
cd website
npm install
npm run dev
```

## 许可证与品牌

项目品牌为 **hoyozero 宏宇**。发布前请根据实际仓库策略补充许可证文件和版权声明；如果以公开开源项目发布，建议在仓库根目录增加 `LICENSE` 文件。
