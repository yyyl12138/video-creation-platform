<template>
  <div class="project-edit-container">
    <!-- 头部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="createNewProject">
          <el-icon><Plus /></el-icon>
          新建工程
        </el-button>
        <el-button @click="showImportDialog = true">
          <el-icon><Upload /></el-icon>
          导入素材
        </el-button>
      </div>
      <div class="toolbar-right">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索工程..."
          prefix-icon="Search"
          clearable
          style="width: 200px"
        />
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 工程列表 -->
      <div class="projects-section" v-if="!currentProject">
        <h3>我的工程</h3>
        <div class="projects-grid" v-if="filteredProjects.length > 0">
          <div
            v-for="project in filteredProjects"
            :key="project.id"
            class="project-card"
            @click="openProject(project)"
          >
            <div class="project-thumbnail">
              <el-icon :size="48"><VideoCamera /></el-icon>
            </div>
            <div class="project-info">
              <h4>{{ project.name }}</h4>
              <p class="project-meta">
                <span>{{ project.duration || '00:00' }}</span>
                <span>{{ formatDate(project.updateTime) }}</span>
              </p>
            </div>
            <div class="project-actions" @click.stop>
              <el-button type="primary" link size="small" @click="editProjectInfo(project)">
                编辑
              </el-button>
              <el-button type="danger" link size="small" @click="deleteProject(project)">
                删除
              </el-button>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无工程，点击新建工程开始创作" />
      </div>

      <!-- 工程编辑器 -->
      <div class="editor-section" v-else>
        <!-- 编辑器头部 -->
        <div class="editor-header">
          <el-button @click="closeProject">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <div class="project-name">
            <el-input
              v-model="currentProject.name"
              placeholder="工程名称"
              size="small"
              @blur="saveProject"
            />
          </div>
          <div class="editor-actions">
            <el-button @click="saveProject" :loading="saving">
              <el-icon><FolderChecked /></el-icon>
              保存
            </el-button>
            <el-button type="primary" @click="exportProject" :loading="exporting">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </div>

        <!-- 编辑器主体 -->
        <div class="editor-body">
          <!-- 预览区域 -->
          <div class="preview-area">
            <div class="video-preview">
              <video
                v-if="previewVideo"
                :src="previewVideo"
                controls
                class="preview-video"
              ></video>
              <div v-else class="preview-placeholder">
                <el-icon :size="64"><VideoPlay /></el-icon>
                <p>添加素材开始创作</p>
              </div>
            </div>
            <!-- 素材库 -->
            <div class="materials-panel">
              <div class="panel-header">
                <span>素材库</span>
                <el-button type="primary" size="small" @click="showImportDialog = true">
                  添加素材
                </el-button>
              </div>
              <div class="materials-list">
                <div
                  v-for="material in projectMaterials"
                  :key="material.id"
                  class="material-item"
                  draggable="true"
                  @dragstart="handleDragStart($event, material)"
                >
                  <el-icon><VideoCamera /></el-icon>
                  <span>{{ material.name }}</span>
                  <span class="material-duration">{{ material.duration }}</span>
                </div>
                <el-empty v-if="projectMaterials.length === 0" description="暂无素材" :image-size="60" />
              </div>
            </div>
          </div>

          <!-- 时间线 -->
          <div class="timeline-area">
            <div class="timeline-header">
              <span>时间线</span>
              <div class="timeline-controls">
                <el-button-group>
                  <el-button size="small" @click="skipBackward">
                    <el-icon><DArrowLeft /></el-icon>
                  </el-button>
                  <el-button size="small" @click="togglePlay">
                    <el-icon><component :is="isPlaying ? 'VideoPause' : 'VideoPlay'" /></el-icon>
                  </el-button>
                  <el-button size="small" @click="skipForward">
                    <el-icon><DArrowRight /></el-icon>
                  </el-button>
                </el-button-group>
                <span class="time-display">{{ currentTime }} / {{ totalTime }}</span>
              </div>
            </div>
            <div class="timeline-tracks">
              <!-- 视频轨道 -->
              <div class="track video-track">
                <div class="track-label">视频</div>
                <div class="track-content">
                  <div
                    v-for="clip in videoClips"
                    :key="clip.id"
                    class="clip video-clip"
                    :style="getClipStyle(clip)"
                    @click="selectClip(clip)"
                  >
                    {{ clip.name }}
                  </div>
                </div>
              </div>
              <!-- 音频轨道 -->
              <div class="track audio-track">
                <div class="track-label">音频</div>
                <div class="track-content">
                  <div
                    v-for="clip in audioClips"
                    :key="clip.id"
                    class="clip audio-clip"
                    :style="getClipStyle(clip)"
                    @click="selectClip(clip)"
                  >
                    {{ clip.name }}
                  </div>
                </div>
              </div>
              <!-- 字幕轨道 -->
              <div class="track subtitle-track">
                <div class="track-label">字幕</div>
                <div class="track-content">
                  <div
                    v-for="subtitle in subtitles"
                    :key="subtitle.id"
                    class="clip subtitle-clip"
                    :style="getClipStyle(subtitle)"
                    @click="selectClip(subtitle)"
                  >
                    {{ subtitle.text }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新建/编辑工程弹窗 -->
    <el-dialog
      v-model="showProjectDialog"
      :title="editingProject?.id ? '编辑工程' : '新建工程'"
      width="500px"
    >
      <el-form :model="projectForm" label-width="80px">
        <el-form-item label="工程名称" required>
          <el-input v-model="projectForm.name" placeholder="请输入工程名称" />
        </el-form-item>
        <el-form-item label="分辨率">
          <el-select v-model="projectForm.resolution" style="width: 100%">
            <el-option label="1080p (1920x1080)" value="1080p" />
            <el-option label="720p (1280x720)" value="720p" />
            <el-option label="4K (3840x2160)" value="4k" />
          </el-select>
        </el-form-item>
        <el-form-item label="帧率">
          <el-select v-model="projectForm.frameRate" style="width: 100%">
            <el-option label="24 fps" :value="24" />
            <el-option label="30 fps" :value="30" />
            <el-option label="60 fps" :value="60" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showProjectDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProjectInfo">确定</el-button>
      </template>
    </el-dialog>

    <!-- 导入素材弹窗 -->
    <el-dialog
      v-model="showImportDialog"
      title="导入素材"
      width="600px"
    >
      <el-upload
        drag
        multiple
        :auto-upload="false"
        :on-change="handleFileSelect"
        accept="video/*,audio/*,image/*"
      >
        <el-icon class="upload-icon"><Upload /></el-icon>
        <div class="upload-text">
          拖拽文件到此处，或<em>点击选择</em>
        </div>
        <template #tip>
          <div class="upload-tip">
            支持 MP4, MOV, MP3, WAV, JPG, PNG 格式
          </div>
        </template>
      </el-upload>
      <template #footer>
        <el-button @click="showImportDialog = false">取消</el-button>
        <el-button type="primary" @click="importMaterials" :loading="importing">
          导入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Upload, VideoCamera, ArrowLeft, FolderChecked, Download,
  VideoPlay, VideoPause, DArrowLeft, DArrowRight
} from '@element-plus/icons-vue'

// 状态
const searchKeyword = ref('')
const currentProject = ref(null)
const saving = ref(false)
const exporting = ref(false)
const importing = ref(false)
const showProjectDialog = ref(false)
const showImportDialog = ref(false)
const editingProject = ref(null)
const isPlaying = ref(false)
const currentTime = ref('00:00')
const totalTime = ref('00:00')
const previewVideo = ref('')

const projectForm = reactive({
  name: '',
  resolution: '1080p',
  frameRate: 30
})

// 工程列表
const projects = ref([
  {
    id: 1,
    name: '产品宣传视频',
    duration: '02:35',
    createTime: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
    updateTime: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000),
    resolution: '1080p',
    frameRate: 30
  },
  {
    id: 2,
    name: '公司年会开场',
    duration: '01:20',
    createTime: new Date(Date.now() - 14 * 24 * 60 * 60 * 1000),
    updateTime: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
    resolution: '1080p',
    frameRate: 30
  }
])

// 过滤工程列表
const filteredProjects = computed(() => {
  if (!searchKeyword.value) return projects.value
  return projects.value.filter(p => 
    p.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

// 工程素材
const projectMaterials = ref([])

// 时间线片段
const videoClips = ref([
  { id: 1, name: '片段1', start: 0, duration: 5 },
  { id: 2, name: '片段2', start: 5, duration: 8 }
])

const audioClips = ref([
  { id: 1, name: '背景音乐', start: 0, duration: 13 }
])

const subtitles = ref([
  { id: 1, text: '欢迎观看', start: 0, duration: 3 }
])

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

// 创建新工程
const createNewProject = () => {
  editingProject.value = null
  projectForm.name = ''
  projectForm.resolution = '1080p'
  projectForm.frameRate = 30
  showProjectDialog.value = true
}

// 编辑工程信息
const editProjectInfo = (project) => {
  editingProject.value = project
  projectForm.name = project.name
  projectForm.resolution = project.resolution || '1080p'
  projectForm.frameRate = project.frameRate || 30
  showProjectDialog.value = true
}

// 保存工程信息
const saveProjectInfo = () => {
  if (!projectForm.name) {
    ElMessage.warning('请输入工程名称')
    return
  }

  if (editingProject.value?.id) {
    // 更新
    Object.assign(editingProject.value, projectForm)
    ElMessage.success('工程已更新')
  } else {
    // 新建
    const newProject = {
      id: Date.now(),
      ...projectForm,
      duration: '00:00',
      createTime: new Date(),
      updateTime: new Date()
    }
    projects.value.unshift(newProject)
    ElMessage.success('工程创建成功')
  }
  showProjectDialog.value = false
}

// 打开工程
const openProject = (project) => {
  currentProject.value = { ...project }
  totalTime.value = project.duration || '00:00'
}

// 关闭工程
const closeProject = () => {
  saveProject()
  currentProject.value = null
}

// 保存工程
const saveProject = async () => {
  try {
    saving.value = true
    // 这里调用保存API
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 删除工程
const deleteProject = async (project) => {
  try {
    await ElMessageBox.confirm('确定删除该工程吗？', '确认删除', {
      type: 'warning'
    })
    projects.value = projects.value.filter(p => p.id !== project.id)
    ElMessage.success('删除成功')
  } catch (error) {
    // 取消删除
  }
}

// 导出工程
const exportProject = async () => {
  try {
    exporting.value = true
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  } finally {
    exporting.value = false
  }
}

// 文件选择处理
const handleFileSelect = (file) => {
  console.log('选择文件:', file.name)
}

// 导入素材
const importMaterials = async () => {
  try {
    importing.value = true
    await new Promise(resolve => setTimeout(resolve, 1000))
    projectMaterials.value.push({
      id: Date.now(),
      name: '新素材.mp4',
      duration: '00:10',
      type: 'video'
    })
    ElMessage.success('导入成功')
    showImportDialog.value = false
  } catch (error) {
    ElMessage.error('导入失败')
  } finally {
    importing.value = false
  }
}

// 拖拽开始
const handleDragStart = (event, material) => {
  event.dataTransfer.setData('material', JSON.stringify(material))
}

// 获取片段样式
const getClipStyle = (clip) => {
  const pixelPerSecond = 50
  return {
    left: `${clip.start * pixelPerSecond}px`,
    width: `${clip.duration * pixelPerSecond}px`
  }
}

// 选择片段
const selectClip = (clip) => {
  console.log('选择片段:', clip)
}

// 播放控制
const togglePlay = () => {
  isPlaying.value = !isPlaying.value
}

const skipBackward = () => {
  console.log('后退')
}

const skipForward = () => {
  console.log('前进')
}

onMounted(() => {
  // 加载工程列表
})
</script>

<style scoped>
.project-edit-container {
  padding: 24px;
  min-height: calc(100vh - 80px);
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: #fff;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.projects-section {
  padding: 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
}

.projects-section h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
}

.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.project-card {
  background: rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
}

.project-card:hover {
  transform: translateY(-4px);
  background: rgba(255, 255, 255, 0.12);
}

.project-thumbnail {
  height: 140px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.8);
}

.project-info {
  padding: 16px;
}

.project-info h4 {
  margin: 0 0 8px 0;
  font-size: 16px;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  margin: 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.6);
}

.project-actions {
  padding: 12px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 编辑器样式 */
.editor-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  margin-bottom: 16px;
}

.project-name {
  flex: 1;
}

.project-name :deep(.el-input__wrapper) {
  background: transparent;
  box-shadow: none;
}

.project-name :deep(.el-input__inner) {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.editor-actions {
  display: flex;
  gap: 12px;
}

.editor-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.preview-area {
  display: flex;
  gap: 16px;
}

.video-preview {
  flex: 1;
  height: 360px;
  background: #000;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.preview-video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.preview-placeholder {
  text-align: center;
  color: rgba(255, 255, 255, 0.4);
}

.materials-panel {
  width: 280px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 16px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.materials-list {
  max-height: 280px;
  overflow-y: auto;
}

.material-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: grab;
}

.material-duration {
  margin-left: auto;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

/* 时间线 */
.timeline-area {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 16px;
}

.timeline-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.timeline-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.time-display {
  font-size: 14px;
  font-family: monospace;
}

.timeline-tracks {
  overflow-x: auto;
}

.track {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.track-label {
  width: 60px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

.track-content {
  flex: 1;
  height: 40px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 8px;
  position: relative;
  overflow: hidden;
}

.clip {
  position: absolute;
  height: 100%;
  display: flex;
  align-items: center;
  padding: 0 8px;
  font-size: 12px;
  border-radius: 6px;
  cursor: pointer;
}

.video-clip {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.audio-clip {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.subtitle-clip {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

/* 上传区域 */
.upload-icon {
  font-size: 48px;
  color: #667eea;
  margin-bottom: 16px;
}

.upload-text {
  color: #606266;
}

.upload-text em {
  color: #667eea;
  font-style: normal;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

@media (max-width: 1024px) {
  .preview-area {
    flex-direction: column;
  }

  .materials-panel {
    width: 100%;
  }

  .video-preview {
    height: 240px;
  }
}
</style>
