import request from '@/utils/request'
import { setTokens, setUserInfo, clearAuthStorage as clearAuthStorageUtil } from '@/utils/auth'

/**
 * 发送验证码
 * @param {string} target - 目标手机号或邮箱
 * @param {string} type - 验证码类型 (login/register/reset)
 */
export function sendAuthCode(target, type = 'REGISTER') {
  return request({
    url: '/auth/verification-code',
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
    data: { username: account, password }
  }).then(res => {
    // 存储token信息
    if (res.data) {
      const { token, refreshToken, expireIn, userInfo } = res.data
      setTokens({ token, refreshToken, expireIn })
      if (userInfo) setUserInfo(userInfo)
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
      const { token, refreshToken, expireIn, userInfo } = res.data
      setTokens({ token, refreshToken, expireIn })
      if (userInfo) setUserInfo(userInfo)
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
    url: '/auth/password/reset',
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
  // 兼容老代码：保留此导出，但委托给 utils/auth
  clearAuthStorageUtil()
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