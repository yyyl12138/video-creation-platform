import request from '@/utils/request'

// 获取 TTS 语音列表
export function fetchTtsConfigs() {
  return request({
    url: '/admin/tts-configs',
    method: 'get'
  })
}

// 配置 TTS 参数
export function saveTtsConfig(data) {
  return request({
    url: '/admin/tts-configs',
    method: 'post',
    data
  })
}

