<template>
  <div class="admin-page">
    <div class="page-header">
      <div>
        <h2>审核规则配置</h2>
        <p class="subtitle">配置文本敏感词、图片鉴黄等自动审核策略</p>
      </div>
      <el-button type="primary" @click="openEditDialog()">
        <el-icon><Plus /></el-icon>
        新增规则
      </el-button>
    </div>

    <el-card shadow="never">
      <el-form :inline="true" :model="searchForm" class="filter-form">
        <el-form-item label="规则类型">
          <el-select v-model="searchForm.ruleType" placeholder="全部" clearable>
            <el-option label="全部" value="" />
            <el-option label="文本敏感" value="TEXT_SENSITIVE" />
            <el-option label="图片鉴黄" value="IMAGE_PORN" />
            <el-option label="版权检测" value="COPYRIGHT" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadRules">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="ruleList"
        border
        stripe
      >
        <el-table-column prop="ruleId" label="规则ID" width="100" />
        <el-table-column prop="ruleName" label="规则名称" min-width="180" />
        <el-table-column prop="ruleType" label="类型" width="140" />
        <el-table-column prop="priority" label="优先级" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLE' ? 'success' : 'info'" size="small">
              {{ row.status === 'ENABLE' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="openEditDialog(row)">
              编辑
            </el-button>
            <el-button
              link
              :type="row.status === 'ENABLE' ? 'warning' : 'success'"
              size="small"
              @click="toggleStatus(row)"
            >
              {{ row.status === 'ENABLE' ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="editDialogVisible"
      :title="editForm.ruleId ? '编辑规则' : '新增规则'"
      width="520px"
    >
      <el-form
        ref="editFormRef"
        :model="editForm"
        :rules="editRules"
        label-width="90px"
      >
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="editForm.ruleName" />
        </el-form-item>
        <el-form-item label="规则类型" prop="ruleType">
          <el-select v-model="editForm.ruleType">
            <el-option label="文本敏感" value="TEXT_SENSITIVE" />
            <el-option label="图片鉴黄" value="IMAGE_PORN" />
            <el-option label="版权检测" value="COPYRIGHT" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-input-number v-model="editForm.priority" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="条件 JSON" prop="conditions">
          <el-input
            v-model="editForm.conditions"
            type="textarea"
            :rows="4"
            placeholder='如：{"threshold": 80}'
          />
        </el-form-item>
        <el-form-item label="动作 JSON" prop="actions">
          <el-input
            v-model="editForm.actions"
            type="textarea"
            :rows="3"
            placeholder='如：{"action": "REJECT"}'
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="saving" @click="submitRule">
          保 存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  fetchReviewRules,
  saveReviewRule,
  updateReviewRuleStatus
} from '@/api/admin/reviewRules'

const loading = ref(false)
const ruleList = ref([])

const searchForm = reactive({
  ruleType: ''
})

const editDialogVisible = ref(false)
const editFormRef = ref()
const editForm = reactive({
  ruleId: null,
  ruleName: '',
  ruleType: '',
  conditions: '',
  actions: '',
  priority: 5
})
const saving = ref(false)

const editRules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  ruleType: [{ required: true, message: '请选择规则类型', trigger: 'change' }],
  conditions: [{ required: true, message: '请输入条件 JSON', trigger: 'blur' }],
  actions: [{ required: true, message: '请输入动作 JSON', trigger: 'blur' }]
}

const loadRules = async () => {
  loading.value = true
  try {
    const res = await fetchReviewRules({
      ruleType: searchForm.ruleType || undefined
    })
    ruleList.value = res.data?.list || []
  } catch (e) {
    ElMessage.error('加载规则列表失败')
  } finally {
    loading.value = false
  }
}

const openEditDialog = (row) => {
  if (row) {
    editForm.ruleId = row.ruleId
    editForm.ruleName = row.ruleName
    editForm.ruleType = row.ruleType
    editForm.priority = row.priority
    editForm.conditions = JSON.stringify(row.conditions || {}, null, 2)
    editForm.actions = JSON.stringify(row.actions || {}, null, 2)
  } else {
    editForm.ruleId = null
    editForm.ruleName = ''
    editForm.ruleType = ''
    editForm.priority = 5
    editForm.conditions = ''
    editForm.actions = ''
  }
  editDialogVisible.value = true
}

const submitRule = () => {
  editFormRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      let conditionsObj
      let actionsObj
      try {
        conditionsObj = JSON.parse(editForm.conditions || '{}')
        actionsObj = JSON.parse(editForm.actions || '{}')
      } catch (e) {
        ElMessage.error('条件或动作 JSON 格式不正确')
        saving.value = false
        return
      }

      await saveReviewRule({
        ruleId: editForm.ruleId || undefined,
        ruleName: editForm.ruleName,
        ruleType: editForm.ruleType,
        conditions: conditionsObj,
        actions: actionsObj,
        priority: editForm.priority
      })
      ElMessage.success('保存成功')
      editDialogVisible.value = false
      loadRules()
    } catch (e) {
      ElMessage.error('保存失败')
    } finally {
      saving.value = false
    }
  })
}

const toggleStatus = async (row) => {
  const targetStatus = row.status === 'ENABLE' ? 'DISABLE' : 'ENABLE'
  try {
    await updateReviewRuleStatus(row.ruleId, { status: targetStatus })
    ElMessage.success('状态已更新')
    loadRules()
  } catch (e) {
    ElMessage.error('更新规则状态失败')
  }
}

onMounted(() => {
  loadRules()
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
</style>

