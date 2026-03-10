<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="220px" class="sidebar">
        <div class="logo-area">
          <div class="logo-mark">VC</div>
          <div class="logo-text">
            <div class="logo-title">视频创作平台</div>
            <div class="logo-subtitle">Admin Console</div>
          </div>
        </div>
        <el-menu
          router
          :default-active="activeMenu"
          class="menu-vertical"
          background-color="transparent"
          text-color="#d5e2ff"
          active-text-color="#fff"
        >
          <el-menu-item index="/admin/stats">
            <el-icon><DataAnalysis /></el-icon>
            <span>运营看板</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/materials">
            <el-icon><FolderOpened /></el-icon>
            <span>素材管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/review">
            <el-icon><Memo /></el-icon>
            <span>内容审核</span>
          </el-menu-item>
          <el-menu-item index="/admin/rules">
            <el-icon><Finished /></el-icon>
            <span>审核规则</span>
          </el-menu-item>
          <el-menu-item index="/admin/models">
            <el-icon><Cpu /></el-icon>
            <span>模型与 TTS</span>
          </el-menu-item>
          <el-menu-item index="/admin/finance">
            <el-icon><Wallet /></el-icon>
            <span>财务报表</span>
          </el-menu-item>
          <el-menu-item index="/admin/config">
            <el-icon><Setting /></el-icon>
            <span>系统配置</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container class="main-container">
        <el-header class="header">
          <div class="header-left">
            <div class="header-title">管理后台</div>
            <div class="header-breadcrumb">
              高级运营与配置中心
            </div>
          </div>
          <div class="header-right">
            <el-dropdown trigger="click" placement="bottom-end">
              <div class="admin-trigger">
                <el-avatar :src="avatarUrl" :size="28" class="admin-avatar">
                  {{ avatarFallback }}
                </el-avatar>
                <div class="admin-trigger-text">
                  <div class="admin-name">{{ displayName }}</div>
                  <div class="admin-role">{{ roleLabel }}</div>
                </div>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="admin-dropdown">
                  <el-dropdown-item disabled class="admin-dropdown-info">
                    <el-descriptions :column="1" border size="small">
                      <el-descriptions-item label="用户ID">
                        {{ mergedInfo.userId || '-' }}
                      </el-descriptions-item>
                      <el-descriptions-item label="用户名">
                        {{ mergedInfo.username || '-' }}
                      </el-descriptions-item>
                      <el-descriptions-item label="手机号">
                        {{ mergedInfo.phone || '-' }}
                      </el-descriptions-item>
                      <el-descriptions-item label="邮箱">
                        {{ mergedInfo.email || '-' }}
                      </el-descriptions-item>
                      <el-descriptions-item label="角色">
                        {{ (authUserInfo.roles || []).join(', ') || '-' }}
                      </el-descriptions-item>
                      <el-descriptions-item label="账号状态">
                        {{ mergedInfo.status || '-' }}
                      </el-descriptions-item>
                      <el-descriptions-item label="会员状态">
                        {{ mergedInfo.vipStatus || '-' }}
                      </el-descriptions-item>
                    </el-descriptions>
                  </el-dropdown-item>
                  <el-dropdown-item divided class="admin-dropdown-logout" @click="handleLogout">
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main class="main">
          <div class="main-inner">
            <router-view />
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Cpu,
  DataAnalysis,
  Finished,
  FolderOpened,
  Memo,
  Setting,
  User,
  Wallet
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { clearAuthStorage, getUserInfo } from '@/utils/auth'
import { getUserProfile } from '@/api/user/users'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

const authUserInfo = ref(getUserInfo() || {})
const userProfile = ref(null)

const mergedInfo = computed(() => {
  // authUserInfo: { userId, username, avatarUrl, roles }
  // userProfile: /users/me 返回更多字段（phone/email/status...）
  return {
    ...(authUserInfo.value || {}),
    ...(userProfile.value || {})
  }
})

const avatarUrl = computed(() => mergedInfo.value?.avatarUrl || '')
const displayName = computed(() => {
  const p = mergedInfo.value?.profile
  return p?.realName || mergedInfo.value?.username || '管理员'
})
const avatarFallback = computed(() => {
  const name = (mergedInfo.value?.username || 'A').toString()
  return name.slice(0, 1).toUpperCase()
})
const roleLabel = computed(() => {
  const roles = authUserInfo.value?.roles
  if (Array.isArray(roles) && roles.length > 0) return roles.join(', ')
  return '平台管理员'
})

const handleLogout = () => {
  clearAuthStorage()
  router.push({ path: '/login', query: { mode: 'admin' } })
}

onMounted(async () => {
  // 补全管理员详细信息（手机号/邮箱/状态/VIP等）
  try {
    const res = await getUserProfile()
    userProfile.value = res.data || null
    localStorage.setItem('userProfile', JSON.stringify(userProfile.value || {}))
  } catch (e) {
    // 不阻塞页面，仅提示一次
    ElMessage.warning('获取管理员信息失败，将仅展示基础信息')
  }
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  /* 若依风格：浅灰背景 + 深色文字 */
  background: #f0f2f5;
  color: #1f2933;
}

.el-container {
  height: 100%;
}

.sidebar {
  /* 若依风格深蓝侧边栏 */
  background: linear-gradient(180deg, #001529, #000c17);
  border-right: 1px solid #001529;
  display: flex;
  flex-direction: column;
  padding: 16px 12px;
  box-sizing: border-box;
  color: #e5edff;
}

.logo-area {
  display: flex;
  align-items: center;
  padding: 8px 10px 16px;
  margin-bottom: 8px;
  border-bottom: 1px solid rgba(191, 219, 254, 0.5);
}

.logo-mark {
  width: 32px;
  height: 32px;
  border-radius: 10px;
  /* 深蓝背景下的亮蓝 Logo，高级一点的渐变 */
  background: linear-gradient(135deg, #1890ff, #40a9ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  color: #0f172a;
  margin-right: 10px;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-title {
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
}

.logo-subtitle {
  font-size: 11px;
  color: #8c9bb3;
}

.menu-vertical {
  border-right: none;
  margin-top: 8px;
  background: transparent;
}

.menu-vertical :deep(.el-menu-item) {
  border-radius: 8px;
  margin: 2px 0;
  height: 40px;
  line-height: 40px;
}

.menu-vertical :deep(.el-menu-item.is-active) {
  /* 深蓝体系下的选中态，参考若依/Ant Design */
  background: linear-gradient(90deg, rgba(24, 144, 255, 0.2), rgba(24, 144, 255, 0.05));
  box-shadow: none;
  color: #ffffff !important;
}

.menu-vertical :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.06);
}

.main-container {
  background: transparent;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  border-bottom: 1px solid #e5e7eb;
  background: #ffffff;
  box-sizing: border-box;
}

.header-left {
  display: flex;
  flex-direction: column;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  /* 标题用深灰而不是高饱和蓝，更接近若依 */
  color: #303133;
}

.header-breadcrumb {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 10px;
  border: 1px solid #eef2f7;
  background: #ffffff;
}

.admin-trigger:hover {
  background: #f7f9fc;
}

.admin-trigger-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  line-height: 1.1;
}

.admin-name {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.admin-role {
  font-size: 11px;
  color: #909399;
  margin-top: 2px;
}

.admin-dropdown :deep(.el-dropdown-menu__item) {
  white-space: normal;
}

.admin-dropdown-info {
  cursor: default !important;
  padding: 10px 12px !important;
}

.admin-dropdown-logout {
  color: #f56c6c;
}

.main {
  padding: 16px 24px;
  box-sizing: border-box;
}

.main-inner {
  height: 100%;
  border-radius: 16px;
  background: #ffffff;
  box-shadow:
    0 6px 18px rgba(0, 0, 0, 0.06);
  padding: 18px 20px 20px;
  box-sizing: border-box;
  overflow: auto;
}

@media (max-width: 992px) {
  .admin-layout {
    background: #eff6ff;
  }

  .sidebar {
    width: 72px !important;
    padding: 12px 8px;
  }

  .logo-text {
    display: none;
  }

  .header {
    padding-inline: 16px;
  }

  .main {
    padding: 12px 12px 16px;
  }

  .main-inner {
    border-radius: 12px;
    padding: 14px 12px 16px;
  }
}
</style>

