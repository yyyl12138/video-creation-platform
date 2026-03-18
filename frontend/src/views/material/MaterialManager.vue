<template>
  <div class="material-manager">
    <!-- 内容区域 -->
    <div class="content-area">
      <!-- 资源类型切换 -->
      <div class="resource-tabs">
        <el-tabs v-model="activeResource" type="card" @tab-change="handleResourceChange">
          <el-tab-pane name="materials" label="素材资源">
            <template #label>
              <span class="tab-label">
                <el-icon><Picture /></el-icon>
                素材库
              </span>
            </template>
          </el-tab-pane>
          <el-tab-pane name="templates" label="创作模板">
            <template #label>
              <span class="tab-label">
                <el-icon><MagicStick /></el-icon>
                模板库
              </span>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 筛选和搜索区域 -->
      <div class="filter-section">
        <div class="filter-group">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索资源..."
            prefix-icon="Search"
            class="search-input"
            clearable
            @keyup.enter="loadResources"
          />
          
          <template v-if="activeResource === 'materials'">
            <el-select v-model="materialType" placeholder="素材类型" clearable class="filter-select">
              <el-option label="全部类型" value="" />
              <el-option label="视频" value="video" />
              <el-option label="音频" value="audio" />
              <el-option label="图片" value="image" />
            </el-select>
          </template>
          
          <template v-else>
            <el-select v-model="templateCategory" placeholder="模板分类" clearable class="filter-select">
              <el-option v-for="item in templateCategories" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
            <el-select v-model="templateScene" placeholder="使用场景" clearable class="filter-select">
              <el-option v-for="item in templateScenes" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </template>

          <el-button type="primary" @click="loadResources" class="search-btn">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
        </div>
      </div>

      <!-- 资源列表 -->
      <div class="resource-list">
        <el-skeleton :rows="6" animated v-if="loading" />
        
        <template v-else>
          <!-- 素材资源列表 -->
          <template v-if="activeResource === 'materials' && materialList.length > 0">
            <div class="resource-grid">
              <div v-for="material in materialList" :key="material.id" class="resource-card material-card">
                <div class="card-preview">
                  <img v-if="material.type === 'image'" :src="material.url || getPlaceholder(material.type)" :alt="material.name" />
                  <div v-else class="type-indicator">
                    <el-icon :size="32"><component :is="getTypeIcon(material.type)" /></el-icon>
                    <span>{{ getTypeLabel(material.type) }}</span>
                  </div>
                </div>
                <div class="card-info">
                  <h4 class="card-title">{{ material.name }}</h4>
                  <div class="card-meta">
                    <el-tag size="small" :type="getTypeTag(material.type)">{{ getTypeLabel(material.type) }}</el-tag>
                    <span class="file-size">{{ formatFileSize(material.size) }}</span>
                  </div>
                </div>
                <div class="card-actions">
                  <el-tooltip content="预览">
                    <el-button size="small" @click="handlePreview(material)">
                      <el-icon><View /></el-icon>
                    </el-button>
                  </el-tooltip>
                  <el-tooltip content="删除">
                    <el-button size="small" type="danger" @click="handleDelete(material)">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </template>

          <!-- 模板资源列表 -->
          <template v-else-if="activeResource === 'templates' && templateList.length > 0">
            <div class="resource-grid">
              <div v-for="template in templateList" :key="template.id" class="resource-card template-card">
                <div class="card-preview template-preview">
                  <div class="template-icon">
                    <el-icon :size="48"><VideoCamera /></el-icon>
                  </div>
                </div>
                <div class="card-info">
                  <h4 class="card-title">{{ template.name }}</h4>
                  <div class="card-meta">
                    <el-tag size="small" :type="getCategoryTag(template.category)">{{ getCategoryLabel(template.category) }}</el-tag>
                    <el-tag size="small" :type="getSceneTag(template.scene)">{{ getSceneLabel(template.scene) }}</el-tag>
                  </div>
                </div>
                <div class="card-actions">
                  <el-button size="small" @click="handlePreviewTemplate(template)">预览</el-button>
                  <el-button size="small" type="success" @click="handleUseTemplate(template)">使用</el-button>
                </div>
              </div>
            </div>
          </template>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <el-icon size="64" color="#c0c4cc"><FolderOpened /></el-icon>
            <p>暂无{{ activeResource === 'materials' ? '素材' : '模板' }}</p>
            <el-button type="primary" @click="showUploadDialog = true">
              上传{{ activeResource === 'materials' ? '素材' : '模板' }}
            </el-button>
          </div>
        </template>
      </div>
    </div>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="showUploadDialog"
      :title="`上传${activeResource === 'materials' ? '素材' : '模板'}`"
      width="600px"
      class="upload-dialog"
    >
      <div class="upload-area">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :multiple="true"
          :limit="20"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          drag
          class="upload-component"
        >
          <el-icon class="upload-icon"><UploadFilled /></el-icon>
          <div class="upload-text">
            <p class="main-text">拖拽文件到此处，或<em>点击选择</em></p>
            <p class="sub-text">支持批量上传，单个文件不超过500MB</p>
          </div>
        </el-upload>
      </div>
      
      <div class="file-list" v-if="uploadFileList.length > 0">
        <div class="file-list-header">
          <span>已选择 {{ uploadFileList.length }} 个文件</span>
          <el-button text type="primary" size="small" @click="clearUploadList">清空</el-button>
        </div>
        <div v-for="(file, index) in uploadFileList" :key="index" class="file-item">
          <el-icon class="file-icon"><Document /></el-icon>
          <span class="file-name">{{ file.name }}</span>
          <span class="file-size">{{ formatFileSize(file.size) }}</span>
          <el-icon class="remove-icon" @click="handleFileRemove(file)"><Delete /></el-icon>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :disabled="uploadFileList.length === 0" :loading="uploading">
          {{ uploading ? '上传中...' : `上传 ${uploadFileList.length} 个文件` }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog v-model="showPreviewDialog" :title="previewTitle" width="800px">
      <div v-if="previewItem" class="preview-content">
        <template v-if="previewItem.type">
          <img v-if="previewItem.type === 'image'" class="preview-image" :src="previewItem.url" :alt="previewItem.name" />
          <video v-else-if="previewItem.type === 'video'" class="preview-video" :src="previewItem.url" controls />
          <audio v-else-if="previewItem.type === 'audio'" class="preview-audio" :src="previewItem.url" controls />
        </template>
        <template v-else>
          <div class="template-preview-placeholder">
            <el-icon size="64"><VideoCamera /></el-icon>
            <p>模板预览</p>
          </div>
        </template>
        <div class="preview-meta">
          <div><strong>名称：</strong>{{ previewItem.name }}</div>
          <div v-if="previewItem.type"><strong>类型：</strong>{{ getTypeLabel(previewItem.type) }}</div>
          <div v-if="previewItem.size"><strong>大小：</strong>{{ formatFileSize(previewItem.size) }}</div>
          <div v-if="previewItem.createTime"><strong>上传时间：</strong>{{ formatDate(previewItem.createTime) }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showPreviewDialog = false">关闭</el-button>
        <el-button v-if="previewItem?.url" type="primary" @click="openInNewTab(previewItem.url)">新窗口打开</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Collection, Plus, Picture, MagicStick, Search,
  UploadFilled, Document, Delete, View, VideoCamera,
  FolderOpened
} from '@element-plus/icons-vue'
import { getMaterials, uploadMaterial, deleteMaterial, getTemplates, uploadTemplate, deleteTemplate, useTemplate } from '@/api/user/material'

const activeResource = ref('materials')
const searchKeyword = ref('')
const loading = ref(false)
const showUploadDialog = ref(false)
const uploading = ref(false)
const uploadRef = ref(null)
const uploadFileList = ref([])
const showPreviewDialog = ref(false)
const previewItem = ref(null)

const materialList = ref([])
const materialType = ref('')
const templateList = ref([])
const templateCategory = ref('')
const templateScene = ref('')

const templateCategories = [
  { value: 'short_video', label: '短视频模板' },
  { value: 'intro', label: '片头模板' },
  { value: 'transition', label: '转场模板' },
  { value: 'subtitle', label: '字幕模板' },
  { value: 'effect', label: '特效模板' }
]

const templateScenes = [
  { value: 'advertising', label: '广告营销' },
  { value: 'education', label: '教育培训' },
  { value: 'social_media', label: '社交媒体' },
  { value: 'corporate', label: '企业宣传' },
  { value: 'personal', label: '个人创作' }
]

const previewTitle = computed(() => previewItem.value?.type ? '素材预览' : '模板预览')

const getTypeIcon = (type) => {
  const icons = { video: VideoCamera, audio: 'Headset', image: Picture }
  return icons[type] || Picture
}

const getTypeLabel = (type) => {
  const labels = { video: '视频', audio: '音频', image: '图片' }
  return labels[type] || type
}

const getTypeTag = (type) => {
  const tags = { video: '', audio: 'success', image: 'warning' }
  return tags[type] || ''
}

const getPlaceholder = (type) => {
  const colors = { video: '#409EFF', audio: '#67C23A', image: '#E6A23C' }
  return `data:image/svg+xml,${encodeURIComponent(`<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100"><rect width="100" height="100" fill="${colors[type] || '#909399'}"/><text x="50" y="55" fill="white" text-anchor="middle" font-size="40">${type[0]?.toUpperCase()}</text></svg>`)}`
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

const getCategoryLabel = (category) => {
  const found = templateCategories.find(item => item.value === category)
  return found ? found.label : category
}

const getCategoryTag = (category) => {
  const tags = { short_video: '', intro: 'warning', transition: 'info', subtitle: 'success', effect: 'danger' }
  return tags[category] || ''
}

const getSceneLabel = (scene) => {
  const found = templateScenes.find(item => item.value === scene)
  return found ? found.label : scene
}

const getSceneTag = (scene) => {
  const tags = { advertising: '', education: 'success', social_media: 'warning', corporate: 'info', personal: 'danger' }
  return tags[scene] || ''
}

const loadResources = async () => {
  try {
    loading.value = true
    if (activeResource.value === 'materials') {
      await loadMaterials()
    } else {
      await loadTemplates()
    }
  } catch (error) {
    ElMessage.error('加载失败: ' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const loadMaterials = async () => {
  const params = { page: 1, size: 50, type: materialType.value || undefined }
  const res = await getMaterials(params)
  if (res.code === 20000 && res.data) {
    materialList.value = (res.data.records || []).map(item => ({
      id: item.materialId, name: item.name, type: (item.type || '').toLowerCase(),
      size: item.fileSize, createTime: item.createTime, url: item.url
    }))
  } else {
    materialList.value = []
  }
}

const loadTemplates = async () => {
  const params = { page: 1, size: 50, category: templateCategory.value || undefined, scene: templateScene.value || undefined }
  const res = await getTemplates(params)
  templateList.value = res.list || []
}

const handleResourceChange = () => {
  searchKeyword.value = ''
  materialType.value = ''
  templateCategory.value = ''
  templateScene.value = ''
  loadResources()
}

const handlePreview = (item) => {
  previewItem.value = item
  showPreviewDialog.value = true
}

const handlePreviewTemplate = (template) => {
  previewItem.value = template
  showPreviewDialog.value = true
  ElMessage.info(`预览: ${template.name}`)
}

const handleUseTemplate = async (template) => {
  try {
    await useTemplate(template.templateId || template.id)
    ElMessage.success(`开始使用: ${template.name}`)
  } catch (error) {
    ElMessage.error(error.message || '使用模板失败')
  }
}

const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm(`确定删除 "${item.name}"？`, '确认', { type: 'warning' })
    if (activeResource.value === 'materials') {
      await deleteMaterial(item.id, { type: (item.type || '').toUpperCase() })
      materialList.value = materialList.value.filter(m => m.id !== item.id)
    } else {
      await deleteTemplate(item.templateId || template.id)
      templateList.value = templateList.value.filter(t => t.id !== item.id)
    }
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
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
    let successCount = 0
    for (const file of uploadFileList.value) {
      try {
        const formData = new FormData()
        const rawFile = file.raw || file
        formData.append('file', rawFile)
        formData.append('name', rawFile.name)
        if (activeResource.value === 'materials') {
          await uploadMaterial(formData)
        } else {
          await uploadTemplate(formData)
        }
        successCount++
      } catch (error) {
        ElMessage.error(`文件 ${file.name} 上传失败: ${error.message || '未知错误'}`)
      }
    }
    if (successCount > 0) {
      ElMessage.success(`成功上传 ${successCount} 个文件`)
      showUploadDialog.value = false
      clearUploadList()
      loadResources()
    } else {
      ElMessage.error('所有文件上传失败')
    }
  } catch (error) {
    ElMessage.error('上传失败: ' + (error.message || '未知错误'))
  } finally {
    uploading.value = false
  }
}

const openInNewTab = (url) => { if (url) window.open(url, '_blank') }

onMounted(() => { loadResources() })
</script>

<style scoped>
.material-manager {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.unified-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 0;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-icon {
  width: 72px;
  height: 72px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.header-text h1 {
  margin: 0;
  font-size: 2.2rem;
  font-weight: 700;
  color: #fff;
}

.header-text p {
  margin: 8px 0 0 0;
  color: rgba(255, 255, 255, 0.9);
  font-size: 1.1rem;
}

.upload-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.5);
  font-weight: 600;
  padding: 12px 24px;
  border-radius: 12px;
}

.upload-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.content-area {
  max-width: 1400px;
  margin: 0 auto;
  padding: 32px 24px;
}

.resource-tabs {
  margin-bottom: 24px;
}

.resource-tabs :deep(.el-tabs__item) {
  font-size: 16px;
  font-weight: 500;
  padding: 0 24px;
  height: 48px;
  line-height: 48px;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.filter-group {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input { width: 300px; }
.filter-select { width: 160px; }
.search-btn { min-width: 80px; }

.resource-list { min-height: 400px; }

.resource-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.resource-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.resource-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.card-preview {
  height: 160px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.type-indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.type-indicator span {
  font-size: 12px;
  color: #606266;
}

.template-preview {
  background: linear-gradient(135deg, #8e44ad 0%, #9b59b6 100%);
}

.template-icon {
  color: #fff;
}

.card-info {
  padding: 16px;
}

.card-title {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.file-size {
  font-size: 12px;
  color: #909399;
}

.card-actions {
  padding: 12px 16px;
  border-top: 1px solid #f5f5f5;
  display: flex;
  gap: 8px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: #fff;
  border-radius: 16px;
}

.empty-state p {
  margin: 20px 0;
  font-size: 16px;
  color: #909399;
}

.upload-area { padding: 20px; }

.upload-component { width: 100%; }

.upload-component :deep(.el-upload-dragger) {
  padding: 40px;
  border-radius: 12px;
  border: 2px dashed #dcdfe6;
  background: #fafafa;
}

.upload-icon {
  font-size: 48px;
  color: #667eea;
  margin-bottom: 16px;
}

.upload-text .main-text {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #606266;
}

.upload-text .main-text em {
  color: #667eea;
  font-style: normal;
}

.upload-text .sub-text {
  margin: 0;
  font-size: 13px;
  color: #909399;
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
}

.file-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  border-bottom: 1px solid #ebeef5;
}

.file-item:last-child { border-bottom: none; }

.file-icon {
  margin-right: 12px;
  color: #909399;
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

.preview-content { display: grid; gap: 16px; }

.preview-image {
  width: 100%;
  max-height: 400px;
  object-fit: contain;
  background: #0b0f19;
  border-radius: 10px;
}

.preview-video {
  width: 100%;
  max-height: 400px;
  background: #0b0f19;
  border-radius: 10px;
}

.preview-audio { width: 100%; }

.template-preview-placeholder {
  height: 300px;
  background: linear-gradient(135deg, #8e44ad 0%, #9b59b6 100%);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.preview-meta {
  font-size: 13px;
  color: #606266;
  line-height: 1.8;
}

@media (max-width: 768px) {
  .header-container {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }
  
  .filter-group {
    flex-direction: column;
  }
  
  .search-input,
  .filter-select {
    width: 100%;
  }
  
  .resource-grid {
    grid-template-columns: 1fr;
  }
}
</style>