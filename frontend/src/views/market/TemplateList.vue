<template>
  <div class="template-market">
    <div class="market-header">
      <h2 class="page-title">模版市场</h2>
      <p class="page-subtitle">发现优质模版，激发创作灵感</p>

      <!-- 搜索和筛选 -->
      <div class="filter-section">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索模版名称或标签"
          clearable
          class="search-input"
          @input="handleSearch">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>

        <el-select
          v-model="filterType"
          placeholder="模版类型"
          clearable
          class="type-select"
          @change="handleFilterChange">
          <el-option label="全部类型" value="" />
          <el-option label="短视频" value="VIDEO" />
          <el-option label="图文" value="IMAGE" />
          <el-option label="其他" value="OTHER" />
        </el-select>
      </div>
    </div>

    <!-- 模版列表 -->
    <div class="template-grid">
      <div
        v-for="template in templates"
        :key="template.templateId"
        class="template-card"
        @click="goToDetail(template.templateId)">
        <div class="template-cover">
          <img :src="template.coverUrl || '/default-cover.jpg'" :alt="template.templateName">
          <div class="template-overlay">
            <el-button type="primary" size="small">查看详情</el-button>
          </div>
        </div>
        <div class="template-info">
          <h3 class="template-name">{{ template.templateName }}</h3>
          <div class="template-meta">
            <span class="creator-name">{{ template.creatorName }}</span>
            <span class="template-price">{{ template.price === 0 ? '免费' : `${template.price} 积分` }}</span>
          </div>
          <div class="template-stats">
            <span><el-icon><View /></el-icon> {{ template.usageCount }}</span>
            <span><el-icon><Star /></el-icon> {{ template.likeCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <!-- 空状态 -->
    <el-empty
      v-if="!loading && templates.length === 0"
      description="暂无模版数据" />

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View, Star, Loading } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const templates = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const filterType = ref('')

// 获取模版列表
const fetchTemplates = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }

    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }

    if (filterType.value) {
      params.type = filterType.value
    }

    const response = await axios.get('/api/v1/market/templates', { params })

    if (response.data.code === 20000) {
      templates.value = response.data.data.records || []
      total.value = response.data.data.total || 0
    }
  } catch (error) {
    console.error('获取模版列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索处理（防抖）
let searchTimer = null
const handleSearch = () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = setTimeout(() => {
    currentPage.value = 1
    fetchTemplates()
  }, 500)
}

// 筛选变更
const handleFilterChange = () => {
  currentPage.value = 1
  fetchTemplates()
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  fetchTemplates()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchTemplates()
}

// 跳转详情页
const goToDetail = (templateId) => {
  router.push(`/market/template/${templateId}`)
}

// 生命周期
onMounted(() => {
  fetchTemplates()
})
</script>

<style scoped>
.template-market {
  padding: 24px;
  min-height: calc(100vh - 120px);
}

.market-header {
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  color: #1f2329;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #86909c;
  margin: 0 0 24px 0;
}

.filter-section {
  display: flex;
  gap: 16px;
  align-items: center;
}

.search-input {
  width: 300px;
}

.type-select {
  width: 150px;
}

.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.template-card {
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.template-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.template-cover {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 比例 */
  overflow: hidden;
  background: #f7f8fa;
}

.template-cover img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.template-card:hover .template-cover img {
  transform: scale(1.05);
}

.template-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.template-card:hover .template-overlay {
  opacity: 1;
}

.template-info {
  padding: 16px;
}

.template-name {
  font-size: 16px;
  font-weight: 500;
  color: #1f2329;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.template-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.creator-name {
  font-size: 14px;
  color: #4e5969;
}

.template-price {
  font-size: 14px;
  font-weight: 600;
  color: #f53f3f;
}

.template-stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #86909c;
}

.template-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.template-stats .el-icon {
  font-size: 14px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px;
  color: #86909c;
  gap: 12px;
}

.loading-container .el-icon {
  font-size: 32px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
