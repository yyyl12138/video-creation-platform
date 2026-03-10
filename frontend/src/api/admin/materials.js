import request from '@/utils/request'

// 管理员上传系统素材
export function uploadSystemMaterial(formData) {
  return request({
    url: '/admin/materials',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 素材状态/审核管理
export function updateMaterialStatus(materialId, data) {
  return request({
    url: `/admin/materials/${materialId}/status`,
    method: 'put',
    data
  })
}

// 标记素材版权
export function updateMaterialCopyright(materialId, data) {
  return request({
    url: `/admin/materials/${materialId}/copyright`,
    method: 'put',
    data
  })
}

