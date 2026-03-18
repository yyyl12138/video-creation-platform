<template>
  <div class="marketplace-container">
    <!-- 顶部 Banner 区域 -->
    <div class="market-banner">
      <div class="banner-content">
        <h1>发现无限创意</h1>
        <p>上千款专业视频模板，助力您的每一步创作</p>
        <div class="stats-overview">
          <div class="stat-item">
            <span class="stat-value">5,000+</span>
            <span class="stat-label">精选模板</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">1.2k+</span>
            <span class="stat-label">专业创作者</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">98%</span>
            <span class="stat-label">满意度</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 过滤器区域 -->
    <div class="toolbar">
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="搜索您感兴趣的模板 (如：时尚、科技、自然...)"
          class="custom-search"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
      
      <div class="filter-groups">
        <el-radio-group v-model="currentCategory" class="category-filters">
          <el-radio-button label="ALL">全部</el-radio-button>
          <el-radio-button label="FASHION">时尚</el-radio-button>
          <el-radio-button label="CORPORATE">商务</el-radio-button>
          <el-radio-button label="SOCIAL">社交媒体</el-radio-button>
          <el-radio-button label="NATURE">自然风光</el-radio-button>
        </el-radio-group>
        
        <el-select v-model="sortBy" placeholder="排队方式" class="sort-select">
          <el-option label="综合排序" value="default" />
          <el-option label="最新发布" value="newest" />
          <el-option label="最高人气" value="popularity" />
          <el-option label="价格最低" value="price_asc" />
        </el-select>
      </div>
    </div>

    <!-- 模板列表区域 -->
    <div class="template-grid" v-loading="loading">
      <div 
        v-for="item in filteredTemplates" 
        :key="item.id" 
        class="market-card"
        @mouseenter="item.hover = true"
        @mouseleave="item.hover = false"
      >
        <div class="card-media">
          <MediaPreview 
            :url="item.thumbnail" 
            :type="'image'" 
            height="180px" 
            class="thumb-img"
          />
          <div class="media-overlay" v-if="item.hover">
            <el-button type="primary" circle @click="openPreview(item)">
              <el-icon><VideoPlay /></el-icon>
            </el-button>
          </div>
          <el-tag v-if="item.isNew" type="success" effect="dark" class="new-tag">NEW</el-tag>
        </div>
        <div class="card-info">
          <div class="info-header">
            <h3 class="title">{{ item.title }}</h3>
            <span class="price">¥ {{ item.price.toFixed(2) }}</span>
          </div>
          <div class="info-footer">
            <div class="creator">
              <el-avatar :size="24" :src="item.authorAvatar" />
              <span class="name">{{ item.author }}</span>
            </div>
            <div class="stats">
              <span><el-icon><View /></el-icon> {{ item.views }}</span>
              <span><el-icon><MagicStick /></el-icon> {{ item.used }}</span>
            </div>
          </div>
          <div class="card-actions">
            <el-button type="primary" class="buy-btn" @click="handleBuy(item)">
              立即购买
            </el-button>
            <el-button circle @click="toggleLike(item)">
              <el-icon :color="item.liked ? '#f56c6c' : ''">
                <component :is="item.liked ? 'StarFilled' : 'Star'" />
              </el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 优势/特性展示 -->
    <div class="features-section">
      <div class="feature-item">
        <el-icon size="40"><TrendCharts /></el-icon>
        <h3>实时流行趋势</h3>
        <p>基于 AI 算法，每日更新全网最火爆的视频风格与转场建议。</p>
      </div>
      <div class="feature-item">
        <el-icon size="40"><Cpu /></el-icon>
        <h3>独家 AI 转码</h3>
        <p>一键适配 4K/HDR，即使是普通素材也能焕发极致画质。</p>
      </div>
      <div class="feature-item">
        <el-icon size="40"><Connection /></el-icon>
        <h3>跨平台导出</h3>
        <p>原生支持 TikTok、Bilibili、YouTube 比例，无需手动裁剪。</p>
      </div>
    </div>

    <!-- 底部信任标识 -->
    <div class="trust-badges">
      <span class="trust-title">合作伙伴 & 支持服务</span>
      <div class="badge-logos">
        <div class="logo-placeholder">HUIKE AI</div>
        <div class="logo-placeholder">NVIDIA CUDA</div>
        <div class="logo-placeholder">ADOBE PARTNER</div>
        <div class="logo-placeholder">CLOUDFLARE</div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="100"
        :page-size="12"
      />
    </div>

    <!-- 预览抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      :title="selectedItem?.title"
      size="650px"
      custom-class="market-drawer"
    >
      <div v-if="selectedItem" class="detail-content">
        <div class="video-preview-box">
          <MediaPreview 
            :url="selectedItem.thumbnail" 
            :type="'image'" 
            height="360px"
          />
          <div class="play-hint">
            <el-icon size="48" color="#fff"><VideoPlay /></el-icon>
            <span>预览视频 (演示)</span>
          </div>
        </div>
        
        <div class="detail-info">
          <div class="info-row">
            <div class="label">价格</div>
            <div class="value price-large">¥ {{ selectedItem.price }}</div>
          </div>
          <div class="info-row">
            <div class="label">分辨率</div>
            <div class="value">4K (3840x2160)</div>
          </div>
          <div class="info-row">
            <div class="label">包含内容</div>
            <div class="value">AE 工程文件 + 动态素材包</div>
          </div>
          <p class="description">
            {{ selectedItem.description || '这款模板专为现代数字叙事而设计。采用电影级调色和动态排版，让您的作品在众多内容中脱颖而出。它非常灵活且易于定制，适用于各种行业需求。' }}
          </p>
        </div>

        <div class="detail-actions">
          <el-button type="primary" size="large" class="full-btn" @click="handleBuy(selectedItem)">
            确 认 购 买
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Search, VideoPlay, MagicStick, View, Star, StarFilled,
  TrendCharts, Cpu, Connection
} from '@element-plus/icons-vue'
import MediaPreview from '@/components/MediaPreview.vue'

const searchQuery = ref('')
const currentCategory = ref('ALL')
const sortBy = ref('default')
const loading = ref(false)
const drawerVisible = ref(false)
const selectedItem = ref(null)

// 模拟数据 (使用 generated images 的路径，如果不存在则使用占位)
const mockData = ref([
  {
    id: 1,
    title: 'Neon Runway Fashion Intro',
    thumbnail: '/storage/market/market_fashion_thumb_1773741700240.png',
    price: 19.9,
    author: 'VogueCreator',
    authorAvatar: 'https://i.pravatar.cc/150?u=1',
    views: '1.2k',
    used: 45,
    category: 'FASHION',
    isNew: true,
    liked: false,
    hover: false
  },
  {
    id: 2,
    title: 'Modern Corporate Dashboard',
    thumbnail: '/storage/market/market_corporate_thumb_1773741718687.png',
    price: 29.0,
    author: 'BizPro',
    authorAvatar: 'https://i.pravatar.cc/150?u=2',
    views: '850',
    used: 12,
    category: 'CORPORATE',
    liked: true,
    hover: false
  },
  {
    id: 3,
    title: 'Ethereal Mountain Sunrise',
    thumbnail: '/storage/market/market_nature_thumb_1773741737698.png',
    price: 15.0,
    author: 'NatureGrapher',
    authorAvatar: 'https://i.pravatar.cc/150?u=3',
    views: '2.4k',
    used: 156,
    category: 'NATURE',
    liked: false,
    hover: false
  },
  {
    id: 4,
    title: 'TikTok Viral Trending Pack',
    thumbnail: '/storage/market/market_social_thumb_1773741789693.png',
    price: 9.9,
    author: 'GenZMix',
    authorAvatar: 'https://i.pravatar.cc/150?u=4',
    views: '4.1k',
    used: 890,
    category: 'SOCIAL',
    isNew: true,
    liked: false,
    hover: false
  },
  // 更多重复数据以增强饱满感
  {
    id: 5,
    title: 'Minimalist Tech Showcase',
    thumbnail: '/storage/market/market_corporate_thumb_1773741718687.png',
    price: 35.0,
    author: 'Futurism',
    authorAvatar: 'https://i.pravatar.cc/150?u=5',
    views: '1.1k',
    used: 34,
    category: 'CORPORATE',
    liked: false,
    hover: false
  },
  {
    id: 6,
    title: 'Summer Vibes Collection',
    thumbnail: '/storage/market/market_social_thumb_1773741789693.png',
    price: 12.0,
    author: 'TravelBug',
    authorAvatar: 'https://i.pravatar.cc/150?u=6',
    views: '3.3k',
    used: 245,
    category: 'SOCIAL',
    liked: false,
    hover: false
  }
])

const filteredTemplates = computed(() => {
  let data = mockData.value
  if (currentCategory.value !== 'ALL') {
    data = data.filter(item => item.category === currentCategory.value)
  }
  if (searchQuery.value) {
    const q = searchQuery.value.toLowerCase()
    data = data.filter(item => item.title.toLowerCase().includes(q))
  }
  return data
})

const handleSearch = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
  }, 500)
}

const openPreview = (item) => {
  selectedItem.value = item
  drawerVisible.value = true
}

const handleBuy = async (item) => {
  try {
    await ElMessageBox.confirm(
      `确定花费 ¥${item.price} 购买《${item.title}》吗？`,
      '确认购买',
      {
        confirmButtonText: '立即支付',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    ElMessage.success('购买成功！模板已添加到您的“我的模板”中。')
  } catch (e) {
    // Cancel
  }
}

const toggleLike = (item) => {
  item.liked = !item.liked
  if (item.liked) {
    ElMessage({ message: '已收藏至常用模版', type: 'success' })
  }
}
</script>

<style scoped>
.marketplace-container {
  min-height: 100vh;
  background: #f8f9fb;
  padding-bottom: 60px;
}

/* Banner Section */
.market-banner {
  height: 420px;
  background: linear-gradient(135deg, #1a2a6c 0%, #b21f1f 50%, #fdbb2d 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #fff;
  border-radius: 0 0 40px 40px;
  margin-bottom: -40px;
  position: relative;
  overflow: hidden;
}

.market-banner::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('https://www.transparenttextures.com/patterns/cubes.png');
  opacity: 0.1;
}

.banner-content h1 {
  font-size: 3.5rem;
  font-weight: 800;
  margin-bottom: 1rem;
  letter-spacing: -1px;
}

.banner-content p {
  font-size: 1.2rem;
  opacity: 0.9;
  margin-bottom: 2rem;
}

.stats-overview {
  display: flex;
  justify-content: center;
  gap: 60px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.7;
}

/* Toolbar */
.toolbar {
  max-width: 1200px;
  margin: 0 auto;
  background: #fff;
  padding: 30px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.custom-search :deep(.el-input__wrapper) {
  padding: 12px 20px;
  border-radius: 12px;
  font-size: 1.1rem;
}

.filter-groups {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Grid Layout */
.template-grid {
  max-width: 1200px;
  margin: 40px auto;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 30px;
  padding: 0 20px;
}

/* Market Card */
.market-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  position: relative;
}

.market-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

.card-media {
  height: 180px;
  overflow: hidden;
  position: relative;
}

.thumb-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s;
}

.market-card:hover .thumb-img {
  transform: scale(1.1);
}

.media-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(2px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.new-tag {
  position: absolute;
  top: 12px;
  left: 12px;
}

.card-info {
  padding: 20px;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  flex: 1;
}

.price {
  font-weight: 700;
  color: #e74c3c;
  font-size: 1.1rem;
  margin-left: 10px;
}

.info-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.creator {
  display: flex;
  align-items: center;
  gap: 8px;
}

.creator .name {
  font-size: 0.85rem;
  color: #7f8c8d;
}

.stats {
  font-size: 12px;
  color: #bdc3c7;
  display: flex;
  gap: 12px;
}

.card-actions {
  display: flex;
  gap: 10px;
}

.buy-btn {
  flex: 1;
  border-radius: 10px;
  font-weight: 600;
}

/* Features Section */
.features-section {
  max-width: 1200px;
  margin: 80px auto;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 40px;
  padding: 0 20px;
}

.feature-item {
  text-align: center;
  padding: 40px;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.03);
  transition: transform 0.3s;
}

.feature-item:hover {
  transform: translateY(-5px);
}

.feature-item .el-icon {
  margin-bottom: 20px;
  color: #8e44ad;
}

.feature-item h3 {
  font-size: 1.4rem;
  margin-bottom: 12px;
  color: #2c3e50;
}

.feature-item p {
  color: #7f8c8d;
  line-height: 1.6;
}

/* Trust Badges */
.trust-badges {
  text-align: center;
  margin: 60px 0;
  padding-top: 40px;
  border-top: 1px solid #e0e0e0;
}

.trust-title {
  text-transform: uppercase;
  letter-spacing: 2px;
  color: #bdc3c7;
  font-size: 0.8rem;
  font-weight: 700;
}

.badge-logos {
  display: flex;
  justify-content: center;
  gap: 50px;
  margin-top: 30px;
  filter: grayscale(1);
  opacity: 0.5;
}

.logo-placeholder {
  font-weight: 800;
  font-size: 1.2rem;
  color: #34495e;
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 50px;
}

/* Drawer Detail */
.market-drawer :deep(.el-drawer__body) {
  padding: 0;
}

.detail-content {
  display: flex;
  flex-direction: column;
}

.video-preview-box {
  width: 100%;
  height: 360px;
  background: #000;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.7;
}

.play-hint {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #fff;
  gap: 10px;
  font-weight: 500;
  cursor: pointer;
}

.detail-info {
  padding: 30px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f2f5;
  padding-bottom: 10px;
}

.info-row .label {
  color: #95a5a6;
}

.info-row .value {
  font-weight: 600;
  color: #34495e;
}

.price-large {
  color: #e74c3c;
  font-size: 1.8rem;
}

.description {
  color: #7f8c8d;
  line-height: 1.6;
  font-size: 1rem;
  margin-top: 20px;
}

.detail-actions {
  padding: 30px;
  margin-top: auto;
}

.full-btn {
  width: 100%;
  padding: 25px !important;
  font-size: 1.2rem;
  font-weight: 700;
  border-radius: 16px;
}

/* Animations */
@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.market-card {
  animation: fadeInUp 0.5s ease-out forwards;
}
</style>
