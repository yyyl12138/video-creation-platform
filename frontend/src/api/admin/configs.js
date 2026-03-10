import request from '@/utils/request'

// 获取系统参数集合
export function fetchConfigsByCategory(category) {
  return request({
    url: `/admin/configs/${category}`,
    method: 'get'
  })
}

// 更新存储配置
export function updateStorageConfig(data) {
  return request({
    url: '/admin/configs/storage',
    method: 'put',
    data
  })
}

// 更新转码配置
export function updateTranscodingConfig(data) {
  return request({
    url: '/admin/configs/transcoding',
    method: 'put',
    data
  })
}

// 更新任务调度配置
export function updateSchedulingConfig(data) {
  return request({
    url: '/admin/configs/scheduling',
    method: 'put',
    data
  })
}

