import request from '@/utils/request'

/**
 * ==================== 创作者个人数据服务 (Creator Personal Stats) ====================
 */

/**
 * 4.1 获取个人核心数据
 * @returns {Promise<Object>} 返回创作者核心数据
 * - followerCount: 粉丝数
 * - totalTemplateSales: 模版销售/引用次数
 * - templateRating: 平均评分
 * - totalRevenue: 模版收益
 * - monthlyRevenue: 月度收益
 */
export function getCreatorStats() {
  return request({
    url: '/stats/creator/me',
    method: 'get'
  })
}

/**
 * 4.2 模版表现分析
 * @param {Object} params - 查询参数
 * @param {number} params.days - 最近天数 (7/30)
 * @returns {Promise<Object>} 返回模版分析数据
 * - dates: 日期数组
 * - views: 浏览量数组
 * - likes: 点赞数数组
 */
export function getTemplateAnalysis(params) {
  return request({
    url: '/stats/creator/templates/analysis',
    method: 'get',
    params
  })
}
