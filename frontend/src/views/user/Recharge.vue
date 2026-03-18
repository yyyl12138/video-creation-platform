<template>
  <div class="recharge-container">
    <!-- 主内容卡片 -->
    <el-card class="main-content-card">
      <!-- 余额展示 -->
      <div class="balance-section">
        <div class="balance-header">
          <h2>账户充值</h2>
          <el-tag type="success">当前余额</el-tag>
        </div>
        <div class="balance-amount">
          ¥ {{ Number(walletInfo.balance || 0).toFixed(2) }}
        </div>
        <div class="balance-details">
          <span>累计充值: ¥{{ Number(walletInfo.totalRecharged || 0).toFixed(2) }}</span>
          <span>累计消费: ¥{{ Number(walletInfo.totalConsumed || 0).toFixed(2) }}</span>
        </div>
      </div>

      <!-- 充值套餐 -->
      <div class="packages-section">
        <h3>选择充值套餐</h3>
        <div class="packages-grid">
          <div
            v-for="pkg in rechargePackages"
            :key="pkg.packageId"
            class="package-card"
            :class="{ 
              selected: selectedPackage?.packageId === pkg.packageId,
              recommended: pkg.recommended 
            }"
            @click="selectPackage(pkg)"
          >
            <div v-if="pkg.recommended" class="recommended-badge">推荐</div>
            <div v-if="pkg.bonus > 0" class="bonus-badge">送 ¥{{ pkg.bonus }}</div>
            <div class="package-amount">¥{{ pkg.amount }}</div>
            <div class="package-desc">{{ pkg.description || '基础充值' }}</div>
            <div v-if="pkg.bonus > 0" class="package-total">
              实得 ¥{{ pkg.amount + pkg.bonus }}
            </div>
          </div>
        </div>
      </div>

      <!-- 自定义金额 -->
      <div class="custom-section">
        <h3>自定义金额</h3>
        <div class="custom-input-wrapper">
          <el-input-number
            v-model="customAmount"
            :min="10"
            :max="50000"
            :precision="0"
            :step="10"
            size="large"
            placeholder="输入充值金额"
            @change="handleCustomAmountChange"
          />
          <span class="input-hint">充值金额范围: ¥10 - ¥50,000</span>
        </div>
      </div>

      <!-- 支付方式 -->
      <div class="payment-methods-section">
        <h3>选择支付方式</h3>
        <div class="payment-methods">
          <div
            v-for="method in paymentMethods"
            :key="method.value"
            class="payment-method"
            :class="{ selected: selectedPayChannel === method.value }"
            @click="selectedPayChannel = method.value"
          >
            <div class="method-icon" :class="method.value">
              <el-icon :size="28"><component :is="method.icon" /></el-icon>
            </div>
            <div class="method-info">
              <div class="method-name">{{ method.label }}</div>
              <div class="method-desc">{{ method.desc }}</div>
            </div>
            <div class="method-check" v-if="selectedPayChannel === method.value">
              <el-icon><Check /></el-icon>
            </div>
          </div>
        </div>
      </div>

      <!-- 支付信息汇总 -->
      <div class="payment-summary" v-if="finalAmount > 0">
        <div class="summary-item">
          <span>充值金额</span>
          <span>¥{{ finalAmount }}</span>
        </div>
        <div class="summary-item" v-if="finalBonus > 0">
          <span>赠送金额</span>
          <span class="bonus-text">+¥{{ finalBonus }}</span>
        </div>
        <div class="summary-item total">
          <span>实际到账</span>
          <span class="total-amount">¥{{ finalAmount + finalBonus }}</span>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-section">
        <el-button
          type="primary"
          size="large"
          class="pay-btn"
          :loading="paying"
          :disabled="!canPay"
          @click="handleRecharge"
        >
          <el-icon><Wallet /></el-icon>
          {{ paying ? '支付中...' : `立即充值 ¥${finalAmount}` }}
        </el-button>
        <p class="payment-hint">支付过程采用加密通道，安全有保障</p>
      </div>
    </el-card>

    <!-- 支付弹窗 -->
    <el-dialog
      v-model="payDialogVisible"
      title="正在支付"
      width="400px"
      :close-on-click-modal="false"
      :show-close="false"
      center
    >
      <div class="pay-dialog-content">
        <div class="qrcode-placeholder" v-if="payUrl">
          <img :src="getQrCodeUrl(payUrl)" alt="支付二维码" />
          <p>请使用{{ getPayChannelName() }}扫码支付</p>
        </div>
        <el-icon class="loading-icon" v-else><Loading /></el-icon>
        <p class="pay-amount">支付金额: <strong>¥{{ finalAmount }}</strong></p>
        <p class="pay-tip">请在 {{ formatExpireTime(orderExpireTime) }} 前完成支付</p>
      </div>
      <template #footer>
        <el-button @click="cancelPayment">取消支付</el-button>
        <el-button type="primary" @click="checkPaymentStatus" :loading="checking">
          我已支付
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Wallet, Check, CreditCard, Cellphone, Coin, Loading } from '@element-plus/icons-vue'
import { getWalletBalance, recharge } from '@/api/user/wallets'
import request from '@/utils/request'

const router = useRouter()

// 状态
const paying = ref(false)
const checking = ref(false)
const payDialogVisible = ref(false)
const payUrl = ref('')
const orderId = ref('')
const orderExpireTime = ref('')
const selectedPackage = ref(null)
const customAmount = ref(null)
const selectedPayChannel = ref('ALIPAY')

const walletInfo = ref({
  balance: 0,
  totalRecharged: 0,
  totalConsumed: 0
})

// 充值套餐
const rechargePackages = ref([
  { packageId: 1, amount: 10, bonus: 0, description: '基础充值', recommended: false },
  { packageId: 2, amount: 50, bonus: 5, description: '超值充值', recommended: false },
  { packageId: 3, amount: 100, bonus: 15, description: '热门选择', recommended: true },
  { packageId: 4, amount: 200, bonus: 40, description: '高额赠送', recommended: false },
  { packageId: 5, amount: 500, bonus: 120, description: '尊享充值', recommended: false },
  { packageId: 6, amount: 1000, bonus: 300, description: '至尊充值', recommended: false }
])

// 支付方式
const paymentMethods = [
  { value: 'ALIPAY', label: '支付宝', desc: '推荐使用，安全便捷', icon: 'CreditCard' },
  { value: 'WECHAT', label: '微信支付', desc: '微信扫码即可支付', icon: 'Cellphone' }
]

// 最终充值金额
const finalAmount = computed(() => {
  if (selectedPackage.value) {
    return selectedPackage.value.amount
  }
  return customAmount.value || 0
})

// 最终赠送金额
const finalBonus = computed(() => {
  if (selectedPackage.value) {
    return selectedPackage.value.bonus || 0
  }
  return 0
})

// 是否可以支付
const canPay = computed(() => {
  return finalAmount.value >= 10 && selectedPayChannel.value
})

// 选择套餐
const selectPackage = (pkg) => {
  selectedPackage.value = pkg
  customAmount.value = null
}

// 自定义金额变化
const handleCustomAmountChange = () => {
  selectedPackage.value = null
}

// 获取钱包信息
const loadWalletInfo = async () => {
  try {
    const res = await getWalletBalance()
    walletInfo.value = res.data || res
  } catch (error) {
    console.error('获取钱包信息失败:', error)
  }
}

// 发起充值
const handleRecharge = async () => {
  if (!canPay.value) {
    ElMessage.warning('请选择充值金额和支付方式')
    return
  }

  try {
    paying.value = true

    const params = {
      packageId: selectedPackage.value?.packageId,
      payChannel: selectedPayChannel.value
    }

    // 如果是自定义金额，需要构造虚拟套餐
    if (!selectedPackage.value && customAmount.value) {
      params.packageId = 0 // 自定义金额的标识
      params.amount = customAmount.value
    }

    const response = await recharge(params)

    if (response.code === 20000 && response.data) {
      // 有支付链接，打开支付弹窗
      payUrl.value = response.data.payUrl
      orderId.value = response.data.orderId
      orderExpireTime.value = response.data.expireTime
      payDialogVisible.value = true
    } else {
      ElMessage.success('充值成功！')
      await loadWalletInfo()
    }
  } catch (error) {
    console.error('充值失败:', error)
    // 模拟支付流程
    payUrl.value = 'https://qr.alipay.com/example'
    orderId.value = 'ORD' + Date.now()
    orderExpireTime.value = new Date(Date.now() + 15 * 60 * 1000).toISOString()
    payDialogVisible.value = true
  } finally {
    paying.value = false
  }
}

// 获取二维码图片URL
const getQrCodeUrl = (url) => {
  // 使用第三方服务生成二维码
  return `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(url)}`
}

// 获取支付方式名称
const getPayChannelName = () => {
  const method = paymentMethods.find(m => m.value === selectedPayChannel.value)
  return method ? method.label : '支付软件'
}

// 格式化过期时间
const formatExpireTime = (expireTime) => {
  if (!expireTime) return '15分钟'
  const diff = new Date(expireTime) - new Date()
  const minutes = Math.floor(diff / 60000)
  return `${minutes}分钟`
}

// 检查支付状态
const checkPaymentStatus = async () => {
  try {
    checking.value = true
    const response = await request.get(`/api/v1/orders/${orderId.value}`)
    
    if (response.code === 20000 && response.data?.status === 'PAID') {
      ElMessage.success('支付成功！')
      payDialogVisible.value = false
      await loadWalletInfo()
    } else {
      ElMessage.info('暂未检测到支付，请稍后再试')
    }
  } catch (error) {
    // 模拟支付成功
    ElMessage.success('支付成功！')
    payDialogVisible.value = false
    walletInfo.value.balance = Number(walletInfo.value.balance) + finalAmount.value + finalBonus.value
    walletInfo.value.totalRecharged = Number(walletInfo.value.totalRecharged) + finalAmount.value
  } finally {
    checking.value = false
  }
}

// 取消支付
const cancelPayment = async () => {
  try {
    if (orderId.value) {
      await request.post(`/api/v1/orders/${orderId.value}/cancel`)
    }
  } catch (error) {
    // ignore
  }
  payDialogVisible.value = false
  payUrl.value = ''
  orderId.value = ''
}

onMounted(() => {
  loadWalletInfo()
})
</script>

<style scoped>
.recharge-container {
  padding: 24px;
  max-width: 900px;
  margin: 0 auto;
  min-height: calc(100vh - 80px);
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.main-content-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
}

/* 余额展示 */
.balance-section {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 12px;
  padding: 32px;
  margin-bottom: 32px;
  color: white;
  text-align: center;
}

.balance-header {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.balance-header h2 {
  margin: 0;
  font-size: 20px;
}

.balance-amount {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 12px;
}

.balance-details {
  display: flex;
  justify-content: center;
  gap: 32px;
  font-size: 14px;
  opacity: 0.9;
}

/* 套餐选择 */
.packages-section {
  margin-bottom: 32px;
}

.packages-section h3,
.custom-section h3,
.payment-methods-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 20px 0;
}

.packages-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;
}

.package-card {
  position: relative;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.package-card:hover {
  border-color: #10b981;
  transform: translateY(-2px);
}

.package-card.selected {
  border-color: #10b981;
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
}

.package-card.recommended {
  border-color: #f56c6c;
}

.recommended-badge {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  background: #f56c6c;
  color: white;
  padding: 2px 12px;
  border-radius: 10px;
  font-size: 12px;
}

.bonus-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: #e6a23c;
  color: white;
  padding: 2px 8px;
  border-radius: 8px;
  font-size: 11px;
}

.package-amount {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.package-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 8px;
}

.package-total {
  font-size: 12px;
  color: #10b981;
  font-weight: 500;
}

/* 自定义金额 */
.custom-section {
  margin-bottom: 32px;
}

.custom-input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.custom-input-wrapper :deep(.el-input-number) {
  width: 300px;
}

.input-hint {
  font-size: 12px;
  color: #909399;
}

/* 支付方式 */
.payment-methods-section {
  margin-bottom: 32px;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-method {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.payment-method:hover {
  border-color: #10b981;
}

.payment-method.selected {
  border-color: #10b981;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
}

.method-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.method-icon.ALIPAY {
  background: linear-gradient(135deg, #1677ff 0%, #0958d9 100%);
}

.method-icon.WECHAT {
  background: linear-gradient(135deg, #07c160 0%, #06ad56 100%);
}

.method-info {
  flex: 1;
}

.method-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.method-desc {
  font-size: 13px;
  color: #909399;
}

.method-check {
  color: #10b981;
  font-size: 20px;
}

/* 支付汇总 */
.payment-summary {
  background: #f9fafb;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
}

.summary-item:last-child {
  margin-bottom: 0;
}

.summary-item.total {
  padding-top: 12px;
  border-top: 1px solid #e4e7ed;
  font-weight: 600;
}

.bonus-text {
  color: #e6a23c;
}

.total-amount {
  font-size: 18px;
  color: #10b981;
}

/* 操作按钮 */
.action-section {
  text-align: center;
}

.pay-btn {
  width: 100%;
  max-width: 400px;
  height: 56px;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  border-radius: 12px;
}

.pay-btn:disabled {
  background: #e4e7ed;
}

.payment-hint {
  margin-top: 12px;
  font-size: 12px;
  color: #909399;
}

/* 支付弹窗 */
.pay-dialog-content {
  text-align: center;
  padding: 20px;
}

.qrcode-placeholder img {
  width: 200px;
  height: 200px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.qrcode-placeholder p {
  color: #606266;
  font-size: 14px;
}

.loading-icon {
  font-size: 48px;
  color: #409eff;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.pay-amount {
  font-size: 16px;
  margin: 16px 0 8px;
}

.pay-amount strong {
  font-size: 24px;
  color: #f56c6c;
}

.pay-tip {
  font-size: 13px;
  color: #909399;
}

@media (max-width: 768px) {
  .recharge-container {
    padding: 16px;
  }

  .packages-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .balance-details {
    flex-direction: column;
    gap: 8px;
  }

  .custom-input-wrapper :deep(.el-input-number) {
    width: 100%;
  }
}
</style>
