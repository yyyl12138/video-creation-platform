import request from '@/utils/request'

// 获取模型列表
export function fetchModels(params) {
  return request({
    url: '/admin/models',
    method: 'get',
    params
  })
}

// 获取单个模型配置
export function getModelDetail(modelId) {
  return request({
    url: `/admin/ai-models/${modelId}`,
    method: 'get'
  })
}

// 新增或修改模型配置
export function saveModelConfig(data) {
  return request({
    url: '/admin/models',
    method: 'post',
    data
  })
}

// 启用/停用模型
export function updateModelStatus(modelId, data) {
  return request({
    url: `/admin/models/${modelId}/status`,
    method: 'put',
    data
  })
}

