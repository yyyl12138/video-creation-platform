import request from '@/utils/request'

/**
 * 获取素材列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页大小
 * @param {string} params.keyword - 关键词搜索
 * @param {string} params.type - 类型筛选 (image/video/audio)
 * @param {boolean} params.isSystem - 是否系统素材 (true: 官方素材库, false: 个人素材库, null: 全部)
 */
export function getMaterials(params) {
  return request({
    url: '/materials',
    method: 'get',
    params
  })
}

/**
 * 获取素材详情
 * @param {string} materialId - 素材ID
 * @param {string} type - 素材类型 (IMAGE/VIDEO/AUDIO)
 */
export function getMaterialDetail(materialId, type) {
  return request({
    url: `/materials/${materialId}`,
    method: 'get',
    params: { type }
  })
}

/**
 * 上传素材
 * @param {FormData} data - 表单数据
 * @param {File} data.file - 文件数据
 * @param {string} data.type - 素材类型 (IMAGE/VIDEO/AUDIO)
 * @param {string} data.name - 素材名称
 * @param {string} data.albumId - 目标相册ID (可选)
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

/**
 * 批量删除素材
 * @param {Array} materialIds - 素材ID列表
 * @param {string} type - 素材类型 (IMAGE/VIDEO/AUDIO)
 */
export function batchDeleteMaterials(materialIds, type) {
  return request({
    url: '/materials/batch',
    method: 'delete',
    data: { materialIds, type }
  })
}

/**
 * 获取热门标签/分类
 * @param {string} type - 素材类型 (IMAGE/VIDEO/AUDIO)
 */
export function getHotTags(type) {
  return request({
    url: '/materials/tags/hot',
    method: 'get',
    params: { type }
  })
}

/**
 * AI 自动识别标签
 * @param {string} materialUrl - 素材地址
 * @param {string} type - 素材类型 (IMAGE/VIDEO)
 */
export function predictTags(materialUrl, type) {
  return request({
    url: '/materials/tags/ai-predict',
    method: 'post',
    data: { materialUrl, type }
  })
}

/**
 * 管理员素材上传
 * @param {FormData} data - 表单数据
 * @param {File} data.file - 文件数据
 * @param {string} data.type - 素材类型 (IMAGE/VIDEO/AUDIO)
 * @param {string} data.copyrightStatus - 版权状态
 * @param {string} data.category - 分类
 * @param {string} data.tags - 标签
 */
export function adminUploadMaterial(data) {
  return request({
    url: '/admin/materials',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新素材状态/审核 (管理员)
 * @param {string} materialId - 素材ID
 * @param {Object} data - 更新数据
 * @param {string} data.type - 素材类型
 * @param {string} data.status - 目标状态
 * @param {string} data.reason - 操作原因
 */
export function updateMaterialStatus(materialId, data) {
  return request({
    url: `/admin/materials/${materialId}/status`,
    method: 'put',
    data
  })
}

/**
 * 标记素材版权 (管理员)
 * @param {string} materialId - 素材ID
 * @param {Object} data - 版权数据
 * @param {string} data.type - 素材类型
 * @param {string} data.copyrightStatus - 版权状态
 */
export function updateMaterialCopyright(materialId, data) {
  return request({
    url: `/admin/materials/${materialId}/copyright`,
    method: 'put',
    data
  })
}

/**
 * 获取模板列表 (市场模板)
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.sort - 排序方式 (HOT/NEW/PRICE_ASC)
 * @param {string} params.keyword - 搜索关键词
 * @param {number} params.maxPrice - 价格过滤
 */
export function getTemplates(params) {
  return request({
    url: '/market/templates',
    method: 'get',
    params
  })
}

/**
 * 获取模板详情
 * @param {string} templateId - 模板ID
 */
export function getTemplateDetail(templateId) {
  return request({
    url: `/market/templates/${templateId}`,
    method: 'get'
  })
}

/**
 * 上传模板 (创作者)
 * @param {FormData} data - 表单数据
 */
export function uploadTemplate(data) {
  return request({
    url: '/templates',
    method: 'post',
    data,
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
 * 使用模板/购买模板
 * @param {string} templateId - 模板ID
 */
export function useTemplate(templateId) {
  return request({
    url: `/market/templates/${templateId}/purchase`,
    method: 'post'
  })
}

/**
 * 获取我的模板列表 (创作者)
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 */
export function getMyTemplates(params) {
  return request({
    url: '/templates/me',
    method: 'get',
    params
  })
}

/**
 * 创建/更新模板草稿
 * @param {Object} data - 模板数据
 * @param {string} data.templateId - 模板ID(更新时必填)
 * @param {string} data.name - 模板名称
 * @param {string} data.description - 描述
 * @param {Object} data.workflowConfig - 工作流配置
 * @param {string} data.demoVideoUrl - 演示视频
 * @param {number} data.price - 价格
 * @param {boolean} data.isPublic - 是否公开
 */
export function saveTemplate(data) {
  return request({
    url: '/templates',
    method: 'post',
    data
  })
}
