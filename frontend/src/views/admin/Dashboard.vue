<template>
  <div class="dashboard-container">
    <!-- 顶部欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="banner-text">
          <h1>欢迎回来，<span class="highlight">{{ userName }}</span></h1>
          <p class="banner-subtitle">开始您的智能视频创作之旅</p>
        </div>
        <div class="banner-actions">
          <el-button type="primary" size="large" @click="goToCreation">
            <el-icon><VideoCamera /></el-icon>
            立即创作
          </el-button>
          <el-button size="large" @click="goToMaterials">
            <el-icon><FolderOpened /></el-icon>
            素材库
          </el-button>
        </div>
      </div>
    </div>

    <!-- 数据统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon video">
          <el-icon><VideoPlay /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalVideos }}</div>
          <div class="stat-label">视频总数</div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon task">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.pendingTasks }}</div>
          <div class="stat-label">进行中任务</div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon material">
          <el-icon><Picture /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalMaterials }}</div>
          <div class="stat-label">素材数量</div>
        </div>
      </el-card>

      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon balance">
          <el-icon><Wallet /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ stats.balance }}</div>
          <div class="stat-label">账户余额</div>
        </div>
      </el-card>
    </div>

    <!-- 快捷操作区 -->
    <div class="quick-actions">
      <h3 class="section-title">
        <el-icon><Lightning /></el-icon>
        快捷操作
      </h3>
      <div class="action-grid">
        <div class="action-item" @click="goToCreation">
          <div class="action-icon ai-text">
            <el-icon><DocumentCopy /></el-icon>
          </div>
          <div class="action-text">
            <div class="action-name">AI 文本生成</div>
            <div class="action-desc">智能生成文案脚本</div>
          </div>
        </div>

        <div class="action-item" @click="goToCreation">
          <div class="action-icon ai-image">
            <el-icon><Picture /></el-icon>
          </div>
          <div class="action-text">
            <div class="action-name">AI 文生图</div>
            <div class="action-desc">一键生成精美图片</div>
          </div>
        </div>

        <div class="action-item" @click="goToCreation">
          <div class="action-icon ai-video">
            <el-icon><VideoCamera /></el-icon>
          </div>
          <div class="action-text">
            <div class="action-name">AI 文生视频</div>
            <div class="action-desc">智能生成视频内容</div>
          </div>
        </div>

        <div class="action-item" @click="goToCreation">
          <div class="action-icon ai-img2vid">
            <el-icon><PictureFilled /></el-icon>
          </div>
          <div class="action-text">
            <div class="action-name">图生视频</div>
            <div class="action-desc">图片转动态视频</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 最近作品 -->
    <div class="recent-works" v-if="recentWorks.length > 0">
      <div class="section-header">
        <h3 class="section-title">
          <el-icon><Clock /></el-icon>
          最近作品
        </h3>
        <el-button text @click="goToTasks">查看全部</el-button>
      </div>
      <div class="works-grid">
        <div class="work-item" v-for="work in recentWorks" :key="work.id" @click="viewWork(work)">
          <div class="work-thumbnail">
            <el-icon v-if="work.type === 'video'" class="type-icon"><VideoPlay /></el-icon>
            <el-icon v-else class="type-icon"><Picture /></el-icon>
          </div>
          <div class="work-info">
            <div class="work-name">{{ work.name }}</div>
            <div class="work-time">{{ formatTime(work.createdAt) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态（无作品时显示） -->
    <div class="empty-state" v-else>
      <el-empty description="暂无作品">
        <el-button type="primary" @click="goToCreation">开始创作</el-button>
      </el-empty>
    </div>

    <!-- 新手引导提示 -->
    <div class="guide-card" v-if="isFirstTime">
      <div class="guide-content">
        <div class="guide-icon">
          <el-icon size="40"><Trophy /></el-icon>
        </div>
        <div class="guide-text">
          <h4>新手引导</h4>
          <p>完成以下任务，快速了解平台功能</p>
        </div>
        <div class="guide-steps">
          <div class="step-item" :class="{ completed: step1 }">
            <el-icon v-if="step1" class="check-icon"><CircleCheckFilled /></el-icon>
            <span v-else class="step-number">1</span>
            <span>上传第一个素材</span>
          </div>
          <div class="step-item" :class="{ completed: step2 }">
            <el-icon v-if="step2" class="check-icon"><CircleCheckFilled /></el-icon>
            <span v-else class="step-number">2</span>
            <span>体验 AI 文生视频</span>
          </div>
          <div class="step-item" :class="{ completed: step3 }">
            <el-icon v-if="step3" class="check-icon"><CircleCheckFilled /></el-icon>
            <span v-else class="step-number">3</span>
            <span>完成第一个视频</span>
          </div>
        </div>
        <el-button type="primary" @click="dismissGuide">知道了</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  VideoCamera, VideoPlay, FolderOpened, Clock, Wallet,
  Picture, PictureFilled, DocumentCopy, Lightning,
  Trophy, CircleCheckFilled
} from '@element-plus/icons-vue'

const router = useRouter()

// 用户信息
const userName = ref('创作者')

// 统计数据（实际应从 API 获取）
const stats = reactive({
  totalVideos: 0,
  pendingTasks: 0,
  totalMaterials: 0,
  balance: '0.00'
})

// 最近作品（模拟数据）
const recentWorks = ref([])

// 新手引导
const isFirstTime = ref(false)
const step1 = ref(false)
const step2 = ref(false)
const step3 = ref(false)

// 导航方法
const goToCreation = () => {
  router.push('/creation/generation')
}

const goToMaterials = () => {
  router.push('/material/list')
}

const goToTasks = () => {
  router.push('/creation/task')
}

const viewWork = (work) => {
  console.log('查看作品:', work.id)
  // 实际应跳转到作品详情页
}

const dismissGuide = () => {
  isFirstTime.value = false
  localStorage.setItem('guideDismissed', 'true')
}

// 格式化时间
const formatTime = (time) => {
  const now = Date.now()
  const diff = now - new Date(time).getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return new Date(time).toLocaleDateString()
}

// 初始化
onMounted(() => {
  // 获取用户信息（实际应从 API）
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    userName.value = JSON.parse(userInfo).nickname || '创作者'
  }

  // 检查是否首次使用
  const guideDismissed = localStorage.getItem('guideDismissed')
  isFirstTime.value = !guideDismissed

  // 实际应从 API 获取统计数据
  // loadStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: 100%;
  position: relative;
  overflow: hidden;
}

/* 顶部欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 40px;
  margin-bottom: 24px;
  position: relative;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3);
}

.welcome-banner::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 16px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, transparent 50%, rgba(255, 255, 255, 0.05) 100%);
  pointer-events: none;
}

.banner-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.banner-text h1 {
  margin: 0 0 8px 0;
  font-size: 1.8rem;
  font-weight: 600;
  color: #fff;
  letter-spacing: 1px;
}

.highlight {
  color: #ffd700;
}

.banner-subtitle {
  margin: 0;
  font-size: 1rem;
  color: rgba(255, 255, 255, 0.9);
}

.banner-actions {
  display: flex;
  gap: 12px;
}

.banner-actions .el-button {
  height: 44px;
  padding: 0 24px;
  font-size: 0.95rem;
}

.banner-actions .el-button--default {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
  color: #fff;
}

.banner-actions .el-button--default:hover {
  background: rgba(255, 255, 255, 0.3);
}

.banner-actions .el-button--primary {
  background: #fff;
  border-color: #fff;
  color: #667eea;
  font-weight: 600;
}

.banner-actions .el-button--primary:hover {
  background: #f5f7fa;
}

/* 数据统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: none;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.stat-icon.video {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.stat-icon.task {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3);
}

.stat-icon.material {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
}

.stat-icon.balance {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  box-shadow: 0 4px 12px rgba(67, 233, 123, 0.3);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.85rem;
  color: #909399;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 快捷操作区 */
.quick-actions {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 20px 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #303133;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 12px;
  background: #f5f7fa;
  border: 1px solid transparent;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #fff;
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
}

.action-icon.ai-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.action-icon.ai-image {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3);
}

.action-icon.ai-video {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
}

.action-icon.ai-img2vid {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  box-shadow: 0 4px 12px rgba(67, 233, 123, 0.3);
}

.action-text {
  flex: 1;
}

.action-name {
  font-size: 0.95rem;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 0.8rem;
  color: #909399;
}

/* 最近作品 */
.recent-works {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.works-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.work-item {
  border-radius: 12px;
  overflow: hidden;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.work-item:hover {
  transform: translateY(-4px);
  background: #fff;
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.work-thumbnail {
  height: 120px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  position: relative;
  overflow: hidden;
}

.work-thumbnail::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, transparent 50%, rgba(255, 255, 255, 0.05) 100%);
}

.work-thumbnail .type-icon {
  font-size: 36px;
  position: relative;
  z-index: 1;
}

.work-info {
  padding: 12px;
  background: #fff;
}

.work-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.work-time {
  font-size: 0.8rem;
  color: #909399;
}

/* 空状态 */
.empty-state {
  background: #fff;
  border-radius: 12px;
  padding: 60px 20px;
  text-align: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 新手引导卡片 */
.guide-card {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 12px;
  padding: 24px;
  margin-top: 24px;
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.guide-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.guide-icon {
  color: #667eea;
}

.guide-text h4 {
  margin: 0 0 4px 0;
  font-size: 1.1rem;
  color: #303133;
}

.guide-text p {
  margin: 0;
  font-size: 0.9rem;
  color: #909399;
}

.guide-steps {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin: 0 20px;
}

.step-item {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #303133;
  font-size: 0.9rem;
}

.step-item.completed {
  opacity: 0.5;
}

.step-number {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #667eea;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
  font-weight: 600;
  color: #fff;
}

.check-icon {
  font-size: 20px;
  color: #67c23a;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .welcome-banner {
    padding: 24px;
  }

  .banner-content {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }

  .banner-text h1 {
    font-size: 1.4rem;
  }

  .banner-actions {
    width: 100%;
    flex-direction: column;
  }

  .banner-actions .el-button {
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .action-grid {
    grid-template-columns: 1fr;
  }

  .works-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }

  .guide-content {
    flex-direction: column;
    text-align: center;
  }

  .guide-steps {
    margin: 16px 0;
  }

  .step-item {
    justify-content: center;
  }
}
</style>
