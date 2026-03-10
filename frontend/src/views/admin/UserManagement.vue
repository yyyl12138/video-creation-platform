<template>
  <div class="admin-page">
    <div class="page-header">
      <div>
        <h2>用户管理</h2>
        <p class="subtitle">查看平台用户列表，支持封禁 / 解封用户</p>
      </div>
      <el-space>
        <el-input
          v-model="searchForm.keyword"
          placeholder="用户名 / 手机号搜索"
          clearable
          style="width: 220px"
          @keyup.enter.native="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="searchForm.status"
          placeholder="账号状态"
          clearable
          style="width: 140px"
        >
          <el-option label="正常" value="正常" />
          <el-option label="封禁" value="封禁" />
          <el-option label="禁用" value="禁用" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-space>
    </div>

    <el-card shadow="never">
      <el-table
        v-loading="loading"
        :data="userList"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="userId" label="用户ID" min-width="140" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="email" label="邮箱" min-width="160" />
        <el-table-column prop="roleName" label="角色" min-width="100" />
        <el-table-column prop="videoCount" label="生成视频数" width="120" />
        <el-table-column
          prop="status"
          label="状态"
          width="100"
        >
          <template #default="{ row }">
            <el-tag
              :type="row.status === '正常' ? 'success' : 'danger'"
              size="small"
            >
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="注册时间" min-width="160" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>
            <el-button
              link
              type="warning"
              size="small"
              @click="openStatusDialog(row)"
            >
              状态调整
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
          @current-change="loadUsers"
          @size-change="loadUsers"
        />
      </div>
    </el-card>

    <!-- 用户详情抽屉 -->
    <el-drawer
      v-model="detailVisible"
      title="用户详情"
      size="400px"
    >
      <el-descriptions
        v-if="currentDetail"
        :column="1"
        border
        size="small"
      >
        <el-descriptions-item label="用户ID">
          {{ currentDetail.userId }}
        </el-descriptions-item>
        <el-descriptions-item label="用户名">
          {{ currentDetail.username }}
        </el-descriptions-item>
        <el-descriptions-item label="最近登录时间">
          {{ currentDetail.lastLoginTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="最近登录IP">
          {{ currentDetail.lastLoginIp || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="钱包余额">
          ¥{{ currentDetail.walletBalance ?? '-' }}
        </el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="暂无数据" />
    </el-drawer>

    <!-- 状态调整弹窗 -->
    <el-dialog
      v-model="statusDialogVisible"
      title="调整用户状态"
      width="420px"
    >
      <el-form
        ref="statusFormRef"
        :model="statusForm"
        :rules="statusRules"
        label-width="80px"
      >
        <el-form-item label="当前用户">
          <span>{{ statusForm.username }}</span>
        </el-form-item>
        <el-form-item label="目标状态" prop="status">
          <el-select v-model="statusForm.status" placeholder="请选择状态">
            <el-option label="正常" value="正常" />
            <el-option label="封禁" value="封禁" />
            <el-option label="禁用" value="禁用" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作原因" prop="reason">
          <el-input
            v-model="statusForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请填写本次操作原因，便于后续审计"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="statusSubmitting" @click="submitStatus">
          确 定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  fetchAdminUsers,
  updateUserStatus,
  getAdminUserDetail
} from '@/api/admin/users'

const loading = ref(false)
const userList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

const searchForm = reactive({
  keyword: '',
  status: ''
})

const detailVisible = ref(false)
const currentDetail = ref(null)

const statusDialogVisible = ref(false)
const statusSubmitting = ref(false)
const statusFormRef = ref()
const statusForm = reactive({
  userId: '',
  username: '',
  status: '',
  reason: ''
})

const statusRules = {
  status: [{ required: true, message: '请选择目标状态', trigger: 'change' }],
  reason: [{ required: false, message: '请输入原因', trigger: 'blur' }]
}

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await fetchAdminUsers({
      page: page.value,
      size: pageSize.value,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status || undefined
    })
    // 接口文档返回 { total, records: [] }
    userList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadUsers()
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  handleSearch()
}

const handleViewDetail = async (row) => {
  try {
    const res = await getAdminUserDetail(row.userId)
    currentDetail.value = res.data || null
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('获取用户详情失败')
  }
}

const openStatusDialog = (row) => {
  statusForm.userId = row.userId
  statusForm.username = row.username
  statusForm.status = row.status
  statusForm.reason = ''
  statusDialogVisible.value = true
}

const submitStatus = () => {
  statusFormRef.value.validate(async (valid) => {
    if (!valid) return
    statusSubmitting.value = true
    try {
      await updateUserStatus(statusForm.userId, {
        status: statusForm.status,
        reason: statusForm.reason
      })
      ElMessage.success('用户状态已更新')
      statusDialogVisible.value = false
      loadUsers()
    } catch (e) {
      ElMessage.error('更新用户状态失败')
    } finally {
      statusSubmitting.value = false
    }
  })
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.admin-page {
  padding: 4px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
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

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>

