<template>
  <div class="creator-analytics-container">
    <div class="page-header">
      <h1>创作者数据分析</h1>
      <p>查看您的创作数据和收益情况</p>
    </div>

    <!-- 核心数据卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon followers">
          <el-icon :size="28"><User /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.followerCount || 0 }}</span>
          <span class="stat-label">粉丝数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon sales">
          <el-icon :size="28"><ShoppingCart /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalTemplateSales || 0 }}</span>
          <span class="stat-label">模版销售次数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon rating">
          <el-icon :size="28"><Star /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.templateRating?.toFixed(1) || '0.0' }}</span>
          <span class="stat-label">平均评分</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon revenue">
          <el-icon :size="28"><Money /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">¥{{ stats.totalRevenue?.toFixed(2) || '0.00' }}</span>
          <span class="stat-label">总收益</span>
        </div>
      </div>
      <div class="stat-card highlight">
        <div class="stat-icon monthly">
          <el-icon :size="28"><TrendCharts /></el-icon>
        </div>
        <div class="stat-info">
          <span class="stat-value">¥{{ stats.monthlyRevenue?.toFixed(2) || '0.00' }}</span>
          <span class="stat-label">本月收益</span>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <!-- 时间范围选择 -->
      <div class="chart-header">
        <h2>模版表现趋势</h2>
        <el-radio-group v-model="dateRange" size="small" @change="fetchAnalysisData">
          <el-radio-button label="7">近7天</el-radio-button>
          <el-radio-button label="30">近30天</el-radio-button>
        </el-radio-group>
      </div>

      <el-row :gutter="24">
        <!-- 浏览量图表 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>浏览量趋势</span>
                <el-tag type="info" size="small">
                  总计: {{ totalViews }}
                </el-tag>
              </div>
            </template>
            <div class="chart-wrapper" ref="viewsChartRef"></div>
          </el-card>
        </el-col>

        <!-- 点赞数图表 -->
        <el-col :span="12">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>点赞数趋势</span>
                <el-tag type="danger" size="small">
                  总计: {{ totalLikes }}
                </el-tag>
              </div>
            </template>
            <div class="chart-wrapper" ref="likesChartRef"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 模版列表 -->
    <div class="templates-section">
      <div class="section-header">
        <h2>我的模版</h2>
        <el-button type="primary" @click="$router.push('/market/templates')">
          <el-icon><Plus /></el-icon>
          发布新模版
        </el-button>
      </div>

      <el-table :data="templateList" style="width: 100%" class="template-table">
        <el-table-column prop="name" label="模版名称" min-width="200">
          <template #default="{ row }">
            <div class="template-info">
              <el-image
                :src="row.thumbnail || defaultThumbnail"
                class="template-thumbnail"
                fit="cover"
              />
              <span class="template-name">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span class="price">¥{{ row.price || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="views" label="浏览量" width="90" />
        <el-table-column prop="likes" label="点赞数" width="80" />
        <el-table-column prop="rating" label="评分" width="100">
          <template #default="{ row }">
            <div class="rating-cell">
              <el-icon class="star-icon"><Star /></el-icon>
              <span>{{ row.rating?.toFixed(1) || '0.0' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="revenue" label="收益" width="100">
          <template #default="{ row }">
            <span class="revenue">¥{{ row.revenue?.toFixed(2) || '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 收益明细 -->
    <div class="revenue-section">
      <h2>收益明细</h2>
      <el-table :data="revenueList" style="width: 100%" class="revenue-table">
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="templateName" label="模版" min-width="180" />
        <el-table-column prop="buyer" label="买家" width="120" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            <span class="amount">+¥{{ row.amount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'PURCHASE' ? 'success' : 'primary'" size="small">
              {{ row.type === 'PURCHASE' ? '购买' : '订阅' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="totalRevenue"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { User, ShoppingCart, Star, Money, TrendCharts, Plus } from '@element-plus/icons-vue'
import { getCreatorStats, getTemplateAnalysis } from '@/api/user/stats'
import * as echarts from 'echarts'

// 默认缩略图
const defaultThumbnail = 'https://via.placeholder.com/60x40?text=Template'

// 状态
const dateRange = ref('7')
const stats = ref({})
const templateList = ref([])
const revenueList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalRevenue = ref(0)

// 图表引用
const viewsChartRef = ref(null)
const likesChartRef = ref(null)
let viewsChart = null
let likesChart = null

// 分析数据
const analysisData = ref({
  dates: [],
  views: [],
  likes: []
})

// 计算属性
const totalViews = computed(() => {
  return analysisData.value.views.reduce((a, b) => a + b, 0)
})

const totalLikes = computed(() => {
  return analysisData.value.likes.reduce((a, b) => a + b, 0)
})

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    DRAFT: 'info',
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    DRAFT: '草稿',
    PENDING: '审核中',
    APPROVED: '已发布',
    REJECTED: '已驳回'
  }
  return texts[status] || status
}

// 获取创作者统计数据
const fetchStats = async () => {
  try {
    const res = await getCreatorStats()
    if (res.code === 20000 || res.success) {
      stats.value = res.data || {}
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    // 模拟数据
    stats.value = {
      followerCount: 1234,
      totalTemplateSales: 56,
      templateRating: 4.8,
      totalRevenue: 5678.90,
      monthlyRevenue: 1234.56
    }
  }
}

// 获取分析数据
const fetchAnalysisData = async () => {
  try {
    const res = await getTemplateAnalysis({ days: parseInt(dateRange.value) })
    if (res.code === 20000 || res.success) {
      analysisData.value = res.data || { dates: [], views: [], likes: [] }
    }
    await nextTick()
    renderCharts()
  } catch (error) {
    console.error('获取分析数据失败:', error)
    // 模拟数据
    const days = parseInt(dateRange.value)
    const dates = []
    const views = []
    const likes = []
    const now = new Date()

    for (let i = days - 1; i >= 0; i--) {
      const date = new Date(now)
      date.setDate(date.getDate() - i)
      dates.push(date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' }))
      views.push(Math.floor(Math.random() * 500) + 100)
      likes.push(Math.floor(Math.random() * 50) + 10)
    }

    analysisData.value = { dates, views, likes }
    await nextTick()
    renderCharts()
  }
}

// 获取模版列表
const fetchTemplates = async () => {
  // 模拟数据
  templateList.value = [
    { id: 1, name: '企业宣传模版', type: '商业', price: 99, sales: 23, views: 1234, likes: 89, rating: 4.8, revenue: 2277, status: 'APPROVED', thumbnail: '' },
    { id: 2, name: '婚礼视频模版', type: '婚庆', price: 149, sales: 15, views: 876, likes: 56, rating: 4.9, revenue: 2235, status: 'APPROVED', thumbnail: '' },
    { id: 3, name: '生日派对模版', type: '娱乐', price: 49, sales: 38, views: 2345, likes: 123, rating: 4.7, revenue: 1862, status: 'APPROVED', thumbnail: '' },
    { id: 4, name: '产品介绍模版', type: '商业', price: 199, sales: 8, views: 567, likes: 34, rating: 4.6, revenue: 1592, status: 'APPROVED', thumbnail: '' },
    { id: 5, name: '旅行Vlog模版', type: '生活', price: 79, sales: 28, views: 1890, likes: 98, rating: 4.8, revenue: 2212, status: 'PENDING', thumbnail: '' }
  ]
}

// 获取收益列表
const fetchRevenueList = async () => {
  // 模拟数据
  const now = new Date()
  revenueList.value = Array.from({ length: 10 }, (_, i) => {
    const date = new Date(now)
    date.setDate(date.getDate() - i)
    return {
      date: date.toLocaleDateString('zh-CN'),
      templateName: ['企业宣传模版', '婚礼视频模版', '生日派对模版'][i % 3],
      buyer: `用户${String.fromCharCode(65 + i)}`,
      amount: Math.random() * 100 + 20,
      type: i % 3 === 0 ? 'PURCHASE' : 'PURCHASE'
    }
  })
  totalRevenue.value = 100
}

// 渲染图表
const renderCharts = () => {
  if (!viewsChartRef.value || !likesChartRef.value) return

  // 销毁旧图表
  viewsChart?.dispose()
  likesChart?.dispose()

  // 浏览量图表
  viewsChart = echarts.init(viewsChartRef.value)
  viewsChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: analysisData.value.dates,
      axisLine: { lineStyle: { color: '#e0e6ed' } },
      axisLabel: { color: '#606266' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0' } },
      axisLabel: { color: '#909399' }
    },
    series: [{
      data: analysisData.value.views,
      type: 'line',
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      },
      lineStyle: { color: '#409eff', width: 2 },
      itemStyle: { color: '#409eff' }
    }]
  })

  // 点赞数图表
  likesChart = echarts.init(likesChartRef.value)
  likesChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: analysisData.value.dates,
      axisLine: { lineStyle: { color: '#e0e6ed' } },
      axisLabel: { color: '#606266' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#f0f0f0' } },
      axisLabel: { color: '#909399' }
    },
    series: [{
      data: analysisData.value.likes,
      type: 'bar',
      barWidth: '40%',
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#f56c6c' },
          { offset: 1, color: '#f89898' }
        ]),
        borderRadius: [4, 4, 0, 0]
      }
    }]
  })
}

// 处理窗口大小变化
const handleResize = () => {
  viewsChart?.resize()
  likesChart?.resize()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchRevenueList()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchRevenueList()
}

onMounted(() => {
  fetchStats()
  fetchAnalysisData()
  fetchTemplates()
  fetchRevenueList()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  viewsChart?.dispose()
  likesChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.creator-analytics-container {
  padding: 24px;
  max-width: 1400px;
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

/* 核心数据卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-card.highlight {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.highlight .stat-value,
.stat-card.highlight .stat-label {
  color: #fff;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-icon.followers { background: linear-gradient(135deg, #409eff 0%, #3375b9 100%); }
.stat-icon.sales { background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%); }
.stat-icon.rating { background: linear-gradient(135deg, #e6a23c 0%, #c48a2e 100%); }
.stat-icon.revenue { background: linear-gradient(135deg, #f56c6c 0%, #c45656 100%); }
.stat-icon.monthly { background: rgba(255, 255, 255, 0.2); }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

/* 图表区域 */
.charts-section {
  margin-bottom: 32px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.chart-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-wrapper {
  height: 300px;
}

/* 模版列表 */
.templates-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.template-table {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.template-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.template-thumbnail {
  width: 60px;
  height: 40px;
  border-radius: 6px;
}

.template-name {
  font-weight: 500;
  color: #303133;
}

.price {
  color: #f56c6c;
  font-weight: 600;
}

.rating-cell {
  display: flex;
  align-items: center;
  gap: 4px;
}

.star-icon {
  color: #e6a23c;
}

.revenue {
  color: #67c23a;
  font-weight: 600;
}

/* 收益明细 */
.revenue-section {
  margin-bottom: 32px;
}

.revenue-section h2 {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 20px 0;
}

.revenue-table {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.amount {
  color: #67c23a;
  font-weight: 600;
}

.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .creator-analytics-container {
    padding: 16px;
  }

  .stats-cards {
    grid-template-columns: 1fr;
  }

  .el-col {
    width: 100% !important;
    max-width: 100% !important;
    margin-bottom: 20px;
  }
}
</style>
