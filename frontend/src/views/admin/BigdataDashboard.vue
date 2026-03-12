<template>
  <div class="page">
    <div class="page-hd">
      <div><h2>离线计算看板</h2><p class="sub">PySpark 查询 Hive · 统计日期: {{ kpi?.stat_date || '加载中...' }}</p></div>
      <el-button type="primary" :loading="loading" @click="loadAll"><el-icon><Refresh /></el-icon> 刷新</el-button>
    </div>

    <el-tabs v-model="tab">
      <!-- 概览 -->
      <el-tab-pane label="全局概览" name="overview">
        <el-row :gutter="14" class="kpi-row">
          <el-col :span="6" v-for="c in overviewKpi" :key="c.label">
            <el-card shadow="hover" :class="['kpi',c.th]">
              <span class="ki">{{c.icon}}</span>
              <div><div class="kl">{{c.label}}</div><div class="kv">{{c.pre}}{{fmt(c.val)}}{{c.suf}}</div></div>
            </el-card>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="16">
            <el-card shadow="never" class="sec"><template #header><span class="st">📈 任务趋势(14天)</span></template>
              <el-table :data="taskTrend" size="small" border stripe max-height="300">
                <el-table-column prop="date" label="日期" width="90" align="center"/>
                <el-table-column prop="tasks" label="任务数" align="center"/>
                <el-table-column prop="tokens" label="Token" align="center"><template #default="{row}">{{fmt(row.tokens)}}</template></el-table-column>
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="never" class="sec"><template #header><span class="st">🎯 任务类型分布</span></template>
              <div v-for="t in taskTypes" :key="t.name" class="di"><div class="dh"><span>{{t.name}}</span><b>{{t.value}}</b></div>
                <el-progress :percentage="taskTypePct(t.value)" :stroke-width="10" :show-text="false" :color="t.color"/></div>
              <el-empty v-if="!taskTypes.length" description="暂无" :image-size="50"/>
            </el-card>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <el-card shadow="never" class="sec"><template #header><span class="st">🤖 模型性能</span></template>
              <el-table :data="modelPerf" size="small" border stripe>
                <el-table-column prop="model" label="模型"/>
                <el-table-column prop="success" label="成功率" width="90" align="center"><template #default="{row}">
                  <el-tag :type="row.success>=95?'success':row.success>=85?'warning':'danger'" size="small">{{row.success}}%</el-tag>
                </template></el-table-column>
                <el-table-column prop="avgTime" label="耗时" width="80" align="center"><template #default="{row}">{{row.avgTime}}s</template></el-table-column>
                <el-table-column prop="tokens" label="Token" width="100" align="center"><template #default="{row}">{{fmt(row.tokens)}}</template></el-table-column>
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" class="sec"><template #header><span class="st">📦 素材统计</span></template>
              <div v-if="materials" class="mat-sum">
                <div><div class="mat-n">{{fmt(materials.total)}}</div><div class="mat-l">素材总量</div></div>
                <div><div class="mat-n accent">{{materials.ai_ratio}}%</div><div class="mat-l">AI占比</div></div>
              </div>
              <div v-for="d in (materials?.distribution||[])" :key="d.name" class="di"><div class="dh"><span>{{d.name}}</span><b>{{fmt(d.value)}}</b></div>
                <el-progress :percentage="materials?d.value/materials.total*100:0" :stroke-width="10" :show-text="false" :color="d.color"/></div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 用户 -->
      <el-tab-pane label="用户分析" name="users">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-card shadow="never" class="sec"><template #header><span class="st">📈 新增用户趋势</span></template>
              <el-table :data="userTrend" size="small" border stripe max-height="380">
                <el-table-column prop="date" label="日期" width="90" align="center"/>
                <el-table-column prop="newUsers" label="新增" align="center"/>
                <el-table-column prop="dau" label="DAU" align="center"/>
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" class="sec"><template #header><span class="st">📉 用户留存率</span></template>
              <el-table :data="retention" size="small" border stripe>
                <el-table-column prop="name" label="周期" align="center"/>
                <el-table-column prop="rate" label="留存率" align="center">
                  <template #default="{row}"><el-progress :percentage="row.rate" :stroke-width="16" :text-inside="true"
                    :color="row.rate>30?'#67c23a':row.rate>15?'#e6a23c':'#f56c6c'"/></template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 营收 -->
      <el-tab-pane label="营收财务" name="revenue">
        <el-row :gutter="14" class="kpi-row">
          <el-col :span="6" v-for="c in revKpi" :key="c.label">
            <el-card shadow="hover" :class="['kpi',c.th]">
              <span class="ki">{{c.icon}}</span>
              <div><div class="kl">{{c.label}}</div><div class="kv">{{c.pre}}{{fmt(c.val)}}{{c.suf}}</div></div>
            </el-card>
          </el-col>
        </el-row>
        <el-card shadow="never" class="sec"><template #header><span class="st">💰 营收趋势(14天)</span></template>
          <el-table :data="revenueTrend" size="small" border stripe max-height="360">
            <el-table-column prop="date" label="日期" width="90" align="center"/>
            <el-table-column prop="revenue" label="充值金额" align="center">
              <template #default="{row}">¥{{fmt(row.revenue)}}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as api from '@/api/admin/bigdata'

const tab = ref('overview')
const loading = ref(false)
const kpi = ref(null); const userTrend = ref([]); const retention = ref([])
const taskOverview = ref(null); const taskTypes = ref([]); const taskTrend = ref([])
const modelPerf = ref([]); const revenue = ref(null); const revenueTrend = ref([])
const materials = ref(null)

const overviewKpi = computed(() => [
  {label:'累计用户',val:kpi.value?.total_users,icon:'👥',th:'p',pre:'',suf:''},
  {label:'昨日DAU',val:kpi.value?.dau,icon:'📱',th:'c',pre:'',suf:''},
  {label:'昨日任务',val:kpi.value?.daily_tasks,icon:'⚡',th:'a',pre:'',suf:''},
  {label:'成功率',val:kpi.value?.success_rate,icon:'✅',th:'s',pre:'',suf:'%'},
])
const revKpi = computed(() => [
  {label:'昨日营收',val:revenue.value?.daily_revenue,icon:'💰',th:'a',pre:'¥',suf:''},
  {label:'月累计',val:revenue.value?.monthly_revenue,icon:'📈',th:'s',pre:'¥',suf:''},
  {label:'付费用户',val:revenue.value?.paying_users,icon:'💎',th:'p',pre:'',suf:''},
  {label:'ARPPU',val:revenue.value?.arppu,icon:'👤',th:'d',pre:'¥',suf:''},
])
const ttTotal = computed(() => taskTypes.value.reduce((a,t) => a+t.value,0))
function taskTypePct(v) { return ttTotal.value>0?Number((v/ttTotal.value*100).toFixed(1)):0 }
function fmt(v) { return v==null?'0':typeof v==='number'&&v%1!==0?v.toFixed(1):Number(v).toLocaleString() }

async function loadAll() {
  loading.value = true
  try {
    const results = await Promise.allSettled([
      api.fetchKpi().then(d=>{kpi.value=d}),
      api.fetchUserTrend().then(d=>{userTrend.value=d||[]}),
      api.fetchRetention().then(d=>{retention.value=d||[]}),
      api.fetchTaskOverview().then(d=>{taskOverview.value=d}),
      api.fetchTaskTypes().then(d=>{taskTypes.value=d||[]}),
      api.fetchTaskTrend().then(d=>{taskTrend.value=d||[]}),
      api.fetchModelPerf().then(d=>{modelPerf.value=d||[]}),
      api.fetchRevenue().then(d=>{revenue.value=d}),
      api.fetchRevenueTrend().then(d=>{revenueTrend.value=d||[]}),
      api.fetchMaterials().then(d=>{materials.value=d}),
    ])
    ElMessage.success('数据加载完成')
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}
onMounted(() => loadAll())
</script>

<style scoped>
.page-hd { display:flex; justify-content:space-between; align-items:center; margin-bottom:12px; }
.page-hd h2 { margin:0; font-size:20px; color:#303133; }
.sub { margin:2px 0 0; font-size:12px; color:#909399; }
.kpi-row { margin-bottom:16px; }
.kpi { display:flex; align-items:center; gap:10px; padding:4px 8px; border-radius:12px; border:1px solid #dbeafe; min-height:80px; }
.kpi.p{background:#eff6ff} .kpi.c{background:#e0f2fe} .kpi.a{background:#fef3c7} .kpi.s{background:#ecfdf5} .kpi.d{background:#fef2f2}
.ki{font-size:28px} .kl{font-size:11px;color:#6b7280} .kv{font-size:20px;font-weight:700;color:#111827}
.sec { border-radius:14px; border:1px solid #e5e7eb; }
.st { font-size:14px; font-weight:600; color:#1f2937; }
.di { margin-bottom:12px; } .dh { display:flex; justify-content:space-between; margin-bottom:3px; font-size:12px; color:#4b5563; }
.mat-sum { display:flex; gap:32px; margin-bottom:16px; padding-bottom:12px; border-bottom:1px solid #f3f4f6; }
.mat-n { font-size:26px; font-weight:700; color:#111827; } .mat-n.accent{color:#2563eb} .mat-l{font-size:11px;color:#6b7280}
</style>
