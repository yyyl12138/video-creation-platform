<template>
  <div class="home-container">
    <!-- 顶部导航 -->
    <header class="navbar">
      <div class="navbar-inner">
        <div class="navbar-left">
          <div class="logo">
            <svg width="32" height="32" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="100" height="100" rx="18" fill="#3B82F6"/>
              <rect x="20" y="25" width="60" height="50" rx="8" fill="white" opacity="0.95"/>
              <circle cx="50" cy="50" r="14" fill="#3B82F6"/>
              <path d="M40 55L60 45" stroke="white" stroke-width="3" stroke-linecap="round"/>
            </svg>
            <span class="logo-text">VideoAI</span>
          </div>
        </div>
        <nav class="nav-links">
          <a class="nav-link active">首页</a>
          <a class="nav-link" @click="goToCreation">AI创作</a>
          <a class="nav-link" @click="goToCommunity">探索</a>
          <a class="nav-link" @click="goToMaterial">素材库</a>
        </nav>
        <div class="navbar-right">
          <el-button type="primary" class="login-btn" @click="goToLogin">登录</el-button>
          <el-button class="register-btn" @click="goToRegister">注册</el-button>
        </div>
      </div>
    </header>

    <!-- Hero区域 - 即梦风格 -->
    <section class="hero-section">
      <div class="hero-bg">
        <video
          ref="bgVideo"
          class="hero-video"
          autoplay
          muted
          loop
          playsinline
          :src="backgroundVideo"
        ></video>
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content">
        <h1 class="hero-title">即刻创作</h1>
        <p class="hero-desc">AI驱动的智能视频创作平台，让创意触手可及</p>
        <el-button type="primary" size="large" class="hero-btn" @click="goToCreation">
          开始创作
          <el-icon class="btn-icon"><ArrowRight /></el-icon>
        </el-button>
        <p class="video-credit">此视频内容由 VideoAI 生成</p>
      </div>
    </section>

    <!-- 视频生成功能展示 -->
    <section class="feature-section">
      <div class="section-label">支持文/图 生视频</div>
      <h2 class="section-title">灵感即刻成片</h2>
      <p class="section-subtitle">流畅运镜，生动自然</p>
      
      <div class="feature-grid">
        <div class="feature-item">
          <div class="feature-num">01</div>
          <div class="feature-info">
            <h3>从首帧到尾帧，精准掌控</h3>
            <p>创新的首帧图片和尾帧图片输入方式，增强视频生成的可控性，轻松打造高品质素材</p>
            <el-button type="primary" @click="goToCreation">立即创作</el-button>
          </div>
          <div class="feature-video">
            <div class="video-wrapper">
              <video
                ref="video01"
                class="demo-video"
                autoplay
                muted
                loop
                playsinline
                :src="previewVideo01"
              ></video>
              <div class="video-frame">
                <div class="frame-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
            <div class="video-controls">
              <el-button circle size="small" @click="toggleVideo01">
                <el-icon v-if="!video01Playing"><VideoPlay /></el-icon>
                <el-icon v-else><VideoPause /></el-icon>
              </el-button>
              <span class="video-label">首帧尾帧控制演示</span>
            </div>
          </div>
        </div>
        
        <div class="feature-item reverse">
          <div class="feature-num">02</div>
          <div class="feature-info">
            <h3>中文创作，得心应手</h3>
            <p>支持根据中文提示词进行创作，拥有更好的语义理解能力，准确把握你的需求</p>
            <el-button type="primary" @click="goToCreation">立即创作</el-button>
          </div>
          <div class="feature-video">
            <div class="video-wrapper">
              <video
                ref="video02"
                class="demo-video"
                autoplay
                muted
                loop
                playsinline
                :src="previewVideo02"
              ></video>
              <div class="video-frame">
                <div class="frame-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
            <div class="video-controls">
              <el-button circle size="small" @click="toggleVideo02">
                <el-icon v-if="!video02Playing"><VideoPlay /></el-icon>
                <el-icon v-else><VideoPause /></el-icon>
              </el-button>
              <span class="video-label">中文创作演示</span>
            </div>
          </div>
        </div>
        
        <div class="feature-item">
          <div class="feature-num">03</div>
          <div class="feature-info">
            <h3>智能剪辑，一键成片</h3>
            <p>AI智能分析视频内容，自动剪辑精彩片段，让你的创作更加高效便捷</p>
            <el-button type="primary" @click="goToCreation">立即创作</el-button>
          </div>
          <div class="feature-video">
            <div class="video-wrapper">
              <video
                ref="video03"
                class="demo-video"
                autoplay
                muted
                loop
                playsinline
                :src="previewVideo03"
              ></video>
              <div class="video-frame">
                <div class="frame-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
            <div class="video-controls">
              <el-button circle size="small" @click="toggleVideo03">
                <el-icon v-if="!video03Playing"><VideoPlay /></el-icon>
                <el-icon v-else><VideoPause /></el-icon>
              </el-button>
              <span class="video-label">智能剪辑演示</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- AI绘画展示 -->
    <section class="painting-section">
      <div class="section-label">支持文/图生图片</div>
      <h2 class="section-title">AI绘画 梦境成真</h2>
      <p class="section-desc">仅需简单提示词，即可生成精彩的图片。你还可以对现有图片进行创意改造，满足各种场景的创作需求</p>
      
      <div class="painting-gallery">
        <div class="painting-item" v-for="(item, index) in paintingWorks" :key="index">
          <div class="painting-img" :style="{ background: item.color }">
            <div class="painting-placeholder">
              <el-icon><Picture /></el-icon>
            </div>
          </div>
          <p class="painting-title">{{ item.title }}</p>
        </div>
      </div>
      
      <el-button type="primary" size="large" class="painting-btn" @click="goToCreation">
        开始创作
        <el-icon class="btn-icon"><ArrowRight /></el-icon>
      </el-button>
    </section>

    <!-- 创意社区 -->
    <section class="community-section">
      <h2 class="section-title">创意涌动 灵感绽放</h2>
      <p class="section-desc">在VideoAI的创意社区，与其他用户共同探索无限的影像灵感</p>
      
      <div class="community-grid">
        <div class="community-item" v-for="(work, index) in communityWorks" :key="index">
          <div class="work-img" :style="{ background: work.color }">
            <div class="work-placeholder">
              <el-icon><Picture /></el-icon>
            </div>
          </div>
          <div class="work-info">
            <span class="work-author">{{ work.author }}</span>
            <span class="work-likes">
              <el-icon><Star /></el-icon>
              {{ work.likes }}
            </span>
          </div>
        </div>
      </div>
      
      <el-button type="primary" size="large" class="community-btn" @click="goToCommunity">
        获取创作灵感
        <el-icon class="btn-icon"><ArrowRight /></el-icon>
      </el-button>
    </section>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-nav">
        <a class="footer-link">首页</a>
        <a class="footer-link" @click="goToCreation">AI创作</a>
        <a class="footer-link" @click="goToCommunity">探索</a>
        <a class="footer-link" @click="goToMaterial">素材库</a>
      </div>
      <div class="footer-divider"></div>
      <div class="footer-info">
        <a class="footer-link">用户协议</a>
        <a class="footer-link">隐私政策</a>
      </div>
      <p class="copyright">&copy; 2024 VideoAI. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'
import { ArrowRight, Picture, Star, VideoPlay, VideoPause } from '@element-plus/icons-vue'

const router = useRouter()
const bgVideo = ref(null)
const video01 = ref(null)
const video02 = ref(null)
const video03 = ref(null)

// 视频播放状态
const video01Playing = ref(true)
const video02Playing = ref(true)
const video03Playing = ref(true)

// 视频路径
const backgroundVideo = ref('/videos/8ad1a29fab8541b387f425c05b0d9801.mp4')
const previewVideo01 = ref('/videos/990e235536fc4a598ff9f4f418c401e2.mp4')
const previewVideo02 = ref('/videos/8b21085e2a7d470c98282b433918ec0b.mp4')
const previewVideo03 = ref('/videos/AnimateDiff_00003.mp4')

// AI绘画作品数据
const paintingWorks = ref([
  { title: '3D游戏人物风格，戴夸张耳饰的民族风少女', image: '/images/造相-Z-image.png' },
  { title: '插画风格，太空飞行器在粉色星球低空飞行', image: '/images/1bd7b4f9e6be1923c157e4fb2888551467986f6ceb1c4e06e7a1635d5f845e75.png' },
  { title: '未来风，女性机械人形AI', image: '/images/bab80a199c0428bf223c4ee11bc535682e0badaf2698252b079928ed0dc465fe.png' },
  { title: '超写实插画，夜晚月光下的花园', image: '/images/造相.png' },
  { title: '文艺复兴风格，身着贵族丝绸服饰猫咪', image: '/images/f0f41257ae3b777e7617b2109499c74035bb31838afde028a9f38bcb449c6acd.png' },
  { title: '电影质感，盔甲勇士在火光下奔赴战场', image: '/images/20260121_172146_1534268270.png' }
])

// 社区作品数据
const communityWorks = ref([
  { author: '创作者A', likes: 52, image: '/images/ecommerce_20260123_151306_952717137.png' },
  { author: '创作者B', likes: 26, image: '/images/ecommerce_20260123_151849_4207435666.png' },
  { author: '创作者C', likes: 56, image: '/images/ecommerce_20260123_154739_61970699.png' },
  { author: '创作者D', likes: 35, image: '/images/造相-Z-image.png' },
  { author: '创作者E', likes: 34, image: '/images/1bd7b4f9e6be1923c157e4fb2888551467986f6ceb1c4e06e7a1635d5f845e75.png' },
  { author: '创作者F', likes: 10, image: '/images/bab80a199c0428bf223c4ee11bc535682e0badaf2698252b079928ed0dc465fe.png' },
  { author: '创作者G', likes: 25, image: '/images/造相.png' },
  { author: '创作者H', likes: 19, image: '/images/f0f41257ae3b777e7617b2109499c74035bb31838afde028a9f38bcb449c6acd.png' },
  { author: '创作者I', likes: 10, image: '/images/20260121_172146_1534268270.png' },
  { author: '创作者J', likes: 16, image: '/images/ecommerce_20260123_151306_952717137.png' }
])

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

const goToMaterial = () => {
  router.push('/material')
}

// 视频控制功能
const toggleVideo01 = () => {
  if (video01.value) {
    if (video01.value.paused) {
      video01.value.play()
      video01Playing.value = true
    } else {
      video01.value.pause()
      video01Playing.value = false
    }
  }
}

const toggleVideo02 = () => {
  if (video02.value) {
    if (video02.value.paused) {
      video02.value.play()
      video02Playing.value = true
    } else {
      video02.value.pause()
      video02Playing.value = false
    }
  }
}

const toggleVideo03 = () => {
  if (video03.value) {
    if (video03.value.paused) {
      video03.value.play()
      video03Playing.value = true
    } else {
      video03.value.pause()
      video03Playing.value = false
    }
  }
}
</script>

<style scoped>
/* 全局样式 */
.home-container {
  min-height: 100vh;
  background: #020205;
  color: #1f2329;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 顶部导航 */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  background: rgba(2, 2, 5, 0.9);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.navbar-inner {
  max-width: 1440px;
  margin: 0 auto;
  padding: 0 48px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-left {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.logo-text {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
}

.nav-links {
  display: flex;
  gap: 32px;
}

.nav-link {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  cursor: pointer;
  transition: color 0.2s;
}

.nav-link:hover,
.nav-link.active {
  color: #ffffff;
}

.navbar-right {
  display: flex;
  gap: 12px;
}

.login-btn {
  background: #3b82f6;
  border-color: #3b82f6;
}

.register-btn {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.9);
}

.register-btn:hover {
  border-color: rgba(255, 255, 255, 0.4);
  color: #ffffff;
  background: rgba(255, 255, 255, 0.15);
}

/* Hero区域 */
.hero-section {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 64px;
  overflow: hidden;
  background: linear-gradient(180deg, #020205 0%, #050508 50%, #0a0a1a 100%);
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(2px 2px at 50px 80px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 120px 150px, rgba(99, 102, 241, 1), transparent),
    radial-gradient(2px 2px at 180px 50px, #ffffff, transparent),
    radial-gradient(2px 2px at 250px 200px, rgba(139, 92, 246, 1), transparent),
    radial-gradient(2.5px 2.5px at 320px 100px, #ffffff, transparent),
    radial-gradient(2px 2px at 400px 250px, rgba(199, 125, 255, 1), transparent),
    radial-gradient(2px 2px at 480px 60px, rgba(59, 130, 246, 1), transparent),
    radial-gradient(2.5px 2.5px at 560px 180px, #ffffff, transparent),
    radial-gradient(2px 2px at 640px 300px, rgba(236, 72, 153, 1), transparent),
    radial-gradient(2px 2px at 720px 90px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 800px 220px, rgba(99, 102, 241, 1), transparent),
    radial-gradient(2px 2px at 880px 50px, rgba(139, 92, 246, 1), transparent),
    radial-gradient(2px 2px at 960px 280px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 1040px 140px, rgba(199, 125, 255, 1), transparent),
    radial-gradient(2px 2px at 1120px 70px, #ffffff, transparent),
    radial-gradient(2px 2px at 1200px 240px, rgba(59, 130, 246, 1), transparent),
    radial-gradient(2.5px 2.5px at 1280px 110px, #ffffff, transparent),
    radial-gradient(2px 2px at 1360px 300px, rgba(236, 72, 153, 1), transparent),
    radial-gradient(2px 2px at 1440px 40px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 80px 320px, rgba(99, 102, 241, 1), transparent),
    radial-gradient(2px 2px at 160px 260px, #ffffff, transparent),
    radial-gradient(2px 2px at 240px 340px, rgba(139, 92, 246, 1), transparent),
    radial-gradient(2.5px 2.5px at 340px 30px, #ffffff, transparent),
    radial-gradient(2px 2px at 440px 310px, rgba(199, 125, 255, 1), transparent),
    radial-gradient(2px 2px at 540px 80px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 640px 330px, rgba(59, 130, 246, 1), transparent),
    radial-gradient(2px 2px at 740px 120px, #ffffff, transparent),
    radial-gradient(2px 2px at 840px 290px, rgba(236, 72, 153, 1), transparent),
    radial-gradient(2.5px 2.5px at 940px 50px, #ffffff, transparent),
    radial-gradient(2px 2px at 1040px 310px, rgba(99, 102, 241, 1), transparent),
    radial-gradient(2px 2px at 1140px 90px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 1240px 270px, rgba(139, 92, 246, 1), transparent),
    radial-gradient(2px 2px at 1340px 40px, #ffffff, transparent),
    radial-gradient(2px 2px at 1440px 320px, rgba(199, 125, 255, 1), transparent);
  background-size: 1500px 350px;
  animation: twinkle 5s ease-in-out infinite;
  pointer-events: none;
  z-index: 0;
}

.hero-bg {
  position: absolute;
  inset: 0;
}

.hero-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.7;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    rgba(26, 26, 46, 0.4) 0%,
    rgba(26, 26, 46, 0.6) 100%
  );
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #ffffff;
  max-width: 800px;
}

.hero-title {
  font-size: 72px;
  font-weight: 700;
  margin-bottom: 16px;
  letter-spacing: -2px;
}

.hero-desc {
  font-size: 20px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 40px;
}

.hero-btn {
  background: #3b82f6;
  border-color: #3b82f6;
  padding: 16px 48px;
  font-size: 16px;
  border-radius: 12px;
}

.btn-icon {
  margin-left: 8px;
}

.video-credit {
  margin-top: 24px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

/* 功能展示区 - 星空宇宙风格 */
.feature-section {
  padding: 100px 48px;
  background:
    radial-gradient(ellipse at 30% 20%, rgba(99, 102, 241, 0.25) 0%, transparent 50%),
    radial-gradient(ellipse at 70% 80%, rgba(139, 92, 246, 0.2) 0%, transparent 50%),
    linear-gradient(180deg, #050508 0%, #0a0a1a 50%, #0d1117 100%);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.feature-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(2px 2px at 20px 30px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 40px 70px, rgba(99, 102, 241, 1), transparent),
    radial-gradient(2px 2px at 50px 160px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 90px 40px, rgba(139, 92, 246, 1), transparent),
    radial-gradient(2px 2px at 130px 80px, #ffffff, transparent),
    radial-gradient(3px 3px at 160px 120px, rgba(199, 125, 255, 1), transparent),
    radial-gradient(2px 2px at 200px 50px, rgba(59, 130, 246, 1), transparent),
    radial-gradient(2px 2px at 250px 180px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 300px 90px, rgba(236, 72, 153, 1), transparent),
    radial-gradient(3px 3px at 350px 140px, #ffffff, transparent),
    radial-gradient(2px 2px at 400px 60px, rgba(99, 102, 241, 1), transparent),
    radial-gradient(2px 2px at 450px 200px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 500px 100px, rgba(139, 92, 246, 1), transparent),
    radial-gradient(2px 2px at 550px 170px, #ffffff, transparent),
    radial-gradient(2px 2px at 600px 30px, rgba(199, 125, 255, 1), transparent),
    radial-gradient(2.5px 2.5px at 650px 220px, rgba(59, 130, 246, 1), transparent),
    radial-gradient(2px 2px at 700px 80px, #ffffff, transparent),
    radial-gradient(2px 2px at 750px 150px, rgba(236, 72, 153, 1), transparent),
    radial-gradient(2px 2px at 800px 190px, #ffffff, transparent),
    radial-gradient(3px 3px at 850px 45px, rgba(99, 102, 241, 1), transparent),
    radial-gradient(2px 2px at 50px 220px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 120px 200px, rgba(199, 125, 255, 1), transparent),
    radial-gradient(2px 2px at 180px 20px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 280px 190px, rgba(59, 130, 246, 1), transparent),
    radial-gradient(3px 3px at 380px 30px, #ffffff, transparent),
    radial-gradient(2px 2px at 480px 220px, rgba(139, 92, 246, 1), transparent),
    radial-gradient(2px 2px at 580px 20px, #ffffff, transparent),
    radial-gradient(2.5px 2.5px at 680px 190px, rgba(236, 72, 153, 1), transparent),
    radial-gradient(2px 2px at 780px 30px, #ffffff, transparent),
    radial-gradient(3px 3px at 880px 220px, rgba(99, 102, 241, 1), transparent);
  background-size: 900px 250px;
  animation: twinkle 4s ease-in-out infinite;
  pointer-events: none;
}

.feature-section::after {
  content: '';
  position: absolute;
  bottom: -50px;
  left: 0;
  width: 100%;
  height: 100px;
  background: linear-gradient(to top, rgba(99, 102, 241, 0.1), transparent);
  pointer-events: none;
}

@keyframes twinkle {
  0%, 100% { opacity: 1; transform: scale(1); }
  25% { opacity: 0.7; transform: scale(1.05); }
  50% { opacity: 1; transform: scale(1); }
  75% { opacity: 0.6; transform: scale(1.1); }
}

.section-label {
  display: inline-block;
  padding: 6px 16px;
  background: rgba(59, 130, 246, 0.2);
  color: #60a5fa;
  font-size: 14px;
  border-radius: 20px;
  margin-bottom: 16px;
  border: 1px solid rgba(59, 130, 246, 0.3);
}

.section-title {
  font-size: 40px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 12px;
}

.section-subtitle {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 60px;
}

.section-desc {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  max-width: 700px;
  margin: 0 auto 48px;
  line-height: 1.8;
}

.feature-grid {
  max-width: 1200px;
  margin: 0 auto;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 60px;
  padding: 60px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
}

.feature-item:last-child {
  border-bottom: none;
}

.feature-item.reverse {
  flex-direction: row-reverse;
}

.feature-num {
  font-size: 96px;
  font-weight: 700;
  color: #3b82f6;
  opacity: 0.2;
  line-height: 1;
  flex-shrink: 0;
}

.feature-info {
  text-align: left;
  flex: 1;
}

.feature-info h3 {
  font-size: 28px;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 16px;
}

.feature-info p {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.8;
  margin-bottom: 24px;
}

.feature-info .el-button {
  background: #3b82f6;
  border-color: #3b82f6;
}

/* 功能视频样式 */
.feature-video {
  flex-shrink: 0;
  width: 500px;
}

.video-wrapper {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  background: #f2f3f5;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.demo-video {
  width: 100%;
  height: 320px;
  object-fit: cover;
  display: block;
}

.video-frame {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 36px;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  padding: 0 14px;
  backdrop-filter: blur(10px);
}

.frame-dots {
  display: flex;
  gap: 7px;
}

.frame-dots span {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
}

.frame-dots span:nth-child(1) { background: #ff5f57; }
.frame-dots span:nth-child(2) { background: #febc2e; }
.frame-dots span:nth-child(3) { background: #28c840; }

.video-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 14px;
}

.video-controls .el-button {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #ffffff;
}

.video-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

/* AI绘画区 - 星空宇宙风格 */
.painting-section {
  padding: 100px 48px;
  background:
    radial-gradient(ellipse at 20% 30%, rgba(236, 72, 153, 0.25) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 70%, rgba(99, 102, 241, 0.2) 0%, transparent 50%),
    linear-gradient(180deg, #050508 0%, #0a0a1a 50%, #0d1117 100%);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.painting-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(2px 2px at 25px 45px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 60px 95px, rgba(236, 72, 153, 1), transparent),
    radial-gradient(2.5px 2.5px at 100px 150px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 150px 60px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 200px 200px, rgba(99, 102, 241, 1), transparent),
    radial-gradient(2px 2px at 280px 110px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 350px 70px, rgba(139, 92, 246, 1), transparent),
    radial-gradient(2.5px 2.5px at 420px 180px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 500px 50px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 580px 140px, rgba(236, 72, 153, 1), transparent),
    radial-gradient(2px 2px at 650px 90px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 720px 210px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 800px 65px, rgba(99, 102, 241, 1), transparent),
    radial-gradient(2.5px 2.5px at 870px 175px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 940px 125px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 40px 220px, rgba(199, 125, 255, 1), transparent),
    radial-gradient(2.5px 2.5px at 110px 200px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 180px 30px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 260px 210px, rgba(59, 130, 246, 1), transparent),
    radial-gradient(2.5px 2.5px at 340px 40px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 430px 220px, rgba(139, 92, 246, 1), transparent),
    radial-gradient(2px 2px at 530px 20px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2.5px 2.5px at 620px 200px, rgba(236, 72, 153, 1), transparent),
    radial-gradient(2px 2px at 720px 40px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 820px 210px, rgba(99, 102, 241, 1), transparent),
    radial-gradient(2.5px 2.5px at 920px 30px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(3px 3px at 980px 200px, rgba(199, 125, 255, 1), transparent);
  background-size: 1000px 250px;
  animation: twinkle 4.5s ease-in-out infinite;
  pointer-events: none;
}

.painting-section::after {
  content: '';
  position: absolute;
  bottom: -30px;
  left: 0;
  width: 100%;
  height: 80px;
  background: linear-gradient(to top, rgba(236, 72, 153, 0.08), transparent);
  pointer-events: none;
}

.painting-gallery {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  max-width: 1200px;
  margin: 0 auto 48px;
}

.painting-item {
  cursor: pointer;
  transition: transform 0.3s;
}

.painting-item:hover {
  transform: translateY(-4px);
}

.painting-img {
  height: 200px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}

.painting-placeholder {
  color: rgba(255, 255, 255, 0.6);
  font-size: 48px;
}

.painting-title {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
  text-align: left;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.painting-btn {
  background: #3b82f6;
  border-color: #3b82f6;
  padding: 16px 48px;
  font-size: 16px;
  border-radius: 12px;
}

/* 创意社区区 - 星空宇宙风格 */
.community-section {
  padding: 100px 48px;
  background:
    radial-gradient(ellipse at 40% 25%, rgba(34, 197, 94, 0.2) 0%, transparent 50%),
    radial-gradient(ellipse at 60% 75%, rgba(20, 184, 166, 0.15) 0%, transparent 50%),
    linear-gradient(180deg, #050508 0%, #0a0a1a 50%, #0d1117 100%);
  text-align: center;
  position: relative;
  overflow: hidden;
}

.community-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(2px 2px at 30px 50px, rgba(34, 197, 94, 1), transparent),
    radial-gradient(2px 2px at 80px 120px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2.5px 2.5px at 140px 80px, rgba(20, 184, 166, 1), transparent),
    radial-gradient(2px 2px at 210px 180px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 290px 45px, rgba(34, 197, 94, 1), transparent),
    radial-gradient(2px 2px at 360px 150px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 440px 90px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2.5px 2.5px at 520px 200px, rgba(20, 184, 166, 1), transparent),
    radial-gradient(2px 2px at 600px 60px, rgba(34, 197, 94, 1), transparent),
    radial-gradient(2px 2px at 680px 130px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 760px 85px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 840px 170px, rgba(20, 184, 166, 1), transparent),
    radial-gradient(2px 2px at 920px 35px, rgba(34, 197, 94, 1), transparent),
    radial-gradient(2.5px 2.5px at 1000px 145px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 1080px 95px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 60px 220px, rgba(16, 185, 129, 1), transparent),
    radial-gradient(2.5px 2.5px at 160px 200px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 260px 30px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 340px 210px, rgba(20, 184, 166, 1), transparent),
    radial-gradient(2.5px 2.5px at 440px 40px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 540px 220px, rgba(34, 197, 94, 1), transparent),
    radial-gradient(2px 2px at 640px 20px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2.5px 2.5px at 740px 200px, rgba(16, 185, 129, 1), transparent),
    radial-gradient(2px 2px at 840px 30px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(2px 2px at 940px 210px, rgba(20, 184, 166, 1), transparent),
    radial-gradient(2.5px 2.5px at 1040px 40px, rgba(255, 255, 255, 1), transparent),
    radial-gradient(3px 3px at 1140px 180px, rgba(34, 197, 94, 1), transparent);
  background-size: 1100px 220px;
  animation: twinkle 5s ease-in-out infinite;
  pointer-events: none;
}

.community-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto 48px;
}

.community-item {
  cursor: pointer;
}

.work-img {
  height: 240px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  transition: transform 0.3s;
}

.community-item:hover .work-img {
  transform: scale(1.02);
}

.work-placeholder {
  color: rgba(255, 255, 255, 0.6);
  font-size: 48px;
}

.work-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.work-author {
  font-size: 14px;
  color: #ffffff;
}

.work-likes {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

.community-btn {
  background: #3b82f6;
  border-color: #3b82f6;
  padding: 16px 48px;
  font-size: 16px;
  border-radius: 12px;
}

/* 底部 */
.footer {
  padding: 48px;
  background: #020205;
  text-align: center;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: #ffffff;
  position: relative;
}

.footer > * {
  position: relative;
  z-index: 1;
}

.footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    radial-gradient(1.5px 1.5px at 40px 30px, rgba(255, 255, 255, 0.8), transparent),
    radial-gradient(1.5px 1.5px at 100px 80px, rgba(99, 102, 241, 0.9), transparent),
    radial-gradient(1.5px 1.5px at 180px 40px, rgba(255, 255, 255, 0.7), transparent),
    radial-gradient(2px 2px at 260px 100px, rgba(139, 92, 246, 0.8), transparent),
    radial-gradient(1.5px 1.5px at 340px 50px, rgba(255, 255, 255, 0.6), transparent),
    radial-gradient(1.5px 1.5px at 420px 90px, rgba(199, 125, 255, 0.7), transparent),
    radial-gradient(2px 2px at 500px 30px, rgba(255, 255, 255, 0.8), transparent),
    radial-gradient(1.5px 1.5px at 580px 80px, rgba(59, 130, 246, 0.9), transparent),
    radial-gradient(1.5px 1.5px at 660px 40px, rgba(255, 255, 255, 0.7), transparent),
    radial-gradient(2px 2px at 740px 100px, rgba(236, 72, 153, 0.8), transparent),
    radial-gradient(1.5px 1.5px at 820px 50px, rgba(255, 255, 255, 0.6), transparent),
    radial-gradient(1.5px 1.5px at 900px 90px, rgba(99, 102, 241, 0.7), transparent);
  background-size: 950px 120px;
  animation: twinkle 6s ease-in-out infinite;
  pointer-events: none;
}

.footer-nav {
  display: flex;
  justify-content: center;
  gap: 32px;
  margin-bottom: 24px;
}

.footer-link {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  cursor: pointer;
  transition: color 0.2s;
}

.footer-link:hover {
  color: #ffffff;
}

.footer-divider {
  width: 100%;
  max-width: 600px;
  height: 1px;
  background: rgba(255, 255, 255, 0.1);
  margin: 0 auto 24px;
}

.copyright {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.5);
}

/* 响应式 */
@media (max-width: 1024px) {
  .painting-gallery {
    grid-template-columns: repeat(3, 1fr);
  }

  .community-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .feature-video {
    width: 380px;
  }

  .demo-video {
    height: 240px;
  }
}

@media (max-width: 768px) {
  .navbar-inner {
    padding: 0 24px;
  }

  .nav-links {
    display: none;
  }

  .hero-title {
    font-size: 40px;
  }

  .feature-item,
  .feature-item.reverse {
    flex-direction: column;
    text-align: center;
    gap: 30px;
  }

  .feature-info {
    text-align: center;
    width: 100%;
  }

  .feature-num {
    font-size: 72px;
  }

  .feature-video {
    width: 100%;
    max-width: 400px;
  }

  .demo-video {
    height: 240px;
  }

  .painting-gallery {
    grid-template-columns: repeat(2, 1fr);
  }

  .community-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .footer-nav {
    flex-wrap: wrap;
    gap: 20px;
  }
}
</style>