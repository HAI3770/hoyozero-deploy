<template>
  <n-layout has-sider class="layout" v-if="menuLayout === 'side'">
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="64"
      :width="240"
      :collapsed="collapsed"
      show-trigger
      @collapse="collapsed = true"
      @expand="collapsed = false"
    >
      <div class="sidebar-shell">
        <div class="logo">
          <div class="logo-mark"><n-icon :size="collapsed ? 25 : 27"><RocketSharp /></n-icon></div>
          <div v-if="!collapsed" class="brand-copy"><span class="logo-text">hoyozero</span><span>宏宇持续交付平台</span></div>
        </div>

        <n-menu
          v-model:value="activeKey"
          class="workflow-menu"
          :collapsed="collapsed"
          :collapsed-width="64"
          :collapsed-icon-size="21"
          :options="sideMenuOptions"
          @update:value="handleMenuSelect"
        />

        <div class="sidebar-footer" v-if="!collapsed">
          <n-button type="primary" block @click="router.push('/project/add')">
            <template #icon><n-icon><FolderOpenSharp /></n-icon></template>新建项目
          </n-button>
          <div class="quick-links">
            <button type="button" @click="router.push('/build')"><n-icon><RocketSharp /></n-icon>构建记录</button>
            <button type="button" @click="openAssistant"><n-icon><ChatbubbleEllipsesSharp /></n-icon>智能助手</button>
          </div>
          <div class="version-info">v3 · hoyozero 宏宇</div>
        </div>
      </div>
    </n-layout-sider>

    <n-layout>
      <n-layout-header bordered class="header">
        <div class="header-content">
          <div class="header-left">
            <n-breadcrumb>
              <n-breadcrumb-item>{{ currentMenuName }}</n-breadcrumb-item>
            </n-breadcrumb>
          </div>

          <div class="header-right">
            <n-space :size="16" align="center">
              <n-tooltip placement="bottom">
                <template #trigger>
                  <n-button text class="icon-button" @click="toggleMenuLayout">
                    <n-icon size="20">
                      <ReorderThreeSharp />
                    </n-icon>
                  </n-button>
                </template>
                切换为顶部菜单
              </n-tooltip>
              
              <n-tooltip placement="bottom">
                <template #trigger>
                  <n-button text class="icon-button" @click="toggleTheme">
                    <n-icon size="20">
                      <MoonSharp v-if="!isDark" />
                      <SunnySharp v-else />
                    </n-icon>
                  </n-button>
                </template>
                {{ isDark ? '切换为亮色模式' : '切换为暗黑模式' }}
              </n-tooltip>
              
              <n-tooltip placement="bottom">
                <template #trigger>
                  <n-button text class="icon-button" @click="toggleFullscreen">
                    <n-icon size="20">
                      <ExpandSharp v-if="!isFullscreen" />
                      <ContractSharp v-else />
                    </n-icon>
                  </n-button>
                </template>
                {{ isFullscreen ? '退出全屏' : '全屏' }}
              </n-tooltip>

              <n-dropdown :options="userOptions" @select="handleUserAction">
                <div class="user-info">
                  <n-avatar round size="small" :style="{ background: '#18a058' }">
                    {{ userInitial }}
                  </n-avatar>
                  <span class="username">{{ user?.nickname || user?.username }}</span>
                </div>
              </n-dropdown>
            </n-space>
          </div>
        </div>
      </n-layout-header>

      <n-layout-content class="page-content" :native-scrollbar="false">
        <router-view />
      </n-layout-content>
    </n-layout>
  </n-layout>
  <!-- 顶部菜单布局 -->
  <n-layout v-else class="layout">
    <n-layout-header bordered class="header header-with-menu">
      <div class="header-top">
        <div class="header-logo">
          <n-icon size="32" :color="'#18a058'">
            <RocketSharp />
          </n-icon>
        </div>
        
        <n-space style="flex: 1; justify-content: center;">
          <n-menu
            v-model:value="activeKey"
            mode="horizontal"
            :options="menuOptions"
            @update:value="handleMenuSelect"
          />
        </n-space>
        
        <div class="header-right">
          <n-space :size="16" align="center">
            <n-tooltip placement="bottom">
              <template #trigger>
                <n-button text class="icon-button" @click="toggleMenuLayout">
                  <n-icon size="20">
                    <MenuSharp />
                  </n-icon>
                </n-button>
              </template>
              切换为侧边栏菜单
            </n-tooltip>
            
            <n-tooltip placement="bottom">
              <template #trigger>
                <n-button text class="icon-button" @click="toggleTheme">
                  <n-icon size="20">
                    <MoonSharp v-if="!isDark" />
                    <SunnySharp v-else />
                  </n-icon>
                </n-button>
              </template>
              {{ isDark ? '切换为亮色模式' : '切换为暗黑模式' }}
            </n-tooltip>
            
            <n-tooltip placement="bottom">
              <template #trigger>
                <n-button text class="icon-button" @click="toggleFullscreen">
                  <n-icon size="20">
                    <ExpandSharp v-if="!isFullscreen" />
                    <ContractSharp v-else />
                  </n-icon>
                </n-button>
              </template>
              {{ isFullscreen ? '退出全屏' : '全屏' }}
            </n-tooltip>

            <n-dropdown :options="userOptions" @select="handleUserAction">
              <div class="user-info">
                <n-avatar round size="small" :style="{ background: '#18a058' }">
                  {{ userInitial }}
                </n-avatar>
                <span class="username">{{ user?.nickname || user?.username }}</span>
              </div>
            </n-dropdown>
          </n-space>
        </div>
      </div>
    </n-layout-header>

    <n-layout-content class="page-content layout-content-top" :native-scrollbar="false">
      <router-view />
    </n-layout-content>
  </n-layout>
  <DifyAssistant ref="assistantRef" />
</template>

<script setup>
import { ref, computed, h, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { NIcon, useMessage } from 'naive-ui'
import {
  SpeedometerSharp,
  FolderOpenSharp,
  ServerSharp,
  RocketSharp,
  LogOutSharp,
  PersonSharp,
  PeopleSharp,
  Albums,
  ShieldCheckmarkSharp,
  MenuSharp,
  ExpandSharp,
  ContractSharp,
  ExtensionPuzzleSharp,
  MoonSharp,
  SunnySharp,
  DocumentTextSharp,
  LogInSharp,
  ListSharp,
  ReorderThreeSharp,
  ChatbubbleEllipsesSharp
} from '@vicons/ionicons5'
import { logout } from '@/api/auth'
import { getUserMenus } from '@/api/menu'
import { getBuildList } from '@/api/build'
import { getServerList } from '@/api/server'
import DifyAssistant from '@/components/DifyAssistant.vue'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const collapsed = ref(false)
const activeKey = ref('/dashboard')
const menuOptions = ref([])
const isFullscreen = ref(false)
const isDark = ref(false)
const menuLayout = ref('side') // 'side' 或 'top'
const assistantRef = ref(null)
const navHealth = ref({ failedBuilds: 0, runningBuilds: 0, onlineServers: 0, totalServers: 0 })

const user = computed(() => {
  const userStr = localStorage.getItem('user')
  return userStr ? JSON.parse(userStr) : null
})

const userInitial = computed(() => {
  const name = user.value?.nickname || user.value?.username || 'U'
  return name.charAt(0).toUpperCase()
})

const renderIcon = (iconName) => {
  const iconMap = {
    SpeedometerSharp,
    FolderOpenSharp,
    ServerSharp,
    RocketSharp,
    PeopleSharp,
    Albums,
    ShieldCheckmarkSharp,
    MenuSharp,
    ExtensionPuzzleSharp,
    DocumentTextSharp,
    LogInSharp,
    ListSharp
  }
  const IconComponent = iconMap[iconName]
  return () => h(NIcon, null, { default: () => h(IconComponent || SpeedometerSharp) })
}

const loadUserMenus = async () => {
  try {
    const menus = await getUserMenus()
    menuOptions.value = convertToMenuOptions(menus)
    // 发布模块兼容旧菜单数据：即使数据库尚未写入菜单，也保证 CD 入口可见。
    if (!menuOptions.value.some(item => item.key === '/release')) {
      menuOptions.value.push({ label: '持续交付', key: '/release', icon: renderIcon('RocketSharp') })
    }
  } catch (error) {
    console.error('加载菜单失败', error)
  }
}

const findMenu = (menus, path) => {
  for (const menu of menus) {
    if (menu.key === path) return menu
    const child = menu.children && findMenu(menu.children, path)
    if (child) return child
  }
  return null
}

const statusText = (path) => {
  if (path === '/build' && navHealth.value.failedBuilds > 0) return `${navHealth.value.failedBuilds} 失败`
  if (path === '/build' && navHealth.value.runningBuilds > 0) return `${navHealth.value.runningBuilds} 运行中`
  if (path === '/server' && navHealth.value.totalServers > 0) return `${navHealth.value.onlineServers}/${navHealth.value.totalServers}`
  return ''
}

const sideMenuOptions = computed(() => {
  const groups = [
    { label: '工作台', paths: ['/dashboard'] },
    { label: '项目与代码', paths: ['/project', '/project-group'] },
    { label: '构建与交付', paths: ['/build', '/release'] },
    { label: '基础设施', paths: ['/server', '/file'] },
    { label: '平台管理', paths: ['/user', '/role', '/menu', '/plugin', '/log/operation', '/log/login'] }
  ]
  const used = new Set()
  const options = groups.map(group => {
    const children = group.paths.map(path => findMenu(menuOptions.value, path)).filter(Boolean).map(menu => {
      used.add(menu.key)
      const badge = statusText(menu.key)
      return {
        ...menu,
        label: () => h('div', { class: 'menu-label' }, [
          h('span', menu.label),
          badge ? h('span', { class: ['menu-status', menu.key === '/build' && navHealth.value.failedBuilds > 0 ? 'is-alert' : ''] }, badge) : null
        ])
      }
    })
    return children.length ? { key: `group-${group.label}`, label: group.label, type: 'group', children } : null
  }).filter(Boolean)
  const remaining = menuOptions.value.filter(item => !used.has(item.key))
  if (remaining.length) options.push({ key: 'group-other', label: '其他', type: 'group', children: remaining })
  return options
})

const loadNavHealth = async () => {
  try {
    const [buildPage, serverPage] = await Promise.all([
      getBuildList({ current: 1, size: 100 }),
      getServerList({ current: 1, size: 100 })
    ])
    const builds = buildPage?.records || []
    const servers = serverPage?.records || []
    navHealth.value = {
      failedBuilds: builds.filter(item => item.status === 'FAILED').length,
      runningBuilds: builds.filter(item => ['RUNNING', 'PENDING'].includes(item.status)).length,
      onlineServers: servers.filter(item => item.status === 'ONLINE').length,
      totalServers: servers.length
    }
  } catch (error) {
    console.warn('加载侧栏状态失败', error)
  }
}

const convertToMenuOptions = (menus) => {
  return menus.map(menu => ({
    label: menu.name,
    key: menu.path,
    icon: renderIcon(menu.icon),
    children: menu.children ? convertToMenuOptions(menu.children) : undefined
  }))
}

const currentMenuName = computed(() => {
  // 先尝试一级菜单
  let menu = menuOptions.value.find(item => item.key === activeKey.value)
  if (menu) return menu.label

  // 尝试二级菜单
  for (const item of menuOptions.value) {
    if (item.children) {
      const child = item.children.find(c => c.key === activeKey.value)
      if (child) return child.label
    }
  }

  return '仪表盘'
})

const userOptions = [
  {
    label: '个人信息',
    key: 'profile',
    icon: renderIcon(PersonSharp)
  },
  {
    label: '退出登录',
    key: 'logout',
    icon: renderIcon(LogOutSharp)
  }
]

const handleMenuSelect = (key) => {
  activeKey.value = key
  router.push(key)
}

const openAssistant = () => assistantRef.value?.openPanel()

const handleUserAction = async (key) => {
  if (key === 'profile') {
    router.push('/profile')
  } else if (key === 'logout') {
    try {
      await logout()
    } catch (error) {
      console.error(error)
    } finally {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      message.success('已退出登录')
      router.push('/login')
    }
  }
}

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
    isFullscreen.value = true
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
      isFullscreen.value = false
    }
  }
}

// 监听全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
}

// 主题切换
const toggleTheme = () => {
  isDark.value = !isDark.value
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light')
  // 触发自定义事件，通知 App.vue 更新主题
  window.dispatchEvent(new CustomEvent('theme-change', { detail: isDark.value ? 'dark' : 'light' }))
}

// 菜单布局切换
const toggleMenuLayout = () => {
  menuLayout.value = menuLayout.value === 'side' ? 'top' : 'side'
  localStorage.setItem('menuLayout', menuLayout.value)
  message.success(`已切换为${menuLayout.value === 'side' ? '侧边栏' : '顶部'}菜单`)
}

onMounted(() => {
  activeKey.value = route.path
  loadUserMenus()
  loadNavHealth()
  document.addEventListener('fullscreenchange', handleFullscreenChange)
  
  // 加载保存的主题
  const savedTheme = localStorage.getItem('theme')
  isDark.value = savedTheme === 'dark'
  
  // 加载保存的菜单布局
  const savedLayout = localStorage.getItem('menuLayout')
  if (savedLayout === 'top' || savedLayout === 'side') {
    menuLayout.value = savedLayout
  }
})

watch(() => route.path, (path) => { activeKey.value = path })

onUnmounted(() => {
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
})
</script>

<style scoped>
.layout {
  height: 100vh;
  background: #f5f7f9;
}

.sidebar-shell { height: 100%; display: flex; flex-direction: column; }

.logo {
  height: 76px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 12px;
  padding: 0 16px;
  border-bottom: 1px solid #f0f0f0;
}

.logo-mark { width: 38px; height: 38px; display: grid; place-items: center; flex: none; color: #fff; border-radius: 11px; background: linear-gradient(135deg, #18a058, #0c7a43); box-shadow: 0 8px 18px rgba(24, 160, 88, .25); }
.brand-copy { display: flex; min-width: 0; flex-direction: column; gap: 2px; color: #89938c; font-size: 11px; letter-spacing: .02em; }
.logo-text { color: #18261d; font-size: 18px; font-weight: 750; letter-spacing: -.02em; }
.workflow-menu { flex: 1; overflow-y: auto; padding: 14px 0; }
.sidebar-footer { padding: 14px 14px 16px; border-top: 1px solid #eef1ee; background: linear-gradient(180deg, #fff, #f7fbf8); }
.quick-links { display: grid; grid-template-columns: 1fr 1fr; gap: 6px; margin-top: 10px; }
.quick-links button { display: inline-flex; align-items: center; justify-content: center; gap: 5px; min-width: 0; padding: 7px 4px; color: #627067; font: inherit; font-size: 12px; border: 0; border-radius: 7px; background: transparent; cursor: pointer; }
.quick-links button:hover { color: #0c7a43; background: #eaf6ee; }
.version-info { margin-top: 12px; color: #9ba49e; font-size: 11px; text-align: center; }

.header {
  height: 64px;
  padding: 0 28px;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, .88);
  backdrop-filter: blur(12px);
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 4px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f0f7f2;
}

.username {
  color: #333;
  font-size: 14px;
}

.icon-button {
  padding: 4px;
  height: auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-button:hover {
  background: #f0f7f2;
  border-radius: 4px;
}

:deep(.workflow-menu .n-menu-item-group-title) { padding: 11px 20px 5px; color: #a1aaa3; font-size: 10px; font-weight: 700; letter-spacing: .12em; }
:deep(.workflow-menu .n-menu-item-content) { margin: 2px 10px; border-radius: 9px; }
:deep(.workflow-menu .n-menu-item-content--selected) { font-weight: 650; box-shadow: inset 3px 0 0 #18a058; }
:deep(.workflow-menu .menu-label) { display: flex; align-items: center; justify-content: space-between; gap: 8px; width: 100%; }
:deep(.workflow-menu .menu-status) { flex: none; padding: 1px 6px; color: #16834b; font-size: 10px; line-height: 16px; border-radius: 10px; background: #e8f6ed; }
:deep(.workflow-menu .menu-status.is-alert) { color: #c43c45; background: #fff0f0; }

/* 顶部菜单布局样式 */
.header-with-menu {
  height: 64px;
  padding: 0;
}

.header-top {
  height: 64px;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 50px;
}

.header-logo .logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #18a058;
  white-space: nowrap;
}

.header-menu {
  padding: 0 24px;
}

.layout-content-top {
  margin-top: 0;
  height: calc(100vh - 64px);
}

.page-content { padding: 28px; }

/* 暗黑模式适配 */
:deep(.n-layout.n-layout--static-positioned) {
  background: var(--n-color);
}

:deep(.n-layout-sider) { background: #fff; }
:deep(.n-menu-item-content) { border-radius: 8px; margin: 2px 10px; }
:deep(.n-menu-item-content--selected) { font-weight: 600; }

@media (max-width: 680px) {
  .header { padding: 0 14px; }
  .page-content { padding: 16px 12px 96px; }
  .username { display: none; }
}
</style>
