import request from '@/utils/request'

/**
 * ==================== 用户素材服务 (User Material Service) ====================
 */

/**
 * 3.1 获取素材详情
 * @param {string} materialId - 素材ID
 * @param {string} type - 素材类型: IMAGE, VIDEO, AUDIO
 */
export function getMaterialDetail(materialId, type) {
  return request({
    url: `/materials/${materialId}`,
    method: 'get',
    params: { type }
  })
}

/**
 * 3.2 获取素材列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {boolean} params.isSystem - 是否系统素材 (true: 官方素材库, false: 个人素材库, null: 全部)
 * @param {string} params.type - 素材类型: IMAGE, VIDEO, AUDIO
 */
export function getMaterialList(params) {
  return request({
    url: '/materials',
    method: 'get',
    params
  })
}

// 兼容旧版本命名
export const getMaterials = getMaterialList

/**
 * 3.2.1 删除单个素材
 * @param {string} materialId - 素材ID
 * @param {Object} params - 额外参数
 */
export function deleteMaterial(materialId, params = {}) {
  return request({
    url: `/materials/${materialId}`,
    method: 'delete',
    params
  })
}

/**
 * 3.3 批量删除素材
 * @param {Object} data - 删除数据
 * @param {Array<string>} data.materialIds - ID列表
 * @param {string} data.type - 素材类型: IMAGE, VIDEO, AUDIO
 */
export function batchDeleteMaterials(data) {
  return request({
    url: '/materials/batch',
    method: 'delete',
    data
  })
}

/**
 * 3.4 用户上传素材
 * @param {FormData} formData - 表单数据
 * @param {File} formData.file - 文件数据 (最大500MB)
 * @param {string} formData.type - 素材类型: IMAGE, VIDEO, AUDIO
 * @param {string} formData.name - 素材名称 (可选)
 * @param {string} formData.albumId - 目标相册 (可选)
 */
export function uploadMaterial(formData) {
  return request({
    url: '/materials',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * ==================== 管理员素材管理 (Admin Material Management) ====================
 */

/**
 * 1.1 系统素材上传 (管理员)
 * @param {FormData} formData - 表单数据
 * @param {File} formData.file - 文件
 * @param {string} formData.type - 素材类型: IMAGE, VIDEO, AUDIO
 * @param {string} formData.copyrightStatus - 版权状态: FREE_COMMERCIAL, PAID
 * @param {string} formData.category - 分类 (可选)
 * @param {string} formData.tags - 标签，逗号分隔 (可选)
 */
export function adminUploadMaterial(formData) {
  return request({
    url: '/admin/materials',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 1.2 素材状态/审核管理
 * @param {string} materialId - 素材ID
 * @param {Object} data - 状态数据
 * @param {string} data.type - 素材类型: IMAGE, VIDEO, AUDIO
 * @param {string} data.status - 目标状态: NORMAL, BANNED, REVIEWING
 * @param {string} data.reason - 操作原因 (封禁时必填)
 */
export function updateMaterialStatus(materialId, data) {
  return request({
    url: `/admin/materials/${materialId}/status`,
    method: 'put',
    data
  })
}

/**
 * 1.3 标记素材版权
 * @param {string} materialId - 素材ID
 * @param {Object} data - 版权数据
 * @param {string} data.type - 素材类型: IMAGE, VIDEO, AUDIO
 * @param {string} data.copyrightStatus - 版权状态: FREE_COMMERCIAL, PAID, PERSONAL_USE
 */
export function updateMaterialCopyright(materialId, data) {
  return request({
    url: `/admin/materials/${materialId}/copyright`,
    method: 'put',
    data
  })
}

/**
 * ==================== 标签与分类服务 (Tag & Category Service) ====================
 */

/**
 * 2.1 获取热门标签/分类
 * @param {string} type - 素材类型 (可选)
 */
export function getHotTags(type) {
  return request({
    url: '/materials/tags/hot',
    method: 'get',
    params: { type }
  })
}

/**
 * 2.2 AI 自动识别标签 (辅助接口)
 * @param {Object} data - 识别数据
 * @param {string} data.materialUrl - 素材地址
 * @param {string} data.type - 素材类型: IMAGE, VIDEO
 */
export function predictTags(data) {
  return request({
    url: '/materials/tags/ai-predict',
    method: 'post',
    data
  })
}

/**
 * ==================== 模板服务 (Template Service) ====================
 */

/**
 * 获取模板列表
 * @param {Object} params - 查询参数
 */
export function getTemplates(params) {
  return request({
    url: '/market/templates',
    method: 'get',
    params
  })
}

/**
 * 上传模板
 * @param {FormData} formData - 表单数据
 */
export function uploadTemplate(formData) {
  return request({
    url: '/templates',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除模板
 * @param {string} templateId - 模板ID
 */
export function deleteTemplate(templateId) {
  return request({
    url: `/templates/${templateId}`,
    method: 'delete'
  })
}

/**
 * 使用模板
 * @param {string} templateId - 模板ID
 */
export function useTemplate(templateId) {
  return request({
    url: `/market/templates/${templateId}/purchase`,
    method: 'post'
  })
}
