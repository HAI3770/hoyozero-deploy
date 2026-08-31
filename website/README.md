# hoyozero-deploy 官方网站

hoyozero-deploy 官方网站，基于 Vue3 + Vite + Naive UI 构建。

## 特性

- ✨ 现代化设计风格
- 🚀 响应式布局，支持移动端
- 🎨 精美的动画效果
- 📱 完整的功能介绍
- 🔧 快速开始指南
- 📚 技术栈展示

## 技术栈

- Vue 3.4.0
- Vite 5.0.0
- Naive UI 2.38.0
- Vue Router 4.2.5
- @vicons/ionicons5

## 快速开始

### 安装依赖

\`\`\`bash
npm install
\`\`\`

### 开发环境

\`\`\`bash
npm run dev
\`\`\`

访问 http://localhost:3000

### 生产构建

\`\`\`bash
npm run build
\`\`\`

构建产物在 `dist` 目录。

### 预览构建

\`\`\`bash
npm run preview
\`\`\`

## 目录结构

\`\`\`
website/
├── src/
│   ├── views/          # 页面组件
│   │   └── Home.vue    # 首页
│   ├── router/         # 路由配置
│   ├── App.vue         # 根组件
│   └── main.js         # 入口文件
├── index.html          # HTML 模板
├── vite.config.js      # Vite 配置
├── package.json        # 依赖配置
└── README.md          # 说明文档
\`\`\`

## 部署

### Nginx 部署

\`\`\`bash
# 构建
npm run build

# 配置 Nginx
server {
    listen 80;
    server_name your-domain.com;
    
    root /path/to/website/dist;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
}
\`\`\`

### Docker 部署

\`\`\`dockerfile
FROM node:18-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
\`\`\`

## License

MIT License
