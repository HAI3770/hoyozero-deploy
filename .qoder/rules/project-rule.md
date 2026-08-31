---
trigger: always_on
---

---
trigger: always_on
alwaysApply: true
---

# 项目规则（SpringBoot3.0.5 + MyBatis-Plus + SaToken + Mysql + Vue3 + Naive UI）

## 系统整体规则
- 系统语言：中文界面、中文接口注释、中文文案。
- 前后台完全分离开发。
- 统一主题颜色：`#18A058`（Naive UI 主题同步）。
- 图标统一使用 vicons/ionicons5。
- 登录页面 UI 必须采用左右布局（左侧图 / 文案，右侧表单）。

---

## 后端开发规则（Spring Boot 3）
- 使用 Spring Boot 3 + MyBatis-Plus + SaToken + SQLite。
- 包结构规范：
    - controller（仅处理请求）
    - service / service.impl（业务逻辑）
    - mapper（继承 BaseMapper）
    - entity
    - dto
    - vo
- 接口必须返回统一格式 `Result<T>`：
  Result.ok(data)
  Result.fail(msg)
- 所有接口遵循 RESTful 风格，前缀：
  /api/{module}
- SaToken 作为权限认证：
- 登录成功使用 `StpUtil.login(userId)`
- 鉴权接口统一加：`@SaCheckLogin` 或角色权限注解
- MyBatis-Plus 规范：
- 所有 Mapper 继承 `BaseMapper<T>`
- 查询使用 `LambdaQueryWrapper`
- 尽量不要写手工 SQL（除非必须）

---

## 前端开发规则（Vue3 + Vite + Naive UI）
- 技术栈：Vue 3 + script setup + 可选 TypeScript。
- 全局 UI 统一使用 Naive UI，主题颜色必须应用 `#18A058`。
- 图标统一使用 vicons/ionicons5：
  import { HomeOutline } from 'vicons/ionicons5'
- 项目结构：
- src/views （页面）
- src/components（组件）
- src/api（接口）
- src/store（Pinia）
- src/utils（工具）
- 所有接口统一使用 axios 封装的 `http` 实例：
- 项目结构：
- src/views （页面）
- src/components（组件）
- src/api（接口）
- src/store（Pinia）
- src/utils（工具）
- 所有接口统一使用 axios 封装的 `http` 实例：
  http.get('/api/user/info')
- 登录页面规则：
- 左右布局
- 左侧展示 Logo/文案/背景图
- 右侧为表单（Naive UI）
- 表单规则：
- 使用 `n-form`、`n-form-item`、`n-input`、`n-button`
- 表单校验规则写在页面内部
- 后台页面布局使用：
- 顶部导航 + 左侧菜单栏 + 主内容区

---

## 权限与路由规则
- 前端路由必须通过后端返回的角色/权限决定是否展示。
- 路由定义写在 `src/router/index.js`。
- 登录后必须记录 token（localStorage），API 请求自动携带。
- 未登录自动跳转到 `/login`。

---

## 接口命名规则（统一规范）
- 查询列表：`list`
- 查询详情：`info/{id}`
- 新增：`add`
- 修改：`update`
- 删除：`delete`
- 登录：`login`
- 登出：`logout`

示例：
GET /api/user/list
POST /api/user/add
POST /api/auth/login

---

## 通用编码风格
- 所有文件使用小写短横线（frontend）或驼峰（backend）命名。
- 任何新增文件必须遵循本规则。
- AI 生成代码时必须遵守以上所有规范，不得偏离当前项目风格。