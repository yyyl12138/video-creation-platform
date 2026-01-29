import request from '@/utils/request'

/**
 * 获取素材列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {string} params.keyword - 关键词搜索
 * @param {string} params.type - 类型筛选 (image/video/audio)
 */
export function getMaterials(params) {
  return request({
    url: '/materials',
    method: 'get',
    params
  })
}

/**
 * 上传素材
 * @param {FormData} data - 表单数据
 */
export function uploadMaterial(data) {
  return request({
    url: '/materials',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除素材
 * @param {string|number} id - 素材ID
 * @param {Object} params - 额外参数
 * @param {string} params.type - 素材类型 (IMAGE/VIDEO/AUDIO)
 */
export function deleteMaterial(id, params = {}) {
  return request({
    url: `/materials/${id}`,
    method: 'delete',
    params
  })
}
