import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/Login.vue')
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/Dashboard.vue')
      },
      {
        path: 'project',
        name: 'ProjectList',
        component: () => import('../views/project/ProjectList.vue')
      },
      {
        path: 'project-group',
        name: 'ProjectGroupList',
        component: () => import('../views/project/ProjectGroupList.vue')
      },
      {
        path: 'project/add',
        name: 'ProjectAdd',
        component: () => import('../views/project/ProjectAdd.vue')
      },
      {
        path: 'project/edit/:id',
        name: 'ProjectEdit',
        component: () => import('../views/project/ProjectAdd.vue')
      },
      {
        path: 'server',
        name: 'ServerList',
        component: () => import('../views/server/ServerList.vue')
      },
      {
        path: 'build/:projectId?',
        name: 'BuildList',
        component: () => import('../views/build/BuildList.vue')
      },
      {
        path: 'build-detail/:id',
        name: 'BuildDetail',
        component: () => import('../views/build/BuildDetail.vue')
      },
      {
        path: 'release/:projectId?',
        name: 'ReleaseList',
        component: () => import('../views/release/ReleaseList.vue')
      },
      {
        path: 'user',
        name: 'UserList',
        component: () => import('../views/user/UserList.vue')
      },
      {
        path: 'role',
        name: 'RoleList',
        component: () => import('../views/role/RoleList.vue')
      },
      {
        path: 'menu',
        name: 'MenuList',
        component: () => import('../views/menu/MenuList.vue')
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/profile/Profile.vue')
      },
      {
        path: 'plugin',
        name: 'PluginStore',
        component: () => import('../views/plugin/PluginStore.vue')
      },
      {
        path: 'file',
        name: 'FileManager',
        component: () => import('../views/file/FileManager.vue')
      },
      {
        path: 'log/login',
        name: 'LoginLog',
        component: () => import('../views/log/LoginLog.vue')
      },
      {
        path: 'log/operation',
        name: 'OperationLog',
        component: () => import('../views/log/OperationLog.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.path === '/login') {
    // 已有本地会话时，访问登录地址直接回到仪表盘，避免返回/刷新时再次登录。
    next(token ? '/dashboard' : undefined)
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
