<template>
  <div class="system-config-container">
    <!-- 主内容区域 -->
    <div class="config-content">
      <!-- 基础配置 -->
      <el-card class="config-card">
        <template #header>
          <div class="card-header">
            <el-icon><InfoFilled /></el-icon>
            <span>基础配置</span>
          </div>
        </template>
        <el-form :model="basicConfig" label-width="140px">
          <el-form-item label="系统名称">
            <el-input v-model="basicConfig.systemName" placeholder="请输入系统名称" />
          </el-form-item>
          <el-form-item label="网站标题">
            <el-input v-model="basicConfig.siteTitle" placeholder="请输入网站标题" />
          </el-form-item>
          <el-form-item label="网站描述">
            <el-input
              v-model="basicConfig.siteDescription"
              type="textarea"
              :rows="3"
              placeholder="请输入网站描述"
            />
          </el-form-item>
          <el-form-item label="联系邮箱">
            <el-input v-model="basicConfig.contactEmail" placeholder="请输入联系邮箱" />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="basicConfig.contactPhone" placeholder="请输入联系电话" />
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 用户配置 -->
      <el-card class="config-card">
        <template #header>
          <div class="card-header">
            <el-icon><User /></el-icon>
            <span>用户配置</span>
          </div>
        </template>
        <el-form :model="userConfig" label-width="140px">
          <el-form-item label="允许注册">
            <el-switch v-model="userConfig.allowRegister" />
          </el-form-item>
          <el-form-item label="默认用户角色">
            <el-select v-model="userConfig.defaultRole" placeholder="选择默认角色">
              <el-option label="普通用户" value="user" />
              <el-option label="VIP用户" value="vip" />
            </el-select>
          </el-form-item>
          <el-form-item label="单文件大小限制">
            <el-input-number
              v-model="userConfig.maxFileSize"
              :min="1"
              :max="500"
              :step="10"
              controls-position="right"
            />
            <span style="margin-left: 8px">MB</span>
          </el-form-item>
          <el-form-item label="每日生成次数限制">
            <el-input-number
              v-model="userConfig.dailyLimit"
              :min="0"
              :max="1000"
              :step="10"
              controls-position="right"
            />
          </el-form-item>
          <el-form-item label="VIP每日生成次数">
            <el-input-number
              v-model="userConfig.vipDailyLimit"
              :min="0"
              :max="10000"
              :step="100"
              controls-position="right"
            />
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 内容审核配置 -->
      <el-card class="config-card">
        <template #header>
          <div class="card-header">
            <el-icon><DocumentChecked /></el-icon>
            <span>内容审核配置</span>
          </div>
        </template>
        <el-form :model="reviewConfig" label-width="140px">
          <el-form-item label="开启自动审核">
            <el-switch v-model="reviewConfig.autoReview" />
          </el-form-item>
          <el-form-item label="敏感词过滤">
            <el-switch v-model="reviewConfig.sensitiveWordFilter" />
          </el-form-item>
          <el-form-item label="敏感词列表">
            <el-input
              v-model="reviewConfig.sensitiveWords"
              type="textarea"
              :rows="4"
              placeholder="请输入敏感词，用逗号分隔"
            />
          </el-form-item>
          <el-form-item label="违规内容处理">
            <el-radio-group v-model="reviewConfig.violationAction">
              <el-radio value="reject">自动拒绝</el-radio>
              <el-radio value="pending">标记待审核</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 支付配置 -->
      <el-card class="config-card">
        <template #header>
          <div class="card-header">
            <el-icon><Wallet /></el-icon>
            <span>支付配置</span>
          </div>
        </template>
        <el-form :model="paymentConfig" label-width="140px">
          <el-form-item label="开启支付功能">
            <el-switch v-model="paymentConfig.enablePayment" />
          </el-form-item>
          <el-form-item label="支付方式">
            <el-checkbox-group v-model="paymentConfig.paymentMethods">
              <el-checkbox value="alipay">支付宝</el-checkbox>
              <el-checkbox value="wechat">微信支付</el-checkbox>
              <el-checkbox value="stripe">Stripe</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          <el-form-item label="支付宝AppID">
            <el-input v-model="paymentConfig.alipayAppId" placeholder="请输入支付宝AppID" />
          </el-form-item>
          <el-form-item label="微信支付AppID">
            <el-input v-model="paymentConfig.wechatAppId" placeholder="请输入微信支付AppID" />
          </el-form-item>
          <el-form-item label="Stripe公钥">
            <el-input v-model="paymentConfig.stripePublicKey" placeholder="请输入Stripe公钥" />
          </el-form-item>
        </el-form>
      </el-card>

      <!-- AI模型配置 -->
      <el-card class="config-card">
        <template #header>
          <div class="card-header">
            <el-icon><Cpu /></el-icon>
            <span>AI模型配置</span>
          </div>
        </template>
        <el-form :model="aiConfig" label-width="140px">
          <el-form-item label="默认视频模型">
            <el-select v-model="aiConfig.defaultVideoModel" placeholder="选择默认视频模型">
              <el-option label="Kling" value="kling" />
              <el-option label="Minimax" value="minimax" />
              <el-option label="Doubao Seedance" value="doubao" />
            </el-select>
          </el-form-item>
          <el-form-item label="默认图片模型">
            <el-select v-model="aiConfig.defaultImageModel" placeholder="选择默认图片模型">
              <el-option label="Wanx v1" value="wanx" />
              <el-option label="Stable Diffusion" value="sd" />
            </el-select>
          </el-form-item>
          <el-form-item label="API密钥">
            <el-input v-model="aiConfig.apiKey" type="password" placeholder="请输入API密钥" show-password />
          </el-form-item>
          <el-form-item label="超时时间">
            <el-input-number
              v-model="aiConfig.timeout"
              :min="10"
              :max="300"
              :step="10"
              controls-position="right"
            />
            <span style="margin-left: 8px">秒</span>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Setting, Check, InfoFilled, User, DocumentChecked,
  Wallet, Cpu
} from '@element-plus/icons-vue'

const saving = ref(false)

const basicConfig = reactive({
  systemName: 'VideoAI 创作平台',
  siteTitle: 'VideoAI - 智能视频创作平台',
  siteDescription: 'AI驱动的智能视频创作平台，支持文生视频、图生视频等多种创作方式',
  contactEmail: 'support@videoai.com',
  contactPhone: '400-123-4567'
})

const userConfig = reactive({
  allowRegister: true,
  defaultRole: 'user',
  maxFileSize: 100,
  dailyLimit: 50,
  vipDailyLimit: 500
})

const reviewConfig = reactive({
  autoReview: false,
  sensitiveWordFilter: true,
  sensitiveWords: '违法,暴力,色情,诈骗,赌博',
  violationAction: 'pending'
})

const paymentConfig = reactive({
  enablePayment: true,
  paymentMethods: ['alipay', 'wechat'],
  alipayAppId: '',
  wechatAppId: '',
  stripePublicKey: ''
})

const aiConfig = reactive({
  defaultVideoModel: 'kling',
  defaultImageModel: 'wanx',
  apiKey: '',
  timeout: 60
})

const saveAllConfig = async () => {
  try {
    saving.value = true
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('配置保存成功')
  } catch (error) {
    console.error('保存配置失败:', error)
    ElMessage.error('保存配置失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  // 模拟加载配置
  console.log('加载系统配置')
})
</script>

<style scoped>
.system-config-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: calc(100vh - 40px);
}

.config-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.config-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-header .el-icon {
  color: #3b82f6;
}

.config-card :deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

@media (max-width: 1024px) {
  .config-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .system-config-container {
    padding: 16px;
  }

  .config-card :deep(.el-form-item__label) {
    width: 120px !important;
  }
}
</style>
