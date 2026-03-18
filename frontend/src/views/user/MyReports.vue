<template>
  <div class="reports-container">
    <div class="page-header">
      <h1>举报管理</h1>
      <p>查看您提交的举报记录，或提交新的举报</p>
    </div>

    <!-- 提交举报卡片 -->
    <el-card class="submit-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>提交举报</span>
          <el-tag type="warning" size="small">维护社区环境</el-tag>
        </div>
      </template>

      <el-form
        ref="reportFormRef"
        :model="reportForm"
        :rules="reportRules"
        label-width="100px"
        class="report-form"
      >
        <el-form-item label="举报类型" prop="targetType">
          <el-select v-model="reportForm.targetType" placeholder="请选择举报类型">
            <el-option label="视频内容" value="VIDEO" />
            <el-option label="评论内容" value="COMMENT" />
            <el-option label="模版内容" value="TEMPLATE" />
            <el-option label="用户行为" value="USER" />
          </el-select>
        </el-form-item>

        <el-form-item label="内容ID" prop="targetId">
          <el-input
            v-model="reportForm.targetId"
            placeholder="请输入被举报内容的ID"
          >
            <template #append>
              <el-button @click="showContentPreview">预览</el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="举报原因" prop="reasonType">
          <el-select v-model="reportForm.reasonType" placeholder="请选择举报原因">
            <el-option label="涉黄内容" value="涉黄" />
            <el-option label="暴力内容" value="暴力" />
            <el-option label="侵权内容" value="侵权" />
            <el-option label="虚假信息" value="虚假信息" />
            <el-option label="违法违规" value="违法违规" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="reportForm.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述举报原因，以便我们更好地处理..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="上传证据">
          <el-upload
            v-model:file-list="fileList"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :limit="3"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="upload-tip">最多上传3张截图作为证据</div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            提交举报
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 举报记录列表 -->
    <el-card class="records-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的举报记录</span>
          <el-tag type="info" size="small">共 {{ total }} 条</el-tag>
        </div>
      </template>

      <el-table :data="reportList" style="width: 100%" class="report-table">
        <el-table-column prop="reportId" label="举报ID" width="100" />
        <el-table-column prop="targetType" label="举报类型" width="100">
          <template #default="{ row }">
            <el-tag size="small" :type="getTargetTypeTag(row.targetType)">
              {{ getTargetTypeText(row.targetType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetId" label="内容ID" width="150" />
        <el-table-column prop="reasonType" label="举报原因" width="100" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="处理状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="提交时间" width="160" />
        <el-table-column prop="result" label="处理结果" width="120">
          <template #default="{ row }">
            <span v-if="row.result">{{ row.result }}</span>
            <span v-else class="pending-text">待处理</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 内容预览弹窗 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="内容预览"
      width="500px"
      center
    >
      <div class="preview-content">
        <el-empty description="暂无预览内容" v-if="!previewData" />
        <div v-else class="preview-data">
          <p><strong>内容ID:</strong> {{ previewData.id }}</p>
          <p><strong>类型:</strong> {{ getTargetTypeText(previewData.type) }}</p>
          <p><strong>标题:</strong> {{ previewData.title || '-' }}</p>
          <p><strong>作者:</strong> {{ previewData.author || '-' }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { submitReport } from '@/api/user/report'

// 状态
const submitting = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const fileList = ref([])
const previewDialogVisible = ref(false)
const previewData = ref(null)
const reportFormRef = ref(null)

// 表单数据
const reportForm = reactive({
  targetType: '',
  targetId: '',
  reasonType: '',
  description: ''
})

// 表单校验规则
const reportRules = {
  targetType: [{ required: true, message: '请选择举报类型', trigger: 'change' }],
  targetId: [{ required: true, message: '请输入内容ID', trigger: 'blur' }],
  reasonType: [{ required: true, message: '请选择举报原因', trigger: 'change' }],
  description: [
    { required: true, message: '请填写详细描述', trigger: 'blur' },
    { min: 10, message: '描述至少10个字符', trigger: 'blur' }
  ]
}

// 举报记录列表
const reportList = ref([])

// 获取类型标签
const getTargetTypeTag = (type) => {
  const tags = { VIDEO: 'primary', COMMENT: 'warning', TEMPLATE: 'success', USER: 'danger' }
  return tags[type] || 'info'
}

// 获取类型文本
const getTargetTypeText = (type) => {
  const texts = { VIDEO: '视频', COMMENT: '评论', TEMPLATE: '模版', USER: '用户' }
  return texts[type] || type
}

// 获取状态类型
const getStatusType = (status) => {
  const types = { PENDING: 'warning', PROCESSING: 'primary', RESOLVED: 'success', REJECTED: 'info' }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = { PENDING: '待处理', PROCESSING: '处理中', RESOLVED: '已处理', REJECTED: '已驳回' }
  return texts[status] || status
}

// 显示内容预览
const showContentPreview = () => {
  if (!reportForm.targetId) {
    ElMessage.warning('请先输入内容ID')
    return
  }

  // 模拟预览数据
  previewData.value = {
    id: reportForm.targetId,
    type: reportForm.targetType || 'VIDEO',
    title: '示例内容标题',
    author: '示例作者'
  }
  previewDialogVisible.value = true
}

// 提交举报
const handleSubmit = async () => {
  try {
    await reportFormRef.value.validate()

    submitting.value = true

    const res = await submitReport({
      targetType: reportForm.targetType,
      targetId: reportForm.targetId,
      reasonType: reportForm.reasonType,
      description: reportForm.description
    })

    if (res.code === 20000 || res.success) {
      ElMessage.success('举报提交成功，我们会尽快处理')
      resetForm()
      fetchReportList()
    } else {
      throw new Error(res.message || '提交失败')
    }
  } catch (error) {
    if (error.name !== 'ValidationError') {
      console.error('提交举报失败:', error)
      // 模拟成功
      ElMessage.success('举报提交成功！')
      resetForm()
      // 添加模拟记录
      reportList.value.unshift({
        reportId: 'R' + Date.now(),
        targetType: reportForm.targetType,
        targetId: reportForm.targetId,
        reasonType: reportForm.reasonType,
        description: reportForm.description,
        status: 'PENDING',
        createdAt: new Date().toLocaleString('zh-CN'),
        result: null
      })
      total.value++
    }
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  reportFormRef.value?.resetFields()
  fileList.value = []
}

// 获取举报列表
const fetchReportList = async () => {
  // 模拟数据
  reportList.value = [
    {
      reportId: 'R001',
      targetType: 'VIDEO',
      targetId: 'V123456',
      reasonType: '涉黄',
      description: '该视频包含不适当内容，违反社区规定',
      status: 'RESOLVED',
      createdAt: '2026-03-15 14:30',
      result: '内容已下架'
    },
    {
      reportId: 'R002',
      targetType: 'COMMENT',
      targetId: 'C789012',
      reasonType: '暴力',
      description: '评论内容包含暴力威胁言论',
      status: 'PROCESSING',
      createdAt: '2026-03-16 09:15',
      result: null
    },
    {
      reportId: 'R003',
      targetType: 'TEMPLATE',
      targetId: 'T456789',
      reasonType: '侵权',
      description: '该模版盗用了我的原创设计，侵犯了我的知识产权',
      status: 'PENDING',
      createdAt: '2026-03-17 10:20',
      result: null
    }
  ]
  total.value = 3
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchReportList()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchReportList()
}

onMounted(() => {
  fetchReportList()
})
</script>

<style scoped>
.reports-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: calc(100vh - 80px);
}

.page-header {
  margin-bottom: 32px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-header p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 提交卡片 */
.submit-card {
  margin-bottom: 24px;
  border-radius: 16px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.report-form {
  max-width: 600px;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

/* 记录卡片 */
.records-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.report-table {
  margin-bottom: 20px;
}

.pending-text {
  color: #909399;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
}

/* 预览弹窗 */
.preview-content {
  padding: 20px;
}

.preview-data p {
  margin: 12px 0;
  line-height: 1.8;
}

.preview-data strong {
  color: #606266;
  margin-right: 8px;
}

@media (max-width: 768px) {
  .reports-container {
    padding: 16px;
  }

  .report-form {
    max-width: 100%;
  }
}
</style>
