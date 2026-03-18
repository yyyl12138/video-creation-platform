import request from '@/utils/request'

/**
 * 获取模版市场列表 (公开发现)
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.type - 分类筛选
 * @param {string} params.keyword - 关键词
 */
export function getMarketTemplates(params) {
  return request({
    url: '/market/templates',
    method: 'get',
    params
  })
}

/**
 * 获取模版详情
 * @param {string} templateId - 模版ID
 */
export function getMarketDetail(templateId) {
  return request({
    url: `/market/templates/${templateId}`,
    method: 'get'
  })
}

/**
 * 购买/获取模版
 */
export function purchaseTemplate(templateId) {
  return request({
    url: `/market/templates/${templateId}/purchase`,
    method: 'post'
  })
}
