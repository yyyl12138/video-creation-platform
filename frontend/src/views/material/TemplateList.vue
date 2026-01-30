<template>
  <div class="template-container">
    <!-- 页面标题卡片 -->
    <div class="page-header-card">
      <div class="header-content">
        <div class="header-icon">
          <el-icon size="32" color="#fff"><MagicStick /></el-icon>
        </div>
        <div class="header-text">
          <h2>模板管理</h2>
          <p>选择精美模板，快速创作专业视频</p>
        </div>
      </div>
      <el-button type="primary" size="large" @click="showUploadDialog = true" class="upload-btn">
        <el-icon><Plus /></el-icon>
        上传模板
      </el-button>
    </div>

    <!-- 主内容区域 -->
    <el-card class="main-content-card">
      <!-- 搜索和筛选区域 -->
      <div class="filter-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索模板名称..."
          prefix-icon="Search"
          class="search-input"
          clearable
          @keyup.enter="loadTemplates"
        />
        <el-select v-model="filterCategory" placeholder="分类" clearable class="filter-select">
          <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-select v-model="filterScene" placeholder="场景" clearable class="filter-select">
          <el-option v-for="item in scenes" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" @click="loadTemplates" class="search-btn">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>

      <!-- 模板列表 -->
      <div class="template-list" v-loading="loading">
        <div v-if="templateList.length === 0" class="empty-state">
          <el-icon size="64" color="#c0c4cc"><FolderOpened /></el-icon>
          <p>暂无模板</p>
          <el-button type="primary" @click="showUploadDialog = true">上传模板</el-button>
        </div>
        
        <el-table v-else :data="templateList" style="width: 100%" stripe>
          <el-table-column label="预览" width="120" align="center">
            <template #default="{ row }">
              <div class="preview-thumb">
                <div class="template-icon">
                  <el-icon :size="36"><VideoCamera /></el-icon>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="name" label="模板名称" min-width="180" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="template-name">{{ row.name }}</span>
            </template>
          </el-table-column>

          <el-table-column label="分类" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="getCategoryTag(row.category)" size="small">
                {{ getCategoryLabel(row.category) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="场景" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="getSceneTag(row.scene)" size="small">
                {{ getSceneLabel(row.scene) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="180" align="center" fixed="right">
            <template #default="{ row }">
              <el-button-group>
                <el-tooltip content="预览">
                  <el-button size="small" @click="handlePreview(row)">
                    <el-icon><View /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="使用">
                  <el-button size="small" type="success" @click="handleUseTemplate(row)">
                    <el-icon><Check /></el-icon>
                  </el-button>
                </el-tooltip>
                <el-tooltip content="删除">
                  <el-button size="small" type="danger" @click="handleDelete(row)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </el-tooltip>
              </el-button-group>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-card>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      title="上传视频模板"
      width="600px"
      class="upload-dialog"
    >
      <div class="upload-area">
        <el-upload
          ref="uploadRef"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :auto-upload="false"
          :multiple="true"
          :limit="10"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          drag
          class="upload-component"
        >
          <el-icon class="upload-icon"><UploadFilled /></el-icon>
          <div class="upload-text">
            <p class="main-text">拖拽视频模板文件到此处，或<em>点击选择</em></p>
            <p class="sub-text">支持MP4/MOV格式，单个文件不超过500MB</p>
          </div>
        </el-upload>
      </div>
      
      <div class="file-list" v-if="uploadFileList.length > 0">
        <div class="file-list-header">
          <span>已选择 {{ uploadFileList.length }} 个文件</span>
          <el-button text type="primary" size="small" @click="clearUploadList">清空</el-button>
        </div>
        <div 
          v-for="(file, index) in uploadFileList" 
          :key="index" 
          class="file-item"
        >
          <el-icon class="file-icon"><VideoCamera /></el-icon>
          <span class="file-name">{{ file.name }}</span>
          <span class="file-size">{{ formatFileSize(file.size) }}</span>
          <el-icon class="remove-icon" @click="handleFileRemove(file)"><Delete /></el-icon>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="submitUpload" 
          :disabled="uploadFileList.length === 0"
          :loading="uploading"
        >
          {{ uploading ? '上传中...' : `上传 ${uploadFileList.length} 个文件` }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Search, UploadFilled, VideoCamera, MagicStick,
  View, Delete, Check, FolderOpened
} from '@element-plus/icons-vue'
import { getTemplates, uploadTemplate, deleteTemplate, useTemplate } from '@/api/user/material'

const searchKeyword = ref('')
const filterCategory = ref('')
const filterScene = ref('')
const loading = ref(false)
const showUploadDialog = ref(false)
const uploading = ref(false)
const uploadRef = ref(null)
const uploadFileList = ref([])
const templateList = ref([])

const categories = [
  { value: 'short_video', label: '短视频模板' },
  { value: 'intro', label: '片头模板' },
  { value: 'transition', label: '转场模板' },
  { value: 'subtitle', label: '字幕模板' },
  { value: 'effect', label: '特效模板' }
]

const scenes = [
  { value: 'advertising', label: '广告营销' },
  { value: 'education', label: '教育培训' },
  { value: 'social_media', label: '社交媒体' },
  { value: 'corporate', label: '企业宣传' },
  { value: 'personal', label: '个人创作' }
]

const mockTemplates = [
  { 
    id: 1, 
    name: '时尚产品展示模板.mp4', 
    category: 'short_video', 
    scene: 'advertising',
    size: 52428800,
    createTime: '2024-01-26T10:30:00'
  },
  { 
    id: 2, 
    name: '企业宣传片头模板.mov', 
    category: 'intro', 
    scene: 'corporate',
    size: 73400320,
    createTime: '2024-01-25T14:20:00'
  }
]

const uploadUrl = computed(() => `${import.meta.env.VITE_APP_BASE_API}/template/upload`)
const uploadHeaders = computed(() => ({ Authorization: localStorage.getItem('token') }))

const getCategoryLabel = (category) => {
  const found = categories.find(item => item.value === category)
  return found ? found.label : category
}

const getCategoryTag = (category) => {
  const tags = { 
    short_video: '', 
    intro: 'warning', 
    transition: 'info', 
    subtitle: 'success', 
    effect: 'danger' 
  }
  return tags[category] || ''
}

const getSceneLabel = (scene) => {
  const found = scenes.find(item => item.value === scene)
  return found ? found.label : scene
}

const getSceneTag = (scene) => {
  const tags = { 
    advertising: '', 
    education: 'success', 
    social_media: 'warning', 
    corporate: 'info', 
    personal: 'danger' 
  }
  return tags[scene] || ''
}

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i]
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

const loadTemplates = async () => {
  try {
    loading.value = true
    const params = {
      page: 1,
      size: 20
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    if (filterCategory.value) {
      params.category = filterCategory.value
    }
    if (filterScene.value) {
      params.scene = filterScene.value
    }
    const res = await getTemplates(params)
    templateList.value = res.list || []
    loading.value = false
  } catch (error) {
    console.error('加载模板失败:', error)
    ElMessage.error(error.message || '加载失败')
    loading.value = false
  }
}

const handleFileChange = (file) => {
  if (file.size > 500 * 1024 * 1024) {
    ElMessage.error(`文件 ${file.name} 超过500MB`)
    return
  }
  uploadFileList.value.push(file)
}

const handleFileRemove = (file) => {
  const index = uploadFileList.value.findIndex(f => f.uid === file.uid || f === file)
  if (index > -1) uploadFileList.value.splice(index, 1)
}

const clearUploadList = () => {
  uploadFileList.value = []
  if (uploadRef.value) uploadRef.value.clearFiles()
}

const submitUpload = async () => {
  if (uploadFileList.value.length === 0) {
    ElMessage.warning('请选择文件')
    return
  }
  try {
    uploading.value = true
    const formData = new FormData()
    uploadFileList.value.forEach(file => {
      formData.append('files', file.raw)
    })
    formData.append('name', uploadFileList.value[0].name)
    await uploadTemplate(formData)
    ElMessage.success(`成功上传 ${uploadFileList.value.length} 个模板`)
    showUploadDialog.value = false
    clearUploadList()
    loadTemplates()
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error(error.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

const handlePreview = (template) => {
  ElMessage.info(`预览: ${template.name}`)
  // TODO: 实现预览功能，可以打开视频播放器弹窗
}

const handleUseTemplate = async (template) => {
  try {
    await useTemplate(template.templateId || template.id)
    ElMessage.success(`开始使用: ${template.name}`)
    // TODO: 跳转到创作页面
  } catch (error) {
    console.error('使用模板失败:', error)
    ElMessage.error(error.message || '使用模板失败')
  }
}

const handleDelete = async (template) => {
  try {
    await ElMessageBox.confirm(`确定删除 "${template.name}"？`, '确认', { type: 'warning' })
    await deleteTemplate(template.templateId || template.id)
    ElMessage.success('删除成功')
    loadTemplates()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => loadTemplates())
</script>

<style scoped>
.template-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: calc(100vh - 40px);
}

.page-header-card {
  background: linear-gradient(135deg, #8e44ad 0%, #9b59b6 100%);
  border-radius: 16px;
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(142, 68, 173, 0.3);
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

.upload-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.5);
  font-weight: 600;
}

.upload-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.main-content-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
}

.filter-bar {
  display: flex;
  gap: 12px;
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
  margin: 16px;
}

.search-input {
  width: 280px;
}

.filter-select {
  width: 160px;
}

.search-btn {
  min-width: 80px;
}

.template-list {
  padding: 0 16px 16px;
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

.preview-thumb {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  overflow: hidden;
  margin: 0 auto;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.template-icon {
  color: #8e44ad;
}

.template-name {
  font-weight: 500;
  color: #303133;
}

.file-list {
  margin-top: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  max-height: 250px;
  overflow-y: auto;
}

.file-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  font-weight: 500;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  border-bottom: 1px solid #ebeef5;
}

.file-item:last-child {
  border-bottom: none;
}

.file-icon {
  margin-right: 12px;
  color: #8e44ad;
}

.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  margin: 0 16px;
  color: #909399;
  font-size: 12px;
}

.remove-icon {
  color: #f56c6c;
  cursor: pointer;
}

.upload-area {
  padding: 20px;
}

.upload-component {
  width: 100%;
}

.upload-component :deep(.el-upload-dragger) {
  padding: 40px;
  border-radius: 12px;
  border: 2px dashed #dcdfe6;
  background: #fafafa;
}

.upload-icon {
  font-size: 48px;
  color: #8e44ad;
  margin-bottom: 16px;
}

.upload-text .main-text {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #606266;
}

.upload-text .main-text em {
  color: #8e44ad;
  font-style: normal;
}

.upload-text .sub-text {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

@media (max-width: 768px) {
  .template-container {
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
  
  .filter-bar {
    flex-wrap: wrap;
  }
  
  .search-input,
  .filter-select {
    width: 100%;
  }
}
</style>
