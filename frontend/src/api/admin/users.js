import request from '@/utils/request'

// 管理员获取用户列表
export function fetchAdminUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

// 管理员修改用户状态（封禁/解封）
export function updateUserStatus(userId, data) {
  return request({
    url: `/admin/users/${userId}/status`,
    method: 'put',
    data
  })
}

// 管理员获取用户详情
export function getAdminUserDetail(userId) {
  return request({
    url: `/admin/users/${userId}`,
    method: 'get'
  })
}

