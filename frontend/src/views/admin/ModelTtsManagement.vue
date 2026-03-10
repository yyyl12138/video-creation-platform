<template>
  <div class="admin-page">
    <div class="page-header">
      <div>
        <h2>模型与 TTS 管理</h2>
        <p class="subtitle">配置 AI 文本 / 图像 / 视频模型，以及 TTS 语音参数</p>
      </div>
    </div>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="AI 模型" name="models">
        <el-card shadow="never">
          <div class="toolbar">
            <el-space>
              <el-select
                v-model="modelFilter.type"
                placeholder="模型类型"
                clearable
                style="width: 140px"
              >
                <el-option label="文本" value="TEXT" />
                <el-option label="图片" value="IMAGE" />
                <el-option label="视频" value="VIDEO" />
                <el-option label="音频" value="AUDIO" />
              </el-select>
              <el-select
                v-model="modelFilter.provider"
                placeholder="提供商"
                clearable
                style="width: 140px"
              >
                <el-option label="OpenAI" value="OpenAI" />
                <el-option label="Local" value="Local" />
              </el-select>
              <el-button type="primary" @click="loadModels">
                <el-icon><Search /></el-icon>
                查询
              </el-button>
            </el-space>
            <el-button type="primary" @click="openModelDialog()">
              <el-icon><Plus /></el-icon>
              新增模型
            </el-button>
          </div>

          <el-table
            v-loading="modelsLoading"
            :data="modelList"
            border
            stripe
          >
            <el-table-column prop="modelId" label="ID" width="80" />
            <el-table-column prop="modelName" label="名称" min-width="160" />
            <el-table-column prop="modelKey" label="标识" min-width="140" />
            <el-table-column prop="provider" label="提供商" width="120" />
            <el-table-column prop="modelType" label="类型" width="100" />
            <el-table-column prop="unitPrice" label="单价" width="100" />
            <el-table-column prop="isActive" label="启用" width="90">
              <template #default="{ row }">
                <el-tag :type="row.isActive ? 'success' : 'info'" size="small">
                  {{ row.isActive ? '启用' : '停用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click="openModelDialog(row)">
                  编辑
                </el-button>
                <el-button
                  link
                  :type="row.isActive ? 'warning' : 'success'"
                  size="small"
                  @click="toggleModelStatus(row)"
                >
                  {{ row.isActive ? '停用' : '启用' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="TTS 配置" name="tts">
        <el-card shadow="never">
          <div class="toolbar">
            <span>系统 TTS 语音列表</span>
            <el-button type="primary" @click="openTtsDialog()">
              <el-icon><Plus /></el-icon>
              新增 TTS
            </el-button>
          </div>
          <el-table
            v-loading="ttsLoading"
            :data="ttsList"
            border
            stripe
          >
            <el-table-column prop="ttsId" label="ID" width="80" />
            <el-table-column prop="modelName" label="模型名称" min-width="180" />
            <el-table-column prop="voiceType" label="音色" width="100" />
            <el-table-column prop="languageType" label="语言" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'ENABLE' ? 'success' : 'info'" size="small">
                  {{ row.status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 模型编辑弹窗 -->
    <el-dialog
      v-model="modelDialogVisible"
      :title="modelForm.modelId ? '编辑模型' : '新增模型'"
      width="560px"
    >
      <el-form
        ref="modelFormRef"
        :model="modelForm"
        :rules="modelRules"
        label-width="100px"
      >
        <el-form-item label="模型名称" prop="modelName">
          <el-input v-model="modelForm.modelName" />
        </el-form-item>
        <el-form-item label="调用标识" prop="modelKey">
          <el-input v-model="modelForm.modelKey" />
        </el-form-item>
        <el-form-item label="提供商" prop="provider">
          <el-input v-model="modelForm.provider" />
        </el-form-item>
        <el-form-item label="模型类型" prop="modelType">
          <el-select v-model="modelForm.modelType">
            <el-option label="文本" value="TEXT" />
            <el-option label="图片" value="IMAGE" />
            <el-option label="视频" value="VIDEO" />
            <el-option label="音频" value="AUDIO" />
          </el-select>
        </el-form-item>
        <el-form-item label="接口地址" prop="apiEndpoint">
          <el-input v-model="modelForm.apiEndpoint" />
        </el-form-item>
        <el-form-item label="鉴权配置 JSON" prop="apiConfig">
          <el-input
            v-model="modelForm.apiConfig"
            type="textarea"
            :rows="4"
            placeholder='如：{"apiKey": "sk-***"}'
          />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number v-model="modelForm.unitPrice" :min="0" :step="0.001" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="modelDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="modelSaving" @click="submitModel">
          保 存
        </el-button>
      </template>
    </el-dialog>

    <!-- TTS 编辑弹窗 -->
    <el-dialog
      v-model="ttsDialogVisible"
      title="新增 / 配置 TTS"
      width="520px"
    >
      <el-form
        ref="ttsFormRef"
        :model="ttsForm"
        :rules="ttsRules"
        label-width="100px"
      >
        <el-form-item label="模型名称" prop="modelName">
          <el-input v-model="ttsForm.modelName" />
        </el-form-item>
        <el-form-item label="声音类型" prop="voiceType">
          <el-input v-model="ttsForm.voiceType" placeholder="如：女声 / 男声" />
        </el-form-item>
        <el-form-item label="采样率">
          <el-input v-model="ttsForm.sampleRate" placeholder="如：44100" />
        </el-form-item>
        <el-form-item label="默认语速">
          <el-input-number v-model="ttsForm.speedWordsPerMin" :min="0" />
        </el-form-item>
        <el-form-item label="配置 JSON">
          <el-input
            v-model="ttsForm.configJson"
            type="textarea"
            :rows="4"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="ttsDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="ttsSaving" @click="submitTts">
          保 存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  fetchModels,
  saveModelConfig,
  updateModelStatus
} from '@/api/admin/models'
import {
  fetchTtsConfigs,
  saveTtsConfig
} from '@/api/admin/tts'

const activeTab = ref('models')

// 模型列表
const modelsLoading = ref(false)
const modelList = ref([])
const modelFilter = reactive({
  type: '',
  provider: ''
})

// 模型表单
const modelDialogVisible = ref(false)
const modelFormRef = ref()
const modelForm = reactive({
  modelId: null,
  modelName: '',
  modelKey: '',
  provider: '',
  modelType: '',
  apiEndpoint: '',
  apiConfig: '',
  unitPrice: 0,
  isActive: true
})
const modelSaving = ref(false)
const modelRules = {
  modelName: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
  modelKey: [{ required: true, message: '请输入调用标识', trigger: 'blur' }],
  provider: [{ required: true, message: '请输入提供商', trigger: 'blur' }],
  modelType: [{ required: true, message: '请选择模型类型', trigger: 'change' }],
  apiEndpoint: [{ required: true, message: '请输入接口地址', trigger: 'blur' }],
  apiConfig: [{ required: true, message: '请输入鉴权配置', trigger: 'blur' }]
}

// TTS 列表
const ttsLoading = ref(false)
const ttsList = ref([])

// TTS 表单
const ttsDialogVisible = ref(false)
const ttsFormRef = ref()
const ttsForm = reactive({
  modelName: '',
  voiceType: '',
  sampleRate: '',
  speedWordsPerMin: 0,
  configJson: ''
})
const ttsSaving = ref(false)
const ttsRules = {
  modelName: [{ required: true, message: '请输入模型名称', trigger: 'blur' }],
  voiceType: [{ required: true, message: '请输入声音类型', trigger: 'blur' }]
}

const loadModels = async () => {
  modelsLoading.value = true
  try {
    const res = await fetchModels({
      type: modelFilter.type || undefined,
      provider: modelFilter.provider || undefined
    })
    modelList.value = res.data?.list || []
  } catch (e) {
    ElMessage.error('加载模型列表失败')
  } finally {
    modelsLoading.value = false
  }
}

const openModelDialog = (row) => {
  if (row) {
    modelForm.modelId = row.modelId
    modelForm.modelName = row.modelName
    modelForm.modelKey = row.modelKey
    modelForm.provider = row.provider
    modelForm.modelType = row.modelType
    modelForm.apiEndpoint = row.apiEndpoint
    modelForm.unitPrice = row.unitPrice
    modelForm.isActive = row.isActive
    modelForm.apiConfig = '' // 详细配置需通过单独接口获取，这里简化为重新填写
  } else {
    modelForm.modelId = null
    modelForm.modelName = ''
    modelForm.modelKey = ''
    modelForm.provider = ''
    modelForm.modelType = ''
    modelForm.apiEndpoint = ''
    modelForm.unitPrice = 0
    modelForm.isActive = true
    modelForm.apiConfig = ''
  }
  modelDialogVisible.value = true
}

const submitModel = () => {
  modelFormRef.value.validate(async (valid) => {
    if (!valid) return
    modelSaving.value = true
    try {
      let apiConfigObj
      try {
        apiConfigObj = JSON.parse(modelForm.apiConfig || '{}')
      } catch (e) {
        ElMessage.error('鉴权配置 JSON 格式不正确')
        modelSaving.value = false
        return
      }

      await saveModelConfig({
        modelId: modelForm.modelId || undefined,
        modelName: modelForm.modelName,
        modelKey: modelForm.modelKey,
        provider: modelForm.provider,
        modelType: modelForm.modelType,
        apiEndpoint: modelForm.apiEndpoint,
        apiConfig: apiConfigObj,
        unitPrice: modelForm.unitPrice,
        isActive: modelForm.isActive
      })
      ElMessage.success('模型配置已保存')
      modelDialogVisible.value = false
      loadModels()
    } catch (e) {
      ElMessage.error('保存模型配置失败')
    } finally {
      modelSaving.value = false
    }
  })
}

const toggleModelStatus = async (row) => {
  try {
    await updateModelStatus(row.modelId, { isActive: !row.isActive })
    ElMessage.success('状态已更新')
    loadModels()
  } catch (e) {
    ElMessage.error('更新模型状态失败')
  }
}

const loadTts = async () => {
  ttsLoading.value = true
  try {
    const res = await fetchTtsConfigs()
    ttsList.value = res.data?.list || []
  } catch (e) {
    ElMessage.error('加载 TTS 配置失败')
  } finally {
    ttsLoading.value = false
  }
}

const openTtsDialog = () => {
  ttsForm.modelName = ''
  ttsForm.voiceType = ''
  ttsForm.sampleRate = ''
  ttsForm.speedWordsPerMin = 0
  ttsForm.configJson = ''
  ttsDialogVisible.value = true
}

const submitTts = () => {
  ttsFormRef.value.validate(async (valid) => {
    if (!valid) return
    ttsSaving.value = true
    try {
      let configObj = undefined
      if (ttsForm.configJson) {
        try {
          configObj = JSON.parse(ttsForm.configJson)
        } catch (e) {
          ElMessage.error('配置 JSON 格式不正确')
          ttsSaving.value = false
          return
        }
      }

      await saveTtsConfig({
        modelName: ttsForm.modelName,
        voiceType: ttsForm.voiceType,
        sampleRate: ttsForm.sampleRate || undefined,
        speedWordsPerMin: ttsForm.speedWordsPerMin || undefined,
        configJson: configObj
      })
      ElMessage.success('TTS 配置已保存')
      ttsDialogVisible.value = false
      loadTts()
    } catch (e) {
      ElMessage.error('保存 TTS 配置失败')
    } finally {
      ttsSaving.value = false
    }
  })
}

onMounted(() => {
  loadModels()
  loadTts()
})
</script>

<style scoped>
.admin-page {
  padding: 4px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
</style>

