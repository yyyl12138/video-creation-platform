<template>
  <div class="home-container">
    <div class="hero-section">
      <h1>自动化短视频创作平台</h1>
      <p class="subtitle">让创作更简单，让内容更精彩</p>
      
      <div class="hero-actions">
        <el-button type="primary" size="large" @click="goToLogin">
          立即登录
        </el-button>
        <el-button size="large" @click="goToRegister">
          注册账号
        </el-button>
      </div>
      
      <div class="secondary-actions">
        <el-button link @click="goToCreation">开始创作</el-button>
        <el-button link @click="goToCommunity">探索社区</el-button>
      </div>
    </div>

    <div class="feature-section">
      <h2>核心功能</h2>
      <div class="feature-grid">
        <el-card class="feature-card">
          <template #header>
            <h3>智能创作</h3>
          </template>
          <p>AI驱动的视频生成和编辑工具</p>
        </el-card>
        
        <el-card class="feature-card">
          <template #header>
            <h3>丰富素材</h3>
          </template>
          <p>海量模板、音乐和特效资源</p>
        </el-card>
        
        <el-card class="feature-card">
          <template #header>
            <h3>社区互动</h3>
          </template>
          <p>分享作品，获取灵感</p>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToLogin = () => {
  router.push('/login')
}

const goToRegister = () => {
  router.push('/register')
}

const goToCreation = () => {
  router.push('/creation')
}

const goToCommunity = () => {
  router.push('/community')
}
</script>

<style scoped>
/* 全局容器 - 蓝白渐变+梦幻光影背景 */
.home-container {
  min-height: 100vh;
  background: linear-gradient(120deg, #f0f8ff 0%, #e6f7ff 50%, #d1eaff 100%);
  position: relative;
  overflow: hidden;
}

/* 梦幻光影装饰 - 动态漂浮效果 */
.home-container::before,
.home-container::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  filter: blur(80px);
  z-index: 0;
  animation: float 15s ease-in-out infinite;
}

.home-container::before {
  width: 600px;
  height: 600px;
  top: -200px;
  right: -200px;
  background: rgba(103, 194, 255, 0.2);
  animation-delay: 0s;
}

.home-container::after {
  width: 500px;
  height: 500px;
  bottom: -150px;
  left: -150px;
  background: rgba(145, 213, 255, 0.2);
  animation-delay: 7.5s;
}

/* 首屏区域 */
.hero-section {
  text-align: center;
  padding: 120px 20px;
  color: #1e40af; /* 深蓝主色 */
  position: relative;
  z-index: 1;
}

.hero-section h1 {
  font-size: 3.2rem;
  margin-bottom: 20px;
  font-weight: 600;
  background: linear-gradient(90deg, #1e40af, #3b82f6);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  animation: fadeUp 1s ease-out;
}

.subtitle {
  font-size: 1.2rem;
  margin-bottom: 50px;
  color: #475569; /* 浅蓝灰 */
  opacity: 0.9;
  animation: fadeUp 1s ease-out 0.3s both;
}

/* 按钮区域 */
.hero-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
  animation: fadeUp 1s ease-out 0.6s both;
}

/* 重写Element UI按钮样式 - 蓝白主题 */
.hero-actions .el-button--primary {
  background: #3b82f6;
  border-color: #3b82f6;
  padding: 12px 30px;
  border-radius: 50px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.hero-actions .el-button--primary:hover {
  background: #2563eb;
  border-color: #2563eb;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);
}

.hero-actions .el-button--default {
  background: #ffffff;
  color: #3b82f6;
  border-color: #dbeafe;
  padding: 12px 30px;
  border-radius: 50px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.hero-actions .el-button--default:hover {
  background: #f0f9ff;
  border-color: #bfdbfe;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(219, 234, 254, 0.4);
}

/* 次要按钮 */
.secondary-actions {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 40px;
  animation: fadeUp 1s ease-out 0.9s both;
}

.secondary-actions .el-button {
  color: #3b82f6;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.secondary-actions .el-button:hover {
  color: #2563eb;
  transform: translateY(-2px);
}

/* 功能板块 */
.feature-section {
  padding: 100px 20px;
  background: #ffffff;
  position: relative;
  z-index: 1;
}

.feature-section h2 {
  text-align: center;
  margin-bottom: 60px;
  font-size: 2.2rem;
  color: #1e40af;
  font-weight: 600;
  position: relative;
}

/* 标题下方梦幻线条 */
.feature-section h2::after {
  content: '';
  position: absolute;
  bottom: -15px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6, #93c5fd);
  border-radius: 3px;
}

/* 功能卡片网格 */
.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 40px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 功能卡片样式 */
.feature-card {
  text-align: center;
  border: none;
  border-radius: 16px;
  padding: 40px 20px;
  background: #f8fafc;
  box-shadow: 0 4px 20px rgba(219, 234, 254, 0.5);
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;
}

/* 卡片hover效果 */
.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(147, 197, 253, 0.4);
}

/* 卡片头部样式 */
.feature-card :deep(.el-card__header) {
  border-bottom: none;
  padding: 0 0 15px 0;
}

.feature-card h3 {
  font-size: 1.5rem;
  color: #1e40af;
  font-weight: 600;
}

.feature-card p {
  color: #64748b;
  font-size: 1rem;
  line-height: 1.6;
}

/* 卡片梦幻装饰线 */
.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #3b82f6, #93c5fd);
}

/* 动态动画定义 */
@keyframes float {
  0%, 100% {
    transform: translate(0, 0);
  }
  50% {
    transform: translate(20px, -20px);
  }
}

@keyframes fadeUp {
  0% {
    opacity: 0;
    transform: translateY(30px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式适配 */
@media (max-width: 768px) {
  .hero-section h1 {
    font-size: 2.5rem;
  }
  
  .feature-section h2 {
    font-size: 1.8rem;
  }
  
  .feature-grid {
    grid-template-columns: 1fr;
    gap: 30px;
  }
}
</style>