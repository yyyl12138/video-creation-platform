<template>
  <div class="admin-page">
    <div class="page-header">
      <div>
        <h2>系统配置</h2>
        <p class="subtitle">存储、转码与任务调度等全局系统参数</p>
      </div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="存储配置" name="storage">
        <el-card shadow="never">
          <el-form
            ref="storageFormRef"
            :model="storageForm"
            label-width="120px"
          >
            <el-form-item label="存储类型">
              <el-select v-model="storageForm.storageType" style="width: 200px">
                <el-option label="视频" value="VIDEO" />
                <el-option label="素材" value="MATERIAL" />
                <el-option label="日志" value="LOG" />
              </el-select>
            </el-form-item>
            <el-form-item label="基础路径">
              <el-input v-model="storageForm.basePath" />
            </el-form-item>
            <el-form-item label="最大容量（字节）">
              <el-input-number v-model="storageForm.maxCapacity" :min="0" style="width: 240px" />
            </el-form-item>
            <el-form-item label="允许文件类型 JSON">
              <el-input
                v-model="storageForm.fileTypes"
                type="textarea"
                :rows="3"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingStorage" @click="submitStorage">
                保存存储配置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="转码配置" name="transcoding">
        <el-card shadow="never">
          <el-form
            ref="transFormRef"
            :model="transForm"
            label-width="120px"
          >
            <el-form-item label="视频编码">
              <el-select v-model="transForm.videoCodec" style="width: 200px">
                <el-option label="H.264" value="H.264" />
                <el-option label="H.265" value="H.265" />
              </el-select>
            </el-form-item>
            <el-form-item label="质量预设">
              <el-select v-model="transForm.qualityPreset" style="width: 200px">
                <el-option label="高" value="HIGH" />
                <el-option label="中" value="MEDIUM" />
                <el-option label="低" value="LOW" />
              </el-select>
            </el-form-item>
            <el-form-item label="帧率">
              <el-input-number v-model="transForm.frameRate" :min="1" :max="120" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingTrans" @click="submitTrans">
                保存转码配置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="任务调度" name="scheduling">
        <el-card shadow="never">
          <el-form
            ref="scheduleFormRef"
            :model="scheduleForm"
            label-width="140px"
          >
            <el-form-item label="最大生成并发数">
              <el-input-number v-model="scheduleForm.maxConcurrentGeneration" :min="1" :max="100" />
            </el-form-item>
            <el-form-item label="任务队列长度">
              <el-input-number v-model="scheduleForm.taskQueueLimit" :min="1" :max="1000" />
            </el-form-item>
            <el-form-item label="CPU 保护阈值(%)">
              <el-input-number v-model="scheduleForm.cpuThresholdPercent" :min="10" :max="100" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingSchedule" @click="submitSchedule">
                保存调度配置
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  fetchConfigsByCategory,
  updateStorageConfig,
  updateTranscodingConfig,
  updateSchedulingConfig
} from '@/api/admin/configs'

const activeTab = ref('storage')

const storageForm = reactive({
  storageType: 'VIDEO',
  basePath: '',
  maxCapacity: 0,
  fileTypes: ''
})
const savingStorage = ref(false)

const transForm = reactive({
  videoCodec: 'H.264',
  qualityPreset: 'HIGH',
  frameRate: 25
})
const savingTrans = ref(false)

const scheduleForm = reactive({
  maxConcurrentGeneration: 3,
  taskQueueLimit: 50,
  cpuThresholdPercent: 80
})
const savingSchedule = ref(false)

const loadSchedulingConfig = async () => {
  try {
    const res = await fetchConfigsByCategory('SCHEDULING')
    const config = res.data?.config
    if (config) {
      scheduleForm.maxConcurrentGeneration = config.maxConcurrentGeneration ?? scheduleForm.maxConcurrentGeneration
      scheduleForm.taskQueueLimit = config.taskQueueLimit ?? scheduleForm.taskQueueLimit
      scheduleForm.cpuThresholdPercent = config.cpuThresholdPercent ?? scheduleForm.cpuThresholdPercent
    }
  } catch (e) {
    // 忽略加载错误，使用默认值
  }
}

const submitStorage = async () => {
  try {
    savingStorage.value = true
    let fileTypesObj
    if (storageForm.fileTypes) {
      try {
        fileTypesObj = JSON.parse(storageForm.fileTypes)
      } catch (e) {
        ElMessage.error('文件类型 JSON 格式不正确')
        savingStorage.value = false
        return
      }
    }
    await updateStorageConfig({
      storageType: storageForm.storageType,
      basePath: storageForm.basePath,
      maxCapacity: storageForm.maxCapacity || undefined,
      fileTypes: fileTypesObj
    })
    ElMessage.success('存储配置已保存')
  } catch (e) {
    ElMessage.error('保存存储配置失败')
  } finally {
    savingStorage.value = false
  }
}

const submitTrans = async () => {
  try {
    savingTrans.value = true
    await updateTranscodingConfig({
      videoCodec: transForm.videoCodec,
      qualityPreset: transForm.qualityPreset,
      frameRate: transForm.frameRate || undefined
    })
    ElMessage.success('转码配置已保存')
  } catch (e) {
    ElMessage.error('保存转码配置失败')
  } finally {
    savingTrans.value = false
  }
}

const submitSchedule = async () => {
  try {
    savingSchedule.value = true
    await updateSchedulingConfig({
      maxConcurrentGeneration: scheduleForm.maxConcurrentGeneration,
      taskQueueLimit: scheduleForm.taskQueueLimit,
      cpuThresholdPercent: scheduleForm.cpuThresholdPercent || undefined
    })
    ElMessage.success('调度配置已保存')
  } catch (e) {
    ElMessage.error('保存调度配置失败')
  } finally {
    savingSchedule.value = false
  }
}

onMounted(() => {
  loadSchedulingConfig()
})
</script>

<style scoped>
.admin-page {
  padding: 4px;
}

.page-header {
  margin-bottom: 8px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6b7280;
}
</style>
