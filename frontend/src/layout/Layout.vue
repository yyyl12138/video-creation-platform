<template>
  <div class="liblib-layout">
    <!-- 左侧导航栏 -->
    <div class="sidebar">
      <!-- Logo区域 -->
      <div class="logo-area">
        <div class="logo">
          <div class="logo-icon">
            <svg width="28" height="28" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="100" height="100" rx="18" fill="#3B82F6"/>
              <rect x="20" y="25" width="60" height="50" rx="8" fill="white" opacity="0.95"/>
              <circle cx="50" cy="50" r="14" fill="#3B82F6"/>
              <path d="M40 55L60 45" stroke="white" stroke-width="3" stroke-linecap="round"/>
            </svg>
          </div>
          <span class="logo-text">VideoAI</span>
        </div>
      </div>

      <!-- 导航菜单 -->
      <el-menu
        router
        default-active="/dashboard"
        class="sidebar-menu"
        :default-openeds="['0', '1', '2']">

        <!-- 首页 -->
        <el-menu-item index="/dashboard" class="menu-item">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <!-- 创作中心 -->
        <el-sub-menu index="0" class="menu-group">
          <template #title>
            <el-icon><EditPen /></el-icon>
            <span>创作中心</span>
          </template>
          <el-menu-item index="/creation/generation">
            <span>AI 创作</span>
          </el-menu-item>
          <el-menu-item index="/creation/task">
            <span>任务管理</span>
          </el-menu-item>
          <el-menu-item index="/creation/project">
            <span>剪辑工程</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 用户中心 -->
        <el-sub-menu index="1" class="menu-group">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户中心</span>
          </template>
          <el-menu-item index="/user/profile">
            <span>个人资料</span>
          </el-menu-item>
          <el-menu-item index="/user/wallet">
            <span>我的钱包</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 模版市场 -->
        <el-sub-menu index="2" class="menu-group">
          <template #title>
            <el-icon><Collection /></el-icon>
            <span>模版市场</span>
          </template>
          <el-menu-item index="/market/templates">
            <span>模版列表</span>
          </el-menu-item>
          <el-menu-item index="/market/following">
            <span>关注的创作者</span>
          </el-menu-item>
        </el-sub-menu>

        <!-- 资源管理 -->
        <el-menu-item index="/material/manager" class="menu-item">
          <el-icon><FolderOpened /></el-icon>
          <span>资源管理</span>
        </el-menu-item>

        <!-- 创作者中心 -->
        <el-menu-item index="/user/analytics" class="menu-item">
          <el-icon><TrendCharts /></el-icon>
          <span>创作者中心</span>
        </el-menu-item>

        <!-- 消息通知 -->
        <el-menu-item index="/user/notification" class="menu-item">
          <el-icon><Bell /></el-icon>
          <span>消息通知</span>
        </el-menu-item>
      </el-menu>

      <!-- 底部用户信息 -->
      <div class="user-section">
        <div class="user-info">
          <el-avatar :size="40" class="user-avatar">
            <img v-if="userInfo.avatar" :src="userInfo.avatar" alt="用户头像" />
            <el-icon v-else><User /></el-icon>
          </el-avatar>
          <div class="user-detail">
            <span class="user-name">{{ userInfo.username }}</span>
            <span class="user-role">{{ userInfo.role }}</span>
          </div>
          <el-icon class="logout-icon" @click="handleLogout"><SwitchButton /></el-icon>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  HomeFilled, EditPen, FolderOpened, User, Collection,
  SwitchButton, TrendCharts, Bell
} from '@element-plus/icons-vue'
import { getUserProfile } from '@/api/user/users'

const router = useRouter()

// 用户信息
const userInfo = ref({
  username: '用户',
  role: '创作者',
  avatar: ''
})

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await getUserProfile()
    if (res.data) {
      userInfo.value = {
        username: res.data.username || res.data.realName || '用户',
        role: res.data.role || '创作者',
        avatar: res.data.avatarUrl || ''
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 保持默认值
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.liblib-layout {
  display: flex;
  height: 100vh;
  background: #f7f8fa;
}

/* 侧边栏样式 */
.sidebar {
  width: 240px;
  background: #ffffff;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  border-right: 1px solid #e8eaec;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.02);
}

/* Logo区域 */
.logo-area {
  padding: 16px;
  border-bottom: 1px solid #f0f2f5;
  background: #ffffff;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 8px;
  border-radius: 8px;
}

.logo-icon {
  flex-shrink: 0;
  transition: transform 0.2s ease;
}

.logo:hover .logo-icon {
  transform: scale(1.05);
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #1f2329;
  letter-spacing: -0.3px;
}

/* 菜单样式 */
.sidebar-menu {
  flex: 1;
  border-right: none;
  overflow-y: auto;
  background: #ffffff;
  padding: 8px 0;
}

.sidebar-menu :deep(.el-menu) {
  background: #ffffff !important;
  border-right: none;
}

.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  height: 42px;
  line-height: 42px;
  margin: 0;
  border-radius: 0;
  font-size: 14px;
  color: #4e5969;
  transition: all 0.2s ease;
}

.sidebar-menu :deep(.el-menu-item .el-icon),
.sidebar-menu :deep(.el-sub-menu__title .el-icon) {
  color: #86909c;
  transition: color 0.2s ease;
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: #f2f3f5 !important;
  color: #1f2329 !important;
}

.sidebar-menu :deep(.el-menu-item:hover .el-icon),
.sidebar-menu :deep(.el-sub-menu__title:hover .el-icon) {
  color: #3b82f6 !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: #e8f3ff !important;
  color: #3b82f6 !important;
  font-weight: 500;
}

.sidebar-menu :deep(.el-menu-item.is-active .el-icon) {
  color: #3b82f6 !important;
}

.sidebar-menu :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
  color: #3b82f6 !important;
  background: #f7f8fa !important;
}

.sidebar-menu :deep(.el-sub-menu.is-active > .el-sub-menu__title .el-icon) {
  color: #3b82f6 !important;
}

.sidebar-menu :deep(.el-menu-item) {
  padding-left: 48px !important;
}

.sidebar-menu :deep(.el-sub-menu .el-menu--inline) {
  background: #fafbfc !important;
  position: relative;
}

/* 子菜单项左侧竖线 */
.sidebar-menu :deep(.el-sub-menu .el-menu--inline)::before {
  content: '';
  position: absolute;
  left: 34px;
  top: 8px;
  bottom: 8px;
  width: 2px;
  background: linear-gradient(180deg, #e4e7ed 0%, #dcdfe6 50%, #e4e7ed 100%);
  border-radius: 1px;
}

.sidebar-menu :deep(.el-sub-menu .el-menu--inline .el-menu-item) {
  height: 38px;
  line-height: 38px;
  margin: 0;
  border-radius: 0;
  font-size: 13px;
  color: #4e5969;
  padding-left: 52px !important;
  position: relative;
}

/* 子菜单项激活状态下的竖线 */
.sidebar-menu :deep(.el-sub-menu .el-menu--inline .el-menu-item.is-active)::after {
  content: '';
  position: absolute;
  left: 34px;
  top: 50%;
  transform: translateY(-50%);
  width: 2px;
  height: 20px;
  background: #3b82f6;
  border-radius: 1px;
  z-index: 1;
}

.sidebar-menu :deep(.el-sub-menu .el-menu--inline .el-menu-item:hover) {
  background: #f2f3f5 !important;
  color: #1f2329 !important;
}

.sidebar-menu :deep(.el-sub-menu .el-menu--inline .el-menu-item.is-active) {
  background: #e8f3ff !important;
  color: #3b82f6 !important;
}

.sidebar-menu :deep(.el-icon) {
  width: 16px;
  height: 16px;
  margin-right: 8px;
  font-size: 16px;
}

.sidebar-menu :deep(.el-sub-menu__icon-arrow) {
  color: #c9cdd4;
  transition: transform 0.3s ease;
}

/* 用户信息区域 */
.user-section {
  padding: 12px 16px;
  border-top: 1px solid #f0f2f5;
  background: #ffffff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px;
  border-radius: 8px;
  transition: background 0.2s ease;
}

.user-info:hover {
  background: #f2f3f5;
}

.user-avatar {
  background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
  flex-shrink: 0;
  border: 2px solid #e8f3ff;
}

.user-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.user-name {
  font-size: 13px;
  font-weight: 500;
  color: #1f2329;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 12px;
  color: #86909c;
}

.logout-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #86909c;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s ease;
  font-size: 16px;
}

.logout-icon:hover {
  color: #f53f3f;
  background: #f2f3f5;
}

/* 主内容区 */
.main-content {
  flex: 1;
  overflow-y: auto;
  background: #f7f8fa;
}
</style>