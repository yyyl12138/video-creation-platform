import request from '@/utils/request'

// 核心指标卡片
export function fetchOverviewStats() {
  return request({
    url: '/admin/stats/overview',
    method: 'get'
  })
}

// 趋势图表数据
export function fetchTrendStats(params) {
  return request({
    url: '/admin/stats/trend',
    method: 'get',
    params
  })
}

// 任务类型分布
export function fetchTaskDistribution() {
  return request({
    url: '/admin/stats/tasks/distribution',
    method: 'get'
  })
}

// 任务失败原因分析
export function fetchTaskFailureAnalysis() {
  return request({
    url: '/admin/stats/tasks/failure-analysis',
    method: 'get'
  })
}

// 财务报表
export function fetchFinanceStats(params) {
  return request({
    url: '/admin/stats/finance',
    method: 'get',
    params
  })
}

