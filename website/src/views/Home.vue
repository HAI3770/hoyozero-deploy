<template>
  <div class="home">
    <!-- 导航栏 -->
    <header class="header">
      <div class="container">
        <div class="logo">
          <n-icon size="32" color="#18A058">
            <RocketSharp />
          </n-icon>
          <span class="logo-text">hoyozero-deploy</span>
        </div>
        <nav class="nav">
          <a href="#features">功能特性</a>
          <a href="#tech-stack">技术栈</a>
          <a href="#quickstart">快速开始</a>
          <a href="#docs">文档</a>
          <n-button type="primary" @click="goToDemo">
            立即体验
          </n-button>
        </nav>
      </div>
    </header>

    <!-- Hero 区域 -->
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <div class="hero-left">
            <h1 class="hero-title">
              <span class="gradient-text">hoyozero-deploy</span>
              <br />
              轻量级 CICD 自动化部署平台
            </h1>
            <p class="hero-desc">
              简而轻的低侵入式在线构建、自动部署、日常运维、项目运维监控<br />
              可视化操作，让部署变得轻而易举
            </p>
            <div class="hero-actions">
              <n-button type="primary" size="large" @click="goToDemo">
                <template #icon>
                  <n-icon><PlayCircleSharp /></n-icon>
                </template>
                立即开始
              </n-button>
              <n-button size="large" @click="viewDocs">
                <template #icon>
                  <n-icon><DocumentTextSharp /></n-icon>
                </template>
                查看文档
              </n-button>
              <n-button size="large" @click="goToGithub">
                <template #icon>
                  <n-icon><LogoGithub /></n-icon>
                </template>
                GitHub
              </n-button>
            </div>
            <div class="hero-stats">
              <div class="stat-item">
                <n-icon size="20" color="#18A058"><StarSharp /></n-icon>
                <span>开源免费</span>
              </div>
              <div class="stat-item">
                <n-icon size="20" color="#18A058"><CloudDoneSharp /></n-icon>
                <span>轻量易用</span>
              </div>
              <div class="stat-item">
                <n-icon size="20" color="#18A058"><FlashSharp /></n-icon>
                <span>快速部署</span>
              </div>
            </div>
          </div>
          <div class="hero-right">
            <div class="hero-image">
              <n-icon size="280" color="#18A058" class="rocket-icon">
                <RocketSharp />
              </n-icon>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 功能特性 -->
    <section id="features" class="features">
      <div class="container">
        <div class="section-header">
          <h2>核心功能</h2>
          <p>宝塔般简单的操作，Jenkins级的部署能力</p>
        </div>
        <n-grid :x-gap="24" :y-gap="24" :cols="3" responsive="screen">
          <n-gi v-for="feature in features" :key="feature.title">
            <n-card hoverable class="feature-card">
              <div class="feature-icon">
                <n-icon size="48" :color="feature.color">
                  <component :is="feature.icon" />
                </n-icon>
              </div>
              <h3>{{ feature.title }}</h3>
              <p>{{ feature.desc }}</p>
              <ul class="feature-list">
                <li v-for="item in feature.items" :key="item">
                  <n-icon size="16" color="#18A058"><CheckmarkSharp /></n-icon>
                  {{ item }}
                </li>
              </ul>
            </n-card>
          </n-gi>
        </n-grid>
      </div>
    </section>

    <!-- 工作流程 -->
    <section class="workflow">
      <div class="container">
        <div class="section-header">
          <h2>自动化部署流程</h2>
          <p>从代码提交到应用上线，全程自动化</p>
        </div>
        <div class="workflow-steps">
          <div v-for="(step, index) in workflowSteps" :key="index" class="workflow-step">
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-icon">
              <n-icon size="40" color="#18A058">
                <component :is="step.icon" />
              </n-icon>
            </div>
            <h3>{{ step.title }}</h3>
            <p>{{ step.desc }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 技术栈 -->
    <section id="tech-stack" class="tech-stack">
      <div class="container">
        <div class="section-header">
          <h2>技术栈</h2>
          <p>基于现代化技术栈构建</p>
        </div>
        <n-grid :x-gap="24" :y-gap="24" :cols="2" responsive="screen">
          <n-gi>
            <n-card title="前端技术" hoverable class="tech-card">
              <div class="tech-items">
                <div v-for="tech in frontendTechs" :key="tech.name" class="tech-item">
                  <n-tag :type="tech.type" size="large">{{ tech.name }}</n-tag>
                  <span class="tech-version">{{ tech.version }}</span>
                </div>
              </div>
            </n-card>
          </n-gi>
          <n-gi>
            <n-card title="后端技术" hoverable class="tech-card">
              <div class="tech-items">
                <div v-for="tech in backendTechs" :key="tech.name" class="tech-item">
                  <n-tag :type="tech.type" size="large">{{ tech.name }}</n-tag>
                  <span class="tech-version">{{ tech.version }}</span>
                </div>
              </div>
            </n-card>
          </n-gi>
        </n-grid>
      </div>
    </section>

    <!-- 快速开始 -->
    <section id="quickstart" class="quickstart">
      <div class="container">
        <div class="section-header">
          <h2>快速开始</h2>
          <p>三步即可启动 hoyozero-deploy</p>
        </div>
        <n-tabs type="line" animated>
          <n-tab-pane name="docker" tab="Docker 部署">
            <n-card>
              <n-code language="bash" :code="dockerCode" show-line-numbers />
              <div class="code-tips">
                <n-alert type="info" title="提示">
                  推荐使用 Docker 部署，一键启动所有服务
                </n-alert>
              </div>
            </n-card>
          </n-tab-pane>
          <n-tab-pane name="manual" tab="手动部署">
            <n-card>
              <n-steps vertical :current="3">
                <n-step title="启动后端">
                  <n-code language="bash" :code="backendCode" show-line-numbers />
                </n-step>
                <n-step title="启动前端">
                  <n-code language="bash" :code="frontendCode" show-line-numbers />
                </n-step>
                <n-step title="访问系统">
                  <div class="access-info">
                    <p>浏览器访问: <n-text code>http://localhost:5173</n-text></p>
                    <p>默认账号: <n-text code>admin / 123456</n-text></p>
                  </div>
                </n-step>
              </n-steps>
            </n-card>
          </n-tab-pane>
        </n-tabs>
      </div>
    </section>

    <!-- 文档链接 -->
    <section id="docs" class="docs">
      <div class="container">
        <div class="section-header">
          <h2>文档与资源</h2>
          <p>了解更多关于 hoyozero-deploy 的信息</p>
        </div>
        <n-grid :x-gap="24" :y-gap="24" :cols="3" responsive="screen">
          <n-gi v-for="doc in docs" :key="doc.title">
            <n-card hoverable class="doc-card" @click="doc.action">
              <div class="doc-icon">
                <n-icon size="48" :color="doc.color">
                  <component :is="doc.icon" />
                </n-icon>
              </div>
              <h3>{{ doc.title }}</h3>
              <p>{{ doc.desc }}</p>
            </n-card>
          </n-gi>
        </n-grid>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-left">
            <div class="footer-logo">
              <n-icon size="28" color="#18A058">
                <RocketSharp />
              </n-icon>
              <span>hoyozero-deploy</span>
            </div>
            <p>轻量级宝塔+Jenkins</p>
            <p class="copyright">© 2024 hoyozero-deploy. MIT License.</p>
          </div>
          <div class="footer-links">
            <div class="link-group">
              <h4>产品</h4>
              <a href="#features">功能特性</a>
              <a href="#tech-stack">技术栈</a>
              <a href="#quickstart">快速开始</a>
            </div>
            <div class="link-group">
              <h4>资源</h4>
              <a href="#docs">文档</a>
              <a @click="goToGithub">GitHub</a>
              <a href="#">更新日志</a>
            </div>
            <div class="link-group">
              <h4>社区</h4>
              <a href="#">问题反馈</a>
              <a href="#">参与贡献</a>
              <a href="#">联系我们</a>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p class="beian-text">备案号：<a href="https://beian.miit.gov.cn/" target="_blank" class="beian-link">蜀ICP备2022022007号-1</a></p>
          <p class="copyright-text">Copyright © 2024-2025 All Rights Reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  NButton,
  NCard,
  NGrid,
  NGi,
  NIcon,
  NTag,
  NTabs,
  NTabPane,
  NCode,
  NSteps,
  NStep,
  NText,
  NAlert,
  useMessage
} from 'naive-ui'
import {
  RocketSharp,
  PlayCircleSharp,
  DocumentTextSharp,
  LogoGithub,
  StarSharp,
  CloudDoneSharp,
  FlashSharp,
  CheckmarkSharp,
  GitBranchSharp,
  ServerSharp,
  BuildSharp,
  CloudUploadSharp,
  TerminalSharp,
  CheckmarkCircleSharp,
  FolderOpenSharp,
  CodeSlashSharp,
  BookSharp,
  ChatbubblesSharp,
  PeopleSharp
} from '@vicons/ionicons5'

const message = useMessage()

// 功能特性
const features = ref([
  {
    title: '项目管理',
    icon: FolderOpenSharp,
    color: '#18A058',
    desc: '支持多项目管理，灵活配置构建参数',
    items: ['Git 仓库集成', '分支管理', '构建命令配置', '产物路径设置', '多服务器绑定']
  },
  {
    title: '一键部署',
    icon: RocketSharp,
    color: '#2080F0',
    desc: '自动化构建部署流程，简化运维工作',
    items: ['自动拉取代码', '自动构建打包', 'SFTP 文件上传', '自动执行脚本', '状态实时反馈']
  },
  {
    title: '实时日志',
    icon: TerminalSharp,
    color: '#F0A020',
    desc: 'WebSocket 实时推送构建日志',
    items: ['实时日志输出', '构建进度跟踪', '错误信息提示', '历史记录查询', '日志下载导出']
  },
  {
    title: '服务器管理',
    icon: ServerSharp,
    color: '#D03050',
    desc: '统一管理多台服务器，SSH 连接测试',
    items: ['SSH 密钥登录', '连接状态测试', '部署目录配置', '启停命令管理', '批量操作支持']
  },
  {
    title: '权限控制',
    icon: PeopleSharp,
    color: '#9333EA',
    desc: '基于角色的权限管理系统',
    items: ['用户角色管理', '项目成员管理', '细粒度权限', '操作日志记录', '安全认证机制']
  },
  {
    title: '数据可视化',
    icon: BuildSharp,
    color: '#0EA5E9',
    desc: '构建数据统计与可视化展示',
    items: ['构建趋势图', '状态分布图', '成功率统计', 'Dashboard 看板', '实时数据更新']
  }
])

// 工作流程
const workflowSteps = ref([
  { icon: GitBranchSharp, title: '拉取代码', desc: '从 Git 仓库自动拉取最新代码' },
  { icon: BuildSharp, title: '执行构建', desc: '根据配置执行 Maven/npm 构建命令' },
  { icon: CheckmarkCircleSharp, title: '收集产物', desc: '自动查找并收集构建产物' },
  { icon: CloudUploadSharp, title: '上传部署', desc: 'SFTP 上传文件到目标服务器' },
  { icon: TerminalSharp, title: '执行脚本', desc: '执行部署脚本，启动应用服务' },
])

// 前端技术栈
const frontendTechs = ref([
  { name: 'Vue 3', version: '3.4.0', type: 'success' },
  { name: 'Vite', version: '5.0.0', type: 'warning' },
  { name: 'Naive UI', version: '2.38.0', type: 'info' },
  { name: 'Vue Router', version: '4.2.5', type: 'default' },
  { name: 'Axios', version: '1.6.2', type: 'error' },
  { name: 'ECharts', version: '5.4.3', type: 'success' }
])

// 后端技术栈
const backendTechs = ref([
  { name: 'Spring Boot', version: '3.2.0', type: 'success' },
  { name: 'MyBatis-Plus', version: '3.5.5', type: 'info' },
  { name: 'Sa-Token', version: '1.37.0', type: 'warning' },
  { name: 'JGit', version: '6.8.0', type: 'default' },
  { name: 'JSch', version: '0.2.16', type: 'error' },
  { name: 'MySQL', version: '8.0', type: 'info' }
])

// Docker 部署代码
const dockerCode = ref(`# 克隆项目
git clone https://github.com/your-repo/hoyozero-deploy.git
cd hoyozero-deploy

# 启动服务
docker-compose up -d

# 访问系统
# http://localhost (默认账号: admin / 123456)`)

// 后端代码
const backendCode = ref(`# 导入数据库
mysql -u root -p < doc/hoyozero_deploy.sql

# 构建启动
mvn clean package
java -jar target/hoyozero-deploy-1.0.0.jar`)

// 前端代码
const frontendCode = ref(`cd web
npm install
npm run dev`)

// 文档资源
const docs = ref([
  {
    title: '快速入门',
    icon: BookSharp,
    color: '#18A058',
    desc: '了解如何快速搭建和使用 hoyozero-deploy',
    action: () => window.open("https://gitee.com/hoyozero/hoyozero-deploy/blob/master/README.md",'_blank')
  },
  {
    title: '开发文档',
    icon: CodeSlashSharp,
    color: '#2080F0',
    desc: '查看详细的 API 文档和开发指南',
    action: () => window.open("https://gitee.com/hoyozero/hoyozero-deploy/blob/master/README.md",'_blank')
  },
  {
    title: '社区支持',
    icon: ChatbubblesSharp,
    color: '#F0A020',
    desc: '加入社区，获取帮助和分享经验',
    action: () => window.open("https://gitee.com/hoyozero/hoyozero-deploy/blob/master/README.md",'_blank')
  }
])

// 方法
const goToDemo = () => {
  window.open('http://localhost:5173', '_blank')
}

const viewDocs = () => {
  window.open("https://gitee.com/hoyozero/hoyozero-deploy/blob/master/README.md",'_blank')
}

const goToGithub = () => {
  window.open("https://gitee.com/hoyozero/hoyozero-deploy",'_blank')
}
</script>

<style scoped>
.home {
  width: 100%;
  overflow-x: hidden;
}

/* 导航栏 */
.header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 64px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e5e7eb;
  z-index: 1000;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.header .container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 24px;
  font-weight: 700;
  color: #18A058;
}

.logo-text {
  background: linear-gradient(135deg, #18A058 0%, #36AD6A 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav {
  display: flex;
  align-items: center;
  gap: 32px;
}

.nav a {
  color: #4b5563;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.nav a:hover {
  color: #18A058;
}

/* Hero 区域 */
.hero {
  padding: 120px 0 80px;
  background: linear-gradient(135deg, #f0fdf4 0%, #ffffff 100%);
}

.hero-content {
  display: flex;
  align-items: center;
  gap: 60px;
}

.hero-left {
  flex: 1;
}

.hero-title {
  font-size: 40px;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 24px;
  color: #1f2937;
}

.gradient-text {
  font-size: 52px;
  background: linear-gradient(135deg, #18A058 0%, #36AD6A 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 18px;
  color: #6b7280;
  line-height: 1.8;
  margin-bottom: 32px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  margin-bottom: 40px;
}

.hero-stats {
  display: flex;
  gap: 32px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4b5563;
  font-weight: 500;
}

.hero-right {
  flex: 1;
  display: flex;
  justify-content: center;
}

.hero-image {
  position: relative;
  width: 400px;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #18A05810 0%, #36AD6A10 100%);
  border-radius: 50%;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}



/* 通用区块样式 */
section {
  padding: 80px 0;
}

.section-header {
  text-align: center;
  margin-bottom: 60px;
}

.section-header h2 {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  color: #1f2937;
}

.section-header p {
  font-size: 18px;
  color: #6b7280;
}

/* 功能特性 */
.features {
  background: #ffffff;
}

.feature-card {
  height: 100%;
  transition: all 0.3s;
  cursor: pointer;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #1f2937;
}

.feature-card p {
  color: #6b7280;
  margin-bottom: 20px;
  line-height: 1.6;
}

.feature-list {
  list-style: none;
}

.feature-list li {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  color: #4b5563;
  font-size: 14px;
}

/* 工作流程 */
.workflow {
  background: linear-gradient(135deg, #f0fdf4 0%, #ffffff 100%);
}

.workflow-steps {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 24px;
}

.workflow-step {
  text-align: center;
  padding: 32px 20px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  position: relative;
}

.workflow-step:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.step-number {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #18A058;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 16px;
}

.step-icon {
  margin-bottom: 16px;
}

.workflow-step h3 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1f2937;
}

.workflow-step p {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
}

/* 技术栈 */
.tech-stack {
  background: #ffffff;
}

.tech-card {
  height: 100%;
}

.tech-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tech-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  transition: all 0.3s;
}

.tech-item:hover {
  background: #f3f4f6;
  transform: translateX(4px);
}

.tech-version {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

/* 快速开始 */
.quickstart {
  background: linear-gradient(135deg, #f0fdf4 0%, #ffffff 100%);
}

.code-tips {
  margin-top: 16px;
}

.access-info {
  padding: 20px;
  background: #f9fafb;
  border-radius: 8px;
}

.access-info p {
  margin-bottom: 12px;
  font-size: 16px;
  color: #4b5563;
}

.access-info p:last-child {
  margin-bottom: 0;
}

/* 文档 */
.docs {
  background: #ffffff;
}

.doc-card {
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.doc-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
}

.doc-icon {
  margin-bottom: 20px;
}

.doc-card h3 {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #1f2937;
}

.doc-card p {
  color: #6b7280;
  line-height: 1.6;
}

/* 页脚 */
.footer {
  background: #1f2937;
  color: #9ca3af;
  padding: 60px 0 20px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 700;
  color: #18A058;
  margin-bottom: 16px;
}

.footer-left p {
  margin-bottom: 8px;
  font-size: 14px;
}

.copyright {
  color: #6b7280;
  font-size: 13px;
}

.footer-links {
  display: flex;
  gap: 80px;
}

.link-group h4 {
  color: #f3f4f6;
  margin-bottom: 16px;
  font-size: 16px;
}

.link-group a {
  display: block;
  color: #9ca3af;
  text-decoration: none;
  margin-bottom: 12px;
  font-size: 14px;
  transition: color 0.3s;
  cursor: pointer;
}

.link-group a:hover {
  color: #18A058;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #374151;
}

.beian-text,
.copyright-text {
  color: #9ca3af;
  font-size: 14px;
  margin-bottom: 8px;
}

.copyright-text {
  margin-bottom: 0;
}

.beian-link {
  color: #9ca3af;
  text-decoration: none;
  transition: color 0.3s;
}

.beian-link:hover {
  color: #18A058;
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
  }

  .hero-title {
    font-size: 36px;
  }

  .hero-right {
    display: none;
  }

  .workflow-steps {
    grid-template-columns: 1fr;
  }

  .footer-content {
    flex-direction: column;
    gap: 40px;
  }

  .footer-links {
    gap: 40px;
  }
}
</style>
