import { createRouter, createWebHistory } from 'vue-router'

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
                { path: 'wallet', component: () => import('../views/user/Wallet.vue') },
                { path: 'notification', component: () => import('../views/user/Notification.vue') },
                { path: 'analytics', component: () => import('../views/user/CreatorAnalytics.vue') },
                { path: 'payment', component: () => import('../views/user/Payment.vue') }
            ]
        },
        // Material Module - 统一的资源管理中心
        {
            path: '/material',
            component: () => import('../layout/Layout.vue'),
            children: [
                { path: '', redirect: '/material/manager' },
                { path: 'manager', component: () => import('../views/material/MaterialManager.vue') },
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
            { path: 'chat', name: 'ChatCreation', component: () => import('../views/creation/ChatCreation.vue') },
            { path: 'result', name: 'ResultDisplay', component: () => import('../views/creation/ResultDisplay.vue') },
            { path: 'task', component: () => import('../views/creation/TaskList.vue') },
            { path: 'project', component: () => import('../views/creation/ProjectEdit.vue') }
            ]
        },
        // Market Module (模版市场)
        {
            path: '/market',
            component: () => import('../layout/Layout.vue'),
            children: [
                { path: '', redirect: '/market/templates' },
                { path: 'templates', component: () => import('../views/market/TemplateList.vue') },
                { path: 'template/:id', component: () => import('../views/market/TemplateDetail.vue') },
                { path: 'following', component: () => import('../views/market/FollowingCreators.vue') }
            ]
        },
        // Admin Module
        {
            path: '/admin',
            component: () => import('../layout/Layout.vue'),
            children: [
                { path: 'review', component: () => import('../views/admin/ContentReview.vue') },
                { path: 'config', component: () => import('../views/admin/SystemConfig.vue') }
            ]
        }
    ]
})

export default router

