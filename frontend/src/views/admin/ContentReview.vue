<template>
  <div class="content-review-container">
    <!-- 主内容区域 -->
    <el-card class="main-content-card">
      <!-- 统计卡片 -->
      <div class="stats-row">
        <div class="stat-card">
          <div class="stat-icon pending">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pending }}</div>
            <div class="stat-label">待审核</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon approved">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.approved }}</div>
            <div class="stat-label">已通过</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon rejected">
            <el-icon><CircleClose /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.rejected }}</div>
            <div class="stat-label">已拒绝</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon total">
            <el-icon><DataAnalysis /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.total }}</div>
            <div class="stat-label">总计</div>
          </div>
        </div>
      </div>

      <!-- 筛选和搜索区域 -->
      <div class="filter-bar">
        <el-select v-model="filterStatus" placeholder="审核状态" clearable class="filter-select" @change="loadPendingContent">
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已拒绝" value="rejected" />
        </el-select>
        <el-select v-model="filterType" placeholder="内容类型" clearable class="filter-select" @change="loadPendingContent">
          <el-option label="视频" value="video" />
          <el-option label="图片" value="image" />
          <el-option label="文本" value="text" />
        </el-select>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户或内容..."
          prefix-icon="Search"
          class="search-input"
          clearable
          @keyup.enter="loadPendingContent"
        />
        <el-button type="primary" @click="loadPendingContent" class="search-btn">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>

      <!-- 内容列表 -->
      <div class="content-list" v-loading="loading">
        <div v-if="contentList.length === 0" class="empty-state">
          <el-icon size="64" color="#c0c4cc"><DocumentChecked /></el-icon>
          <p>暂无待审核内容</p>
        </div>

        <div v-else class="review-cards">
          <div v-for="item in contentList" :key="item.id" class="review-card">
            <div class="card-header">
              <div class="user-info">
                <el-avatar :size="40" :src="item.userAvatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
                <div class="user-details">
                  <div class="user-name">{{ item.userName }}</div>
                  <div class="submit-time">{{ formatDate(item.submitTime) }}</div>
                </div>
              </div>
              <el-tag :type="getStatusTag(item.status)" size="small">
                {{ getStatusLabel(item.status) }}
              </el-tag>
            </div>

            <div class="card-content">
              <div class="content-preview">
                <div v-if="item.type === 'video'" class="video-preview">
                  <video :src="item.contentUrl" controls class="preview-video"></video>
                </div>
                <div v-else-if="item.type === 'image'" class="image-preview">
                  <img :src="item.contentUrl" alt="预览" class="preview-image" />
                </div>
                <div v-else class="text-preview">
                  {{ item.content }}
                </div>
              </div>
              <div class="content-meta">
                <div class="meta-item">
                  <el-icon><Collection /></el-icon>
                  <span>类型：{{ getTypeLabel(item.type) }}</span>
                </div>
                <div v-if="item.description" class="meta-item description">
                  <el-icon><Document /></el-icon>
                  <span>描述：{{ item.description }}</span>
                </div>
              </div>
            </div>

            <div class="card-actions">
              <div v-if="item.status === 'pending'" class="action-buttons">
                <el-button type="danger" size="large" @click="handleReject(item)">
                  <el-icon><Close /></el-icon>
                  拒绝
                </el-button>
                <el-button type="success" size="large" @click="handleApprove(item)">
                  <el-icon><Check /></el-icon>
                  通过
                </el-button>
              </div>
              <div v-else class="review-result">
                <span v-if="item.status === 'approved'" class="result-text approved">
                  <el-icon><CircleCheck /></el-icon>
                  已通过审核
                </span>
                <span v-else class="result-text rejected">
                  <el-icon><CircleClose /></el-icon>
                  已拒绝审核
                </span>
                <span v-if="item.reviewNote" class="review-note">{{ item.reviewNote }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="contentList.length > 0" class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadPendingContent"
          @size-change="loadPendingContent"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  DocumentChecked, Refresh, Clock, CircleCheck, CircleClose,
  DataAnalysis, Search, User, Collection, Document, Check, Close
} from '@element-plus/icons-vue'

const loading = ref(false)
const searchKeyword = ref('')
const filterStatus = ref('pending')
const filterType = ref('')
const contentList = ref([])

const stats = reactive({
  pending: 23,
  approved: 456,
  rejected: 12,
  total: 491
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const mockContentList = [
  {
    id: 1,
    userId: 'user-001',
    userName: '张三',
    userAvatar: '',
    type: 'video',
    status: 'pending',
    content: 'AI生成的产品宣传视频',
    contentUrl: '/videos/sample1.mp4',
    description: '展示新产品的特点和优势',
    submitTime: '2024-01-26T10:30:00'
  },
  {
    id: 2,
    userId: 'user-002',
    userName: '李四',
    userAvatar: '',
    type: 'image',
    status: 'pending',
    content: '创意海报设计',
    contentUrl: '/images/sample1.png',
    description: '节日促销活动海报',
    submitTime: '2024-01-26T09:15:00'
  },
  {
    id: 3,
    userId: 'user-003',
    userName: '王五',
    userAvatar: '',
    type: 'text',
    status: 'approved',
    content: '这是一段关于产品介绍的文本内容...',
    contentUrl: '',
    description: '产品详细说明',
    submitTime: '2024-01-25T16:45:00',
    reviewNote: '内容合规，已通过'
  }
]

const getStatusLabel = (status) => {
  const labels = { pending: '待审核', approved: '已通过', rejected: '已拒绝' }
  return labels[status] || status
}

const getStatusTag = (status) => {
  const tags = { pending: 'warning', approved: 'success', rejected: 'danger' }
  return tags[status] || ''
}

const getTypeLabel = (type) => {
  const labels = { video: '视频', image: '图片', text: '文本' }
  return labels[type] || type
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

const loadPendingContent = async () => {
  try {
    loading.value = true
    // 模拟加载
    await new Promise(resolve => setTimeout(resolve, 500))
    contentList.value = mockContentList.filter(item => {
      if (filterStatus.value && item.status !== filterStatus.value) return false
      if (filterType.value && item.type !== filterType.value) return false
      if (searchKeyword.value && !item.userName.includes(searchKeyword.value)) return false
      return true
    })
    pagination.total = contentList.value.length
    loading.value = false
  } catch (error) {
    console.error('加载内容失败:', error)
    ElMessage.error('加载失败')
    loading.value = false
  }
}

const handleApprove = async (item) => {
  try {
    await ElMessageBox.confirm(`确定通过 "${item.userName}" 的内容？`, '确认', { type: 'success' })
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    item.status = 'approved'
    item.reviewNote = '内容合规，已通过'
    stats.pending--
    stats.approved++
    ElMessage.success('审核通过')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
      ElMessage.error('审核失败')
    }
  }
}

const handleReject = async (item) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因：', '拒绝审核', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入拒绝原因'
    })
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    item.status = 'rejected'
    item.reviewNote = value
    stats.pending--
    stats.rejected++
    ElMessage.success('已拒绝该内容')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败:', error)
      ElMessage.error('审核失败')
    }
  }
}

onMounted(() => loadPendingContent())
</script>

<style scoped>
.content-review-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: calc(100vh - 40px);
}

.main-content-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: #fff;
}

.stat-icon.approved {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: #fff;
}

.stat-icon.rejected {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: #fff;
}

.stat-icon.total {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.filter-bar {
  display: flex;
  gap: 12px;
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
  margin: 0 20px;
}

.search-input {
  width: 280px;
}

.filter-select {
  width: 140px;
}

.search-btn {
  min-width: 80px;
}

.content-list {
  padding: 0 20px 20px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-state p {
  margin: 16px 0;
  font-size: 16px;
}

.review-cards {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.submit-time {
  font-size: 13px;
  color: #909399;
}

.card-content {
  margin-bottom: 16px;
}

.content-preview {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.video-preview,
.image-preview {
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
}

.preview-video,
.preview-image {
  width: 100%;
  border-radius: 8px;
}

.text-preview {
  padding: 12px;
  background: #fff;
  border-radius: 6px;
  color: #606266;
  line-height: 1.6;
}

.content-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #606266;
}

.meta-item.description {
  padding-top: 8px;
  border-top: 1px dashed #e4e7ed;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.review-result {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #909399;
  font-size: 14px;
}

.result-text {
  display: flex;
  align-items: center;
  gap: 6px;
}

.result-text.approved {
  color: #10b981;
}

.result-text.rejected {
  color: #ef4444;
}

.review-note {
  color: #606266;
  font-style: italic;
}

.pagination-bar {
  display: flex;
  justify-content: center;
  padding: 20px;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .content-review-container {
    padding: 16px;
  }

  .stats-row {
    grid-template-columns: repeat(2, 1fr);
  }

  .filter-bar {
    flex-wrap: wrap;
  }

  .search-input,
  .filter-select {
    width: 100%;
  }
}
</style>
