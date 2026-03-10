<template>
  <div class="admin-page stats-page">
    <div class="page-header">
      <div>
        <h2>运营看板</h2>
        <p class="subtitle">平台用户、任务与存储等核心指标与趋势</p>
      </div>
    </div>

    <el-row :gutter="16" class="top-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card primary">
          <div class="stat-card-header">
            <h3 class="card-title">用户概况</h3>
            <span class="card-tag">USER</span>
          </div>
          <div v-if="overview" class="stat-card-body">
            <div class="stat-item">
              <div class="stat-label">总用户</div>
              <div class="stat-value large">{{ overview.users.total }}</div>
            </div>
            <div class="stat-row">
              <div class="stat-item">
                <div class="stat-label">今日新增</div>
                <div class="stat-value">{{ overview.users.newToday }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">今日活跃</div>
                <div class="stat-value">{{ overview.users.activeToday }}</div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card secondary">
          <div class="stat-card-header">
            <h3 class="card-title">任务概况</h3>
            <span class="card-tag">TASK</span>
          </div>
          <div v-if="overview" class="stat-card-body">
            <div class="stat-item">
              <div class="stat-label">累计生成</div>
              <div class="stat-value large">{{ overview.tasks.totalGenerated }}</div>
            </div>
            <div class="stat-row">
              <div class="stat-item">
                <div class="stat-label">成功率</div>
                <div class="stat-value">{{ overview.tasks.successRate }}%</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">峰值并发</div>
                <div class="stat-value">{{ overview.tasks.peakConcurrent }}</div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card accent">
          <div class="stat-card-header">
            <h3 class="card-title">存储概况</h3>
            <span class="card-tag">STORAGE</span>
          </div>
          <div v-if="overview" class="stat-card-body">
            <div class="stat-item">
              <div class="stat-label">已用存储</div>
              <div class="stat-value large">
                {{ (overview.storage.totalUsedBytes / (1024*1024*1024)).toFixed(2) }} GB
              </div>
            </div>
            <div class="stat-row">
              <div class="stat-item">
                <div class="stat-label">存储营收</div>
                <div class="stat-value">¥{{ overview.storage.storageRevenue }}</div>
              </div>
            </div>
          </div>
          <el-empty v-else description="暂无数据" />
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="section-card trend-section">
      <div class="section-header">
        <div>
          <h3 class="card-title">趋势数据</h3>
          <p class="section-subtitle">按时间维度查看核心指标变化</p>
        </div>
        <el-space>
          <el-date-picker
            v-model="trendRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
          <el-select v-model="trendMetric" style="width: 180px">
            <el-option label="新增用户" value="NEW_USERS" />
            <el-option label="活跃用户" value="ACTIVE_USERS" />
            <el-option label="任务量" value="TASK_VOLUME" />
          </el-select>
          <el-button type="primary" @click="loadTrend">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-space>
      </div>
      <el-table
        v-loading="trendLoading"
        :data="trendTable"
        size="small"
        border
        class="trend-table"
      >
        <el-table-column prop="date" label="日期" width="140" />
        <el-table-column prop="value" label="数值" />
      </el-table>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never" class="section-card">
          <h3 class="card-title">任务类型分布</h3>
          <el-table
            v-loading="distributionLoading"
            :data="distribution"
            size="small"
            border
          >
            <el-table-column prop="type" label="任务类型" />
            <el-table-column prop="count" label="数量" />
            <el-table-column prop="ratio" label="占比" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="section-card">
          <h3 class="card-title">任务失败原因 Top</h3>
          <el-table
            v-loading="failureLoading"
            :data="failures"
            size="small"
            border
          >
            <el-table-column prop="reason" label="原因" />
            <el-table-column prop="count" label="次数" />
            <el-table-column prop="description" label="描述" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  fetchOverviewStats,
  fetchTrendStats,
  fetchTaskDistribution,
  fetchTaskFailureAnalysis
} from '@/api/admin/stats'

const overview = ref(null)

const trendRange = ref([])
const trendMetric = ref('NEW_USERS')
const trendTable = ref([])
const trendLoading = ref(false)

const distribution = ref([])
const distributionLoading = ref(false)

const failures = ref([])
const failureLoading = ref(false)

const loadOverview = async () => {
  try {
    const res = await fetchOverviewStats()
    overview.value = res.data || null
  } catch (e) {
    ElMessage.error('加载概览数据失败')
  }
}

const loadTrend = async () => {
  if (!trendRange.value || trendRange.value.length !== 2) {
    ElMessage.warning('请选择时间范围')
    return
  }
  trendLoading.value = true
  try {
    const [startDate, endDate] = trendRange.value
    const res = await fetchTrendStats({
      startDate,
      endDate,
      metric: trendMetric.value
    })
    const xAxis = res.data?.xAxis || []
    const series = res.data?.series?.[0]?.data || []
    trendTable.value = xAxis.map((date, index) => ({
      date,
      value: series[index] ?? 0
    }))
  } catch (e) {
    ElMessage.error('加载趋势数据失败')
  } finally {
    trendLoading.value = false
  }
}

const loadDistribution = async () => {
  distributionLoading.value = true
  try {
    const res = await fetchTaskDistribution()
    distribution.value = res.data?.list || []
  } catch (e) {
    ElMessage.error('加载任务分布失败')
  } finally {
    distributionLoading.value = false
  }
}

const loadFailures = async () => {
  failureLoading.value = true
  try {
    const res = await fetchTaskFailureAnalysis()
    failures.value = res.data?.list || []
  } catch (e) {
    ElMessage.error('加载失败原因失败')
  } finally {
    failureLoading.value = false
  }
}

onMounted(() => {
  loadOverview()
  loadDistribution()
  loadFailures()
})
</script>

<style scoped>
.admin-page {
  padding: 8px 4px;
}

.stats-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  margin-bottom: 4px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  /* 高级感：用接近若依的深灰标题色，而不是鲜艳蓝 */
  color: #303133;
}

.subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6b7280;
}

.top-row {
  margin-bottom: 4px;
}

.stat-card {
  position: relative;
  min-height: 150px;
  border-radius: 14px;
  border: 1px solid #dbeafe;
  background: #ffffff;
  box-shadow:
    0 10px 25px rgba(15, 23, 42, 0.05),
    0 0 0 1px rgba(219, 234, 254, 0.6);
  color: #1f2933;
}

.stat-card.primary {
  background: #eff6ff;
}

.stat-card.secondary {
  background: #e0f2fe;
}

.stat-card.accent {
  background: #dbeafe;
}

.stat-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.card-title {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.card-tag {
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid #93c5fd;
  font-size: 11px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: #1d4ed8;
}

.stat-card-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
}

.stat-value {
  margin-top: 2px;
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.stat-value.large {
  font-size: 22px;
}

.section-card {
  margin-bottom: 4px;
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  color: #111827;
}

.trend-section {
  margin-top: 4px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-subtitle {
  margin: 4px 0 0;
  font-size: 12px;
  color: #6b7280;
}

.trend-table :deep(.el-table__cell) {
  background-color: #ffffff;
}
</style>

