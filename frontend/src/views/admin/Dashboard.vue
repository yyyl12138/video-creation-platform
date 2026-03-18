<template>
  <div class="dashboard-container">
    <!-- 顶部广告轮播区域 -->
    <div class="ad-banner">
      <div class="banner-slides" :style="{ transform: `translateX(-${currentSlide * 100}%)` }">
        <div class="banner-slide" v-for="(slide, index) in slides" :key="index">
          <div class="slide-content" :style="{ backgroundImage: slide.image ? `url(${slide.image})` : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }">
            <div class="slide-overlay">
              <div class="slide-text">
                <h2>{{ slide.title }}</h2>
                <p>{{ slide.subtitle }}</p>
                <el-button type="primary" size="large" @click="handleSlideClick(slide)">
                  {{ slide.buttonText }}
                </el-button>
              </div>
              <div class="slide-image">
                <el-icon :size="120"><component :is="slide.icon" /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="banner-dots">
        <span 
          v-for="(slide, index) in slides" 
          :key="index"
          class="dot"
          :class="{ active: currentSlide === index }"
          @click="currentSlide = index"
        ></span>
      </div>
    </div>

    <!-- 模型选择区域 -->
    <div class="models-section">
      <div class="section-header">
        <h3>热门模型</h3>
        <el-button type="text" @click="router.push('/creation/generation')">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="models-scroll">
        <div
          v-for="(model, index) in modelCards"
          :key="index"
          class="model-card"
          :class="model.tagClass"
          @click="goToCreation(model)"
        >
          <div class="model-header">
            <div class="model-icon" :style="{ background: model.iconBg }">
              <el-icon :size="20"><component :is="model.icon" /></el-icon>
            </div>
            <div class="model-info">
              <div class="model-name">{{ model.name }}</div>
              <div class="model-tag" v-if="model.tag">{{ model.tag }}</div>
            </div>
          </div>
          <div class="model-desc">{{ model.description }}</div>
          <div class="model-footer">
            <span class="model-capability">{{ model.capability }}</span>
            <el-button type="primary" size="small" class="use-btn">去使用</el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片展示区域 -->
    <div class="gallery-section">
      <div class="image-grid">
        <div
          class="grid-item"
          v-for="(item, index) in imageItems"
          :key="index"
          @click="handleItemClick(item)"
        >
          <div class="item-image">
            <img :src="item.src" :alt="item.alt" />
          </div>
          <div class="grid-item-overlay">
            <h4 class="grid-item-title">{{ item.title }}</h4>
            <p class="grid-item-desc">{{ item.desc }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  VideoCamera,
  Picture,
  PictureFilled,
  DocumentCopy,
  Clock,
  Star,
  Medal,
  TrendCharts,
  ChatDotRound,
  VideoPlay
} from '@element-plus/icons-vue'

const router = useRouter()

// 轮播图数据
const slides = ref([
  {
    title: 'AI 视频创作新时代',
    subtitle: '输入文案，自动生成精美视频',
    buttonText: '立即体验',
    image: '/images/1bd7b4f9e6be1923c157e4fb2888551467986f6ceb1c4e06e7a1635d5f845e75.png',
    link: '/creation/generation'
  },
  {
    title: '文生图 AI 上线',
    subtitle: '描述你想象的画面，AI 为你实现',
    buttonText: '开始创作',
    image: '/images/bab80a199c0428bf223c4ee11bc535682e0badaf2698252b079928ed0dc465fe.png',
    link: '/creation/generation'
  },
  {
    title: '图生视频功能',
    subtitle: '静态图片秒变动态视频',
    buttonText: '了解更多',
    image: '/images/f0f41257ae3b777e7617b2109499c74035bb31838afde028a9f38bcb449c6acd.png',
    link: '/creation/generation'
  }
])

// 当前轮播索引
const currentSlide = ref(0)

// 轮播自动切换
let slideInterval = null
const startAutoSlide = () => {
  slideInterval = setInterval(() => {
    currentSlide.value = (currentSlide.value + 1) % slides.value.length
  }, 5000)
}

onMounted(() => {
  startAutoSlide()
})

onUnmounted(() => {
  if (slideInterval) {
    clearInterval(slideInterval)
  }
})

// AI模型数据 - 与项目中实际支持的模型一致
const aiModels = ref([
  {
    id: 'deepseek-v3',
    name: 'DeepSeek V3',
    description: '强大的文本理解与生成能力',
    icon: 'DocumentCopy',
    iconColor: '#3b82f6',
    tags: [{ text: 'NEW', type: 'danger' }],
    link: '/creation/generation?model=DeepSeek%20V3&type=TEXT_TO_TEXT',
    capability: '文本生成'
  },
  {
    id: 'qwen-flash',
    name: 'Qwen Flash',
    description: '快速响应的文本生成模型',
    icon: 'ChatDotRound',
    iconColor: '#10b981',
    tags: [{ text: '推荐', type: 'success' }],
    link: '/creation/generation?model=Qwen%20Flash&type=TEXT_TO_TEXT',
    capability: '文本生成'
  },
  {
    id: 'wanx-v1',
    name: 'Wanx v1',
    description: '高精度图像生成模型',
    icon: 'Picture',
    iconColor: '#f59e0b',
    tags: [],
    link: '/creation/generation?model=Wanx%20v1&type=TEXT_TO_IMAGE',
    capability: '图片生成'
  },
  {
    id: 'kling-video',
    name: 'Kling',
    description: '专业级视频生成模型',
    icon: 'VideoCamera',
    iconColor: '#10b981',
    tags: [{ text: '热门', type: 'warning' }],
    link: '/creation/generation?model=Kling&type=TEXT_TO_VIDEO',
    capability: '视频生成'
  },
  {
    id: 'minimax-video',
    name: 'Minimax',
    description: '高质量视频与图像生成',
    icon: 'TrendCharts',
    iconColor: '#ec4899',
    tags: [{ text: 'NEW', type: 'danger' }],
    link: '/creation/generation?model=Minimax&type=TEXT_TO_VIDEO',
    capability: '视频生成'
  },
  {
    id: 'doubao-seedance',
    name: 'Doubao Seedance',
    description: '多模态视频创作模型',
    icon: 'VideoPlay',
    iconColor: '#8b5cf6',
    tags: [],
    link: '/creation/generation?model=Doubao%20Seedance&type=TEXT_TO_VIDEO',
    capability: '视频生成'
  }
])

// 计算属性：模型卡片数据
const modelCards = computed(() => {
  return aiModels.value.map(model => ({
    name: model.name,
    description: model.description,
    icon: model.icon,
    iconBg: model.iconColor,
    tag: model.tags[0]?.text || '',
    tagClass: model.tags[0]?.type || '',
    capability: model.capability,
    link: model.link
  }))
})

const useModel = (model) => {
  router.push(model.link)
}

const goToCreation = (model) => {
  router.push(model.link)
}

// 真实图片数据 - 底部展示6张其他图片
const imageItems = ref([
  { src: '/images/20260121_172146_1534268270.png', alt: '产品图片1', title: '创意设计', desc: 'AI 生成的精美设计作品' },
  { src: '/images/造相-Z-image.png', alt: '造相Z图片', title: '品牌形象', desc: '品牌视觉识别系统' },
  { src: '/images/造相.png', alt: '造相logo', title: 'Logo 设计', desc: '个性化标志设计' },
  { src: '/images/ecommerce_20260123_151306_952717137.png', alt: '电商图片1', title: '电商主图', desc: '产品展示图' },
  { src: '/images/ecommerce_20260123_151849_4207435666.png', alt: '电商图片2', title: '营销素材', desc: '推广宣传图片' },
  { src: '/images/ecommerce_20260123_154739_61970699.png', alt: '电商图片3', title: '详情页面', desc: '商品详情图' }
])

// 点击处理
const handleSlideClick = (slide) => {
  if (slide.link) {
    router.push(slide.link)
  }
}

const handleItemClick = (item) => {
  router.push('/creation/generation')
}
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(180deg, #f0f5ff 0%, #fafafa 100%);
  min-height: calc(100vh - 60px);
}

/* 轮播广告区域 */
.ad-banner {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.banner-slides {
  display: flex;
  transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.banner-slide {
  min-width: 100%;
}

.slide-content {
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background-size: cover;
  background-position: center;
}

.slide-overlay {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 80px;
  width: 100%;
  position: relative;
  z-index: 2;
}

.slide-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, rgba(15, 23, 42, 0.75) 0%, rgba(15, 23, 42, 0.4) 100%);
  z-index: 1;
}

.slide-text {
  position: relative;
  z-index: 1;
  max-width: 500px;
}

.slide-text h2 {
  margin: 0 0 16px 0;
  font-size: 2.5rem;
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.5px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.slide-text p {
  margin: 0 0 28px 0;
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.6;
}

.slide-text .el-button {
  height: 48px;
  padding: 0 32px;
  font-size: 1rem;
  border-radius: 12px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  color: #fff;
  font-weight: 600;
  box-shadow: 0 4px 16px rgba(99, 102, 241, 0.4);
  transition: all 0.3s ease;
}

.slide-text .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.5);
}

.slide-image {
  position: relative;
  z-index: 1;
  color: rgba(255, 255, 255, 0.15);
}

.slide-image .el-icon {
  font-size: 180px;
}

.banner-dots {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
  z-index: 10;
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.4);
  cursor: pointer;
  transition: all 0.3s ease;
}

.dot.active {
  width: 32px;
  border-radius: 5px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.dot:hover {
  background: rgba(255, 255, 255, 0.8);
}

/* 模型选择区域 */
.models-section {
  background: #fff;
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

.section-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.models-scroll {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding: 8px 4px;
  scrollbar-width: thin;
  scrollbar-color: #d1d5db transparent;
}

.models-scroll::-webkit-scrollbar {
  height: 6px;
}

.models-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.models-scroll::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.model-card {
  flex-shrink: 0;
  width: 300px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 16px;
  padding: 24px;
  border: 2px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.3s ease;
}

.model-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.15);
  transform: translateY(-4px);
}

.model-card.danger .model-icon {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
}

.model-card.success .model-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.model-card.primary .model-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
}

.model-card.warning .model-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.model-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.model-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.model-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.model-name {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
}

.model-tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 10px;
  font-weight: 600;
  background: #fecaca;
  color: #dc2626;
  display: inline-block;
}

.model-desc {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin-bottom: 20px;
  min-height: 44px;
}

.model-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.model-capability {
  font-size: 13px;
  color: #9ca3af;
  background: #f3f4f6;
  padding: 4px 10px;
  border-radius: 6px;
}

.use-btn {
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 500;
}

/* 图片展示区域 */
.gallery-section {
  background: #fff;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.grid-item {
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  position: relative;
}

.grid-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, transparent 60%, rgba(0, 0, 0, 0.4) 100%);
  z-index: 1;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.grid-item:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.15);
}

.grid-item:hover::before {
  opacity: 1;
}

.item-image {
  height: 220px;
  width: 100%;
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.grid-item:hover .item-image img {
  transform: scale(1.1);
}

.grid-item-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 16px;
  color: #fff;
  z-index: 2;
  transform: translateY(100%);
  transition: transform 0.3s ease;
}

.grid-item:hover .grid-item-overlay {
  transform: translateY(0);
}

.grid-item-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.grid-item-desc {
  font-size: 13px;
  opacity: 0.8;
  margin: 0;
}

.model-cards-section {
  margin-bottom: 40px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.view-more {
  color: #3b82f6;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.3s ease;
}

.view-more:hover {
  color: #2563eb;
}

.model-cards-scroll {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding: 4px;
  scrollbar-width: thin;
  scrollbar-color: #d1d5db transparent;
}

.model-cards-scroll::-webkit-scrollbar {
  height: 6px;
}

.model-cards-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.model-cards-scroll::-webkit-scrollbar-thumb {
  background: #d1d5db;
  border-radius: 3px;
}

.model-card {
  flex-shrink: 0;
  width: 280px;
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.model-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 8px 24px rgba(59, 130, 246, 0.15);
  transform: translateY(-2px);
}

.model-card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.model-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.model-icon.image {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: #fff;
}

.model-icon.video {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
}

.model-icon.text {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff;
}

.model-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.model-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.model-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 600;
}

.model-tag.new {
  background: #fecaca;
  color: #dc2626;
}

.model-tag.hot {
  background: #dbeafe;
  color: #2563eb;
}

.model-tag.popular {
  background: #d1fae5;
  color: #059669;
}

.model-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.model-card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.model-feature {
  font-size: 12px;
  color: #9ca3af;
}

.use-btn {
  padding: 6px 16px;
  font-size: 13px;
  border-radius: 8px;
  background: #eff6ff;
  color: #3b82f6;
  border: none;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.use-btn:hover {
  background: #3b82f6;
  color: #fff;
}

  /* 响应式 */
  @media (max-width: 1200px) {
    .image-grid {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  @media (max-width: 768px) {
    .dashboard-container {
      padding: 16px;
    }

    .slide-content {
      height: 260px;
    }

    .slide-overlay {
      padding: 0 32px;
    }

    .slide-text h2 {
      font-size: 1.8rem;
    }

    .slide-text p {
      font-size: 1rem;
    }

    .slide-image .el-icon {
      font-size: 100px;
    }

    .image-grid {
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
    }

    .item-image {
      height: 160px;
    }

    .gallery-section {
      padding: 20px;
    }
  }

  @media (max-width: 480px) {
    .image-grid {
      grid-template-columns: 1fr;
    }

    .slide-overlay {
      flex-direction: column;
      text-align: center;
      padding: 24px;
    }

    .slide-image {
      display: none;
    }
  }
</style>