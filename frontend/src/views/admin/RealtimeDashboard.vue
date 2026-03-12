<template>
  <div class="page">
    <!-- Header -->
    <div class="header">
      <div class="header-left">
        <div class="logo">🎬</div>
        <div>
          <h1>实时计算看板</h1>
          <p class="desc">MySQL CDC → Kafka → Python 实时消费 · 每3秒刷新</p>
        </div>
      </div>
      <div class="header-right">
        <el-tag :type="status === 'connected' ? 'success' : 'danger'" size="small" effect="plain">
          <span class="dot" :class="status"></span>
          {{ status === 'connected' ? '实时接收中' : '未连接' }}
        </el-tag>
        <el-tag type="info" size="small" effect="plain" v-if="d.last_update">{{ d.last_update }}</el-tag>
        <el-button :type="running ? 'danger' : 'primary'" size="small" @click="toggle">
          {{ running ? '⏸ 暂停' : '▶ 启动' }}
        </el-button>
      </div>
    </div>

    <!-- KPI -->
    <div class="kpi-grid">
      <div v-for="c in kpiCards" :key="c.label" :class="['kpi', c.theme]">
        <span class="kpi-icon">{{ c.icon }}</span>
        <div>
          <div class="kpi-label">{{ c.label }}</div>
          <div class="kpi-val">{{ c.prefix }}{{ fmt(c.value) }}{{ c.suffix }}</div>
        </div>
      </div>
    </div>

    <el-row :gutter="16">
      <!-- 左侧 -->
      <el-col :span="16">
        <!-- 分钟趋势 -->
        <el-card shadow="never" class="sec">
          <template #header>
            <div class="sec-hd">
              <span class="sec-title">📈 分钟级趋势</span>
              <el-tag size="small" type="info">{{ (d.trend_minutes||[]).length }} 分钟</el-tag>
            </div>
          </template>
          <el-table :data="d.trend_minutes||[]" size="small" border stripe max-height="220">
            <el-table-column prop="minute" label="时间" width="80" align="center" />
            <el-table-column label="任务" align="center">
              <template #default="{row}">
                <div class="bar-cell">
                  <div class="bar" :style="{width:barW(row.tasks,maxT)}"></div>
                  <span>{{ row.tasks }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="success" label="成功" width="60" align="center">
              <template #default="{row}"><span class="g">{{ row.success }}</span></template>
            </el-table-column>
            <el-table-column prop="failed" label="失败" width="60" align="center">
              <template #default="{row}"><span class="r">{{ row.failed }}</span></template>
            </el-table-column>
            <el-table-column label="Token" width="90" align="center">
              <template #default="{row}">{{ Number(row.token).toFixed(1) }}</template>
            </el-table-column>
            <el-table-column label="充值" width="90" align="center">
              <template #default="{row}"><span class="g">+¥{{ Number(row.recharge).toFixed(0) }}</span></template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 分布 -->
        <el-row :gutter="16" style="margin-top:16px">
          <el-col :span="12">
            <el-card shadow="never" class="sec">
              <template #header><span class="sec-title">🎯 任务类型分布</span></template>
              <div v-for="(v,k) in d.task_by_type" :key="k" class="dist">
                <div class="dist-hd"><span>{{ k }}</span><b>{{ v }}</b></div>
                <el-progress :percentage="pct(v,typeSum)" :stroke-width="10" :show-text="false" :color="tc[k]||'#909399'" />
              </div>
              <el-empty v-if="!Object.keys(d.task_by_type||{}).length" description="等待数据..." :image-size="50" />
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" class="sec">
              <template #header><span class="sec-title">🤖 模型调用统计</span></template>
              <div v-for="(v,k) in d.task_by_model" :key="k" class="dist">
                <div class="dist-hd"><span>{{ k }}</span><b>{{ v }}</b></div>
                <el-progress :percentage="pct(v,modelSum)" :stroke-width="10" :show-text="false" color="#409eff" />
              </div>
              <el-empty v-if="!Object.keys(d.task_by_model||{}).length" description="等待数据..." :image-size="50" />
            </el-card>
          </el-col>
        </el-row>
      </el-col>

      <!-- 右侧 -->
      <el-col :span="8">
        <!-- 任务状态 -->
        <el-card shadow="never" class="sec">
          <template #header><span class="sec-title">📊 任务状态</span></template>
          <div class="stat-grid">
            <div v-for="s in statItems" :key="s.label" class="stat-item">
              <div class="stat-num" :style="{color:s.color}">{{ s.value }}</div>
              <div class="stat-label">{{ s.label }}</div>
            </div>
          </div>
        </el-card>

        <!-- 事件流 -->
        <el-card shadow="never" class="sec" style="margin-top:16px">
          <template #header>
            <div class="sec-hd">
              <span class="sec-title">📡 实时事件流</span>
              <div class="live"></div>
            </div>
          </template>
          <div class="events">
            <div v-for="(e,i) in (d.recent_events||[]).slice(0,20)" :key="i" class="evt">
              <el-tag :type="et(e.type)" size="small" effect="plain">{{ e.type }}</el-tag>
              <span class="evt-txt">{{ e.summary }}</span>
              <span class="evt-t">{{ e.time?.slice(11) }}</span>
            </div>
            <el-empty v-if="!(d.recent_events||[]).length" description="等待事件..." :image-size="50" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <div class="foot">
      MySQL binlog → Flink CDC → Kafka (user/task/payment_events) → Python Flask 消费 → Vue3 看板 (3s轮询)
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { Poller } from '@/api/admin/realtime'

const d = ref({
  total_users:0, online_users:0, new_users_today:0,
  total_tasks:0, tasks_waiting:0, tasks_running:0,
  tasks_success:0, tasks_failed:0, tasks_cancelled:0,
  success_rate:0, avg_duration:0, total_token:0,
  task_by_type:{}, task_by_model:{},
  total_recharge:0, total_consume:0,
  last_update:'', recent_events:[], trend_minutes:[],
})

const status = ref('disconnected')
const running = ref(false)
let poller = null

const kpiCards = computed(() => [
  { label:'总用户', value:d.value.total_users, icon:'👥', theme:'p', prefix:'', suffix:'' },
  { label:'活跃', value:d.value.online_users, icon:'🟢', theme:'s', prefix:'', suffix:'' },
  { label:'新注册', value:d.value.new_users_today, icon:'🆕', theme:'c', prefix:'', suffix:'' },
  { label:'总任务', value:d.value.total_tasks, icon:'⚡', theme:'a', prefix:'', suffix:'' },
  { label:'成功率', value:d.value.success_rate, icon:'✅', theme:'s', prefix:'', suffix:'%' },
  { label:'耗时', value:d.value.avg_duration, icon:'⏱️', theme:'c', prefix:'', suffix:'s' },
  { label:'充值', value:d.value.total_recharge, icon:'💰', theme:'w', prefix:'¥', suffix:'' },
  { label:'消费', value:d.value.total_consume, icon:'🔥', theme:'d', prefix:'¥', suffix:'' },
])

const statItems = computed(() => [
  { label:'等待中', value:d.value.tasks_waiting, color:'#909399' },
  { label:'处理中', value:d.value.tasks_running, color:'#409eff' },
  { label:'成功', value:d.value.tasks_success, color:'#67c23a' },
  { label:'失败', value:d.value.tasks_failed, color:'#f56c6c' },
  { label:'取消', value:d.value.tasks_cancelled, color:'#e6a23c' },
])

const typeSum = computed(() => Object.values(d.value.task_by_type||{}).reduce((a,b)=>a+b,0))
const modelSum = computed(() => Object.values(d.value.task_by_model||{}).reduce((a,b)=>a+b,0))
const maxT = computed(() => Math.max(...(d.value.trend_minutes||[]).map(r=>r.tasks),1))

const tc = {'文生文':'#409eff','文生图':'#67c23a','文生视频':'#e6a23c','图生视频':'#f56c6c'}

function fmt(v) { return v==null?'0': typeof v==='number'&&v%1!==0?v.toFixed(1):Number(v).toLocaleString() }
function barW(v,m) { return m>0?Math.max(v/m*100,3)+'%':'3%' }
function pct(v,t) { return t>0?Number((v/t*100).toFixed(1)):0 }
function et(t) { return {user:'',task:'warning',payment:'success'}[t]||'info' }

function toggle() {
  if (running.value) { poller?.stop(); running.value = false }
  else { start() }
}

function start() {
  poller = new Poller(
    data => { d.value = {...d.value,...data} },
    s => { status.value = s }
  )
  poller.start(3000)
  running.value = true
}

onMounted(() => start())
onUnmounted(() => poller?.stop())
</script>

<style scoped>
.page { max-width: 1400px; margin: 0 auto; padding: 16px 24px; }

/* Header */
.header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.header-left { display:flex; align-items:center; gap:12px; }
.logo { width:40px; height:40px; border-radius:10px; background:linear-gradient(135deg,#409eff,#67c23a); display:flex; align-items:center; justify-content:center; font-size:20px; }
.header h1 { margin:0; font-size:20px; color:#303133; }
.desc { margin:2px 0 0; font-size:12px; color:#909399; }
.header-right { display:flex; align-items:center; gap:10px; }
.dot { display:inline-block; width:6px; height:6px; border-radius:50%; margin-right:4px; }
.dot.connected { background:#67c23a; animation:blink 1.5s infinite; }
.dot.disconnected,.dot.error { background:#f56c6c; }
@keyframes blink { 0%,100%{opacity:1}50%{opacity:.3} }

/* KPI */
.kpi-grid { display:grid; grid-template-columns:repeat(8,1fr); gap:10px; margin-bottom:16px; }
.kpi { display:flex; align-items:center; gap:8px; padding:12px; border-radius:12px; border:1px solid #dbeafe; }
.kpi.p { background:#eff6ff; } .kpi.s { background:#ecfdf5; } .kpi.c { background:#e0f2fe; }
.kpi.a { background:#fef3c7; } .kpi.w { background:#fffbeb; } .kpi.d { background:#fef2f2; }
.kpi-icon { font-size:22px; }
.kpi-label { font-size:11px; color:#6b7280; }
.kpi-val { font-size:17px; font-weight:700; color:#111827; white-space:nowrap; }

/* Section */
.sec { border-radius:14px; border:1px solid #e5e7eb; }
.sec-hd { display:flex; justify-content:space-between; align-items:center; }
.sec-title { font-size:14px; font-weight:600; color:#1f2937; }

/* Bar */
.bar-cell { position:relative; display:flex; align-items:center; height:20px; }
.bar { position:absolute; left:0; top:0; height:100%; background:#409eff; opacity:.2; border-radius:3px; transition:width .4s; }
.bar-cell span { position:relative; z-index:1; font-size:12px; font-weight:600; }
.g { color:#67c23a; font-weight:600; } .r { color:#f56c6c; font-weight:600; }

/* Distribution */
.dist { margin-bottom:12px; }
.dist-hd { display:flex; justify-content:space-between; margin-bottom:3px; font-size:12px; color:#4b5563; }

/* Status */
.stat-grid { display:flex; flex-wrap:wrap; gap:12px; justify-content:space-around; padding:8px 0; }
.stat-item { text-align:center; min-width:55px; }
.stat-num { font-size:22px; font-weight:700; }
.stat-label { font-size:11px; color:#6b7280; margin-top:2px; }

/* Events */
.live { width:8px; height:8px; border-radius:50%; background:#f56c6c; animation:blink .8s infinite; }
.events { max-height:420px; overflow-y:auto; }
.evt { display:flex; align-items:center; gap:6px; padding:5px 0; border-bottom:1px solid #f3f4f6; font-size:12px; }
.evt:last-child { border-bottom:none; }
.evt-txt { flex:1; color:#374151; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.evt-t { flex-shrink:0; color:#9ca3af; font-size:11px; }

.foot { margin-top:24px; padding-top:12px; border-top:1px solid #e5e7eb; font-size:11px; color:#9ca3af; text-align:center; }
</style>
