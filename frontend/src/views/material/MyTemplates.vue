<template>
  <div class="my-templates-container">
    <!-- 页面标题卡片 -->
    <div class="page-header-card">
      <div class="header-content">
        <div class="header-icon">
          <el-icon size="32" color="#fff"><MagicStick /></el-icon>
        </div>
        <div class="header-text">
          <h2>我的模板</h2>
          <p>管理您上传和创作的个人视频模板</p>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <el-card class="main-content-card">
      <!-- 模板列表 -->
      <div class="template-list" v-loading="loading">
        <div v-if="templateList.length === 0" class="empty-state">
          <el-icon size="64" color="#c0c4cc"><FolderOpened /></el-icon>
          <p>您还没有上传过模板</p>
          <el-button type="primary" @click="$router.push('/material/templates')">去上传</el-button>
        </div>
        
        <el-table v-else :data="templateList" style="width: 100%" stripe>
          <el-table-column label="预览" width="120" align="center">
            <template #default="{ row }">
              <div class="preview-thumb">
                <el-image 
                  v-if="row.previewImagePath" 
                  :src="row.previewImagePath" 
                  fit="cover"
                  class="thumb-img"
                />
                <div v-else class="template-icon">
                  <el-icon :size="36"><VideoCamera /></el-icon>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="templateName" label="模板名称" min-width="180" show-overflow-tooltip />

          <el-table-column label="状态" width="120" align="center">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">
                {{ row.status === 1 ? '已启用' : '草稿' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
            <template #default="{ row }">
              {{ formatDate(row.createdAt) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="150" align="center" fixed="right">
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

        <!-- 分页 -->
        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.size"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="loadTemplates"
            @current-change="loadTemplates"
          />
        </div>
      </div>
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" title="模板预览" width="800px" destroy-on-close>
      <div v-if="currentTemplate" class="preview-content">
        <video 
          v-if="currentTemplate.templateFilePath" 
          :src="currentTemplate.templateFilePath" 
          controls 
          autoplay 
          class="preview-video"
        ></video>
        <div v-else class="no-video">暂无视频文件</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MagicStick, VideoCamera, FolderOpened, View, Delete } from '@element-plus/icons-vue'
import { getMyTemplates, deleteTemplate } from '@/api/user/material'

const loading = ref(false)
const templateList = ref([])
const total = ref(0)
const queryParams = reactive({
  page: 1,
  size: 10
})

const previewVisible = ref(false)
const currentTemplate = ref(null)

const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

const loadTemplates = async () => {
  try {
    loading.value = true
    const res = await getMyTemplates(queryParams)
    templateList.value = res.records || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载我的模板失败:', error)
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const handlePreview = (row) => {
  currentTemplate.value = row
  previewVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除模板 "${row.templateName}"？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteTemplate(row.id)
    ElMessage.success('删除成功')
    loadTemplates()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除模板失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadTemplates()
})
</script>

<style scoped>
.my-templates-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  color: #fff;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-icon {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-text h2 {
  margin: 0;
  font-size: 1.6rem;
}

.header-text p {
  margin: 4px 0 0 0;
  opacity: 0.8;
}

.main-content-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.preview-thumb {
  width: 80px;
  height: 45px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin: 0 auto;
}

.thumb-img {
  width: 100%;
  height: 100%;
}

.template-icon {
  color: #764ba2;
}

.empty-state {
  padding: 80px 0;
  text-align: center;
  color: #909399;
}

.empty-state p {
  margin: 16px 0;
}

.pagination-container {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}

.preview-content {
  display: flex;
  justify-content: center;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}

.preview-video {
  max-width: 100%;
  max-height: 60vh;
}

.no-video {
  padding: 100px;
  color: #fff;
}
</style>
