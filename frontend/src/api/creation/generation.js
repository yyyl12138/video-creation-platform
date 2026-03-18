import request from '@/utils/request'

/**
 * ==================== AI 辅助创作服务 (AI Auxiliary Service) ====================
 */

/**
 * 1.1 AI 通用文本生成 (同步任务)
 * @param {Object} data - 生成数据
 * @param {string} data.prompt - 提示词 (长度<5000)
 * @param {string} data.modelKey - 指定模型 (可选)
 */
export function generateText(data) {
  return request({
    url: '/creation/text/generate',
    method: 'post',
    data
  })
}

/**
 * ==================== 核心生成任务服务 (Core Generation Service) ====================
 */

/**
 * 2.1 提交生成任务
 * @param {Object} data - 任务数据
 * @param {string} data.taskType - 任务类型: TEXT_TO_IMAGE, TEXT_TO_VIDEO, IMAGE_TO_VIDEO
 * @param {string} data.modelName - 模型名称
 * @param {string} data.templateId - 使用的模板ID (可选)
 * @param {Object} data.inputConfig - 核心参数 (prompt, initImageUrl, ratio, duration等)
 * @param {number} data.priority - 优先级 (默认5，会员可传高值)
 */
export function submitGenerationTask(data) {
  return request({
    url: '/generation/tasks',
    method: 'post',
    data
  })
}

/**
 * 2.2 查询任务详情/状态
 * @param {string} taskId - 任务ID
 */
export function getTaskStatus(taskId) {
  return request({
    url: `/generation/tasks/${taskId}`,
    method: 'get'
  })
}

/**
 * 2.3 获取历史任务列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {string} params.status - 状态筛选: SUCCESS, FAILED
 * @param {string} params.taskType - 筛选类型
 */
export function getTaskList(params) {
  return request({
    url: '/generation/tasks',
    method: 'get',
    params
  })
}

/**
 * 2.4 取消任务
 * @param {string} taskId - 任务ID
 */
export function cancelGenerationTask(taskId) {
  return request({
    url: `/generation/tasks/${taskId}/cancel`,
    method: 'post'
  })
}
