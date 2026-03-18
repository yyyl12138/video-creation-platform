<template>
  <div class="result-display-page">
    <div class="result-container">
      <!-- 返回按钮 -->
      <div class="back-bar">
        <el-button link @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回创作
        </el-button>
      </div>

      <!-- 无任务状态 -->
      <div v-if="!taskId" class="empty-state">
        <el-empty description="请提交生成任务">
          <template #image>
            <el-icon :size="64" color="#c0c4cc"><MagicStick /></el-icon>
          </template>
          <el-button type="primary" @click="goBack">去创作</el-button>
        </el-empty>
      </div>

      <!-- 处理中状态 -->
      <div v-else-if="taskStatus === 'PENDING' || taskStatus === 'PROCESSING'" class="processing-state">
        <div class="progress-wrapper">
          <el-progress
            type="circle"
            :percentage="progress"
            :status="progress === 100 ? 'success' : ''"
            :width="180"
            :stroke-width="10"
          >
            <template #default="{ percentage }">
              <div class="progress-text">
                <div class="percentage">{{ percentage }}%</div>
                <div class="status">{{ getStatusText(taskStatus) }}</div>
              </div>
            </template>
          </el-progress>
        </div>
        <p class="status-description">{{ getStatusDescription() }}</p>
        <div class="task-info">
          <p>任务ID: {{ taskId }}</p>
          <p>模型: {{ taskInfo.modelName }}</p>
          <p>类型: {{ getTaskTypeDesc(taskInfo.taskType) }}</p>
        </div>
      </div>

      <!-- 失败状态 -->
      <div v-else-if="taskStatus === 'FAILED'" class="failed-state">
        <el-result icon="error" title="生成失败" :sub-title="errorMessage || '任务处理过程中出现错误'">
          <template #extra>
            <el-button type="primary" @click="goBack">
              <el-icon><Refresh /></el-icon>
              重新生成
            </el-button>
          </template>
        </el-result>
      </div>

      <!-- 成功状态 -->
      <div v-else-if="taskStatus === 'SUCCESS'" class="success-state">
        <el-result icon="success" title="生成成功" :sub-title="'您的内容已生成完成'">
          <template #extra>
            <el-button @click="goBack">继续创作</el-button>
          </template>
        </el-result>

        <!-- 文本结果 -->
        <div v-if="taskInfo.taskType === 'TEXT_TO_TEXT'" class="result-card">
          <el-card>
            <template #header>
              <div class="result-header">
                <span>生成结果</span>
                <el-tag type="success" size="small">已完成</el-tag>
              </div>
            </template>
            <p class="text-content">{{ result.content }}</p>
          </el-card>
        </div>

        <!-- 图片结果 -->
        <div v-else-if="taskInfo.taskType === 'TEXT_TO_IMAGE'" class="result-card">
          <el-card>
            <template #header>
              <div class="result-header">
                <span>生成结果</span>
                <el-tag type="success" size="small">已完成</el-tag>
              </div>
            </template>
            <img :src="result.fileUrl" alt="生成图片" class="generated-image" />
            <div v-if="result.size" class="file-info">
              <el-icon><Document /></el-icon>
              <span>大小：{{ formatFileSize(result.size) }}</span>
            </div>
          </el-card>
        </div>

        <!-- 视频结果 -->
        <div v-else class="result-card">
          <el-card>
            <template #header>
              <div class="result-header">
                <span>生成结果</span>
                <el-tag type="success" size="small">已完成</el-tag>
              </div>
            </template>
            <video :src="result.fileUrl" controls class="generated-video" />
            
            <!-- 封面图 -->
            <div v-if="result.coverUrl" class="cover-section">
              <p>封面预览：</p>
              <img :src="result.coverUrl" alt="封面图" class="cover-image" />
            </div>
            
            <!-- 文件信息 -->
            <div v-if="result.size" class="file-info">
              <el-icon><Document /></el-icon>
              <span>大小：{{ formatFileSize(result.size) }}</span>
            </div>
          </el-card>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button type="primary" size="large" @click="downloadResult">
            <el-icon><Download /></el-icon>
            下载作品
          </el-button>
          <el-button size="large" @click="goBack">
            <el-icon><Refresh /></el-icon>
            重新生成
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { MagicStick, Refresh, Download, Document, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 任务信息
const taskId = ref('')
const taskStatus = ref('')
const progress = ref(0)
const errorMessage = ref('')
const result = ref({})
const taskInfo = ref({
  taskType: '',
  modelName: ''
})

// 轮询定时器
let pollingTimer = null

onMounted(() => {
  // 从路由参数获取任务ID
  taskId.value = route.query.taskId
  if (taskId.value) {
    startPolling(taskId.value)
  }
})

onUnmounted(() => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
  }
})

// 轮询任务状态
const startPolling = (id) => {
  // 立即查询一次
  queryTaskStatus(id)
  
  // 每3秒轮询一次
  pollingTimer = setInterval(() => {
    queryTaskStatus(id)
  }, 3000)
}

// 查询任务状态
const queryTaskStatus = async (id) => {
  try {
    const response = await fetch(`/api/v1/generation/tasks/${id}`)
    const data = await response.json()
    
    if (data.code === 20000 && data.data) {
      const { status, progress: taskProgress, result: taskResult, modelName, taskType } = data.data
      
      taskStatus.value = status
      taskInfo.value = { modelName, taskType }
      
      if (taskProgress !== undefined) {
        progress.value = taskProgress
      } else {
        // 模拟进度
        if (status === 'PENDING') {
          progress.value = Math.min(progress.value + 5, 20)
        } else if (status === 'PROCESSING') {
          progress.value = Math.min(progress.value + 10, 90)
        }
      }
      
      if (status === 'SUCCESS') {
        clearInterval(pollingTimer)
        result.value = taskResult || {}
        progress.value = 100
      } else if (status === 'FAILED') {
        clearInterval(pollingTimer)
        errorMessage.value = data.data.errorMessage || '任务处理失败'
      }
    }
  } catch (error) {
    console.error('查询任务状态失败:', error)
  }
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    PENDING: '等待中',
    PROCESSING: '处理中',
    SUCCESS: '成功',
    FAILED: '失败'
  }
  return statusMap[status] || status
}

// 获取状态描述
const getStatusDescription = () => {
  if (taskStatus.value === 'PENDING') {
    return '任务已提交，正在排队等待处理...'
  } else if (taskStatus.value === 'PROCESSING') {
    return 'AI 正在为您生成内容，请耐心等待...'
  }
  return ''
}

// 获取任务类型描述
const getTaskTypeDesc = (taskType) => {
  const descMap = {
    'TEXT_TO_VIDEO': '文生视频',
    'IMAGE_TO_VIDEO': '图生视频',
    'TEXT_TO_IMAGE': '文生图',
    'TEXT_TO_TEXT': '文生文'
  }
  return descMap[taskType] || taskType
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i]
}

// 返回创作页面
const goBack = () => {
  router.push('/creation/generation')
}

// 下载结果
const downloadResult = () => {
  if (!result.value?.fileUrl) return
  
  const link = document.createElement('a')
  link.href = result.value.fileUrl
  link.download = `ai-generated-${Date.now()}`
  link.click()
}
</script>

<style scoped>
.result-display-page {
  padding: 24px;
  min-height: calc(100vh - 64px);
  background: #f5f7fa;
}

.result-container {
  max-width: 900px;
  margin: 0 auto;
}

.back-bar {
  margin-bottom: 24px;
}

.empty-state,
.processing-state,
.failed-state {
  background: #fff;
  border-radius: 16px;
  padding: 60px 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.processing-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.progress-wrapper {
  margin-bottom: 24px;
}

.progress-text {
  text-align: center;
}

.percentage {
  font-size: 36px;
  font-weight: 700;
  color: #1890ff;
  background: linear-gradient(135deg, #1890ff, #722ed1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.status {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.status-description {
  text-align: center;
  color: #606266;
  font-size: 14px;
  margin-bottom: 24px;
}

.task-info {
  text-align: center;
  color: #909399;
  font-size: 13px;
  line-height: 1.8;
}

.task-info p {
  margin: 4px 0;
}

.success-state {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.result-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.result-card :deep(.el-card) {
  border: none;
  box-shadow: none;
}

.result-card :deep(.el-card__header) {
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 24px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.text-content {
  line-height: 1.8;
  white-space: pre-wrap;
  color: #303133;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  margin: 0;
}

.generated-image {
  width: 100%;
  border-radius: 8px;
  display: block;
}

.generated-video {
  width: 100%;
  border-radius: 8px;
  background: #0b0f19;
  max-height: 500px;
}

.cover-section {
  margin-top: 20px;
  padding: 0 24px;
}

.cover-section p {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.cover-image {
  max-width: 200px;
  border-radius: 8px;
  border: 2px solid #ebeef5;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 16px 24px 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  color: #909399;
  font-size: 13px;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

.action-buttons .el-button {
  padding: 12px 32px;
  font-size: 15px;
}
</style>
