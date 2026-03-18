import request from '@/utils/request'

/**
 * ==================== 用户信息服务 (User Service) ====================
 */

/**
 * 2.1 获取当前用户信息
 */
export function getUserProfile() {
  return request({
    url: '/users/me',
    method: 'get'
  })
}

/**
 * 2.2 更新个人资料
 * @param {Object} data - 个人资料数据
 */
export function updateProfile(data) {
  return request({
    url: '/users/me/profile',
    method: 'put',
    data
  })
}

// 兼容旧版本命名
export const updateUserProfile = updateProfile

/**
 * 2.3 修改密码
 * @param {Object} data - 密码数据
 * @param {string} data.oldPassword - 旧密码
 * @param {string} data.newPassword - 新密码
 */
export function changePassword(data) {
  return request({
    url: '/users/me/password',
    method: 'put',
    data
  })
}

/**
 * 2.4 申请成为模版创作者
 * @param {Object} data - 申请数据
 */
export function applyCreator(data) {
  return request({
    url: '/users/me/apply-creator',
    method: 'post',
    data
  })
}

/**
 * 2.5 开通/续费会员
 * @param {Object} data - 订阅数据
 * @param {number} data.planId - 会员套餐ID
 * @param {string} data.payChannel - 支付方式
 */
export function subscribeVip(data) {
  return request({
    url: '/users/me/vip-subscription',
    method: 'post',
    data
  })
}

// 兼容旧版本命名
export const purchaseVipSubscription = subscribeVip

/**
 * 2.6 用户头像上传
 * @param {FormData} formData - 表单数据
 */
export function uploadAvatar(formData) {
  return request({
    url: '/users/me/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 2.7 注销账户
 * @param {Object} data - 注销数据
 */
export function deleteAccount(data) {
  return request({
    url: '/users/me',
    method: 'delete',
    data
  })
}

/**
 * ==================== 管理员用户管理 (Admin User Management) ====================
 */

/**
 * 4.1 获取用户列表 (管理员)
 * @param {Object} params - 查询参数
 */
export function getUserList(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

/**
 * 4.2 封禁/解封用户
 * @param {string} userId - 用户ID
 * @param {Object} data - 状态数据
 */
export function updateUserStatus(userId, data) {
  return request({
    url: `/admin/users/${userId}/status`,
    method: 'put',
    data
  })
}

/**
 * 4.3 获取用户详情 (管理员视图)
 * @param {string} userId - 用户ID
 */
export function getUserDetail(userId) {
  return request({
    url: `/admin/users/${userId}`,
    method: 'get'
  })
}
