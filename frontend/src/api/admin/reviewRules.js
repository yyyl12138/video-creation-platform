import request from '@/utils/request'

// 获取审核规则列表
export function fetchReviewRules(params) {
  return request({
    url: '/admin/review-rules',
    method: 'get',
    params
  })
}

// 新增或修改审核规则
export function saveReviewRule(data) {
  return request({
    url: '/admin/review-rules',
    method: 'post',
    data
  })
}

// 启用/禁用规则
export function updateReviewRuleStatus(ruleId, data) {
  return request({
    url: `/admin/review-rules/${ruleId}/status`,
    method: 'put',
    data
  })
}

