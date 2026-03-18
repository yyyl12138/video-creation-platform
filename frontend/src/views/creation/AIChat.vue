<template>
  <div class="ai-chat-container">
    <!-- 顶部导航栏 -->
    <div class="chat-header">
      <div class="header-left">
        <el-button @click="goBack" class="back-btn">
          <el-icon><ArrowLeft /></el-icon>
          <span>返回创作</span>
        </el-button>
        <div class="task-info">
          <span class="task-type">{{ getTaskTypeLabel }}</span>
          <el-tag size="small" :type="getStatusType">{{ taskStatus }}</el-tag>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="viewResult" v-if="hasResult">
          <el-icon><View /></el-icon>
          查看作品
        </el-button>
      </div>
    </div>

    <!-- 对话区域 -->
    <div class="chat-main" ref="chatContainer">
      <!-- AI欢迎消息 -->
      <div class="welcome-message">
        <div class="ai-avatar">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="message-content">
          <div class="message-bubble ai-bubble">
            <p>你好！我是你的AI创作助手。</p>
            <p>我已经收到了你的创作需求：</p>
            <div class="prompt-box">
              <p class="prompt-text">"{{ initialPrompt }}"</p>
            </div>
            <p>正在为你生成作品，请稍候...</p>
          </div>
          <span class="message-time">{{ formatTime(welcomeTime) }}</span>
        </div>
      </div>

      <!-- 消息列表 -->
      <div 
        v-for="(msg, index) in messages" 
        :key="index"
        :class="['message-item', msg.type]"
      >
        <!-- AI消息 -->
        <template v-if="msg.type === 'ai'">
          <div class="ai-avatar">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-bubble ai-bubble">
              <div v-if="msg.isLoading" class="loading-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
              <template v-else>
                <p>{{ msg.content }}</p>
                <!-- 生成结果预览 -->
                <div v-if="msg.hasResult" class="result-preview">
                  <div class="preview-item" @click="viewResult">
                    <el-image 
                      v-if="msg.resultType === 'image'"
                      :src="msg.resultUrl" 
                      fit="cover"
                      class="preview-media"
                    />
                    <video 
                      v-else-if="msg.resultType === 'video'"
                      :src="msg.resultUrl"
                      class="preview-media"
                      muted
                      loop
                    />
                    <div class="preview-overlay">
                      <el-icon><VideoPlay /></el-icon>
                      <span>点击查看</span>
                    </div>
                  </div>
                </div>
              </template>
            </div>
            <span class="message-time">{{ formatTime(msg.time) }}</span>
          </div>
        </template>

        <!-- 用户消息 -->
        <template v-else>
          <div class="message-content user-content">
            <div class="message-bubble user-bubble">
              <p>{{ msg.content }}</p>
            </div>
            <span class="message-time">{{ formatTime(msg.time) }}</span>
          </div>
          <div class="user-avatar">
            <el-avatar :size="36">{{ userName.charAt(0) }}</el-avatar>
          </div>
        </template>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-area">
      <!-- 快捷操作按钮 -->
      <div class="quick-actions">
        <el-button 
          v-for="action in quickActions" 
          :key="action.text"
          size="small"
          class="quick-btn"
          @click="sendQuickMessage(action.text)"
        >
          <el-icon><component :is="action.icon" /></el-icon>
          {{ action.text }}
        </el-button>
      </div>

      <!-- 输入框 -->
      <div class="input-wrapper">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          :placeholder="getPlaceholder()"
          class="chat-input"
          @keyup.enter.ctrl="sendMessage"
        />
        <div class="input-actions">
          <span class="hint">Ctrl + Enter 发送</span>
          <el-button 
            type="primary" 
            @click="sendMessage"
            :loading="sending"
            :disabled="!inputMessage.trim()"
          >
            <el-icon><Promotion /></el-icon>
            发送
          </el-button>
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
  ArrowLeft,
  ChatDotRound,
  VideoPlay,
  View,
  Promotion,
  Refresh,
  Edit,
  MagicStick,
  Document
} from '@element-plus/icons-vue'
import { getTaskStatus } from '@/api/creation/generation'

const route = useRoute()
const router = useRouter()

// 任务信息
const taskId = ref('')
const taskType = ref('TEXT_TO_VIDEO')
const initialPrompt = ref('')
const taskStatus = ref('生成中')
const hasResult = ref(false)
const welcomeTime = ref(new Date())

// 用户信息
const userName = ref('用户')

// 对话数据
const messages = ref([])
const inputMessage = ref('')
const sending = ref(false)
const chatContainer = ref(null)

// 快捷操作
const quickActions = [
  { text: '调整风格', icon: 'Edit' },
  { text: '修改细节', icon: 'Document' },
  { text: '重新生成', icon: 'Refresh' },
  { text: '添加特效', icon: 'MagicStick' }
]

// 计算属性
const getTaskTypeLabel = computed(() => {
  const labels = {
    TEXT_TO_VIDEO: '视频生成',
    IMAGE_TO_VIDEO: '图生视频',
    TEXT_TO_IMAGE: '图片生成',
    TEXT_TO_TEXT: '文本生成'
  }
  return labels[taskType.value] || 'AI创作'
})

const getStatusType = computed(() => {
  const types = {
    '生成中': 'warning',
    '已完成': 'success',
    '失败': 'danger'
  }
  return types[taskStatus.value] || 'info'
})

const getPlaceholder = () => {
  const placeholders = {
    TEXT_TO_VIDEO: '继续描述你想要的视频效果，比如：添加更多动感元素...',
    IMAGE_TO_VIDEO: '描述你想要的动画效果...',
    TEXT_TO_IMAGE: '描述你想要的图片风格变化...',
    TEXT_TO_TEXT: '继续补充文本内容要求...'
  }
  return placeholders[taskType.value] || '输入你的需求...'
}

// 格式化时间
const formatTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim()) return

  // 添加用户消息
  messages.value.push({
    type: 'user',
    content: inputMessage.value.trim(),
    time: new Date()
  })

  const userInput = inputMessage.value.trim()
  inputMessage.value = ''
  sending.value = true
  scrollToBottom()

  // 模拟AI响应
  setTimeout(() => {
    // 添加AI思考中的消息
    const aiMsgIndex = messages.value.length
    messages.value.push({
      type: 'ai',
      content: '',
      isLoading: true,
      time: new Date()
    })
    scrollToBottom()

    // 模拟AI响应延迟
    setTimeout(() => {
      messages.value[aiMsgIndex] = {
        type: 'ai',
        content: generateAIResponse(userInput),
        isLoading: false,
        time: new Date(),
        hasResult: Math.random() > 0.5,
        resultType: taskType.value === 'TEXT_TO_IMAGE' ? 'image' : 'video',
        resultUrl: taskType.value === 'TEXT_TO_IMAGE' 
          ? 'https://picsum.photos/400/300?random=' + Date.now()
          : '/videos/cyberpunk_10s.mp4'
      }
      hasResult.value = true
      taskStatus.value = '已完成'
      sending.value = false
      scrollToBottom()
    }, 2000)
  }, 500)
}

// 生成AI响应
const generateAIResponse = (input) => {
  const responses = [
    `收到你的修改要求："${input}"。\n\n我正在根据你的描述进行调整，会保持原有的创作风格，同时融入新的元素。`,
    `好的，我理解你想要：${input}\n\n正在重新优化生成参数，这会花费一点时间，请稍候...`,
    `已记录你的需求：${input}\n\n正在处理中，我会确保最终作品符合你的期望。`,
    `明白！我会根据"${input}"进行调整。\n\n正在生成新的版本，你可以随时提出更多修改意见。`
  ]
  return responses[Math.floor(Math.random() * responses.length)]
}

// 快捷发送
const sendQuickMessage = (text) => {
  inputMessage.value = text
  sendMessage()
}

// 返回上一页
const goBack = () => {
  router.push('/creation/generation')
}

// 查看结果
const viewResult = () => {
  router.push({
    path: '/creation/result',
    query: {
      taskId: taskId.value,
      taskType: taskType.value
    }
  })
}

// 轮询任务状态
const pollTaskStatus = async () => {
  if (!taskId.value) return
  
  try {
    const response = await getTaskStatus(taskId.value)
    if (response.code === 20000) {
      const status = response.data.status
      if (status === 'COMPLETED') {
        taskStatus.value = '已完成'
        hasResult.value = true
        // 添加完成消息
        messages.value.push({
          type: 'ai',
          content: '作品生成完成！你可以继续提出修改意见，或者点击查看作品按钮查看最终结果。',
          time: new Date(),
          hasResult: true,
          resultType: taskType.value === 'TEXT_TO_IMAGE' ? 'image' : 'video',
          resultUrl: response.data.resultUrl || '/videos/cyberpunk_10s.mp4'
        })
        scrollToBottom()
      } else if (status === 'FAILED') {
        taskStatus.value = '失败'
        messages.value.push({
          type: 'ai',
          content: '抱歉，生成过程中遇到了问题。请尝试重新描述你的需求，或点击重新生成。',
          time: new Date()
        })
        scrollToBottom()
      }
    }
  } catch (error) {
    console.error('获取任务状态失败:', error)
  }
}

onMounted(() => {
  // 从路由参数获取任务信息
  taskId.value = route.query.taskId || 'demo-' + Date.now()
  taskType.value = route.query.taskType || 'TEXT_TO_VIDEO'
  initialPrompt.value = route.query.prompt || '一只可爱的熊猫在竹林里玩耍'
  
  // 开始轮询任务状态
  const pollInterval = setInterval(() => {
    if (taskStatus.value === '已完成' || taskStatus.value === '失败') {
      clearInterval(pollInterval)
    } else {
      pollTaskStatus()
    }
  }, 3000)

  // 模拟初始生成完成
  setTimeout(() => {
    if (!hasResult.value) {
      messages.value.push({
        type: 'ai',
        content: '初版作品已生成！你觉得效果如何？可以继续描述你想要的修改，我会根据你的反馈进行调整。',
        time: new Date(),
        hasResult: true,
        resultType: taskType.value === 'TEXT_TO_IMAGE' ? 'image' : 'video',
        resultUrl: taskType.value === 'TEXT_TO_IMAGE'
          ? 'https://picsum.photos/400/300?random=' + Date.now()
          : '/videos/pandas-2b.mp4'
      })
      hasResult.value = true
      taskStatus.value = '已完成'
      scrollToBottom()
    }
  }, 5000)
})
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

/* 顶部导航栏 */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.task-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.task-type {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 对话区域 */
.chat-main {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 欢迎消息 */
.welcome-message {
  display: flex;
  gap: 12px;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: 70%;
}

.user-content {
  align-items: flex-end;
}

.message-bubble {
  padding: 16px 20px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
}

.ai-bubble {
  background: #fff;
  color: #303133;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.user-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.prompt-box {
  background: #f5f7fa;
  border-left: 3px solid #667eea;
  padding: 12px 16px;
  margin: 12px 0;
  border-radius: 8px;
}

.prompt-text {
  color: #667eea;
  font-style: italic;
  margin: 0;
}

.message-time {
  font-size: 12px;
  color: #909399;
  padding: 0 8px;
}

/* 消息项 */
.message-item {
  display: flex;
  gap: 12px;
}

.message-item.user {
  justify-content: flex-end;
}

.user-avatar {
  flex-shrink: 0;
}

/* 加载动画 */
.loading-dots {
  display: flex;
  gap: 4px;
  padding: 8px 0;
}

.loading-dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #667eea;
  animation: bounce 1.4s infinite ease-in-out both;
}

.loading-dots span:nth-child(1) { animation-delay: -0.32s; }
.loading-dots span:nth-child(2) { animation-delay: -0.16s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* 结果预览 */
.result-preview {
  margin-top: 12px;
}

.preview-item {
  position: relative;
  width: 280px;
  height: 180px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.preview-media {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.preview-item:hover .preview-overlay {
  opacity: 1;
}

.preview-overlay .el-icon {
  font-size: 32px;
}

/* 输入区域 */
.chat-input-area {
  background: #fff;
  border-top: 1px solid #e4e7ed;
  padding: 16px 24px;
}

.quick-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.quick-btn {
  color: #667eea;
  border-color: #d3d9f0;
}

.quick-btn:hover {
  background: #f0f3ff;
  border-color: #667eea;
}

.input-wrapper {
  background: #f5f7fa;
  border-radius: 12px;
  padding: 12px;
}

.chat-input :deep(.el-textarea__inner) {
  border: none;
  background: transparent;
  resize: none;
  font-size: 14px;
  padding: 8px;
}

.input-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #e4e7ed;
}

.hint {
  font-size: 12px;
  color: #909399;
}
</style>
