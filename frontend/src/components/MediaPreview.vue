<template>
  <div class="media-preview-container" :style="{ width: width, height: height }">
    <!-- 加载中状态 -->
    <div v-if="loading" class="media-placeholder">
      <el-skeleton-item variant="image" style="width: 100%; height: 100%" />
    </div>

    <!-- 视频预览 -->
    <template v-if="mediaType === 'video'">
      <div class="video-wrapper">
        <video
          ref="videoRef"
          :src="fullUrl"
          :poster="fullCoverUrl"
          class="preview-video"
          @loadstart="loading = true"
          @canplay="loading = false"
          @error="handleError"
          controls
          playsinline
        ></video>
        <div v-if="error" class="error-overlay">
          <el-icon><CircleClose /></el-icon>
          <span>视频加载失败</span>
        </div>
      </div>
    </template>

    <!-- 图片预览 -->
    <template v-else-if="mediaType === 'image'">
      <el-image
        :src="fullUrl"
        :preview-src-list="previewList"
        fit="cover"
        class="preview-image"
        @load="loading = false"
        @error="handleError"
        hide-on-click-modal
        preview-teleported
      >
        <template #placeholder>
          <div class="media-placeholder">
            <el-icon class="is-loading"><Loading /></el-icon>
          </div>
        </template>
        <template #error>
          <div class="media-placeholder error">
            <el-icon><Picture /></el-icon>
            <span>加载失败</span>
          </div>
        </template>
      </el-image>
    </template>

    <!-- 音频/其他预览 (图标模式) -->
    <template v-else-if="mediaType === 'audio'">
      <div class="audio-preview">
        <el-icon :size="48"><Microphone /></el-icon>
        <audio :src="fullUrl" controls class="preview-audio"></audio>
      </div>
    </template>

    <!-- 未选状态 -->
    <div v-else class="media-placeholder empty">
      <el-icon :size="32"><PictureFilled /></el-icon>
      <p>暂无预览内容</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { CircleClose, Picture, Loading, PictureFilled, Microphone } from '@element-plus/icons-vue'

const props = defineProps({
  url: {
    type: String,
    default: ''
  },
  coverUrl: {
    type: String,
    default: ''
  },
  type: {
    type: String,
    default: '' // image, video, audio 或自动推断
  },
  width: {
    type: String,
    default: '100%'
  },
  height: {
    type: String,
    default: 'auto'
  }
})

const loading = ref(true)
const error = ref(false)
const videoRef = ref(null)

// 统一 URL 解析逻辑
const getFullUrl = (path) => {
  if (!path) return ''
  // 如果是完整的 URL (http/https)，直接返回
  if (/^https?:\/\//i.test(path)) return path
  
  // 处理 localhost 遗留问题
  if (path.startsWith('http://localhost:8080')) {
    path = path.replace('http://localhost:8080', '')
  }

  // 确保以 / 开头
  if (!path.startsWith('/')) {
    path = '/' + path
  }

  // 这里的逻辑应与后端 getUrl 对应
  // 如果路径中没有 /storage/ 或 /profile/，根据需要补全
  // 实际上后端现在返回的应该是 /storage/xxx.mp4
  const apiBase = import.meta.env.VITE_APP_BASE_API || ''
  
  // 走 Vite Proxy 或同域部署
  if (!apiBase || apiBase.startsWith('/')) {
    return path
  }

  // 直连后端模式
  const base = apiBase.replace(/\/api\/v1\/?$/, '')
  return `${base}${path}`
}

const fullUrl = computed(() => getFullUrl(props.url))
const fullCoverUrl = computed(() => getFullUrl(props.coverUrl))
const previewList = computed(() => [fullUrl.value])

// 自动推断媒体类型
const mediaType = computed(() => {
  if (props.type) return props.type.toLowerCase()
  
  const path = props.url ? props.url.toLowerCase() : ''
  if (!path) return ''

  if (/\.(mp4|webm|ogg|mov)$/i.test(path)) return 'video'
  if (/\.(jpg|jpeg|png|gif|webp|bmp|svg)$/i.test(path)) return 'image'
  if (/\.(mp3|wav|ogg|m4a)$/i.test(path)) return 'audio'
  
  return 'image' // 默认作为图片处理
})

const handleError = () => {
  loading.value = false
  error.value = true
}

// 监听 URL 变化重置状态
watch(() => props.url, () => {
  loading.value = true
  error.value = false
})
</script>

<script>
export default {
  name: 'MediaPreview'
}
</script>

<style scoped>
.media-preview-container {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.media-placeholder {
  width: 100%;
  height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #909399;
  background: #f5f7fa;
  gap: 8px;
}

.media-placeholder.error {
  color: #f56c6c;
}

.media-placeholder.empty {
  color: #c0c4cc;
}

.video-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
  background: #000;
  display: flex;
  align-items: center;
}

.preview-video {
  width: 100%;
  max-height: 500px;
  display: block;
}

.preview-image {
  width: 100%;
  height: 100%;
  display: block;
}

.audio-preview {
  padding: 24px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.preview-audio {
  width: 100%;
}

.error-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.7);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

/* 覆盖 el-image 预览容器层级，确保在最上层 */
:deep(.el-image-viewer__wrapper) {
  z-index: 3000 !important;
}
</style>
