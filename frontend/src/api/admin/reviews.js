import request from '@/utils/request'

// 获取审核任务列表
export function fetchReviewList(params) {
  return request({
    url: '/admin/reviews',
    method: 'get',
    params
  })
}

// 获取单个审核详情
export function getReviewDetail(reviewId) {
  return request({
    url: `/admin/reviews/${reviewId}`,
    method: 'get'
  })
}

// 提交审核决定
export function submitReviewDecision(reviewId, data) {
  return request({
    url: `/admin/reviews/${reviewId}/decision`,
    method: 'put',
    data
  })
}

