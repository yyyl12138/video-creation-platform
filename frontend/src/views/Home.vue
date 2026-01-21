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
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.hero-section {
  text-align: center;
  padding: 100px 20px;
  color: white;
}

.hero-section h1 {
  font-size: 3rem;
  margin-bottom: 20px;
  font-weight: bold;
}

.subtitle {
  font-size: 1.2rem;
  margin-bottom: 40px;
  opacity: 0.9;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.secondary-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.secondary-actions .el-button {
  color: rgba(255, 255, 255, 0.8);
}

.secondary-actions .el-button:hover {
  color: white;
}

.feature-section {
  padding: 80px 20px;
  background: white;
}

.feature-section h2 {
  text-align: center;
  margin-bottom: 50px;
  font-size: 2.5rem;
  color: #333;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.feature-card {
  text-align: center;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}
</style>