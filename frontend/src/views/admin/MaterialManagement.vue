<template>
  <div class="admin-page">
    <div class="page-header">
      <div>
        <h2>素材管理</h2>
        <p class="subtitle">管理系统素材与用户素材，支持审核、封禁与版权标记</p>
      </div>
      <el-button type="primary" @click="openUploadDialog">
        <el-icon><Upload /></el-icon>
        上传系统素材
      </el-button>
    </div>

    <el-card shadow="never">
      <el-form
        :inline="true"
        :model="searchForm"
        class="filter-form"
      >
        <el-form-item label="素材类型">
          <el-select v-model="searchForm.type" placeholder="全部" style="width: 140px">
            <el-option label="全部" value="" />
            <el-option label="图片" value="IMAGE" />
            <el-option label="视频" value="VIDEO" />
            <el-option label="音频" value="AUDIO" />
          </el-select>
        </el-form-item>
        <el-form-item label="来源">
          <el-select v-model="searchForm.isSystem" placeholder="全部" style="width: 140px">
            <el-option label="全部" :value="undefined" />
            <el-option label="系统素材" :value="true" />
            <el-option label="用户素材" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="materialList"
        border
        stripe
      >
        <el-table-column prop="materialId" label="素材ID" min-width="140" />
        <el-table-column prop="name" label="名称" min-width="160" />
        <el-table-column prop="type" label="类型" width="90" />
        <el-table-column prop="sourceType" label="来源" width="110">
          <template #default="{ row }">
            <el-tag size="small" :type="row.sourceType === 'SYSTEM' ? 'success' : 'info'">
              {{ row.sourceType === 'SYSTEM' ? '系统' : '用户上传' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="copyrightStatus" label="版权" width="140">
          <template #default="{ row }">
            <el-tag
              size="small"
              :type="row.copyrightStatus === 'FREE_COMMERCIAL'
                ? 'success'
                : row.copyrightStatus === 'PAID'
                  ? 'warning'
                  : 'info'"
            >
              {{ row.copyrightStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="previewMaterial(row)"
            >
              预览
            </el-button>
            <el-button
              link
              type="warning"
              size="small"
              @click="openStatusDialog(row)"
            >
              状态审核
            </el-button>
            <el-button
              link
              type="info"
              size="small"
              @click="openCopyrightDialog(row)"
            >
              版权标记
            </el-button>
            <el-button
              link
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="total"
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          @current-change="loadMaterials"
          @size-change="loadMaterials"
        />
      </div>
    </el-card>

    <!-- 上传系统素材 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传系统素材"
      width="520px"
    >
      <el-form
        ref="uploadFormRef"
        :model="uploadForm"
        :rules="uploadRules"
        label-width="90px"
      >
        <el-form-item label="素材文件" prop="file">
          <el-upload
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              选择文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                支持图片 / 视频 / 音频文件
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="素材类型" prop="type">
          <el-select v-model="uploadForm.type" placeholder="请选择类型">
            <el-option label="图片" value="IMAGE" />
            <el-option label="视频" value="VIDEO" />
            <el-option label="音频" value="AUDIO" />
          </el-select>
        </el-form-item>
        <el-form-item label="版权状态" prop="copyright">
          <el-select v-model="uploadForm.copyright">
            <el-option label="免费商用" value="FREE_COMMERCIAL" />
            <el-option label="付费授权" value="PAID" />
            <el-option label="个人使用" value="PERSONAL_USE" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-input v-model="uploadForm.category" placeholder="如：风景 / 科技" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="uploadForm.tags" placeholder="多个标签用逗号分隔" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="uploading" @click="submitUpload">
          确 定
        </el-button>
      </template>
    </el-dialog>

    <!-- 状态审核 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="素材状态审核"
      width="420px"
    >
      <el-form
        ref="statusFormRef"
        :model="statusForm"
        :rules="statusRules"
        label-width="90px"
      >
        <el-form-item label="素材ID">
          <span>{{ statusForm.materialId }}</span>
        </el-form-item>
        <el-form-item label="素材类型">
          <span>{{ statusForm.type }}</span>
        </el-form-item>
        <el-form-item label="目标状态" prop="status">
          <el-select v-model="statusForm.status">
            <el-option label="正常" value="NORMAL" />
            <el-option label="封禁" value="BANNED" />
            <el-option label="审核中" value="REVIEWING" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作原因" prop="reason">
          <el-input
            v-model="statusForm.reason"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="statusSubmitting" @click="submitStatus">
          提 交
        </el-button>
      </template>
    </el-dialog>

    <!-- 版权标记 -->
    <el-dialog
      v-model="copyrightDialogVisible"
      title="标记素材版权"
      width="400px"
    >
      <el-form
        ref="copyrightFormRef"
        :model="copyrightForm"
        :rules="copyrightRules"
        label-width="90px"
      >
        <el-form-item label="素材ID">
          <span>{{ copyrightForm.materialId }}</span>
        </el-form-item>
        <el-form-item label="素材类型">
          <span>{{ copyrightForm.type }}</span>
        </el-form-item>
        <el-form-item label="版权状态" prop="copyright">
          <el-select v-model="copyrightForm.copyright">
            <el-option label="免费商用" value="FREE_COMMERCIAL" />
            <el-option label="付费授权" value="PAID" />
            <el-option label="个人使用" value="PERSONAL_USE" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="copyrightDialogVisible = false">
          取 消
        </el-button>
        <el-button type="primary" :loading="copyrightSubmitting" @click="submitCopyright">
          确 定
        </el-button>
      </template>
    </el-dialog>

    <!-- 预览占位 -->
    <el-dialog v-model="previewVisible" title="素材预览" width="600px">
      <div v-if="currentPreview">
        <p>素材ID：{{ currentPreview.materialId }}</p>
        <p>名称：{{ currentPreview.name }}</p>
        <p>类型：{{ currentPreview.type }}</p>
        <p>URL：{{ currentPreview.url }}</p>
      </div>
      <el-empty v-else description="暂无预览数据" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Upload } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMaterials, deleteMaterial } from '@/api/user/material'
import {
  uploadSystemMaterial,
  updateMaterialStatus,
  updateMaterialCopyright
} from '@/api/admin/materials'

const loading = ref(false)
const materialList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

const searchForm = reactive({
  type: '',
  isSystem: undefined
})

const uploadDialogVisible = ref(false)
const uploadFormRef = ref()
const uploadForm = reactive({
  file: null,
  type: '',
  copyright: '',
  category: '',
  tags: ''
})
const uploadRules = {
  file: [{ required: true, message: '请上传素材文件', trigger: 'change' }],
  type: [{ required: true, message: '请选择素材类型', trigger: 'change' }],
  copyright: [{ required: true, message: '请选择版权状态', trigger: 'change' }]
}
const uploading = ref(false)

const statusDialogVisible = ref(false)
const statusFormRef = ref()
const statusForm = reactive({
  materialId: '',
  type: '',
  status: 'NORMAL',
  reason: ''
})
const statusRules = {
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}
const statusSubmitting = ref(false)

const copyrightDialogVisible = ref(false)
const copyrightFormRef = ref()
const copyrightForm = reactive({
  materialId: '',
  type: '',
  copyright: ''
})
const copyrightRules = {
  copyright: [{ required: true, message: '请选择版权状态', trigger: 'change' }]
}
const copyrightSubmitting = ref(false)

const previewVisible = ref(false)
const currentPreview = ref(null)

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定删除素材「${row.name}」吗？此操作不可恢复`,
      '提示',
      { type: 'warning' }
    )
    const res = await deleteMaterial(row.materialId, { type: row.type })
    // 如果后端有统一的 code，可以在这里判断；没有的话直接当成功处理
    if (!res || res.code === undefined || res.code === 20000) {
      ElMessage.success('删除成功')
      // 本地更新列表
      materialList.value = materialList.value.filter(
        (item) => item.materialId !== row.materialId
      )
      total.value = Math.max(0, total.value - 1)
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (e) {
    // 用户取消不提示错误
  }
}

const loadMaterials = async () => {
  loading.value = true
  try {
    const res = await getMaterials({
      page: page.value,
      size: pageSize.value,
      type: searchForm.type || undefined,
      isSystem: searchForm.isSystem
    })
    materialList.value = res.data?.records || res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error('加载素材列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadMaterials()
}

const resetSearch = () => {
  searchForm.type = ''
  searchForm.isSystem = undefined
  handleSearch()
}

const openUploadDialog = () => {
  uploadForm.file = null
  uploadForm.type = ''
  uploadForm.copyright = ''
  uploadForm.category = ''
  uploadForm.tags = ''
  uploadDialogVisible.value = true
}

const handleFileChange = (file) => {
  uploadForm.file = file.raw
}

const submitUpload = () => {
  uploadFormRef.value.validate(async (valid) => {
    if (!valid) return
    uploading.value = true
    try {
      const formData = new FormData()
      formData.append('file', uploadForm.file)
      formData.append('type', uploadForm.type)
      formData.append('copyrightStatus', uploadForm.copyright)
      if (uploadForm.category) formData.append('category', uploadForm.category)
      if (uploadForm.tags) formData.append('tags', uploadForm.tags)

      await uploadSystemMaterial(formData)
      ElMessage.success('上传成功')
      uploadDialogVisible.value = false
      loadMaterials()
    } catch (e) {
      ElMessage.error('上传失败')
    } finally {
      uploading.value = false
    }
  })
}

const openStatusDialog = (row) => {
  statusForm.materialId = row.materialId
  statusForm.type = row.type
  statusForm.status = 'NORMAL'
  statusForm.reason = ''
  statusDialogVisible.value = true
}

const submitStatus = () => {
  statusFormRef.value.validate(async (valid) => {
    if (!valid) return
    statusSubmitting.value = true
    try {
      await updateMaterialStatus(statusForm.materialId, {
        type: statusForm.type,
        status: statusForm.status,
        reason: statusForm.reason
      })
      ElMessage.success('素材状态已更新')
      statusDialogVisible.value = false
      loadMaterials()
    } catch (e) {
      ElMessage.error('更新素材状态失败')
    } finally {
      statusSubmitting.value = false
    }
  })
}

const openCopyrightDialog = (row) => {
  copyrightForm.materialId = row.materialId
  copyrightForm.type = row.type
  copyrightForm.copyright = row.copyrightStatus || ''
  copyrightDialogVisible.value = true
}

const submitCopyright = () => {
  copyrightFormRef.value.validate(async (valid) => {
    if (!valid) return
    copyrightSubmitting.value = true
    try {
      await updateMaterialCopyright(copyrightForm.materialId, {
        type: copyrightForm.type,
        copyrightStatus: copyrightForm.copyright
      })
      ElMessage.success('版权信息已更新')
      copyrightDialogVisible.value = false
      loadMaterials()
    } catch (e) {
      ElMessage.error('更新版权信息失败')
    } finally {
      copyrightSubmitting.value = false
    }
  })
}

const previewMaterial = (row) => {
  currentPreview.value = row
  previewVisible.value = true
}

onMounted(() => {
  loadMaterials()
})
</script>

<style scoped>
.admin-page {
  padding: 4px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6b7280;
}

.filter-form {
  margin-bottom: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>

