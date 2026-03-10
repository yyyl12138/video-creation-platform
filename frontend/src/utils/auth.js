// 统一管理前端登录态与角色判断

export function getToken() {
  return localStorage.getItem('token')
}

export function getRefreshToken() {
  return localStorage.getItem('refreshToken')
}

export function setTokens({ token, refreshToken, expireIn }) {
  if (token) localStorage.setItem('token', token)
  if (refreshToken) localStorage.setItem('refreshToken', refreshToken)
  // 兼容后端 expireIn（秒）或使用默认 2 小时
  const ttlMs = expireIn ? Number(expireIn) * 1000 : 2 * 60 * 60 * 1000
  localStorage.setItem('tokenExpiry', String(Date.now() + ttlMs))
}

export function getUserInfo() {
  const raw = localStorage.getItem('userInfo')
  if (!raw) return null
  try {
    return JSON.parse(raw)
  } catch (e) {
    return null
  }
}

export function setUserInfo(userInfo) {
  if (!userInfo) return
  localStorage.setItem('userInfo', JSON.stringify(userInfo))
}

export function getRoles() {
  const info = getUserInfo()
  const roles = info?.roles
  return Array.isArray(roles) ? roles : []
}

// 角色名来自后端 roles.role_name（常见：ROLE_USER / ROLE_ADMIN / super_admin / 管理员...）
export function isAdminRoleName(roleName) {
  if (!roleName || typeof roleName !== 'string') return false
  const s = roleName.trim().toLowerCase()
  if (!s) return false
  // 明确排除普通用户
  if (s === 'role_user') return false
  // 兼容英文/中文命名
  return s.includes('admin') || roleName.includes('管理员')
}

export function isAdminRoles(roles) {
  if (!Array.isArray(roles)) return false
  return roles.some(isAdminRoleName)
}

export function isAdminUser(userInfo) {
  const roles = Array.isArray(userInfo) ? userInfo : (userInfo?.roles || getRoles())
  return isAdminRoles(roles)
}

export function clearAuthStorage() {
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('tokenExpiry')
  localStorage.removeItem('rememberedLogin')
  localStorage.removeItem('userInfo')
  localStorage.removeItem('userProfile')
  // 清除设备相关信息（用于多设备管理）
  const deviceKeys = Object.keys(localStorage).filter(key => key.startsWith('device_'))
  deviceKeys.forEach(key => localStorage.removeItem(key))
}

export function buildLoginRedirectQuery(toFullPath, mode) {
  const query = { redirect: toFullPath }
  if (mode) query.mode = mode
  return query
}

