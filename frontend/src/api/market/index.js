import request from '@/utils/request'

/**
 * ==================== 模版市场服务 (Marketplace Service) ====================
 */

/**
 * 1.1 获取模版列表/搜索
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 * @param {string} params.type - 模版类型过滤 (可选)
 * @param {string} params.keyword - 关键词模糊匹配名称和标签
 */
export function getTemplateList(params) {
  return request({
    url: '/market/templates',
    method: 'get',
    params
  })
}

/**
 * 1.2 获取模版详情
 * @param {string} templateId - 模版ID
 */
export function getTemplateDetail(templateId) {
  return request({
    url: `/market/templates/${templateId}`,
    method: 'get'
  })
}

/**
 * 1.3 购买/获取模版
 * @param {string} templateId - 模版ID
 */
export function purchaseTemplate(templateId) {
  return request({
    url: `/market/templates/${templateId}/purchase`,
    method: 'post'
  })
}

/**
 * ==================== 社交互动服务 (Interaction Service) ====================
 */

/**
 * 2.1 点赞/取消点赞
 * @param {Object} data - 互动数据
 * @param {string} data.targetId - 目标模版ID
 */
export function toggleLike(data) {
  return request({
    url: '/interactions/like',
    method: 'post',
    data
  })
}

/**
 * 2.2 关注/取关作者
 * @param {Object} data - 互动数据
 * @param {string} data.targetId - 作者的用户ID
 */
export function toggleFollow(data) {
  return request({
    url: '/interactions/follow',
    method: 'post',
    data
  })
}

/**
 * ==================== 评论服务 (Comment Service) ====================
 */

/**
 * 3.1 获取模版评论
 * @param {Object} params - 查询参数
 * @param {string} params.targetId - 模版ID
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 */
export function getCommentList(params) {
  return request({
    url: '/comments',
    method: 'get',
    params
  })
}

/**
 * 3.2 发表评论
 * @param {Object} data - 评论数据
 * @param {string} data.targetId - 目标模版ID
 * @param {number} data.parentId - 父评论ID (顶级评论传0或空)
 * @param {string} data.content - 评论内容文字
 */
export function postComment(data) {
  return request({
    url: '/comments',
    method: 'post',
    data
  })
}

/**
 * 3.3 删除评论
 * @param {number} commentId - 评论ID
 */
export function deleteComment(commentId) {
  return request({
    url: `/comments/${commentId}`,
    method: 'delete'
  })
}

/**
 * ==================== 消息通知服务 (Notification Service) ====================
 */

/**
 * 4.1 获取消息列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页记录数
 * @param {string} params.type - 类型: SYSTEM, LIKE, FOLLOW, COMMENT, PURCHASE
 */
export function getNotificationList(params) {
  return request({
    url: '/notifications',
    method: 'get',
    params
  })
}

/**
 * 4.2 标记单条已读
 * @param {number} notificationId - 通知ID
 */
export function markNotificationRead(notificationId) {
  return request({
    url: `/notifications/${notificationId}/read`,
    method: 'put'
  })
}

/**
 * 4.3 全部标记已读
 */
export function markAllNotificationRead() {
  return request({
    url: '/notifications/read-all',
    method: 'put'
  })
}
