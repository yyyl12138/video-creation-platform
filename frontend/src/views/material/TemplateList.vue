<template>
  <div class="template-market-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle-1"></div>
      <div class="bg-circle bg-circle-2"></div>
      <div class="bg-circle bg-circle-3"></div>
    </div>

    <!-- 页面标题卡片 -->
    <div class="page-header-card">
      <div class="header-content">
        <div class="header-icon-wrapper">
          <div class="header-icon">
            <el-icon size="36" color="#fff"><MagicStick /></el-icon>
          </div>
        </div>
        <div class="header-text">
          <h2>模板市场</h2>
          <p class="header-subtitle">✨ 探索精美模板，一键创建专业视频</p>
        </div>
      </div>
      <el-button
        type="primary"
        size="large"
        @click="showUploadDialog = true"
        class="upload-btn"
        :icon="Plus"
      >
        上传模板
      </el-button>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card hot">
        <div class="stat-icon">
          <el-icon><TrendCharts /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">1.8K</div>
          <div class="stat-label">热门模板</div>
        </div>
        <div class="stat-bg"></div>
      </div>
      <div class="stat-card downloads">
        <div class="stat-icon">
          <el-icon><Download /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">25.6K</div>
          <div class="stat-label">下载次数</div>
        </div>
        <div class="stat-bg"></div>
      </div>
      <div class="stat-card templates">
        <div class="stat-icon">
          <el-icon><Collection /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">856</div>
          <div class="stat-label">模板总数</div>
        </div>
        <div class="stat-bg"></div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <el-card class="main-content-card" shadow="never">
      <!-- 分类导航 -->
      <div class="category-nav">
        <div
          v-for="item in categoryItems"
          :key="item.value"
          class="category-item"
          :class="{ active: currentCategory === item.value }"
          @click="handleCategoryChange(item.value)"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
          <div v-if="item.count" class="category-count">{{ item.count }}</div>
        </div>
      </div>

      <!-- 搜索和筛选区域 -->
      <div class="filter-section">
        <div class="search-area">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索模板名称、关键词..."
            prefix-icon="Search"
            class="search-input"
            clearable
            @keyup.enter="loadTemplates"
          >
            <template #append>
              <el-button :icon="Search" @click="loadTemplates">搜索</el-button>
            </template>
          </el-input>
        </div>
        <div class="filter-controls">
          <el-select v-model="filterScene" placeholder="应用场景" clearable class="filter-select">
            <el-option v-for="item in scenes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
          <el-select v-model="filterSort" placeholder="排序方式" class="filter-select">
            <el-option label="最新上传" value="newest" />
            <el-option label="最多使用" value="mostUsed" />
            <el-option label="最多点赞" value="mostLiked" />
            <el-option label="最多下载" value="mostDownloaded" />
          </el-select>
          <el-button-group class="view-toggle">
            <el-button :type="viewMode === 'grid' ? 'primary' : 'default'" @click="viewMode = 'grid'" :icon="Grid">
              卡片
            </el-button>
            <el-button :type="viewMode === 'list' ? 'primary' : 'default'" @click="viewMode = 'list'" :icon="List">
              列表
            </el-button>
          </el-button-group>
        </div>
      </div>

      <!-- 热门推荐 -->
      <div v-if="currentCategory === 'all' && !searchKeyword" class="hot-section">
        <div class="section-header">
          <h3 class="section-title">
            <el-icon><StarFilled /></el-icon>
            热门推荐
          </h3>
          <el-button text @click="loadTemplates">
            <el-icon><Refresh /></el-icon>
            换一批
          </el-button>
        </div>
        <div class="hot-templates">
          <div
            v-for="template in hotTemplates"
            :key="template.id"
            class="hot-template-card"
            @click="showTemplateDetail(template)"
          >
            <div class="hot-preview">
              <div class="preview-icon">
                <el-icon :size="40"><component :is="getTemplateIcon(template.type)" /></el-icon>
              </div>
              <div class="hot-rank">#{{ template.rank }}</div>
            </div>
            <div class="hot-info">
              <h4>{{ template.name }}</h4>
              <p>{{ template.description }}</p>
              <div class="hot-stats">
                <span><el-icon><View /></el-icon>{{ template.views }}</span>
                <span><el-icon><Star /></el-icon>{{ template.likes }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 模板列表 -->
      <div class="template-content" v-loading="loading">
        <div v-if="templateList.length === 0" class="empty-state">
          <div class="empty-illustration">
            <el-icon size="120" color="#d4d4d8"><FolderOpened /></el-icon>
          </div>
          <div class="empty-text">
            <h3>暂无模板</h3>
            <p>成为第一个上传模板的人吧！</p>
            <el-button type="primary" size="large" @click="showUploadDialog = true" :icon="Plus">
              上传模板
            </el-button>
          </div>
        </div>

        <!-- 卡片视图 -->
        <div v-else-if="viewMode === 'grid'" class="template-grid">
          <div
            v-for="template in templateList"
            :key="template.id"
            class="template-card"
            @click="showTemplateDetail(template)"
          >
            <div class="template-preview">
              <div class="preview-wrapper">
                <div class="preview-icon">
                  <el-icon :size="50"><component :is="getTemplateIcon(template.category)" /></el-icon>
                </div>
                <div class="preview-overlay">
                  <div class="overlay-actions">
                    <el-button type="primary" :icon="Check" @click.stop="handleUseTemplate(template)">
                      使用
                    </el-button>
                    <el-button :icon="Star" @click.stop="toggleFavorite(template)">
                      {{ template.isFavorite ? '已收藏' : '收藏' }}
                    </el-button>
                  </div>
                </div>
              </div>
              <div class="template-badges">
                <el-tag v-if="template.isHot" type="danger" size="small" effect="dark">热门</el-tag>
                <el-tag v-if="template.isNew" type="success" size="small" effect="dark">新</el-tag>
              </div>
            </div>
            <div class="template-info">
              <h3 class="template-name">{{ template.name }}</h3>
              <p class="template-desc">{{ template.description || '暂无描述' }}</p>
              <div class="template-meta">
                <el-tag :type="getCategoryTag(template.category)" size="small">
                  {{ getCategoryLabel(template.category) }}
                </el-tag>
                <el-tag :type="getSceneTag(template.scene)" size="small">
                  {{ getSceneLabel(template.scene) }}
                </el-tag>
              </div>
              <div class="template-stats">
                <span class="stat-item">
                  <el-icon><View /></el-icon>
                  {{ template.views || 0 }}
                </span>
                <span class="stat-item">
                  <el-icon><Star /></el-icon>
                  {{ template.likes || 0 }}
                </span>
                <span class="stat-item">
                  <el-icon><Download /></el-icon>
                  {{ template.downloads || 0 }}
                </span>
                <span class="stat-item">
                  <el-icon><User /></el-icon>
                  {{ template.userName }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- 列表视图 -->
        <div v-else class="template-list-view">
          <div
            v-for="template in templateList"
            :key="template.id"
            class="template-list-item"
          >
            <div class="list-preview">
              <div class="preview-icon">
                <el-icon :size="40"><component :is="getTemplateIcon(template.category)" /></el-icon>
              </div>
            </div>
            <div class="list-info">
              <h3>{{ template.name }}</h3>
              <p>{{ template.description || '暂无描述' }}</p>
              <div class="list-meta">
                <el-tag :type="getCategoryTag(template.category)" size="small">
                  {{ getCategoryLabel(template.category) }}
                </el-tag>
                <el-tag :type="getSceneTag(template.scene)" size="small">
                  {{ getSceneLabel(template.scene) }}
                </el-tag>
                <span class="list-author">作者：{{ template.userName }}</span>
              </div>
            </div>
            <div class="list-stats">
              <span><el-icon><View /></el-icon>{{ template.views || 0 }}</span>
              <span><el-icon><Star /></el-icon>{{ template.likes || 0 }}</span>
              <span><el-icon><Download /></el-icon>{{ template.downloads || 0 }}</span>
            </div>
            <div class="list-actions">
              <el-button type="primary" :icon="Check" @click="handleUseTemplate(template)">
                使用
              </el-button>
              <el-button :icon="Star" @click="toggleFavorite(template)">
                {{ template.isFavorite ? '已收藏' : '收藏' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="templateList.length > 0" class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[12, 24, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadTemplates"
          @size-change="loadTemplates"
          background
        />
      </div>
    </el-card>

    <!-- 模板详情弹窗 -->
    <el-dialog
      v-model="showDetailDialog"
      :title="selectedTemplate?.name"
      width="800px"
      class="template-detail-dialog"
    >
      <div v-if="selectedTemplate" class="template-detail">
        <div class="detail-preview">
          <div class="preview-large">
            <el-icon :size="80"><component :is="getTemplateIcon(selectedTemplate.category)" /></el-icon>
          </div>
          <div class="detail-badges">
            <el-tag v-if="selectedTemplate.isHot" type="danger" size="large" effect="dark">热门</el-tag>
            <el-tag v-if="selectedTemplate.isNew" type="success" size="large" effect="dark">新</el-tag>
          </div>
        </div>
        <div class="detail-content">
          <h3 class="detail-title">{{ selectedTemplate.name }}</h3>
          <p class="detail-desc">{{ selectedTemplate.description || '暂无描述' }}</p>
          <div class="detail-meta">
            <div class="meta-item">
              <el-icon><Folder /></el-icon>
              <span>{{ getCategoryLabel(selectedTemplate.category) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Location /></el-icon>
              <span>{{ getSceneLabel(selectedTemplate.scene) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><User /></el-icon>
              <span>作者：{{ selectedTemplate.userName }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Clock /></el-icon>
              <span>上传时间：{{ formatDate(selectedTemplate.createTime) }}</span>
            </div>
          </div>
          <div class="detail-stats">
            <div class="stat-box">
              <div class="stat-number">{{ selectedTemplate.views || 0 }}</div>
              <div class="stat-label">浏览</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">{{ selectedTemplate.likes || 0 }}</div>
              <div class="stat-label">点赞</div>
            </div>
            <div class="stat-box">
              <div class="stat-number">{{ selectedTemplate.downloads || 0 }}</div>
              <div class="stat-label">下载</div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button size="large" @click="showDetailDialog = false">关闭</el-button>
        <el-button size="large" :icon="Star" @click="toggleFavorite(selectedTemplate)">
          {{ selectedTemplate?.isFavorite ? '已收藏' : '收藏' }}
        </el-button>
        <el-button type="primary" size="large" :icon="Check" @click="handleUseTemplate(selectedTemplate)">
          立即使用
        </el-button>
      </template>
    </el-dialog>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      title="上传视频模板"
      width="600px"
      class="upload-dialog"
    >
      <el-form :model="uploadForm" label-width="100px">
        <el-form-item label="模板名称">
          <el-input v-model="uploadForm.name" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板描述">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入模板描述"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="uploadForm.category" placeholder="选择分类">
            <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="应用场景">
          <el-select v-model="uploadForm.scene" placeholder="选择场景">
            <el-option v-for="item in scenes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板文件">
          <el-upload
            ref="uploadRef"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :auto-upload="false"
            :multiple="false"
            :limit="1"
            drag
            class="upload-component"
          >
            <el-icon class="upload-icon"><UploadFilled /></el-icon>
            <div class="upload-text">
              <p class="main-text">拖拽模板文件到此处，或<em>点击选择</em></p>
              <p class="sub-text">支持MP4/MOV格式，单个文件不超过500MB</p>
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :loading="uploading">
          {{ uploading ? '上传中...' : '上传' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Search, MagicStick, VideoCamera, View, Delete, Check, FolderOpened,
  UploadFilled, TrendCharts, Download, Collection, Star, StarFilled,
  Grid, List, Refresh, Clock, Location, User, Folder, Document
} from '@element-plus/icons-vue'
import { getTemplates, uploadTemplate, deleteTemplate, useTemplate } from '@/api/user/material'

const loading = ref(false)
const searchKeyword = ref('')
const filterCategory = ref('')
const filterScene = ref('')
const filterSort = ref('newest')
const currentCategory = ref('all')
const viewMode = ref('grid')
const showUploadDialog = ref(false)
const showDetailDialog = ref(false)
const uploading = ref(false)
const uploadRef = ref(null)
const selectedTemplate = ref(null)
const templateList = ref([])

const categories = [
  { value: 'short_video', label: '短视频模板', icon: 'VideoCamera' },
  { value: 'intro', label: '片头模板', icon: 'Star' },
  { value: 'transition', label: '转场模板', icon: 'Refresh' },
  { value: 'subtitle', label: '字幕模板', icon: 'Document' },
  { value: 'effect', label: '特效模板', icon: 'MagicStick' }
]

const categoryItems = [
  { value: 'all', label: '全部', icon: 'Grid', count: 856 },
  { value: 'short_video', label: '短视频', icon: 'VideoCamera', count: 324 },
  { value: 'intro', label: '片头', icon: 'Star', count: 156 },
  { value: 'transition', label: '转场', icon: 'Refresh', count: 198 },
  { value: 'subtitle', label: '字幕', icon: 'Document', count: 98 },
  { value: 'effect', label: '特效', icon: 'MagicStick', count: 80 }
]

const scenes = [
  { value: 'advertising', label: '广告营销' },
  { value: 'education', label: '教育培训' },
  { value: 'social_media', label: '社交媒体' },
  { value: 'corporate', label: '企业宣传' },
  { value: 'personal', label: '个人创作' }
]

const uploadForm = reactive({
  name: '',
  description: '',
  category: '',
  scene: ''
})

const pagination = reactive({
  page: 1,
  size: 12,
  total: 0
})

const uploadUrl = computed(() => `${import.meta.env.VITE_APP_BASE_API}/template/upload`)
const uploadHeaders = computed(() => ({ Authorization: localStorage.getItem('token') }))

const hotTemplates = ref([
  { id: 101, name: '赛博朋克风格', description: '未来科技城市夜景', type: 'video', category: 'short_video', rank: 1, views: 12560, likes: 856 },
  { id: 102, name: '企业宣传片头', description: '高端大气开场动画', type: 'video', category: 'intro', rank: 2, views: 9820, likes: 624 },
  { id: 103, name: '炫酷转场效果', description: '平滑过渡动画', type: 'video', category: 'transition', rank: 3, views: 7650, likes: 489 },
  { id: 104, name: '动态字幕模板', description: '文字特效动画', type: 'text', category: 'subtitle', rank: 4, views: 6230, likes: 378 },
  { id: 105, name: '粒子特效包', description: '高级视觉特效', type: 'video', category: 'effect', rank: 5, views: 5480, likes: 296 }
])

const mockTemplates = [
  {
    id: 1,
    name: '时尚产品展示模板',
    description: '适用于时尚品牌、电商产品的精美展示模板',
    category: 'short_video',
    scene: 'advertising',
    userName: '创意设计师',
    isHot: true,
    isNew: false,
    views: 12560,
    likes: 856,
    downloads: 2340,
    isFavorite: false,
    createTime: '2024-01-26T10:30:00'
  },
  {
    id: 2,
    name: '企业宣传片头',
    description: '高端大气的企业宣传开场动画',
    category: 'intro',
    scene: 'corporate',
    userName: '视觉特效师',
    isHot: true,
    isNew: true,
    views: 9820,
    likes: 624,
    downloads: 1876,
    isFavorite: true,
    createTime: '2024-01-25T14:20:00'
  },
  {
    id: 3,
    name: '炫酷转场效果',
    description: '多种平滑过渡的转场动画效果',
    category: 'transition',
    scene: 'social_media',
    userName: '动画达人',
    isHot: true,
    isNew: false,
    views: 7650,
    likes: 489,
    downloads: 1456,
    isFavorite: false,
    createTime: '2024-01-24T16:45:00'
  },
  {
    id: 4,
    name: '动态字幕模板',
    description: '文字特效动画字幕模板',
    category: 'subtitle',
    scene: 'education',
    userName: '字幕设计师',
    isHot: false,
    isNew: true,
    views: 6230,
    likes: 378,
    downloads: 987,
    isFavorite: false,
    createTime: '2024-01-24T11:30:00'
  },
  {
    id: 5,
    name: '粒子特效包',
    description: '高级视觉粒子特效合集',
    category: 'effect',
    scene: 'personal',
    userName: '特效大师',
    isHot: true,
    isNew: false,
    views: 5480,
    likes: 296,
    downloads: 765,
    isFavorite: true,
    createTime: '2024-01-23T09:15:00'
  },
  {
    id: 6,
    name: '美食短视频模板',
    description: '美食展示的短视频模板',
    category: 'short_video',
    scene: 'social_media',
    userName: '美食博主',
    isHot: false,
    isNew: true,
    views: 4320,
    likes: 245,
    downloads: 654,
    isFavorite: false,
    createTime: '2024-01-22T15:40:00'
  }
]

const getCategoryLabel = (category) => {
  const found = categories.find(item => item.value === category)
  return found ? found.label : category
}

const getCategoryTag = (category) => {
  const tags = {
    short_video: '',
    intro: 'warning',
    transition: 'info',
    subtitle: 'success',
    effect: 'danger'
  }
  return tags[category] || ''
}

const getSceneLabel = (scene) => {
  const found = scenes.find(item => item.value === scene)
  return found ? found.label : scene
}

const getSceneTag = (scene) => {
  const tags = {
    advertising: '',
    education: 'success',
    social_media: 'warning',
    corporate: 'info',
    personal: 'danger'
  }
  return tags[scene] || ''
}

const getTemplateIcon = (category) => {
  const icons = {
    short_video: 'VideoCamera',
    intro: 'Star',
    transition: 'Refresh',
    subtitle: 'Document',
    effect: 'MagicStick'
  }
  return icons[category] || 'Folder'
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const handleCategoryChange = (value) => {
  currentCategory.value = value
  if (value !== 'all') {
    filterCategory.value = value
  } else {
    filterCategory.value = ''
  }
  loadTemplates()
}

const loadTemplates = async () => {
  try {
    loading.value = true
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    let filtered = mockTemplates
    if (searchKeyword.value) {
      filtered = filtered.filter(item =>
        item.name.includes(searchKeyword.value) ||
        item.description?.includes(searchKeyword.value)
      )
    }
    if (filterCategory.value) {
      filtered = filtered.filter(item => item.category === filterCategory.value)
    }
    if (filterScene.value) {
      filtered = filtered.filter(item => item.scene === filterScene.value)
    }
    templateList.value = filtered
    pagination.total = filtered.length
    loading.value = false
  } catch (error) {
    console.error('加载模板失败:', error)
    ElMessage.error('加载失败')
    loading.value = false
  }
}

const showTemplateDetail = (template) => {
  selectedTemplate.value = template
  showDetailDialog.value = true
}

const handlePreview = (template) => {
  ElMessage.info(`预览: ${template.name}`)
}

const toggleFavorite = (template) => {
  template.isFavorite = !template.isFavorite
  if (template.isFavorite) {
    template.likes++
    ElMessage.success('已收藏')
  } else {
    template.likes--
    ElMessage.info('已取消收藏')
  }
}

const handleUseTemplate = async (template) => {
  try {
    ElMessage.success(`开始使用: ${template.name}`)
    // TODO: 跳转到创作页面
  } catch (error) {
    console.error('使用模板失败:', error)
    ElMessage.error('使用模板失败')
  }
}

const handleDelete = async (template) => {
  try {
    await ElMessageBox.confirm(`确定删除 "${template.name}"？`, '确认', { type: 'warning' })
    const index = templateList.value.findIndex(t => t.id === template.id)
    if (index > -1) templateList.value.splice(index, 1)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const submitUpload = async () => {
  if (!uploadForm.name) {
    ElMessage.warning('请输入模板名称')
    return
  }

  try {
    uploading.value = true
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('上传成功')
    showUploadDialog.value = false
    uploadForm.name = ''
    uploadForm.description = ''
    uploadForm.category = ''
    uploadForm.scene = ''
    loadTemplates()
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

onMounted(() => loadTemplates())
</script>

<style scoped>
.template-market-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #667eea0d 0%, #764ba20d 100%);
  min-height: calc(100vh - 40px);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
  filter: blur(60px);
  animation: float 20s infinite ease-in-out;
}

.bg-circle-1 {
  width: 600px;
  height: 600px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  top: -200px;
  right: -200px;
  animation-delay: 0s;
}

.bg-circle-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  bottom: -100px;
  left: -100px;
  animation-delay: -5s;
}

.bg-circle-3 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation-delay: -10s;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0) rotate(0deg); }
  25% { transform: translate(30px, -30px) rotate(90deg); }
  50% { transform: translate(0, 30px) rotate(180deg); }
  75% { transform: translate(-30px, 0) rotate(270deg); }
}

/* 页面标题卡片 */
.page-header-card {
  background: linear-gradient(135deg, #8e44ad 0%, #9b59b6 100%);
  border-radius: 20px;
  padding: 28px 36px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 20px 60px rgba(142, 68, 173, 0.4);
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 24px;
  position: relative;
  z-index: 1;
}

.header-icon-wrapper {
  position: relative;
}

.header-icon {
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
  animation: pulse 3s infinite ease-in-out;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.header-text h2 {
  margin: 0;
  font-size: 2rem;
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.5px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.header-subtitle {
  margin: 6px 0 0 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: 1rem;
  font-weight: 500;
}

.upload-btn {
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.6);
  font-weight: 600;
  font-size: 15px;
  padding: 12px 28px;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.upload-btn:hover {
  background: rgba(255, 255, 255, 0.35);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.8);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.12);
}

.stat-card .stat-bg {
  position: absolute;
  top: 0;
  right: 0;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.3;
}

.stat-card.hot .stat-bg {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  right: -40px;
  top: -40px;
}

.stat-card.downloads .stat-bg {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  right: -40px;
  top: -40px;
}

.stat-card.templates .stat-bg {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  right: -40px;
  top: -40px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  position: relative;
  z-index: 1;
}

.stat-card.hot .stat-icon {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: #fff;
  box-shadow: 0 8px 20px rgba(245, 158, 11, 0.4);
}

.stat-card.downloads .stat-icon {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
  box-shadow: 0 8px 20px rgba(16, 185, 129, 0.4);
}

.stat-card.templates .stat-icon {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff;
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
}

.stat-value {
  font-size: 32px;
  font-weight: 800;
  color: #303133;
  line-height: 1.2;
  letter-spacing: -1px;
  position: relative;
  z-index: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
  font-weight: 500;
}

/* 主内容卡片 */
.main-content-card {
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  border: none;
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
}

/* 分类导航 */
.category-nav {
  display: flex;
  gap: 12px;
  padding: 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 16px;
  margin: 20px;
  flex-wrap: wrap;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #64748b;
  background: #fff;
  border: 2px solid transparent;
  font-weight: 500;
  font-size: 14px;
  position: relative;
  overflow: hidden;
}

.category-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #8e44ad 0%, #9b59b6 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1;
}

.category-item:hover {
  color: #8e44ad;
  border-color: #8e44ad;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(142, 68, 173, 0.2);
}

.category-item.active {
  color: #fff;
  border-color: transparent;
  background: linear-gradient(135deg, #8e44ad 0%, #9b59b6 100%);
  box-shadow: 0 8px 24px rgba(142, 68, 173, 0.4);
}

.category-item.active::before {
  opacity: 1;
}

.category-item .category-count {
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 600;
  min-width: 20px;
  text-align: center;
}

.category-item:not(.active) .category-count {
  background: #f3e8ff;
  color: #8e44ad;
}

/* 筛选区域 */
.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 16px;
  margin: 0 20px 20px;
  flex-wrap: wrap;
  gap: 16px;
}

.search-area {
  flex: 1;
  min-width: 280px;
}

.search-input {
  max-width: 100%;
}

.filter-controls {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-select {
  width: 160px;
}

.view-toggle {
  display: flex;
  gap: 4px;
}

/* 热门推荐 */
.hot-section {
  margin: 0 20px 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.hot-templates {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.hot-template-card {
  background: linear-gradient(135deg, #fdf4ff 0%, #fae8ff 100%);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid #f3e8ff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.hot-template-card:hover {
  transform: translateY(-4px);
  border-color: #9b59b6;
  box-shadow: 0 8px 24px rgba(155, 89, 182, 0.3);
}

.hot-preview {
  position: relative;
  flex-shrink: 0;
}

.preview-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(155, 89, 182, 0.3);
}

.hot-rank {
  position: absolute;
  top: -8px;
  left: -8px;
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.4);
}

.hot-info {
  flex: 1;
  min-width: 0;
}

.hot-info h4 {
  margin: 0 0 6px 0;
  font-size: 15px;
  font-weight: 600;
  color: #1f293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-info p {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #64748b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-stats {
  display: flex;
  gap: 12px;
}

.hot-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #94a3b8;
}

/* 模板内容 */
.template-content {
  padding: 0 20px 20px;
  position: relative;
  z-index: 1;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #94a3b8;
}

.empty-illustration {
  margin-bottom: 24px;
  animation: float 3s ease-in-out infinite;
}

.empty-text h3 {
  font-size: 24px;
  color: #475569;
  margin: 0 0 8px 0;
}

.empty-text p {
  font-size: 16px;
  margin: 0 0 24px 0;
}

/* 卡片视图 */
.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.template-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e2e8f0;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  animation: slideUp 0.6s ease-out;
  animation-fill-mode: both;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.template-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
  border-color: #cbd5e1;
}

.template-preview {
  position: relative;
  height: 180px;
  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-icon {
  color: #8e44ad;
  transition: transform 0.3s ease;
}

.template-card:hover .preview-icon {
  transform: scale(1.1);
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(142, 68, 173, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  gap: 12px;
}

.template-card:hover .preview-overlay {
  opacity: 1;
}

.overlay-actions {
  display: flex;
  gap: 12px;
}

.overlay-actions .el-button {
  background: rgba(255, 255, 255, 0.95);
  color: #8e44ad;
  border: 2px solid #fff;
  backdrop-filter: blur(10px);
}

.overlay-actions .el-button:hover {
  background: #fff;
  transform: translateY(-2px);
}

.template-badges {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  gap: 8px;
}

.template-info {
  padding: 20px;
}

.template-name {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.template-desc {
  margin: 0 0 12px 0;
  font-size: 13px;
  color: #64748b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.template-meta {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.template-stats {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #94a3b8;
}

.stat-item .el-icon {
  font-size: 14px;
}

/* 列表视图 */
.template-list-view {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.template-list-item {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  gap: 20px;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #e2e8f0;
  transition: all 0.3s ease;
}

.template-list-item:hover {
  border-color: #9b59b6;
  box-shadow: 0 4px 12px rgba(155, 89, 182, 0.2);
}

.list-preview {
  flex-shrink: 0;
}

.list-preview .preview-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8e44ad;
}

.list-info {
  flex: 1;
  min-width: 0;
}

.list-info h3 {
  margin: 0 0 6px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
}

.list-info p {
  margin: 0 0 10px 0;
  font-size: 13px;
  color: #64748b;
}

.list-meta {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.list-author {
  font-size: 12px;
  color: #94a3b8;
}

.list-stats {
  display: flex;
  gap: 16px;
  padding: 0 20px;
  color: #94a3b8;
  font-size: 13px;
}

.list-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.list-stats .el-icon {
  font-size: 14px;
}

.list-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

/* 模板详情弹窗 */
.template-detail-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #8e44ad 0%, #9b59b6 100%);
  color: #fff;
  border-radius: 20px 20px 0 0;
  padding: 24px 28px;
}

.template-detail-dialog :deep(.el-dialog__title) {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
}

.template-detail {
  display: flex;
  gap: 32px;
}

.detail-preview {
  flex: 1;
  position: relative;
}

.preview-large {
  width: 100%;
  height: 240px;
  background: linear-gradient(135deg, #f3e8ff 0%, #e9d5ff 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8e44ad;
}

.detail-badges {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  gap: 8px;
}

.detail-content {
  flex: 1;
}

.detail-title {
  margin: 0 0 12px 0;
  font-size: 22px;
  font-weight: 700;
  color: #1e293b;
}

.detail-desc {
  margin: 0 0 20px 0;
  font-size: 15px;
  color: #64748b;
  line-height: 1.6;
}

.detail-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #64748b;
}

.meta-item .el-icon {
  color: #8e44ad;
}

.detail-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding-top: 20px;
  border-top: 2px solid #f1f5f9;
}

.stat-box {
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: 800;
  color: #8e44ad;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #94a3b8;
  margin-top: 4px;
}

/* 上传对话框 */
.upload-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #8e44ad 0%, #9b59b6 100%);
  color: #fff;
  border-radius: 16px 16px 0 0;
  padding: 24px 28px;
}

.upload-dialog :deep(.el-dialog__title) {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
}

.upload-component {
  width: 100%;
}

.upload-component :deep(.el-upload-dragger) {
  padding: 40px;
  border-radius: 12px;
  border: 2px dashed #e9d5ff;
  background: #faf5ff;
}

.upload-icon {
  font-size: 48px;
  color: #8e44ad;
  margin-bottom: 16px;
}

.upload-text .main-text {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #606266;
}

.upload-text .main-text em {
  color: #8e44ad;
  font-style: normal;
}

.upload-text .sub-text {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

/* 分页 */
.pagination-bar {
  display: flex;
  justify-content: center;
  padding: 24px;
  border-top: 1px solid #f1f5f9;
  position: relative;
  z-index: 1;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .hot-templates {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }
}

@media (max-width: 768px) {
  .template-market-container {
    padding: 16px;
  }

  .page-header-card {
    flex-direction: column;
    gap: 20px;
    text-align: center;
    padding: 24px 20px;
  }

  .header-content {
    flex-direction: column;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .category-nav,
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-controls {
    width: 100%;
  }

  .filter-select,
  .search-area {
    width: 100%;
  }

  .template-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  }

  .hot-templates {
    grid-template-columns: 1fr;
  }

  .template-list-item {
    flex-direction: column;
    text-align: center;
  }

  .list-stats,
  .list-actions {
    width: 100%;
    justify-content: center;
    padding: 0;
  }

  .template-detail {
    flex-direction: column;
  }

  .detail-meta {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .template-grid {
    grid-template-columns: 1fr;
  }

  .detail-stats {
    grid-template-columns: 1fr;
  }
}
</style>