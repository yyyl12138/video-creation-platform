import request from '@/utils/request'

/**
 * ==================== 违规举报服务 (Report Service) ====================
 */

/**
 * 3.1 提交举报 (用户端)
 * @param {Object} data - 举报数据
 * @param {string} data.targetId - 被举报内容ID
 * @param {string} data.targetType - 类型: VIDEO, COMMENT
 * @param {string} data.reasonType - 举报类型: 涉黄, 暴力, 侵权, 其他
 * @param {string} data.description - 详细描述 (可选)
 * @returns {Promise<boolean>} 返回是否提交成功
 */
export function submitReport(data) {
  return request({
    url: '/reports',
    method: 'post',
    data
  })
}
