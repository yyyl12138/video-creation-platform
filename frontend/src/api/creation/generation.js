import request from '@/utils/request'

/**
 * 提交 AI 生成任务
 * @param {Object} data - 任务数据
 * @param {string} data.taskType - 任务类型 (TEXT_TO_TEXT, TEXT_TO_IMAGE, TEXT_TO_VIDEO, IMAGE_TO_VIDEO)
 * @param {string} data.modelName - 模型名称 (如 "Kling", "Wanx v1")
 * @param {Object} data.inputConfig - 输入配置
 * @param {string} data.inputConfig.prompt - 提示词
 * @param {string} data.inputConfig.negativePrompt - 负面提示词
 * @param {string} data.inputConfig.ratio - 比例
 * @param {number} data.inputConfig.duration - 时长 (视频)
 */
export function submitGenerationTask(data) {
  return request({
    url: '/generation/tasks',
    method: 'post',
    data
  })
}

/**
 * 查询任务状态
 * @param {string} taskId - 任务ID
 */
export function getTaskStatus(taskId) {
  return request({
    url: `/generation/tasks/${taskId}`,
    method: 'get'
  })
}

/**
 * 获取任务列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页大小
 * @param {string} params.taskType - 任务类型筛选
 * @param {string} params.status - 状态筛选
 */
export function getTaskList(params) {
  return request({
    url: '/generation/tasks',
    method: 'get',
    params
  })
}

// generation.js 中需要添加
/**
 * 取消任务
 * @param {string} taskId - 任务ID
 */
export function cancelGenerationTask(taskId) {
  return request({
    url: '/generation/tasks/${taskId}/cancel',
    method: 'post'
  })
}