import request from '@/utils/request'

/**
 * 发送验证码
 * @param {string} target - 目标手机号或邮箱
 * @param {string} type - 验证码类型 (login/register/reset)
 */
export function sendAuthCode(target, type = 'login') {
  return request({
    url: '/auth/send-code',
    method: 'post',
    data: { target, type }
  })
}

/**
 * 用户名密码登录
 * @param {string} account - 账号
 * @param {string} password - 密码
 */
export function loginByPassword(account, password) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: { account, password }
  }).then(res => {
    // 存储token信息
    if (res.data) {
      const { token, refreshToken } = res.data
      localStorage.setItem('token', token)
      localStorage.setItem('refreshToken', refreshToken)
      localStorage.setItem('tokenExpiry', Date.now() + 2 * 60 * 60 * 1000) // 2小时有效期
    }
    return res
  })
}

/**
 * 验证码登录
 * @param {string} phone - 手机号
 * @param {string} code - 验证码
 */
export function loginBySms(phone, code) {
  return request({
    url: '/auth/login-by-sms',
    method: 'post',
    data: { phone, code }
  }).then(res => {
    // 存储token信息
    if (res.data) {
      const { token, refreshToken } = res.data
      localStorage.setItem('token', token)
      localStorage.setItem('refreshToken', refreshToken)
      localStorage.setItem('tokenExpiry', Date.now() + 2 * 60 * 60 * 1000) // 2小时有效期
    }
    return res
  })
}

/**
 * 刷新Token
 * @param {string} refreshToken - 刷新令牌
 */
export function refreshToken(refreshToken) {
  return request({
    url: '/auth/refresh',
    method: 'post',
    data: { refreshToken }
  })
}

/**
 * 用户注册
 * @param {object} userData - 用户信息
 */
export function registerUser(userData) {
  return request({
    url: '/auth/register',
    method: 'post',
    data: userData
  })
}

/**
 * 重置密码
 * @param {object} data - 重置密码数据
 */
export function resetPassword(data) {
  return request({
    url: '/auth/reset-password',
    method: 'post',
    data
  })
}

/**
 * 退出登录（包含本地存储清理）
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  }).then(res => {
    // 清除所有认证相关存储
    clearAuthStorage()
    return res
  }).catch(error => {
    // 即使API调用失败也清理本地存储
    clearAuthStorage()
    throw error
  })
}

/**
 * 清理所有认证相关的本地存储
 */
export function clearAuthStorage() {
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('tokenExpiry')
  localStorage.removeItem('rememberedLogin')
  // 清除设备相关信息（用于多设备管理）
  const deviceKeys = Object.keys(localStorage).filter(key => key.startsWith('device_'))
  deviceKeys.forEach(key => localStorage.removeItem(key))
}

/**
 * 验证Token
 */
export function verifyToken() {
  return request({
    url: '/auth/verify',
    method: 'get'
  })
}