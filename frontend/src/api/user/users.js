import request from '@/utils/request'

/**
 * 获取当前用户信息接口
 * @description GET请求，获取当前登录用户的详细档案（聚合 users 和 user_profiles 表）
 * @returns {Promise<Object>} 返回用户完整信息，数据格式如下：
 * {
 *   "userId": "U20260113001",
 *   "username": "developer_wu",
 *   "email": "wu@example.com",
 *   "phone": "13800138000",
 *   "avatarUrl": "http://oss.../avatar.jpg",
 *   "profile": {
 *     "realName": "吴阳",
 *     "gender": "男", // 枚举值
 *     "birthday": "2000-01-01",
 *     "country": "中国",
 *     "city": "贵阳",
 *     "bio": "全栈开发者"
 *   },
 *   "vipStatus": "NORMAL",         // 会员状态: NORMAL, VIP, SVIP
 *   "vipExpireDate": null,         // 会员过期时间
 *   "creatorStatus": "NONE",       // 创作者身份: NONE, APPLIED, APPROVED, REJECTED
 *   "status": "正常"               // 账号状态：正常、封禁
 * }
 */
export function getUserProfile() {
  return request({
    url: '/users/me',
    method: 'get'
  })
}

/**
 * 更新个人资料接口
 * @description PUT请求，更新用户的扩展信息
 * @param {Object} profileData - 个人资料更新数据，参数格式如下：
 * {
 *   "realName": "2B",
 *   "gender": "男",
 *   "birthday": "2018-12-31",
 *   "country": "zh-CN",
 *   "city": "武汉",
 *   "bio": "大帅哥"
 * }
 * @returns {Promise<boolean>} 返回true表示更新成功
 */
export function updateUserProfile(profileData) {
  return request({
    url: '/users/me/profile',
    method: 'put',
    data: profileData
  })
}

/**
 * 修改密码接口
 * @description PUT请求，修改当前登录用户的密码
 * @param {Object} passwordData - 密码修改数据，参数格式如下：
 * {
 *   "oldPassword": "123123",
 *   "newPassword": "456456"
 * }
 * @returns {Promise<boolean>} 返回true表示修改成功
 */
export function changePassword(passwordData) {
  return request({
    url: '/users/me/password',
    method: 'put',
    data: passwordData
  })
}

/**
 * 申请成为模板创作者接口
 * @description POST请求，普通用户提交申请成为模板创作者
 * @param {Object} applyData - 创作者申请数据，参数格式如下：
 * {
 *   "introduction": "大帅哥",
 *   "portfolioUrl": "127.0.0.1"
 * }
 * @returns {Promise<boolean>} 返回true表示申请提交成功（默认批准，待优化）
 */
export function applyCreator(applyData) {
  return request({
    url: '/users/me/apply-creator',
    method: 'post',
    data: applyData
  })
}

/**
 * 用户头像上传
 * @param {FormData} formData - 包含头像文件的表单数据
 * @param {File} formData.file - 图片文件，最大2MB，支持jpg/png格式
 * @returns {Promise<Object>} 返回上传后的图片信息
 * {
 *   "avatarUrl": "http://oss.huike.com/uploads/avatar/u1001_20260113.jpg",
 *   "fileSize": 1048576
 * }
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