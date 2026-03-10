<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
          <el-menu
            router
            default-active="/dashboard"
            class="el-menu-vertical-demo"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
          >
            <!-- 控制台 -->
            <el-menu-item index="/dashboard">
              <el-icon><Menu /></el-icon>
              <span>控制台</span>
            </el-menu-item>

            <!-- 用户中心 -->
            <el-menu-item index="/user/profile">
              <el-icon><User /></el-icon>
              <span>个人资料</span>
            </el-menu-item>
            <el-menu-item index="/user/wallet">
              <el-icon><User /></el-icon>
              <span>我的钱包</span>
            </el-menu-item>

            <!-- 媒资管理 -->
            <el-menu-item index="/material/list">
              <el-icon><Folder /></el-icon>
              <span>素材库</span>
            </el-menu-item>
            <el-menu-item index="/material/template">
              <el-icon><Folder /></el-icon>
              <span>模板库</span>
            </el-menu-item>

            <!-- 创作中心 -->
            <el-menu-item index="/creation/generation">
              <el-icon><VideoCamera /></el-icon>
              <span>AI 创作</span>
            </el-menu-item>
            <el-menu-item index="/creation/task">
              <el-icon><VideoCamera /></el-icon>
              <span>任务管理</span>
            </el-menu-item>
            <el-menu-item index="/creation/project">
              <el-icon><VideoCamera /></el-icon>
              <span>剪辑工程</span>
            </el-menu-item>

            <!-- 社区互动 -->
            <el-menu-item index="/community/feed">
              <el-icon><ChatDotRound /></el-icon>
              <span>社区视频流</span>
            </el-menu-item>
          </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-content">
            <h3>自动化短视频创作平台</h3>
            <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Menu, User, Folder, VideoCamera, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped>
.common-layout {
  height: 100vh;
}
.el-container {
  height: 100%;
}
.el-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.el-aside {
  background-color: #545c64;
}
</style>
