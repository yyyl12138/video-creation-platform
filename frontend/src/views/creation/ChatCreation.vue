<template>
  <div class="chat-creation-container">
    <!-- 顶部导航 -->
    <div class="chat-header">
      <div class="header-left">
        <el-button @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <div class="header-info">
          <h3>AI 创作助手</h3>
          <span class="task-type">{{ getTaskTypeLabel }}</span>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="chat-main">
      <!-- 对话区 -->
      <div class="chat-section">
        <div class="messages-container" ref="messagesContainer">
          <div
            v-for="(message, index) in messages"
            :key="index"
            class="message-item"
            :class="message.role"
          >
            <div v-if="message.role === 'assistant'" class="avatar ai-avatar">
              <el-icon><MagicStick /></el-icon>
            </div>
            
            <div class="message-content">
              <div v-if="message.type === 'text'" class="text-content">
                {{ message.content }}
              </div>
              
              <div v-else-if="message.type === 'work'" class="work-content">
                <div class="work-preview">
                  <video v-if="isVideo" :src="message.workUrl" controls class="work-video" />
                  <img v-else :src="message.workUrl" class="work-image" />
                </div>
                <div class="work-info">
                  <div class="work-title">{{ message.workTitle }}</div>
                </div>
              </div>

              <div v-if="message.role === 'assistant' && message.showActions" class="message-actions">
                <el-button size="small" @click="regenerate(message)">
                  <el-icon><Refresh /></el-icon>
                  重新生成
                </el-button>
                <el-button size="small" type="primary" @click="downloadWork(message)">
                  <el-icon><Download /></el-icon>
                  下载
                </el-button>
              </div>
            </div>

            <div v-if="message.role === 'user'" class="avatar user-avatar">
              <el-icon><User /></el-icon>
            </div>
          </div>

          <div v-if="generating" class="message-item assistant loading">
            <div class="avatar ai-avatar">
              <el-icon><MagicStick /></el-icon>
            </div>
            <div class="message-content">
              <div class="loading-content">
                <el-icon class="is-loading"><Loading /></el-icon>
                <span>AI正在创作中...</span>
              </div>
            </div>
          </div>
        </div>

        <div class="input-section">
          <div class="input-box">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="3"
              placeholder="继续描述你的需求，例如：调整颜色为暖色调、增加动感效果..."
              @keyup.enter.ctrl="sendMessage"
            />
            <div class="input-actions">
              <span class="input-hint">Ctrl + Enter 发送</span>
              <el-button 
                type="primary" 
                :loading="sending"
                :disabled="!inputMessage.trim()"
                @click="sendMessage"
              >
                <el-icon><Promotion /></el-icon>
                发送
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, MagicStick, User, Refresh, Download,
  Loading, Promotion
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const taskType = ref(route.query.taskType || 'TEXT_TO_VIDEO')
const taskId = ref(route.query.taskId || '')
const initialPrompt = ref(route.query.prompt || '')

const messages = ref([])
const inputMessage = ref('')
const sending = ref(false)
const generating = ref(false)

const isVideo = computed(() => {
  return taskType.value === 'TEXT_TO_VIDEO' || taskType.value === 'IMAGE_TO_VIDEO'
})

const getTaskTypeLabel = computed(() => {
  const labels = {
    TEXT_TO_VIDEO: '文生视频',
    IMAGE_TO_VIDEO: '图生视频',
    TEXT_TO_IMAGE: '文生图片',
    TEXT_TO_TEXT: '文本生成'
  }
  return labels[taskType.value] || 'AI创作'
})

const goBack = () => {
  router.push('/creation/generation')
}

const addMessage = (role, type, content, extra = {}) => {
  messages.value.push({
    role,
    type,
    content,
    ...extra
  })
  scrollToBottom()
}

const scrollToBottom = () => {
  nextTick(() => {
    const container = document.querySelector('.messages-container')
    if (container) {
      container.scrollTop = container.scrollHeight
    }
  })
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || sending.value) return

  const userMessage = inputMessage.value.trim()
  addMessage('user', 'text', userMessage)
  inputMessage.value = ''

  sending.value = true
  generating.value = true

  try {
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    const workTypes = {
      TEXT_TO_VIDEO: { url: '/videos/demo.mp4', title: '生成的视频作品' },
      IMAGE_TO_VIDEO: { url: '/videos/demo.mp4', title: '图生视频作品' },
      TEXT_TO_IMAGE: { url: 'https://via.placeholder.com/512x512/667eea/ffffff?text=AI+Image', title: '生成的图片作品' },
      TEXT_TO_TEXT: { url: '', title: '生成的文本内容' }
    }
    
    const work = workTypes[taskType.value]
    
    addMessage('assistant', 'text', `已根据您的要求"${userMessage}"进行优化调整，生成了新的作品：`)
    addMessage('assistant', 'work', '', {
      workUrl: work.url,
      workTitle: work.title,
      showActions: true
    })
    
  } catch (error) {
    ElMessage.error('生成失败，请重试')
  } finally {
    sending.value = false
    generating.value = false
  }
}

const regenerate = (message) => {
  ElMessage.info('正在重新生成...')
  setTimeout(() => {
    ElMessage.success('重新生成完成')
  }, 2000)
}

const downloadWork = (message) => {
  ElMessage.success('开始下载作品')
}

onMounted(() => {
  if (initialPrompt.value) {
    addMessage('user', 'text', initialPrompt.value)
    generating.value = true
    
    setTimeout(() => {
      const workTypes = {
        TEXT_TO_VIDEO: { url: '/videos/demo.mp4', title: '生成的视频作品' },
        IMAGE_TO_VIDEO: { url: '/videos/demo.mp4', title: '图生视频作品' },
        TEXT_TO_IMAGE: { url: 'https://via.placeholder.com/512x512/667eea/ffffff?text=AI+Image', title: '生成的图片作品' },
        TEXT_TO_TEXT: { url: '', title: '生成的文本内容' }
      }
      
      const work = workTypes[taskType.value]
      
      addMessage('assistant', 'text', '我已收到您的创作需求，正在为您生成作品，请稍候...')
      addMessage('assistant', 'work', '', {
        workUrl: work.url,
        workTitle: work.title,
        showActions: true
      })
      
      generating.value = false
    }, 2000)
  }
})
</script>

<style scoped>
.chat-creation-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f7fa;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  padding: 8px;
}

.header-info h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.task-type {
  font-size: 13px;
  color: #909399;
}

.chat-main {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.chat-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 20px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 24px;
}

.message-item.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ai-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.user-avatar {
  background: #e4e7ed;
  color: #606266;
}

.message-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  background: #f5f7fa;
}

.message-item.user .message-content {
  background: #409eff;
  color: #fff;
}

.text-content {
  font-size: 14px;
  line-height: 1.6;
}

.work-content {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.work-preview {
  width: 100%;
}

.work-video, .work-image {
  width: 100%;
  max-width: 400px;
  border-radius: 8px;
}

.work-info {
  padding: 12px;
}

.work-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.message-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
}

.loading-content {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.input-section {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
}

.input-box {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.input-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.input-hint {
  font-size: 12px;
  color: #909399;
}
</style>
