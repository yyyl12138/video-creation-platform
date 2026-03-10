<template>
  <div class="admin-page">
    <div class="page-header">
      <div>
        <h2>内容审核</h2>
        <p class="subtitle">查看待审核作品，支持通过 / 驳回并记录原因</p>
      </div>
      <el-space>
        <el-select
          v-model="searchForm.status"
          placeholder="审核状态"
          style="width: 150px"
        >
          <el-option label="待审核" value="PENDING" />
          <el-option label="已通过" value="PASSED" />
          <el-option label="已驳回" value="REJECTED" />
        </el-select>
        <el-select
          v-model="searchForm.contentType"
          placeholder="内容类型"
          clearable
          style="width: 140px"
        >
          <el-option label="全部" value="" />
          <el-option label="视频" value="VIDEO" />
          <el-option label="图片" value="IMAGE" />
        </el-select>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
      </el-space>
    </div>

    <el-card shadow="never">
      <el-table
        v-loading="loading"
        :data="reviewList"
        border
        stripe
      >
        <el-table-column prop="reviewId" label="审核ID" width="120" />
        <el-table-column prop="contentId" label="内容ID" min-width="140" />
        <el-table-column prop="creatorName" label="创作者" width="120" />
        <el-table-column prop="submitTime" label="提交时间" min-width="160" />
        <el-table-column prop="machineCheckResult" label="机审结果" width="140">
          <template #default="{ row }">
            <el-tag
              size="small"
              :type="row.machineCheckResult === 'RISK_HIGH' ? 'danger' : 'warning'"
            >
              {{ row.machineCheckResult || 'UNKNOWN' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="审核状态" width="120">
          <template #default="{ row }">
            <el-tag
              size="small"
              :type="row.status === 'PENDING'
                ? 'warning'
                : row.status === 'PASSED'
                  ? 'success'
                  : 'danger'"
            >
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="openDetail(row)"
            >
              详情审核
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
          @current-change="loadReviews"
          @size-change="loadReviews"
        />
      </div>
    </el-card>

    <!-- 审核详情 + 决策 -->
    <el-drawer
      v-model="detailVisible"
      title="审核详情"
      size="60%"
    >
      <div v-if="currentDetail">
        <el-row :gutter="20">
          <el-col :span="14">
            <el-card shadow="never" class="section-card">
              <h3 class="section-title">内容预览</h3>
              <p><strong>标题：</strong>{{ currentDetail.content?.title }}</p>
              <p><strong>描述：</strong>{{ currentDetail.content?.description }}</p>
              <p><strong>地址：</strong>{{ currentDetail.content?.url }}</p>
            </el-card>
          </el-col>
          <el-col :span="10">
            <el-card shadow="never" class="section-card">
              <h3 class="section-title">历史记录</h3>
              <el-timeline v-if="currentDetail.history?.length">
                <el-timeline-item
                  v-for="(item, index) in currentDetail.history"
                  :key="index"
                  :timestamp="item.reviewTime"
                >
                  {{ item.reviewerName }} - {{ item.action }} - {{ item.reason }}
                </el-timeline-item>
              </el-timeline>
              <el-empty v-else description="暂无历史记录" />
            </el-card>
          </el-col>
        </el-row>

        <el-card shadow="never" class="section-card decision-card">
          <h3 class="section-title">审核决策</h3>
          <el-form
            ref="decisionFormRef"
            :model="decisionForm"
            :rules="decisionRules"
            label-width="90px"
          >
            <el-form-item label="审核结果" prop="status">
              <el-radio-group v-model="decisionForm.status">
                <el-radio label="PASSED">通过</el-radio>
                <el-radio label="REJECTED">驳回</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item
              label="驳回原因"
              prop="rejectReason"
              v-if="decisionForm.status === 'REJECTED'"
            >
              <el-input
                v-model="decisionForm.rejectReason"
                type="textarea"
                :rows="3"
                placeholder="请详细说明驳回原因"
              />
            </el-form-item>
            <el-form-item label="修改建议">
              <el-input
                v-model="decisionForm.suggestions"
                type="textarea"
                :rows="2"
                placeholder="可选，给创作者的修改意见"
              />
            </el-form-item>
          </el-form>
          <div class="decision-actions">
            <el-button @click="detailVisible = false">关闭</el-button>
            <el-button
              type="primary"
              :loading="decisionSubmitting"
              @click="submitDecision"
            >
              提交审核结果
            </el-button>
          </div>
        </el-card>
      </div>
      <el-empty v-else description="请选择一条审核任务" />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  fetchReviewList,
  getReviewDetail,
  submitReviewDecision
} from '@/api/admin/reviews'

const loading = ref(false)
const reviewList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)

const searchForm = reactive({
  status: 'PENDING',
  contentType: ''
})

const detailVisible = ref(false)
const currentDetail = ref(null)

const decisionFormRef = ref()
const decisionForm = reactive({
  reviewId: null,
  status: 'PASSED',
  rejectReason: '',
  suggestions: ''
})
const decisionSubmitting = ref(false)

const decisionRules = {
  status: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  rejectReason: [
    {
      required: true,
      message: '请输入驳回原因',
      trigger: 'blur',
      validator: (_, value, callback) => {
        if (decisionForm.status === 'REJECTED' && !value) {
          callback(new Error('请输入驳回原因'))
        } else {
          callback()
        }
      }
    }
  ]
}

const loadReviews = async () => {
  loading.value = true
  try {
    const res = await fetchReviewList({
      page: page.value,
      size: pageSize.value,
      status: searchForm.status || undefined,
      contentType: searchForm.contentType || undefined
    })
    reviewList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error('加载审核列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadReviews()
}

const openDetail = async (row) => {
  try {
    const res = await getReviewDetail(row.reviewId)
    currentDetail.value = res.data || null
    decisionForm.reviewId = row.reviewId
    decisionForm.status = 'PASSED'
    decisionForm.rejectReason = ''
    decisionForm.suggestions = ''
    detailVisible.value = true
  } catch (e) {
    ElMessage.error('获取审核详情失败')
  }
}

const submitDecision = () => {
  if (!decisionForm.reviewId) {
    ElMessage.warning('请选择审核任务')
    return
  }
  decisionFormRef.value.validate(async (valid) => {
    if (!valid) return
    decisionSubmitting.value = true
    try {
      await submitReviewDecision(decisionForm.reviewId, {
        status: decisionForm.status,
        rejectReason: decisionForm.status === 'REJECTED' ? decisionForm.rejectReason : undefined,
        suggestions: decisionForm.suggestions || undefined
      })
      ElMessage.success('审核结果已提交')
      detailVisible.value = false
      loadReviews()
    } catch (e) {
      ElMessage.error('提交审核结果失败')
    } finally {
      decisionSubmitting.value = false
    }
  })
}

onMounted(() => {
  loadReviews()
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

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.section-card {
  margin-bottom: 16px;
}

.section-title {
  margin: 0 0 12px;
  font-size: 14px;
  font-weight: 600;
}

.decision-card {
  margin-top: 12px;
}

.decision-actions {
  text-align: right;
  margin-top: 12px;
}
</style>
