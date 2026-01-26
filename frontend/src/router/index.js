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
