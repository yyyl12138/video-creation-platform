import request from '@/utils/request'

/**
 * 获取模版评论列表
 * @param {Object} params - 查询参数
 * @param {string} params.targetId - 模版ID
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 */
export function getComments(params) {
  return request({
    url: '/comments',
    method: 'get',
    params
  })
}

/**
 * 发表评论
 * @param {Object} data - 评论数据
 * @param {string} data.targetId - 目标ID
 * @param {string} data.content - 评论内容
 */
export function addComment(data) {
  return request({
    url: '/comments',
    method: 'post',
    data
  })
}

/**
 * 删除评论
 * @param {string|number} commentId - 评论ID
 */
export function deleteComment(commentId) {
  return request({
    url: `/comments/${commentId}`,
    method: 'delete'
  })
}
