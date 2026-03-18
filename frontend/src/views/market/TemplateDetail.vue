<template>
  <div class="template-detail">
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading"><Loading /></el-icon>
      <span>加载中...</span>
    </div>

    <div v-else-if="template" class="detail-container">
      <!-- 返回按钮 -->
      <div class="back-button">
        <el-button @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>

      <!-- 模版预览区域 -->
      <div class="preview-section">
        <div class="preview-media">
          <img
            v-if="template.coverUrl"
            :src="template.coverUrl"
            :alt="template.templateName"
            class="cover-image">
          <div v-else class="default-cover">
            <el-icon><VideoCamera /></el-icon>
            <span>暂无预览</span>
          </div>
        </div>
      </div>

      <!-- 模版信息区域 -->
      <div class="info-section">
        <div class="template-header">
          <h1 class="template-title">{{ template.templateName }}</h1>
          <div class="template-actions">
            <el-button
              v-if="!template.purchased"
              type="primary"
              size="large"
              @click="handlePurchase"
              :loading="purchasing">
              {{ template.price === 0 ? '免费获取' : `${template.price} 积分购买` }}
            </el-button>
            <el-button
              v-else
              type="success"
              size="large"
              @click="useTemplate">
              使用模版
            </el-button>
          </div>
        </div>

        <!-- 创作者信息 -->
        <div class="creator-section">
          <div class="creator-info">
            <el-avatar :size="40">
              {{ template.creatorName?.charAt(0) || 'U' }}
            </el-avatar>
            <div class="creator-detail">
              <div class="creator-name">{{ template.creatorName }}</div>
              <div class="creator-stats">
                <span>模版销量: {{ template.usageCount }}</span>
                <span>评分: {{ template.rating || 0 }}</span>
              </div>
            </div>
          </div>
          <el-button
            :type="isFollowed ? 'default' : 'primary'"
            @click="handleFollow"
            :loading="following">
            {{ isFollowed ? '已关注' : '关注' }}
          </el-button>
        </div>

        <!-- 模版描述 -->
        <div class="description-section">
          <h3>模版介绍</h3>
          <p class="description">{{ template.description || '暂无介绍' }}</p>
        </div>

        <!-- 模版标签 -->
        <div v-if="template.tags && template.tags.length > 0" class="tags-section">
          <h3>标签</h3>
          <div class="tags-list">
            <el-tag
              v-for="tag in template.tags"
              :key="tag"
              size="small">
              {{ tag }}
            </el-tag>
          </div>
        </div>

        <!-- 互动数据 -->
        <div class="interaction-section">
          <div class="interaction-item" @click="handleLike">
            <el-icon :class="{ 'liked': isLiked }">
              <Star />
            </el-icon>
            <span>{{ template.likeCount || 0 }}</span>
          </div>
          <div class="interaction-item">
            <el-icon><View /></el-icon>
            <span>{{ template.usageCount || 0 }}</span>
          </div>
        </div>

        <!-- 评论区 -->
        <div class="comments-section">
          <div class="comments-header">
            <h3>评论 ({{ totalComments }})</h3>
          </div>

          <!-- 发表评论 -->
          <div class="comment-input">
            <el-input
              v-model="newComment"
              type="textarea"
              :rows="3"
              placeholder="发表你的评论..."
              maxlength="500"
              show-word-limit />
            <el-button
              type="primary"
              @click="submitComment"
              :loading="submittingComment"
              style="margin-top: 12px;">
              发表评论
            </el-button>
          </div>

          <!-- 评论列表 -->
          <div class="comments-list">
            <div
              v-for="comment in comments"
              :key="comment.commentId"
              class="comment-item">
              <el-avatar :size="36">
                {{ comment.nickname?.charAt(0) || 'U' }}
              </el-avatar>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.nickname }}</span>
                  <span class="comment-time">{{ formatTime(comment.createdTime) }}</span>
                </div>
                <div class="comment-text">{{ comment.content }}</div>
                <div class="comment-actions">
                  <el-button
                    v-if="comment.isCurrentUser"
                    type="danger"
                    size="small"
                    link
                    @click="deleteComment(comment.commentId)">
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div v-if="totalComments > 0" class="pagination-container">
            <el-pagination
              v-model:current-page="commentPage"
              v-model:page-size="commentPageSize"
              :total="totalComments"
              layout="prev, pager, next"
              @current-change="fetchComments" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, VideoCamera, Star, View, Loading } from '@element-plus/icons-vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

// 获取模版ID
const templateId = route.params.id

// 响应式数据
const template = ref(null)
const loading = ref(true)
const purchasing = ref(false)
const following = ref(false)
const submittingComment = ref(false)
const isLiked = ref(false)
const isFollowed = ref(false)

// 评论数据
const comments = ref([])
const newComment = ref('')
const commentPage = ref(1)
const commentPageSize = ref(10)
const totalComments = ref(0)

// 获取模版详情
const fetchTemplateDetail = async () => {
  loading.value = true
  try {
    const response = await axios.get(`/api/v1/market/templates/${templateId}`)
    if (response.data.code === 20000) {
      template.value = response.data.data
      isLiked.value = response.data.data.liked || false
      isFollowed.value = response.data.data.followed || false
    }
  } catch (error) {
    console.error('获取模版详情失败:', error)
    ElMessage.error('获取模版详情失败')
  } finally {
    loading.value = false
  }
}

// 获取评论列表
const fetchComments = async () => {
  try {
    const response = await axios.get('/api/v1/comments', {
      params: {
        targetId: templateId,
        page: commentPage.value,
        size: commentPageSize.value
      }
    })
    if (response.data.code === 20000) {
      comments.value = response.data.data.records || []
      totalComments.value = response.data.data.total || 0
    }
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

// 购买模版
const handlePurchase = async () => {
  try {
    await ElMessageBox.confirm(
      template.value.price === 0 ? '确认免费获取此模版？' : `确认消耗 ${template.value.price} 积分购买此模版？`,
      '确认购买',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    purchasing.value = true
    const response = await axios.post(`/api/v1/market/templates/${templateId}/purchase`)

    if (response.data.code === 20000) {
      ElMessage.success(response.data.data.message || '购买成功')
      // 刷新模版详情
      await fetchTemplateDetail()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('购买模版失败:', error)
      ElMessage.error(error.response?.data?.message || '购买失败')
    }
  } finally {
    purchasing.value = false
  }
}

// 使用模版
const useTemplate = () => {
  ElMessage.info('跳转到创作页面...')
  // TODO: 跳转到创作页面并加载模版
}

// 点赞/取消点赞
const handleLike = async () => {
  try {
    const response = await axios.post('/api/v1/interactions/like', {
      targetId: templateId
    })

    if (response.data.code === 20000) {
      isLiked.value = response.data.data.liked
      template.value.likeCount += isLiked.value ? 1 : -1
      ElMessage.success(isLiked.value ? '点赞成功' : '取消点赞')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 关注/取关
const handleFollow = async () => {
  try {
    following.value = true
    const response = await axios.post('/api/v1/interactions/follow', {
      targetId: template.value.creatorId
    })

    if (response.data.code === 20000) {
      isFollowed.value = response.data.data.followed
      ElMessage.success(isFollowed.value ? '关注成功' : '取消关注')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    ElMessage.error('操作失败')
  } finally {
    following.value = false
  }
}

// 发表评论
const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  submittingComment.value = true
  try {
    const response = await axios.post('/api/v1/comments', {
      targetId: templateId,
      parentId: 0,
      content: newComment.value.trim()
    })

    if (response.data.code === 20000) {
      ElMessage.success('评论发表成功')
      newComment.value = ''
      // 刷新评论列表
      await fetchComments()
    }
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error('发表评论失败')
  } finally {
    submittingComment.value = false
  }
}

// 删除评论
const deleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm('确认删除这条评论？', '确认删除', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const response = await axios.delete(`/api/v1/comments/${commentId}`)

    if (response.data.code === 20000) {
      ElMessage.success('删除成功')
      // 刷新评论列表
      await fetchComments()
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return date.toLocaleDateString()
}

// 返回
const goBack = () => {
  router.back()
}

// 生命周期
onMounted(async () => {
  await fetchTemplateDetail()
  await fetchComments()
})
</script>

<style scoped>
.template-detail {
  padding: 24px;
  min-height: calc(100vh - 120px);
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

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
}

.back-button {
  margin-bottom: 24px;
}

.preview-section {
  margin-bottom: 32px;
}

.preview-media {
  width: 100%;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 16 / 9;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-cover {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: #86909c;
}

.default-cover .el-icon {
  font-size: 48px;
}

.info-section {
  background: #ffffff;
  border-radius: 12px;
  padding: 32px;
}

.template-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f2f5;
}

.template-title {
  font-size: 28px;
  font-weight: 600;
  color: #1f2329;
  margin: 0;
  flex: 1;
  margin-right: 24px;
}

.creator-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px;
  background: #f7f8fa;
  border-radius: 8px;
}

.creator-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.creator-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.creator-name {
  font-size: 16px;
  font-weight: 500;
  color: #1f2329;
}

.creator-stats {
  font-size: 14px;
  color: #86909c;
}

.creator-stats span {
  margin-right: 16px;
}

.description-section {
  margin-bottom: 24px;
}

.description-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2329;
  margin: 0 0 12px 0;
}

.description {
  font-size: 14px;
  color: #4e5969;
  line-height: 1.8;
  margin: 0;
}

.tags-section {
  margin-bottom: 24px;
}

.tags-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2329;
  margin: 0 0 12px 0;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.interaction-section {
  display: flex;
  gap: 24px;
  padding: 16px 0;
  margin-bottom: 24px;
  border-top: 1px solid #f0f2f5;
  border-bottom: 1px solid #f0f2f5;
}

.interaction-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #86909c;
  cursor: pointer;
  transition: color 0.2s ease;
}

.interaction-item:hover {
  color: #3b82f6;
}

.interaction-item .el-icon {
  font-size: 20px;
}

.interaction-item .el-icon.liked {
  color: #f59e0b;
}

.comments-section {
  padding-top: 24px;
}

.comments-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2329;
  margin: 0 0 24px 0;
}

.comment-input {
  margin-bottom: 32px;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: #f7f8fa;
  border-radius: 8px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-author {
  font-size: 14px;
  font-weight: 500;
  color: #1f2329;
}

.comment-time {
  font-size: 12px;
  color: #86909c;
}

.comment-text {
  font-size: 14px;
  color: #4e5969;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>
