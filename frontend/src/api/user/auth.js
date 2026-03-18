import request from '@/utils/request'

/**
 * ==================== 认证服务 (Auth Service) ====================
 */

/**
 * 1.1 用户注册
 * @param {Object} data - 注册数据
 * @param {string} data.username - 用户名 (4-20字符)
 * @param {string} data.email - 邮箱 (可选)
 * @param {string} data.password - 密码 (6-20字符)
 * @param {string} data.phone - 手机号 (可选)
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 兼容旧版本命名
export const registerUser = register

/**
 * 1.2 用户登录
 * @param {Object} data - 登录数据
 * @param {string} data.username - 用户名/邮箱
 * @param {string} data.password - 密码
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 兼容旧版本命名 - 密码登录
export function loginByPassword(username, password) {
  return login({ username, password })
}

// 兼容旧版本命名 - 短信登录
export function loginBySms(phone, code) {
  return request({
    url: '/auth/login-sms',
    method: 'post',
    data: { phone, code }
  })
}

/**
 * 1.3 发送验证码 (注册/找回密码)
 * @param {Object} data - 验证码数据
 * @param {string} data.target - 手机号或邮箱
 * @param {string} data.type - 场景类型: REGISTER, RESET_PWD
 */
export function sendVerificationCode(data) {
  return request({
    url: '/auth/verification-code',
    method: 'post',
    data
  })
}

// 兼容旧版本命名
export const sendAuthCode = sendVerificationCode

/**
 * 1.4 重置密码 (忘记密码流程)
 * @param {Object} data - 重置密码数据
 * @param {string} data.target - 手机号或邮箱
 * @param {string} data.code - 验证码 (6位)
 * @param {string} data.newPassword - 新密码 (6-20字符)
 */
export function resetPassword(data) {
  return request({
    url: '/auth/password/reset',
    method: 'post',
    data
  })
}

/**
 * 1.5 用户登出
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}
