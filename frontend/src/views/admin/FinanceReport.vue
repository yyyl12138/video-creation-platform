<template>
  <div class="admin-page finance-page">
    <div class="page-header">
      <div>
        <h2>财务报表</h2>
        <p class="subtitle">按日 / 周 / 月查看营收与成本情况</p>
      </div>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="queryForm" class="filter-form">
        <el-form-item label="统计周期">
          <el-select v-model="queryForm.period" style="width: 140px">
            <el-option label="按日" value="daily" />
            <el-option label="按周" value="weekly" />
            <el-option label="按月" value="monthly" />
          </el-select>
        </el-form-item>
        <el-form-item label="统计日期">
          <el-date-picker
            v-model="queryForm.date"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadFinance">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="finance" shadow="never" class="summary-card">
      <h3 class="card-title">核心指标</h3>
      <el-descriptions :column="4" border size="small" class="summary-desc">
        <el-descriptions-item label="总营收">
          ¥{{ finance.totalRevenue }}
        </el-descriptions-item>
        <el-descriptions-item label="总支出">
          ¥{{ finance.totalExpense }}
        </el-descriptions-item>
        <el-descriptions-item label="净收入">
          ¥{{ finance.netIncome }}
        </el-descriptions-item>
        <el-descriptions-item label="付费会员数">
          {{ finance.premiumUserCount }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card v-if="finance" shadow="never" class="section-card">
      <h3 class="card-title">营收构成</h3>
      <el-table :data="compositionTable" size="small" border>
        <el-table-column prop="name" label="项目" />
        <el-table-column prop="amount" label="金额" />
      </el-table>
    </el-card>

    <el-empty v-else description="请选择条件后查询" />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { fetchFinanceStats } from '@/api/admin/stats'

const queryForm = reactive({
  period: 'daily',
  date: ''
})

const finance = ref(null)

const compositionTable = ref([])

const loadFinance = async () => {
  if (!queryForm.date) {
    ElMessage.warning('请选择统计日期')
    return
  }
  try {
    const res = await fetchFinanceStats({
      period: queryForm.period,
      date: queryForm.date
    })
    finance.value = res.data || null
    if (finance.value?.composition) {
      compositionTable.value = [
        { name: '任务营收', amount: finance.value.composition.taskRevenue },
        { name: '存储营收', amount: finance.value.composition.storageRevenue }
      ]
    } else {
      compositionTable.value = []
    }
  } catch (e) {
    ElMessage.error('加载财务数据失败')
  }
}
</script>

<style scoped>
.admin-page {
  padding: 8px 4px;
}

.finance-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.page-header {
  margin-bottom: 4px;
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

.filter-card {
  border-radius: 14px;
  border: 1px solid #dbeafe;
  background: #ffffff;
  color: #111827;
}

.filter-form {
  margin-bottom: 0;
}

.summary-card,
.section-card {
  border-radius: 14px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  color: #111827;
}

.card-title {
  margin: 0 0 10px;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
}

.summary-desc :deep(.el-descriptions__label) {
  background-color: transparent;
}
</style>

