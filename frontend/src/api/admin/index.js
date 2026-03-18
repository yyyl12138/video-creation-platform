import request from '@/utils/request'

/**
 * ==================== 审核任务管理 (Review Task Management) ====================
 */

/**
 * 1.1 获取审核列表 (管理员)
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} params.status - 状态筛选: PENDING, PASSED, REJECTED, RE_AUDIT
 * @param {string} params.reviewerId - 审核员ID (可选)
 * @param {string} params.contentType - 内容类型: VIDEO, IMAGE
 */
export function getReviewList(params) {
  return request({
    url: '/admin/reviews',
    method: 'get',
    params
  })
}

/**
 * 1.2 获取审核详情
 * @param {string} reviewId - 审核ID
 */
export function getReviewDetail(reviewId) {
  return request({
    url: `/admin/reviews/${reviewId}`,
    method: 'get'
  })
}

/**
 * 1.3 提交审核决定
 * @param {string} reviewId - 审核ID
 * @param {Object} data - 审核数据
 * @param {string} data.status - 决定结果: PASSED, REJECTED
 * @param {string} data.rejectReason - 驳回原因 (status=REJECTED时必填)
 * @param {string} data.suggestions - 修改建议 (可选)
 */
export function submitReviewDecision(reviewId, data) {
  return request({
    url: `/admin/reviews/${reviewId}/decision`,
    method: 'put',
    data
  })
}

/**
 * ==================== 审核规则配置 (Review Rules Config) ====================
 */

/**
 * 2.1 获取规则列表
 * @param {string} ruleType - 规则类型 (可选)
 */
export function getReviewRules(ruleType) {
  return request({
    url: '/admin/review-rules',
    method: 'get',
    params: { ruleType }
  })
}

/**
 * 2.2 新增/修改规则
 * @param {Object} data - 规则数据
 * @param {number} data.ruleId - ID (更新时必填)
 * @param {string} data.ruleName - 规则名称
 * @param {string} data.ruleType - 类型: TEXT, IMAGE, VIDEO
 * @param {Object} data.conditions - 触发条件
 * @param {Object} data.actions - 执行动作
 * @param {number} data.priority - 优先级 (默认5)
 */
export function saveReviewRule(data) {
  return request({
    url: '/admin/review-rules',
    method: 'post',
    data
  })
}

/**
 * 2.3 启用/禁用规则
 * @param {number} ruleId - 规则ID
 * @param {Object} data - 状态数据
 * @param {string} data.status - ENABLE, DISABLE
 */
export function toggleReviewRule(ruleId, data) {
  return request({
    url: `/admin/review-rules/${ruleId}/status`,
    method: 'put',
    data
  })
}

/**
 * ==================== 违规举报服务 (Report Service) ====================
 */

/**
 * 3.1 提交举报 (用户端)
 * @param {Object} data - 举报数据
 * @param {string} data.targetId - 被举报内容ID
 * @param {string} data.targetType - 类型: VIDEO, COMMENT
 * @param {string} data.reasonType - 举报类型: 涉黄, 暴力, 侵权, 其他
 * @param {string} data.description - 详细描述 (可选)
 */
export function submitReport(data) {
  return request({
    url: '/reports',
    method: 'post',
    data
  })
}

/**
 * ==================== AI 模型接入管理 (AI Model Management) ====================
 */

/**
 * 1.1 获取模型列表 (管理员)
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} params.type - 模型类型: TEXT, IMAGE, VIDEO
 * @param {string} params.provider - 提供商: OpenAI, Local
 */
export function getModelList(params) {
  return request({
    url: '/admin/models',
    method: 'get',
    params
  })
}

/**
 * 1.2 获取单个模型配置
 * @param {number} modelId - 模型ID
 */
export function getModelDetail(modelId) {
  return request({
    url: `/admin/ai-models/${modelId}`,
    method: 'get'
  })
}

/**
 * 1.3 新增/修改模型配置
 * @param {Object} data - 模型数据
 * @param {number} data.modelId - 模型ID (更新时携带)
 * @param {string} data.modelName - 显示名称
 * @param {string} data.modelKey - 调用标识 (唯一)
 * @param {string} data.provider - 提供商
 * @param {string} data.modelType - 模型类型: TEXT, IMAGE, VIDEO, AUDIO
 * @param {string} data.apiEndpoint - 接口地址
 * @param {Object} data.apiConfig - 核心动态参数 (apiKey等)
 * @param {number} data.billingMode - 计费模式
 * @param {number} data.unitPrice - 单价
 * @param {boolean} data.isActive - 是否启用
 */
export function saveModel(data) {
  return request({
    url: '/admin/models',
    method: 'post',
    data
  })
}

/**
 * 1.4 启用/停用模型
 * @param {number} modelId - 模型ID
 * @param {Object} data - 状态数据
 * @param {boolean} data.isActive - 是否启用
 */
export function toggleModel(modelId, data) {
  return request({
    url: `/admin/models/${modelId}/status`,
    method: 'put',
    data
  })
}

/**
 * ==================== 全局动态参数配置 (Dynamic System Configs) ====================
 */

/**
 * 2.1 获取某类系统参数集合
 * @param {string} category - 分类: STORAGE, SCHEDULING, TRANSCODING
 */
export function getSystemConfig(category) {
  return request({
    url: `/admin/configs/${category}`,
    method: 'get'
  })
}

/**
 * 2.2 全量更新特定分类参数
 * @param {string} category - 分类
 * @param {Object} data - 参数键值对
 */
export function updateSystemConfig(category, data) {
  return request({
    url: `/admin/configs/${category}`,
    method: 'put',
    data
  })
}

/**
 * ==================== 运营驾驶舱服务 (Admin Dashboard Service) ====================
 */

/**
 * 1.1 获取核心指标卡片 (Dashboard Cards)
 */
export function getStatsOverview() {
  return request({
    url: '/admin/stats/overview',
    method: 'get'
  })
}

/**
 * 1.2 获取趋势图表数据 (Trend Charts)
 * @param {Object} params - 查询参数
 * @param {string} params.startDate - 开始日期 (yyyy-MM-dd)
 * @param {string} params.endDate - 结束日期 (yyyy-MM-dd)
 * @param {string} params.metric - 指标类型: NEW_USERS, ACTIVE_USERS, TASK_VOLUME
 */
export function getStatsTrend(params) {
  return request({
    url: '/admin/stats/trend',
    method: 'get',
    params
  })
}

/**
 * 2.1 任务类型分布统计
 */
export function getTaskDistribution() {
  return request({
    url: '/admin/stats/tasks/distribution',
    method: 'get'
  })
}

/**
 * 2.2 任务失败原因分析
 */
export function getTaskFailureAnalysis() {
  return request({
    url: '/admin/stats/tasks/failure-analysis',
    method: 'get'
  })
}

/**
 * 3.1 获取财务报表
 * @param {Object} params - 查询参数
 * @param {string} params.period - 周期: daily, weekly, monthly
 * @param {string} params.date - 统计日期
 */
export function getFinanceReport(params) {
  return request({
    url: '/admin/stats/finance',
    method: 'get',
    params
  })
}

/**
 * ==================== 创作者个人数据服务 (Creator Personal Stats) ====================
 */

/**
 * 4.1 获取个人核心数据
 */
export function getCreatorStats() {
  return request({
    url: '/stats/creator/me',
    method: 'get'
  })
}

/**
 * 4.2 模版表现分析
 * @param {number} days - 最近天数 (7/30)
 */
export function getTemplateAnalysis(days) {
  return request({
    url: '/stats/creator/templates/analysis',
    method: 'get',
    params: { days }
  })
}
