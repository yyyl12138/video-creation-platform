<template>
  <div class="task-list-container">
    <!-- 页面标题卡片 -->
    <div class="page-header-card">
      <div class="header-content">
        <div class="header-icon">
          <el-icon size="32" color="#fff"><List /></el-icon>
        </div>
        <div class="header-text">
          <h2>任务管理</h2>
          <p>查看和管理您的 AI 生成任务</p>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <el-card class="main-content-card">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <el-form :model="queryParams" label-position="top" inline>
          <el-form-item label="任务类型">
            <el-select
              v-model="queryParams.taskType"
              placeholder="请选择任务类型"
              clearable
              style="width: 150px"
            >
              <el-option label="全部" value="" />
              <el-option label="文生文" value="TEXT_TO_TEXT" />
              <el-option label="文生图" value="TEXT_TO_IMAGE" />
              <el-option label="文生视频" value="TEXT_TO_VIDEO" />
              <el-option label="图生视频" value="IMAGE_TO_VIDEO" />
            </el-select>
          </el-form-item>

          <el-form-item label="任务状态">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择任务状态"
              clearable
              style="width: 150px"
            >
              <el-option label="全部" value="" />
              <el-option label="等待中" value="PENDING" />
              <el-option label="处理中" value="PROCESSING" />
              <el-option label="成功" value="SUCCESS" />
              <el-option label="失败" value="FAILED" />
              <el-option label="已取消" value="CANCELLED" />
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSearch" :loading="loading">
              <el-icon><Search /></el-icon>
              查询
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 任务列表表格 -->
      <div class="table-section">
        <el-table
          v-loading="loading"
          :data="tableData"
          style="width: 100%"
          stripe
        >
          <el-table-column prop="taskId" label="任务ID" width="160">
            <template #default="scope">
              <span class="task-id">{{ scope.row.taskId }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="taskType" label="任务类型" width="100">
            <template #default="scope">
              <span class="task-type">
                {{ taskTypeMap[scope.row.taskType] || scope.row.taskType }}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusTagType(scope.row.status)">
                {{ statusMap[scope.row.status] || scope.row.status }}
              </el-tag>
              <div v-if="scope.row.status === 'PROCESSING'" class="progress-container">
                <el-progress 
                  :percentage="scope.row.progress || 0" 
                  :stroke-width="6" 
                  :show-text="false"
                />
                <span class="progress-text">{{ scope.row.progress || 0 }}%</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="prompt" label="提示词" min-width="200">
            <template #default="scope">
              <div class="prompt-preview">
                {{ truncateText(scope.row.prompt, 50) }}
              </div>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <div class="action-buttons">
                <el-button 
                  type="primary" 
                  link 
                  @click="showTaskDetail(scope.row)"
                >
                  查看详情
                </el-button>
                
                <el-button 
                  v-if="scope.row.status === 'PENDING' || scope.row.status === 'PROCESSING'"
                  type="danger" 
                  link 
                  @click="handleCancel(scope.row.taskId)"
                  :loading="cancelingTasks[scope.row.taskId]"
                >
                  取消
                </el-button>
                
                <el-button 
                  v-if="scope.row.status === 'SUCCESS' && scope.row.result && scope.row.result.fileUrl"
                  type="success" 
                  link 
                  @click="downloadFile(scope.row.result.fileUrl, scope.row.taskId)"
                >
                  下载
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination-section" v-if="total > 0">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 空状态 -->
      <div class="empty-section" v-if="!loading && tableData.length === 0">
        <el-empty description="暂无任务数据" />
      </div>
    </el-card>

    <!-- 任务详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="'任务详情 - ' + currentTask.taskId"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentTaskDetail" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务ID">
            {{ currentTaskDetail.taskId }}
          </el-descriptions-item>
          <el-descriptions-item label="任务类型">
            {{ taskTypeMap[currentTaskDetail.taskType] || currentTaskDetail.taskType }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTagType(currentTaskDetail.status)">
              {{ statusMap[currentTaskDetail.status] || currentTaskDetail.status }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="进度">
            {{ currentTaskDetail.progress || 0 }}%
          </el-descriptions-item>
        </el-descriptions>

        <div class="detail-section">
          <h4>提示词</h4>
          <div class="prompt-text">
            {{ currentTaskDetail.prompt || '--' }}
          </div>
        </div>

        <div class="detail-section" v-if="currentTaskDetail.status === 'SUCCESS' && currentTaskDetail.result">
          <h4>生成结果</h4>
          <div class="result-content">
            <!-- 文生图结果 -->
            <div v-if="currentTaskDetail.taskType === 'TEXT_TO_IMAGE' && currentTaskDetail.result.fileUrl">
              <img 
                :src="getFullUrl(currentTaskDetail.result.fileUrl)" 
                alt="生成图片" 
                class="result-image"
                style="max-width: 100%; border-radius: 8px;"
              />
              <div class="result-info">
                <p>文件大小: {{ formatFileSize(currentTaskDetail.result.size) }}</p>
              </div>
            </div>

            <!-- 文生视频结果 -->
            <div v-if="currentTaskDetail.taskType === 'TEXT_TO_VIDEO' && currentTaskDetail.result.fileUrl">
              <div class="video-container">
                <video 
                  :src="getFullUrl(currentTaskDetail.result.fileUrl)" 
                  controls 
                  class="result-video"
                  style="max-width: 100%; border-radius: 8px;"
                ></video>
              </div>
              <div class="result-info">
                <p>时长: {{ currentTaskDetail.result.duration || '--' }} 秒</p>
                <p>文件大小: {{ formatFileSize(currentTaskDetail.result.size) }}</p>
              </div>
            </div>

            <!-- 图生视频结果 -->
            <div v-if="currentTaskDetail.taskType === 'IMAGE_TO_VIDEO' && currentTaskDetail.result.fileUrl">
              <div class="video-container">
                <video 
                  :src="getFullUrl(currentTaskDetail.result.fileUrl)" 
                  controls 
                  class="result-video"
                  style="max-width: 100%; border-radius: 8px;"
                ></video>
              </div>
              <div class="result-info">
                <p>时长: {{ currentTaskDetail.result.duration || '--' }} 秒</p>
                <p>文件大小: {{ formatFileSize(currentTaskDetail.result.size) }}</p>
              </div>
            </div>

            <!-- 文生文结果 -->
            <div v-if="currentTaskDetail.taskType === 'TEXT_TO_TEXT' && currentTaskDetail.content">
              <div class="text-result">
                <pre>{{ currentTaskDetail.content }}</pre>
              </div>
            </div>
          </div>
        </div>

        <div class="detail-section" v-if="currentTaskDetail.status === 'FAILED' && currentTaskDetail.error">
          <h4>错误信息</h4>
          <el-alert
            :title="currentTaskDetail.error"
            type="error"
            :closable="false"
          />
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            v-if="currentTaskDetail && currentTaskDetail.status === 'SUCCESS' && currentTaskDetail.result && currentTaskDetail.result.fileUrl"
            type="primary" 
            @click="downloadFile(currentTaskDetail.result.fileUrl, currentTaskDetail.taskId)"
          >
            下载文件
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { getTaskList, getTaskStatus } from '@/api/creation/generation'
import { ElMessage, ElMessageBox } from 'element-plus'
import { List, Search, Refresh } from '@element-plus/icons-vue'

// 数据状态
const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const cancelingTasks = ref({})

// 查询参数 - 修复：使用空字符串而不是null
const queryParams = reactive({
  page: '1',
  status: '',
  taskType: ''
})

// 任务详情相关
const detailDialogVisible = ref(false)
const currentTask = ref({})
const currentTaskDetail = ref(null)

// 映射表
const statusMap = {
  'PENDING': '等待中',
  'PROCESSING': '处理中',
  'SUCCESS': '成功',
  'FAILED': '失败',
  'CANCELLED': '已取消'
}

const taskTypeMap = {
  'TEXT_TO_TEXT': '文生文',
  'TEXT_TO_IMAGE': '文生图',
  'TEXT_TO_VIDEO': '文生视频',
  'IMAGE_TO_VIDEO': '图生视频'
}

// 轮询相关
let pollingInterval = null
const POLLING_INTERVAL = 5000

// 生命周期钩子
onMounted(() => {
  fetchTaskList()
  startPolling()
})

onUnmounted(() => {
  stopPolling()
})

// 获取任务列表
const fetchTaskList = async () => {
  try {
    loading.value = true
    
    // 构建请求参数，将空字符串转为null或不传
    const requestParams = {
      page: queryParams.page,
    }
    
    // 只有当有值时才添加筛选参数
    if (queryParams.taskType) {
      requestParams.taskType = queryParams.taskType
    }
    
    if (queryParams.status) {
      requestParams.status = queryParams.status
    }
    
    const response = await getTaskList(requestParams)
    
    if (response.code === 20000) {
      // 根据文档，返回的是 PageResult<TaskDetailVO>
      tableData.value = response.data.records || response.data.list || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取任务列表失败')
    }
  } catch (error) {
    console.error('获取任务列表失败:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 开始轮询
const startPolling = () => {
  if (pollingInterval) clearInterval(pollingInterval)
  
  pollingInterval = setInterval(() => {
    const hasProcessingTasks = tableData.value.some(
      task => task.status === 'PENDING' || task.status === 'PROCESSING'
    )
    
    if (hasProcessingTasks) {
      fetchTaskList()
    }
  }, POLLING_INTERVAL)
}

// 停止轮询
const stopPolling = () => {
  if (pollingInterval) {
    clearInterval(pollingInterval)
    pollingInterval = null
  }
}

// 查询按钮
const handleSearch = () => {
  queryParams.page = '1'
  fetchTaskList()
}

// 重置按钮
const handleReset = () => {
  queryParams.taskType = ''
  queryParams.status = ''
  queryParams.page = '1'
  fetchTaskList()
}

// 分页大小变化
const handleSizeChange = (newSize) => {
  queryParams.page = '1'
  fetchTaskList()
}

// 页码变化
const handleCurrentChange = (newPage) => {
  queryParams.page = newPage.toString()
  fetchTaskList()
}

// 取消任务
const handleCancel = async (taskId) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消这个任务吗？取消后可能无法恢复。',
      '确认取消',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    cancelingTasks.value[taskId] = true
    
    // 取消任务接口需要在generation.js中补充
    // 这里先模拟调用
    const response = await cancelGenerationTask(taskId)
    
    if (response.code === 20000) {
      ElMessage.success('任务已取消')
      fetchTaskList()
    } else {
      ElMessage.error(response.message || '取消任务失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消任务失败：' + error.message)
    }
  } finally {
    delete cancelingTasks.value[taskId]
  }
}

// 查看任务详情
const showTaskDetail = async (task) => {
  currentTask.value = task
  detailDialogVisible.value = true
  
  try {
    const response = await getTaskStatus(task.taskId)
    
    if (response.code === 20000) {
      currentTaskDetail.value = response.data
    } else {
      ElMessage.error(response.message || '获取任务详情失败')
    }
  } catch (error) {
    console.error('获取任务详情失败:', error)
    ElMessage.error('获取任务详情失败')
  }
}

// 工具函数
const getStatusTagType = (status) => {
  const typeMap = {
    'PENDING': 'info',
    'PROCESSING': 'primary',
    'SUCCESS': 'success',
    'FAILED': 'danger',
    'CANCELLED': 'warning'
  }
  return typeMap[status] || 'info'
}

const truncateText = (text, maxLength) => {
  if (!text) return '--'
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

const formatFileSize = (bytes) => {
  if (!bytes) return '--'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
  return (bytes / (1024 * 1024 * 1024)).toFixed(1) + ' GB'
}

const getFullUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  const baseUrl = import.meta.env.VITE_APP_BASE_API || ''
  return baseUrl + url
}

const downloadFile = (fileUrl, taskId) => {
  const fullUrl = getFullUrl(fileUrl)
  const link = document.createElement('a')
  link.href = fullUrl
  link.download = `ai_generation_${taskId}_${Date.now()}`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 取消任务接口函数（需要在generation.js中补充）
const cancelGenerationTask = (taskId) => {
  return Promise.resolve({ code: 20000, data: true })
}
</script>

<style scoped>
.task-list-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: calc(100vh - 40px);
}

.page-header-card {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border-radius: 16px;
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(79, 172, 254, 0.3);
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

.filter-section {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
  border-radius: 12px 12px 0 0;
}

.table-section {
  padding: 20px;
}

.pagination-section {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #f0f0f0;
}

.empty-section {
  padding: 60px 20px;
  text-align: center;
}

.task-id {
  font-family: 'Courier New', monospace;
  color: #409eff;
  font-weight: 500;
}

.task-type {
  font-weight: 500;
}

.prompt-preview {
  line-height: 1.5;
  color: #606266;
}

.progress-container {
  margin-top: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-text {
  font-size: 12px;
  color: #909399;
  min-width: 40px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-content {
  max-height: 600px;
  overflow-y: auto;
}

.detail-section {
  margin-top: 20px;
}

.detail-section h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 5px;
}

.prompt-text {
  padding: 12px;
  background: #fafafa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  line-height: 1.6;
  color: #606266;
}

.result-content {
  padding: 16px;
  background: #fafafa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.result-info {
  margin-top: 12px;
}

.result-info p {
  margin: 4px 0;
  color: #606266;
  font-size: 14px;
}

.text-result pre {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: inherit;
  margin: 0;
  color: #606266;
  line-height: 1.6;
  background: #fff;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .task-list-container {
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
  
  .filter-section .el-form {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
}
</style>