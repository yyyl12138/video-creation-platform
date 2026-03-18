import request from '@/utils/request'

/**
 * 点赞/取消点赞模版
 * @param {string} targetId - 目标ID
 */
export function toggleLike(targetId) {
  return request({
    url: '/interactions/like',
    method: 'post',
    data: { targetId }
  })
}

/**
 * 关注/取关作者
 * @param {string} targetId - 作者ID
 */
export function toggleFollow(targetId) {
  return request({
    url: '/interactions/follow',
    method: 'post',
    data: { targetId }
  })
}
