<template>
  <div class="generation-container">
    <!-- 页面标题卡片 -->
    <div class="page-header-card">
      <div class="header-content">
        <div class="header-icon">
          <el-icon size="32" color="#fff"><MagicStick /></el-icon>
        </div>
        <div class="header-text">
          <h2>AI 创作</h2>
          <p>使用 AI 技术生成文字、图片、视频</p>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <el-card class="main-content-card">
      <!-- 创作类型标签页 -->
      <el-tabs v-model="activeTab" type="border-card" class="creation-tabs">
        <!-- 文生文 -->
        <el-tab-pane name="TEXT_TO_TEXT">
          <template #label>
            <span class="tab-label text">
              <el-icon><DocumentCopy /></el-icon>
              文生文
            </span>
          </template>
          <div class="creation-content">
            <el-row :gutter="24">
              <el-col :span="12">
                <div class="form-section">
                  <h3>创作参数</h3>
                  <el-form :model="form" label-position="top">
                    <el-form-item label="选择模型">
                      <el-select v-model="form.modelName" placeholder="请选择模型" style="width: 100%">
                        <el-option label="DeepSeek V3" value="DeepSeek V3" />
                        <el-option label="Qwen Flash" value="Qwen Flash" />
                        <el-option label="GLM Flash" value="GLM Flash" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="提示词">
                      <el-input
                        v-model="form.inputConfig.prompt"
                        type="textarea"
                        :rows="6"
                        placeholder="请输入描述文字，例如：写一段关于AI的视频脚本..."
                      />
                    </el-form-item>
                    <el-button type="primary" size="large" :loading="submitting" @click="submitTask" style="width: 100%">
                      <el-icon><VideoPlay /></el-icon>
                      {{ submitting ? '生成中...' : '开始生成' }}
                    </el-button>
                  </el-form>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="result-section">
                  <h3>生成结果</h3>
                  <ResultDisplay
                    :task-id="taskId"
                    :task-status="taskStatus"
                    :result="result"
                    :progress="progress"
                    :error-message="errorMessage"
                    :task-type="form.taskType"
                    @reset="resetForm"
                  />
                </div>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>

        <!-- 文生图 -->
        <el-tab-pane name="TEXT_TO_IMAGE">
          <template #label>
            <span class="tab-label image">
              <el-icon><Picture /></el-icon>
              文生图
            </span>
          </template>
          <div class="creation-content">
            <el-row :gutter="24">
              <el-col :span="12">
                <div class="form-section">
                  <h3>创作参数</h3>
                  <el-form :model="form" label-position="top">
                    <el-form-item label="选择模型">
                      <el-select v-model="form.modelName" placeholder="请选择模型" style="width: 100%">
                        <el-option label="Wanx v1" value="Wanx v1" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="提示词">
                      <el-input
                        v-model="form.inputConfig.prompt"
                        type="textarea"
                        :rows="4"
                        placeholder="请输入描述，例如：一只可爱的小猫在阳光下玩耍..."
                      />
                    </el-form-item>
                    <el-form-item label="负面提示词">
                      <el-input
                        v-model="form.inputConfig.negativePrompt"
                        type="textarea"
                        :rows="2"
                        placeholder="不希望出现的描述，例如：模糊、低质量..."
                      />
                    </el-form-item>
                    <el-form-item label="画面比例">
                      <el-radio-group v-model="form.inputConfig.ratio">
                        <el-radio-button label="16:9">16:9（宽屏）</el-radio-button>
                        <el-radio-button label="9:16">9:16（竖屏）</el-radio-button>
                        <el-radio-button label="1:1">1:1（方形）</el-radio-button>
                      </el-radio-group>
                    </el-form-item>
                    <el-button type="primary" size="large" :loading="submitting" @click="submitTask" style="width: 100%">
                      <el-icon><VideoPlay /></el-icon>
                      {{ submitting ? '生成中...' : '开始生成' }}
                    </el-button>
                  </el-form>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="result-section">
                  <h3>生成结果</h3>
                  <ResultDisplay
                    :task-id="taskId"
                    :task-status="taskStatus"
                    :result="result"
                    :progress="progress"
                    :error-message="errorMessage"
                    :task-type="form.taskType"
                    @reset="resetForm"
                  />
                </div>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>

        <!-- 文生视频 -->
        <el-tab-pane name="TEXT_TO_VIDEO">
          <template #label>
            <span class="tab-label video">
              <el-icon><VideoCamera /></el-icon>
              文生视频
            </span>
          </template>
          <div class="creation-content">
            <el-row :gutter="24">
              <el-col :span="12">
                <div class="form-section">
                  <h3>创作参数</h3>
                  <el-form :model="form" label-position="top">
                    <el-form-item label="选择模型">
                      <el-select v-model="form.modelName" placeholder="请选择模型" style="width: 100%">
                        <el-option label="Kling" value="Kling" />
                        <el-option label="Minimax" value="Minimax" />
                        <el-option label="Doubao Seedance" value="Doubao Seedance" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="提示词">
                      <el-input
                        v-model="form.inputConfig.prompt"
                        type="textarea"
                        :rows="4"
                        placeholder="请输入描述，例如：一辆红色跑车在山间公路上飞驰..."
                      />
                    </el-form-item>
                    <el-form-item label="负面提示词">
                      <el-input
                        v-model="form.inputConfig.negativePrompt"
                        type="textarea"
                        :rows="2"
                        placeholder="不希望出现的描述..."
                      />
                    </el-form-item>
                    <el-form-item label="画面比例">
                      <el-radio-group v-model="form.inputConfig.ratio">
                        <el-radio-button label="16:9">16:9（宽屏）</el-radio-button>
                        <el-radio-button label="9:16">9:16（竖屏）</el-radio-button>
                        <el-radio-button label="1:1">1:1（方形）</el-radio-button>
                      </el-radio-group>
                    </el-form-item>
                    <el-form-item label="时长">
                      <el-radio-group v-model="form.inputConfig.duration">
                        <el-radio-button v-for="d in availableDurations" :key="d" :label="d">{{ d }}s</el-radio-button>
                      </el-radio-group>
                    </el-form-item>
                    <el-form-item label="分辨率">
                      <el-select v-model="form.inputConfig.resolution" style="width: 100%">
                        <el-option label="720P" value="720P" />
                        <el-option label="1080P" value="1080P" />
                        <el-option label="4K" value="4K" />
                      </el-select>
                    </el-form-item>
                    <el-button type="primary" size="large" :loading="submitting" @click="submitTask" style="width: 100%">
                      <el-icon><VideoPlay /></el-icon>
                      {{ submitting ? '生成中...' : '开始生成' }}
                    </el-button>
                  </el-form>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="result-section">
                  <h3>生成结果</h3>
                  <ResultDisplay
                    :task-id="taskId"
                    :task-status="taskStatus"
                    :result="result"
                    :progress="progress"
                    :error-message="errorMessage"
                    :task-type="form.taskType"
                    @reset="resetForm"
                  />
                </div>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>

        <!-- 图生视频 -->
        <el-tab-pane name="IMAGE_TO_VIDEO">
          <template #label>
            <span class="tab-label image-to-video">
              <el-icon><PictureFilled /></el-icon>
              图生视频
            </span>
          </template>
          <div class="creation-content">
            <el-row :gutter="24">
              <el-col :span="12">
                <div class="form-section">
                  <h3>创作参数</h3>
                  <el-form :model="form" label-position="top">
                    <el-form-item label="选择模型">
                      <el-select v-model="form.modelName" placeholder="请选择模型" style="width: 100%">
                        <el-option label="Kling" value="Kling" />
                        <el-option label="Minimax" value="Minimax" />
                        <el-option label="Doubao Seedance" value="Doubao Seedance" />
                      </el-select>
                    </el-form-item>
                    <el-form-item label="参考图片">
                      <el-upload
                        class="upload-area"
                        :auto-upload="false"
                        :on-change="handleImageChange"
                        :limit="1"
                        accept="image/*"
                        drag
                      >
                        <el-icon class="upload-icon"><UploadFilled /></el-icon>
                        <div class="upload-text">
                          <p class="main-text">拖拽图片到此处，或<em>点击选择</em></p>
                        </div>
                      </el-upload>
                    </el-form-item>
                    <el-form-item label="提示词">
                      <el-input
                        v-model="form.inputConfig.prompt"
                        type="textarea"
                        :rows="3"
                        placeholder="描述想要生成的视频效果..."
                      />
                    </el-form-item>
                    <el-form-item label="时长">
                      <el-radio-group v-model="form.inputConfig.duration">
                        <el-radio-button v-for="d in availableDurations" :key="d" :label="d">{{ d }}s</el-radio-button>
                      </el-radio-group>
                    </el-form-item>
                    <el-form-item label="分辨率">
                      <el-select v-model="form.inputConfig.resolution" style="width: 100%">
                        <el-option label="720P" value="720P" />
                        <el-option label="1080P" value="1080P" />
                        <el-option label="4K" value="4K" />
                      </el-select>
                    </el-form-item>
                    <el-button type="primary" size="large" :loading="submitting" @click="submitTask" style="width: 100%">
                      <el-icon><VideoPlay /></el-icon>
                      {{ submitting ? '生成中...' : '开始生成' }}
                    </el-button>
                  </el-form>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="result-section">
                  <h3>生成结果</h3>
                  <ResultDisplay
                    :task-id="taskId"
                    :task-status="taskStatus"
                    :result="result"
                    :progress="progress"
                    :error-message="errorMessage"
                    :task-type="form.taskType"
                    @reset="resetForm"
                  />
                </div>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted, watch } from 'vue'
import { submitGenerationTask, getTaskStatus } from '@/api/creation/generation'
import { ElMessage } from 'element-plus'
import { MagicStick, DocumentCopy, Picture, VideoCamera, PictureFilled, UploadFilled, VideoPlay, Download, Refresh } from '@element-plus/icons-vue'
import ResultDisplay from './components/ResultDisplay.vue'

const activeTab = ref('TEXT_TO_VIDEO')
const taskId = ref('')
const taskStatus = ref('')
const progress = ref(0)
const result = ref({})
const errorMessage = ref('')
const submitting = ref(false)
let pollTimer = null

const form = reactive({
  taskType: 'TEXT_TO_VIDEO',
  modelName: 'Kling',
  inputConfig: {
    prompt: '',
    negativePrompt: '',
    ratio: '16:9',
    duration: 5,
    resolution: '1080P',
    image: null
  }
})

const availableDurations = computed(() => {
  if (form.modelName === 'Minimax') return [6, 10]
  if (form.modelName === 'Doubao Seedance') return [5, 10]
  return [5, 10] // Kling and others
})

// 监听模型变化，重置时长为合法值
watch(() => form.modelName, (newVal) => {
  if (newVal === 'Minimax' && !availableDurations.value.includes(form.inputConfig.duration)) {
    form.inputConfig.duration = 6
  } else if (!availableDurations.value.includes(form.inputConfig.duration)) {
    form.inputConfig.duration = 5
  }
})

// 监听标签页切换
const onTabChange = () => {
  form.taskType = activeTab.value
  
  // 根据任务类型自动选择合适的模型
  if (activeTab.value === 'TEXT_TO_TEXT') {
    form.modelName = 'Qwen Flash'
  } else if (activeTab.value === 'TEXT_TO_IMAGE') {
    form.modelName = 'Wanx v1'
  } else if (activeTab.value === 'TEXT_TO_VIDEO' || activeTab.value === 'IMAGE_TO_VIDEO') {
    form.modelName = 'Kling'
  }
  
  // 重置表单
  resetForm()
}

// 监听 activeTab 变化
// 监听 activeTab 变化
watch(activeTab, onTabChange)

// 图片上传
const handleImageChange = (file) => {
  const reader = new FileReader()
  reader.onload = (e) => {
    form.inputConfig.image = e.target.result
  }
  reader.readAsDataURL(file.raw)
}

// 提交任务
const submitTask = async () => {
  if (!form.inputConfig.prompt && activeTab.value !== 'IMAGE_TO_VIDEO') {
    ElMessage.warning('请输入提示词')
    return
  }
  
  if (activeTab.value === 'IMAGE_TO_VIDEO' && !form.inputConfig.image) {
    ElMessage.warning('请上传参考图片')
    return
  }

  try {
    submitting.value = true
    form.taskType = activeTab.value
    const response = await submitGenerationTask(form)

    if (response.code === 20000) {
      taskId.value = response.data.taskId
      taskStatus.value = 'PENDING'
      progress.value = 0

      ElMessage.success('任务已提交，正在处理中...')
      startPolling()
    } else {
      ElMessage.error(response.message || '提交失败')
    }
  } catch (error) {
    ElMessage.error('网络错误：' + error.message)
  } finally {
    submitting.value = false
  }
}

// 开始轮询
const startPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
  }

  pollTimer = setInterval(async () => {
    try {
      const response = await getTaskStatus(taskId.value)

      if (response.code === 20000) {
        const data = response.data
        taskStatus.value = data.status
        progress.value = data.progress || 0

        if (data.status === 'SUCCESS') {
          result.value = data.result
          progress.value = 100
          stopPolling()
          ElMessage.success('生成完成！')
        } else if (data.status === 'FAILED') {
          errorMessage.value = data.errorMessage || '生成失败'
          stopPolling()
          ElMessage.error('生成失败')
        }
      }
    } catch (error) {
      console.error('轮询错误:', error)
    }
  }, 5000)
}

// 停止轮询
const stopPolling = () => {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
}

// 重置表单
const resetForm = () => {
  taskId.value = ''
  taskStatus.value = ''
  progress.value = 0
  result.value = {}
  errorMessage.value = ''
  stopPolling()
  form.inputConfig.prompt = ''
  form.inputConfig.negativePrompt = ''
  form.inputConfig.image = null
}

// 组件卸载时清理
onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
.generation-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: calc(100vh - 40px);
}

.page-header-card {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 16px;
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(245, 87, 108, 0.3);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-icon {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-text h2 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
  color: #fff;
}

.header-text p {
  margin: 4px 0 0 0;
  color: rgba(255, 255, 255, 0.85);
  font-size: 0.95rem;
}

.main-content-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
}

.creation-tabs {
  border: none !important;
}

.creation-tabs :deep(.el-tabs__header) {
  background: #fafafa;
  border-radius: 12px 12px 0 0;
  padding: 8px 8px 0;
  border-bottom: none !important;
}

.creation-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.creation-tabs :deep(.el-tabs__item) {
  height: 48px;
  line-height: 48px;
  border: none !important;
  font-size: 14px;
  font-weight: 500;
}

.creation-tabs :deep(.el-tabs__item.is-active) {
  background: #fff;
  border-radius: 8px 8px 0 0;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 8px;
}

.tab-label.text { color: #67C23A; }
.tab-label.image { color: #E6A23C; }
.tab-label.video { color: #F56C6C; }
.tab-label.image-to-video { color: #909399; }

.creation-content {
  padding: 24px;
}

.form-section,
.result-section {
  background: #fafafa;
  border-radius: 12px;
  padding: 24px;
  height: 100%;
}

.form-section h3,
.result-section h3 {
  margin: 0 0 20px 0;
  font-size: 1.2rem;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #409EFF;
  padding-bottom: 10px;
}

.result-section h3 {
  border-bottom-color: #f5576c;
}

.upload-area {
  width: 100%;
}

.upload-area :deep(.el-upload-dragger) {
  padding: 40px 20px;
  border-radius: 12px;
  border: 2px dashed #dcdfe6;
  background: #fff;
}

.upload-icon {
  font-size: 48px;
  color: #c0c4cc;
  margin-bottom: 16px;
}

.upload-text .main-text {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.upload-text .main-text em {
  color: #409EFF;
  font-style: normal;
}

@media (max-width: 768px) {
  .generation-container {
    padding: 16px;
  }
  
  .page-header-card {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .header-content {
    flex-direction: column;
  }
}
</style>
