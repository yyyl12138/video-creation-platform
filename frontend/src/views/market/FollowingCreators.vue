<template>
  <div class="following-creators">
    <div class="page-header">
      <h2 class="page-title">关注的创作者</h2>
      <p class="page-subtitle">查看您关注的优秀创作者</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <!-- 创作者列表 -->
    <div v-else-if="creators.length > 0" class="creators-list">
      <div
        v-for="creator in creators"
        :key="creator.userId"
        class="creator-card">
        <el-avatar :size="60" :src="creator.avatarUrl">
          {{ creator.username?.charAt(0) || 'U' }}
        </el-avatar>
        <div class="creator-info">
          <h3 class="creator-name">{{ creator.username }}</h3>
          <p class="creator-bio">{{ creator.bio || '暂无简介' }}</p>
          <div class="creator-stats">
            <span>粉丝: {{ formatNumber(creator.followerCount) }}</span>
            <span>模版: {{ formatNumber(creator.templateCount) }}</span>
          </div>
        </div>
        <div class="creator-actions">
          <el-button
            type="primary"
            @click="viewCreatorTemplates(creator.userId)">
            查看模版
          </el-button>
          <el-button
            type="danger"
            plain
            @click="handleUnfollow(creator.userId)">
            取消关注
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty
      v-else
      description="暂无关注的创作者"
      :image-size="200">
      <template #description>
        <p style="color: #86909c; margin-top: 16px;">去模版市场发现优秀的创作者吧</p>
      </template>
      <el-button type="primary" @click="goToMarket">
        前往模版市场
      </el-button>
    </el-empty>

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
import { Loading } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 响应式数据
const creators = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取关注的创作者列表
const fetchFollowingCreators = async () => {
  loading.value = true
  try {
    // 注意：这里假设有获取关注列表的接口
    // 实际接口路径可能需要根据后端实现调整
    const response = await axios.get('/api/v1/interactions/following', {
      params: {
        page: currentPage.value,
        size: pageSize.value
      }
    })

    if (response.data.code === 20000) {
      creators.value = response.data.data.records || []
      total.value = response.data.data.total || 0
    }
  } catch (error) {
    console.error('获取关注列表失败:', error)

    // 如果接口不存在，显示空状态
    if (error.response?.status === 404) {
      ElMessage.info('该功能暂未开放')
      creators.value = []
    } else {
      ElMessage.error('获取关注列表失败')
    }
  } finally {
    loading.value = false
  }
}

// 取消关注
const handleUnfollow = async (creatorId) => {
  try {
    await ElMessageBox.confirm('确认取消关注这位创作者？', '确认操作', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await axios.post('/api/v1/interactions/follow', {
      targetId: creatorId
    })

    if (response.data.code === 20000) {
      ElMessage.success('取消关注成功')
      // 刷新列表
      await fetchFollowingCreators()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消关注失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 查看创作者的模版
const viewCreatorTemplates = (creatorId) => {
  // 跳转到模版市场并筛选该创作者的模版
  router.push({
    path: '/market/templates',
    query: { creatorId }
  })
}

// 前往模版市场
const goToMarket = () => {
  router.push('/market/templates')
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  fetchFollowingCreators()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  fetchFollowingCreators()
}

// 格式化数字（如：1000 -> 1K）
const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 1000000) return `${(num / 1000000).toFixed(1)}M`
  if (num >= 1000) return `${(num / 1000).toFixed(1)}K`
  return num.toString()
}

// 生命周期
onMounted(() => {
  fetchFollowingCreators()
})
</script>

<style scoped>
.following-creators {
  padding: 24px;
  min-height: calc(100vh - 120px);
}

.page-header {
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
  margin: 0;
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

.creators-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.creator-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.creator-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.creator-info {
  flex: 1;
  min-width: 0;
}

.creator-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f2329;
  margin: 0 0 8px 0;
}

.creator-bio {
  font-size: 14px;
  color: #4e5969;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.creator-stats {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #86909c;
}

.creator-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .creator-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .creator-info {
    width: 100%;
  }

  .creator-actions {
    width: 100%;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
