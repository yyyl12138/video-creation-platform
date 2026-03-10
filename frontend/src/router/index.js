import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { clearAuthStorage, getRoles, getToken, isAdminRoles, buildLoginRedirectQuery } from '@/utils/auth'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        // 认证相关页面（无布局）
        {
            path: '/login',
            name: 'Login',
            component: () => import('../views/user/Login.vue')
        },
        {
            path: '/register',
            name: 'Register',
            component: () => import('../views/user/Register.vue')
        },
        {
            path: '/forgot-password',
            name: 'ForgotPassword',
            component: () => import('../views/user/ForgotPassword.vue')
        },
        {
            path: '/user-agreement',
            name: 'UserAgreement',
            component: () => import('../views/user/UserAgreement.vue')
        },
        {
            path: '/privacy-policy',
            name: 'PrivacyPolicy',
            component: () => import('../views/user/PrivacyPolicy.vue')
        },
        
        // 首页
        {
            path: '/home',
            name: 'Home',
            component: () => import('../views/Home.vue')
        },
        
        // 主布局页面
        {
            path: '/',
            component: () => import('../layout/Layout.vue'),
            redirect: '/home',
            children: [
                {
                    path: 'dashboard',
                    component: () => import('../views/admin/Dashboard.vue')
                }
            ]
        },
        // User Module
        {
            path: '/user',
            component: () => import('../layout/Layout.vue'),
            children: [
                { path: 'profile', component: () => import('../views/user/Profile.vue') },
                { path: 'wallet', component: () => import('../views/user/Wallet.vue') }
            ]
        },
        // Material Module
        {
            path: '/material',
            component: () => import('../layout/Layout.vue'),
            children: [
                { path: 'list', component: () => import('../views/material/MaterialList.vue') },
                { path: 'template', component: () => import('../views/material/TemplateList.vue') }
            ]
        },
        // Creation Module
        {
            path: '/creation',
            component: () => import('../layout/Layout.vue'),
            children: [
                { path: 'generation', component: () => import('../views/creation/Generation.vue') },
                { path: 'task', component: () => import('../views/creation/TaskList.vue') },
                { path: 'project', component: () => import('../views/creation/ProjectEdit.vue') }
            ]
        },
        // Community Module
        {
            path: '/community',
            component: () => import('../layout/Layout.vue'),
            children: [
                { path: 'feed', component: () => import('../views/community/Feed.vue') }
            ]
        },
        // Admin Module（独立管理员端布局）
        {
            path: '/admin',
            component: () => import('../layout/AdminLayout.vue'),
            redirect: '/admin/users',
            children: [
                { path: 'users', component: () => import('../views/admin/UserManagement.vue') },
                { path: 'materials', component: () => import('../views/admin/MaterialManagement.vue') },
                { path: 'review', component: () => import('../views/admin/ContentReview.vue') },
                { path: 'rules', component: () => import('../views/admin/ReviewRules.vue') },
                { path: 'models', component: () => import('../views/admin/ModelTtsManagement.vue') },
                { path: 'stats', component: () => import('../views/admin/AdminStats.vue') },
                { path: 'finance', component: () => import('../views/admin/FinanceReport.vue') },
                { path: 'config', component: () => import('../views/admin/SystemConfig.vue') }
            ]
        }
    ]
})

// 管理员路由守卫：只有管理员角色才能访问 /admin
router.beforeEach((to) => {
  const isAdminRoute = to.path.startsWith('/admin')
  if (!isAdminRoute) return true

  const token = getToken()
  if (!token) {
    return {
      path: '/login',
      query: buildLoginRedirectQuery(to.fullPath, 'admin')
    }
  }

  const roles = getRoles()
  if (!isAdminRoles(roles)) {
    ElMessage.error('非管理员账号，无法进入管理后台')
    clearAuthStorage()
    return {
      path: '/login',
      query: buildLoginRedirectQuery(to.fullPath, 'admin')
    }
  }

  return true
})

export default router

