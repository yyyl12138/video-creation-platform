import request from '@/utils/request'

/**
 * 获取消息通知列表
 * @param {Object} params - 查询参数
 */
export function fetchNotifications(params) {
  return request({
    url: '/notifications',
    method: 'get',
    params
  })
}

/**
 * 标记单条已读
 */
export function markAsRead(notificationId) {
  return request({
    url: `/notifications/${notificationId}/read`,
    method: 'put'
  })
}

/**
 * 全部标记已读
 */
export function markAllAsRead() {
  return request({
    url: '/notifications/read-all',
    method: 'put'
  })
}
