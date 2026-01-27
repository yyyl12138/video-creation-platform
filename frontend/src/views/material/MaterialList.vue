<template>
  <div class="material-container">
    <!-- 页面标题卡片 -->
    <div class="page-header-card">
      <div class="header-content">
        <div class="header-icon">
          <el-icon size="32" color="#fff"><Picture /></el-icon>
        </div>
        <div class="header-text">
          <h2>素材管理</h2>
          <p>管理您的视频、音频、图片素材库</p>
        </div>
      </div>
      <el-button type="primary" size="large" @click="showUploadDialog = true" class="upload-btn">
        <el-icon><Plus /></el-icon>
        上传素材
      </el-button>
    </div>

    <!-- 主内容区域 -->
    <el-card class="main-content-card">
      <!-- 素材类型标签页 -->
      <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange" class="material-tabs">
        <el-tab-pane name="all">
          <template #label>
            <span class="tab-label">
              <el-icon><Grid /></el-icon>
              全部素材
            </span>
          </template>
          <div class="filter-bar">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索素材名称..."
              prefix-icon="Search"
              class="search-input"
              clearable
              @keyup.enter="loadMaterials"
            />
            <el-button type="primary" @click="loadMaterials" class="search-btn">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
          </div>
        </el-tab-pane>
        
        <el-tab-pane name="video">
          <template #label>
            <span class="tab-label video">
              <el-icon><VideoCamera /></el-icon>
              视频素材
            </span>
          </template>
          <div class="filter-bar">
            <el-input
              v-model="videoFilters.keyword"
              placeholder="搜索视频..."
              prefix-icon="Search"
              class="search-input"
              clearable
            />
            <el-select v-model="videoFilters.resolution" placeholder="分辨率" clearable class="filter-select">
              <el-option label="4K" value="4k" />
              <el-option label="1080P" value="1080p" />
              <el-option label="720P" value="720p" />
            </el-select>
            <el-select v-model="videoFilters.duration" placeholder="时长" clearable class="filter-select">
              <el-option label="短 (<1分钟)" value="short" />
              <el-option label="中 (1-5分钟)" value="medium" />
              <el-option label="长 (>5分钟)" value="long" />
            </el-select>
            <el-button type="primary" @click="loadMaterials">
              <el-icon><Search /></el-icon>
            </el-button>
          </div>
        </el-tab-pane>
        
        <el-tab-pane name="audio">
          <template #label>
            <span class="tab-label audio">
              <el-icon><Headset /></el-icon>
              音频素材
            </span>
          </template>
          <div class="filter-bar">
            <el-input
              v-model="audioFilters.keyword"
              placeholder="搜索音频..."
              prefix-icon="Search"
              class="search-input"
              clearable
            />
            <el-select v-model="audioFilters.audioType" placeholder="类型" clearable class="filter-select">
              <el-option label="背景音乐" value="bgm" />
              <el-option label="音效" value="effect" />
              <el-option label="人声" value="voice" />
            </el-select>
            <el-select v-model="audioFilters.bitrate" placeholder="比特率" clearable class="filter-select">
              <el-option label="128kbps" value="128" />
              <el-option label="192kbps" value="192" />
              <el-option label="320kbps" value="320" />
            </el-select>
            <el-button type="primary" @click="loadMaterials">
              <el-icon><Search /></el-icon>
            </el-button>
          </div>
        </el-tab-pane>
        
        <el-tab-pane name="image">
          <template #label>
            <span class="tab-label image">
              <el-icon><Picture /></el-icon>
              图片素材
            </span>
          </template>
          <div class="filter-bar">
            <el-input
              v-model="imageFilters.keyword"
              placeholder="搜索图片..."
              prefix-icon="Search"
              class="search-input"
              clearable
            />
            <el-select v-model="imageFilters.imageType" placeholder="格式" clearable class="filter-select">
              <el-option label="JPG" value="jpg" />
              <el-option label="PNG" value="png" />
              <el-option label="GIF" value="gif" />
              <el-option label="WEBP" value="webp" />
            </el-select>
            <el-select v-model="imageFilters.dimension" placeholder="尺寸" clearable class="filter-select">
              <el-option label="小图" value="small" />
              <el-option label="中图" value="medium" />
              <el-option label="大图" value="large" />
            </el-select>
            <el-button type="primary" @click="loadMaterials">
              <el-icon><Search /></el-icon>
            </el-button>
          </div>
        </el-tab-pane>
      </el-tabs>

      <!-- 素材列表 -->
      <div class="material-list" v-loading="loading">
        <div v-if="materialList.length === 0" class="empty-state">
          <el-icon size="64" color="#c0c4cc"><FolderOpened /></el-icon>
          <p>暂无素材</p>
          <el-button type="primary" @click="showUploadDialog = true">上传素材</el-button>
        </div>
        
        <el-table v-else :data="materialList" style="width: 100%" stripe>
          <el-table-column label="预览" width="100" align="center">
            <template #default="{ row }">
              <div class="preview-thumb">
                <img v-if="row.type === 'image'" :src="row.url || getPlaceholder(row.type)" :alt="row.name" />
                <div v-else class="type-icon">
                  <el-icon :size="24"><component :is="getTypeIcon(row.type)" /></el-icon>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="name" label="素材名称" min-width="180" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="material-name">{{ row.name }}</span>
            </template>
          </el-table-column>

          <el-table-column label="类型" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getTypeTag(row.type)" size="small">{{ getTypeLabel(row.type) }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column label="大小" width="100" align="center">
            <template #default="{ row }">
              {{ formatFileSize(row.size) }}
            </template>
          </el-table-column>

          <el-table-column label="上传时间" width="160" align="center">
            <template #default="{ row }">
              {{ formatDate(row.createTime) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="120" align="center" fixed="right">
            <template #default="{ row }">
              <el-button-group>
                <el-tooltip content="预览">
                  <el-button size="small" @click="handlePreview(row)">
                    <el-icon><View /></el-icon>
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
      :title="`上传${getUploadDialogTitle()}素材`"
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
          :limit="20"
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          drag
          class="upload-component"
        >
          <el-icon class="upload-icon"><UploadFilled /></el-icon>
          <div class="upload-text">
            <p class="main-text">拖拽文件到此处，或<em>点击选择</em></p>
            <p class="sub-text">支持批量上传，单个文件不超过 500MB</p>
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
          <el-icon class="file-icon"><Document /></el-icon>
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
  Plus, Search, Picture, VideoCamera, Headset, UploadFilled,
  Document, Delete, View, Grid, FolderOpened
} from '@element-plus/icons-vue'

const activeTab = ref('all')
const searchKeyword = ref('')
const loading = ref(false)
const showUploadDialog = ref(false)
const uploading = ref(false)
const uploadRef = ref(null)
const uploadFileList = ref([])
const materialList = ref([])

const videoFilters = reactive({ keyword: '', resolution: '', duration: '', aiTags: [] })
const audioFilters = reactive({ keyword: '', audioType: '', bitrate: '', aiTags: [] })
const imageFilters = reactive({ keyword: '', imageType: '', dimension: '', color: '', aiTags: [] })

const mockMaterials = [
  { id: 1, name: '产品宣传视频.mp4', type: 'video', size: 52428800, createTime: '2024-01-26T10:30:00', url: '' },
  { id: 2, name: '背景音乐配乐.mp3', type: 'audio', size: 8388608, createTime: '2024-01-25T14:20:00', url: '' },
  { id: 3, name: '产品展示图片.jpg', type: 'image', size: 2097152, createTime: '2024-01-24T09:15:00', url: '' },
  { id: 4, name: 'Logo动画.mp4', type: 'video', size: 15728640, createTime: '2024-01-23T16:45:00', url: '' },
]

const uploadUrl = computed(() => `${import.meta.env.VITE_APP_BASE_API}/material/upload`)
const uploadHeaders = computed(() => ({ Authorization: localStorage.getItem('token') }))

const getTypeIcon = (type) => {
  const icons = { video: VideoCamera, audio: Headset, image: Picture }
  return icons[type] || Picture
}

const getTypeTag = (type) => {
  const tags = { video: '', audio: 'success', image: 'warning' }
  return tags[type] || ''
}

const getTypeLabel = (type) => {
  const labels = { video: '视频', audio: '音频', image: '图片' }
  return labels[type] || type
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

const getUploadDialogTitle = () => {
  const titles = { all: '', video: '视频', audio: '音频', image: '图片' }
  return titles[activeTab.value] || ''
}

const handleTabChange = () => loadMaterials()
const loadMaterials = async () => {
  try {
    loading.value = true
    setTimeout(() => {
      let filtered = [...mockMaterials]
      if (activeTab.value !== 'all') {
        filtered = filtered.filter(item => item.type === activeTab.value)
      }
      materialList.value = filtered
      loading.value = false
    }, 300)
  } catch (error) {
    ElMessage.error('加载失败')
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
    await new Promise(r => setTimeout(r, 1500))
    ElMessage.success(`成功上传 ${uploadFileList.value.length} 个文件`)
    showUploadDialog.value = false
    clearUploadList()
    loadMaterials()
  } catch (error) {
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
  }
}

const handlePreview = (material) => ElMessage.info(`预览: ${material.name}`)
const handleDelete = async (material) => {
  try {
    await ElMessageBox.confirm(`确定删除 "${material.name}"？`, '确认', { type: 'warning' })
    ElMessage.success('删除成功')
    loadMaterials()
  } catch {}
}

onMounted(() => loadMaterials())
</script>

<style scoped>
.material-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: calc(100vh - 40px);
}

.page-header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
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

.material-tabs {
  border: none !important;
}

.material-tabs :deep(.el-tabs__header) {
  background: #fafafa;
  border-radius: 12px 12px 0 0;
  padding: 8px 8px 0;
  border-bottom: none !important;
}

.material-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.material-tabs :deep(.el-tabs__item) {
  height: 48px;
  line-height: 48px;
  border: none !important;
  font-size: 14px;
  font-weight: 500;
}

.material-tabs :deep(.el-tabs__item.is-active) {
  background: #fff;
  border-radius: 8px 8px 0 0;
}

.tab-label {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 8px;
}

.tab-label.video { color: #409EFF; }
.tab-label.audio { color: #67C23A; }
.tab-label.image { color: #E6A23C; }

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
  width: 140px;
}

.search-btn {
  min-width: 80px;
}

.material-list {
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

.preview-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.type-icon {
  color: #909399;
}

.material-name {
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
  color: #c0c4cc;
  margin-bottom: 16px;
}

.upload-text .main-text {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #606266;
}

.upload-text .main-text em {
  color: #409EFF;
  font-style: normal;
}

.upload-text .sub-text {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

@media (max-width: 768px) {
  .material-container {
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
