<template>
  <div class="result-display">
    <!-- 无任务状态 -->
    <div v-if="!taskId" class="empty-state">
      <el-empty description="请提交生成任务">
        <el-icon :size="64" color="#c0c4cc"><MagicStick /></el-icon>
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
    </div>

    <!-- 失败状态 -->
    <div v-else-if="taskStatus === 'FAILED'" class="failed-state">
      <el-result icon="error" title="生成失败" :sub-title="errorMessage">
        <template #extra>
          <el-button type="primary" @click="$emit('reset')">
            <el-icon><Refresh /></el-icon>
            重新生成
          </el-button>
        </template>
      </el-result>
    </div>

    <!-- 成功状态 -->
    <div v-else-if="taskStatus === 'SUCCESS'" class="success-state">
      <!-- 文本结果 -->
      <div v-if="taskType === 'TEXT_TO_TEXT'" class="text-result">
        <el-card>
          <template #header>
            <div class="result-header">
              <span>生成结果</span>
              <el-tag type="success" size="small">已完成</el-tag>
            </div>
          </template>
          <p class="text-content">{{ result.fileUrl }}</p>
        </el-card>
      </div>

      <!-- 图片结果 -->
      <div v-else-if="taskType === 'TEXT_TO_IMAGE'" class="image-result">
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
      <div v-else class="video-result">
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
        <el-button type="primary" @click="downloadResult">
          <el-icon><Download /></el-icon>
          下载
        </el-button>
        <el-button @click="$emit('reset')">
          <el-icon><Refresh /></el-icon>
          重新生成
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { MagicStick, Refresh, Download, Document } from '@element-plus/icons-vue'

defineProps({
  taskId: String,
  taskStatus: String,
  result: Object,
  progress: Number,
  errorMessage: String,
  taskType: String
})

defineEmits(['reset'])

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i]
}

const getStatusText = (status) => {
  const statusMap = {
    PENDING: '等待中',
    PROCESSING: '处理中',
    SUCCESS: '成功',
    FAILED: '失败'
  }
  return statusMap[status] || status
}

const getStatusDescription = () => {
  if (taskStatus === 'PENDING') {
    return '任务已提交，正在排队等待处理...'
  } else if (taskStatus === 'PROCESSING') {
    return 'AI 正在为您生成内容，请耐心等待...'
  }
  return ''
}

const downloadResult = () => {
  if (!result.value?.fileUrl) return

  const link = document.createElement('a')
  link.href = result.value.fileUrl
  link.download = `ai-generated-${Date.now()}`
  link.click()
}
</script>

<style scoped>
.result-display {
  min-height: 500px;
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
  padding: 40px 20px;
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
  color: #409EFF;
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
  max-width: 300px;
}

.failed-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.success-state {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.text-result .text-content {
  line-height: 1.8;
  white-space: pre-wrap;
  color: #303133;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  margin: 0;
}

.image-result .generated-image {
  width: 100%;
  border-radius: 8px;
  display: block;
}

.video-result .generated-video {
  width: 100%;
  border-radius: 8px;
  background: #0b0f19;
}

.cover-section {
  margin-top: 20px;
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
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  color: #909399;
  font-size: 13px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}
</style>
