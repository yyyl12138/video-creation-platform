<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <el-menu
          router
          default-active="2"
          class="el-menu-vertical-demo"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b">
          
          <el-menu-item index="/dashboard">
            <el-icon><Menu /></el-icon>
            <span>控制台 (Dashboard)</span>
          </el-menu-item>

          <el-sub-menu index="1">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户中心 (User)</span>
            </template>
            <el-menu-item index="/user/profile">个人资料</el-menu-item>
            <el-menu-item index="/user/wallet">我的钱包</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="2">
            <template #title>
              <el-icon><Folder /></el-icon>
              <span>媒资管理 (Material)</span>
            </template>
            <el-menu-item index="/material/list">素材库</el-menu-item>
            <el-menu-item index="/material/template">模板库</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="3">
            <template #title>
              <el-icon><VideoCamera /></el-icon>
              <span>创作中心 (Creation)</span>
            </template>
            <el-menu-item index="/creation/generation">AI 创作</el-menu-item>
            <el-menu-item index="/creation/task">任务管理</el-menu-item>
            <el-menu-item index="/creation/project">剪辑工程</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="4">
            <template #title>
              <el-icon><ChatDotRound /></el-icon>
              <span>社区互动 (Community)</span>
            </template>
            <el-menu-item index="/community/feed">视频流</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="5">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理 (Admin)</span>
            </template>
            <el-menu-item index="/admin/review">内容审核</el-menu-item>
            <el-menu-item index="/admin/config">系统配置</el-menu-item>
          </el-sub-menu>

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
import { Menu, User, Folder, VideoCamera, ChatDotRound, Setting } from '@element-plus/icons-vue'

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
