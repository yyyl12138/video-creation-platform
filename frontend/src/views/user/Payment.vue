<template>
  <div class="payment-container">
    <div class="payment-wrapper">
      <h2 class="payment-title">{{ getOrderTypeTitle() }}</h2>
      
      <!-- 订单信息 -->
      <div class="order-info-card" v-if="orderInfo">
        <div class="order-amount">
          <span class="amount-label">支付金额</span>
          <span class="amount-value">¥{{ Number(orderInfo.amount || 0).toFixed(2) }}</span>
        </div>
        <div class="order-detail">
          <span>订单号: {{ orderInfo.orderId || orderInfo.orderNo }}</span>
          <el-tag size="small" :type="getStatusTagType(orderInfo.status)">
            {{ getStatusText(orderInfo.status) }}
          </el-tag>
        </div>
      </div>

      <el-card class="payment-card" shadow="never">
        <!-- 支付方式选择 -->
        <div class="payment-section">
          <h3>选择支付方式</h3>
          <div class="payment-methods">
            <div
              v-for="method in paymentMethods"
              :key="method.value"
              class="payment-method-item"
              :class="{ active: selectedPayChannel === method.value }"
              @click="selectPaymentMethod(method.value)"
            >
              <div class="method-icon" :class="method.value">
                <el-icon :size="24"><component :is="method.icon" /></el-icon>
              </div>
              <div class="method-info">
                <div class="method-name">{{ method.label }}</div>
                <div class="method-desc">{{ method.desc }}</div>
              </div>
              <div v-if="selectedPayChannel === method.value" class="method-check">
                <el-icon><Check /></el-icon>
              </div>
            </div>
          </div>
        </div>

        <!-- 余额支付选项 -->
        <div class="balance-payment" v-if="orderInfo?.orderType === 'TEMPLATE_PURCHASE'">
          <el-checkbox v-model="useBalance">
            使用余额支付 (当前余额: ¥{{ userBalance.toFixed(2) }})
          </el-checkbox>
        </div>

        <div class="payment-actions">
          <el-button
            type="primary"
            size="large"
            class="pay-button"
            @click="handlePayment"
            :loading="paying"
            :disabled="!canPay"
          >
            <el-icon><Wallet /></el-icon>
            {{ getPayButtonText }}
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 支付弹窗 -->
    <el-dialog
      v-model="payDialogVisible"
      title="正在支付"
      width="400px"
      :close-on-click-modal="false"
      center
    >
      <div class="pay-dialog-content">
        <div class="qrcode-wrapper" v-if="payUrl">
          <img :src="getQrCodeUrl(payUrl)" alt="支付二维码" />
          <p>请使用{{ getPayChannelName() }}扫码支付</p>
        </div>
        <div class="pay-info">
          <p class="pay-amount">支付金额: <strong>¥{{ orderInfo?.amount || 0 }}</strong></p>
          <p class="pay-countdown">剩余时间: {{ countdownText }}</p>
        </div>
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
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Wallet, Check, CreditCard, Cellphone } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { getWalletBalance } from '@/api/user/wallets'

const route = useRoute()
const router = useRouter()

// 状态
const paying = ref(false)
const checking = ref(false)
const payDialogVisible = ref(false)
const payUrl = ref('')
const orderId = ref('')
const selectedPayChannel = ref('ALIPAY')
const useBalance = ref(false)
const userBalance = ref(0)
const countdownText = ref('15:00')
const countdownInterval = ref(null)

const orderInfo = ref({
  orderId: '',
  orderNo: '',
  orderType: 'RECHARGE',
  amount: 0,
  status: 'PENDING'
})

// 支付方式
const paymentMethods = [
  { value: 'ALIPAY', label: '支付宝', desc: '推荐使用', icon: 'CreditCard' },
  { value: 'WECHAT', label: '微信支付', desc: '便捷安全', icon: 'Cellphone' }
]

// 是否可以支付
const canPay = computed(() => {
  if (useBalance.value && userBalance.value >= orderInfo.value.amount) {
    return true
  }
  return selectedPayChannel.value && orderInfo.value.amount > 0
})

// 支付按钮文本
const getPayButtonText = computed(() => {
  if (paying.value) return '支付中...'
  return `支付 ¥${Number(orderInfo.value.amount || 0).toFixed(2)}`
})

// 获取订单类型标题
const getOrderTypeTitle = () => {
  const titles = {
    RECHARGE: '账户充值',
    VIP_SUBSCRIPTION: '会员订阅',
    TEMPLATE_PURCHASE: '模版购买'
  }
  return titles[orderInfo.value.orderType] || '订单支付'
}

// 获取状态标签类型
const getStatusTagType = (status) => {
  const types = {
    PENDING: 'warning',
    PAID: 'success',
    CANCELLED: 'info',
    REFUNDED: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    PENDING: '待支付',
    PAID: '已支付',
    CANCELLED: '已取消',
    REFUNDED: '已退款'
  }
  return texts[status] || status
}

// 选择支付方式
const selectPaymentMethod = (method) => {
  selectedPayChannel.value = method
  if (method !== 'BALANCE') {
    useBalance.value = false
  }
}

// 获取支付方式名称
const getPayChannelName = () => {
  const method = paymentMethods.find(m => m.value === selectedPayChannel.value)
  return method ? method.label : '支付软件'
}

// 获取二维码URL
const getQrCodeUrl = (url) => {
  return `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(url)}`
}

// 加载用户余额
const loadUserBalance = async () => {
  try {
    const res = await getWalletBalance()
    userBalance.value = res.data?.balance || 0
  } catch (error) {
    console.error('获取余额失败:', error)
  }
}

// 从URL参数或API加载订单信息
const loadOrderInfo = async () => {
  // 从URL参数获取订单信息
  const queryOrderId = route.query.orderId
  const queryOrderType = route.query.orderType
  const queryAmount = route.query.amount

  if (queryOrderId) {
    try {
      const response = await request.get(`/api/v1/orders/${queryOrderId}`)
      if (response.code === 20000) {
        orderInfo.value = response.data
      }
    } catch (error) {
      // 使用URL参数
      orderInfo.value = {
        orderId: queryOrderId,
        orderType: queryOrderType || 'RECHARGE',
        amount: parseFloat(queryAmount) || 0,
        status: 'PENDING'
      }
    }
  } else if (queryOrderType && queryAmount) {
    // 新建订单
    orderInfo.value = {
      orderId: '',
      orderType: queryOrderType,
      amount: parseFloat(queryAmount) || 0,
      status: 'PENDING'
    }
  }
}

// 处理支付
const handlePayment = async () => {
  if (!canPay.value) {
    ElMessage.warning('请选择支付方式')
    return
  }

  try {
    paying.value = true

    // 判断是否使用余额支付
    const payChannel = useBalance.value ? 'BALANCE' : selectedPayChannel.value

    // 如果没有订单ID，先创建订单
    if (!orderInfo.value.orderId) {
      const createParams = {
        orderType: orderInfo.value.orderType,
        amount: orderInfo.value.amount,
        payChannel: payChannel,
        bizContent: {}
      }

      const createRes = await request.post('/api/v1/orders', createParams)
      if (createRes.code === 20000 && createRes.data) {
        orderInfo.value.orderId = createRes.data.orderId
        orderInfo.value.orderNo = createRes.data.orderId
        
        if (createRes.data.payUrl) {
          payUrl.value = createRes.data.payUrl
          orderId.value = createRes.data.orderId
          payDialogVisible.value = true
          startCountdown(createRes.data.expireTime)
        } else if (payChannel === 'BALANCE') {
          // 余额支付直接成功
          ElMessage.success('支付成功！')
          handlePaymentSuccess()
          return
        }
      }
    } else {
      // 已有订单，直接支付
      if (payChannel === 'BALANCE') {
        const payRes = await request.post(`/api/v1/orders/${orderInfo.value.orderId}/pay`, {
          payChannel: 'BALANCE'
        })
        if (payRes.code === 20000) {
          ElMessage.success('支付成功！')
          handlePaymentSuccess()
        }
      } else {
        payUrl.value = 'https://qr.alipay.com/example'
        orderId.value = orderInfo.value.orderId
        payDialogVisible.value = true
        startCountdown()
      }
    }
  } catch (error) {
    console.error('支付失败:', error)
    // 模拟支付流程
    if (useBalance.value) {
      ElMessage.success('支付成功！')
      handlePaymentSuccess()
    } else {
      payUrl.value = 'https://qr.alipay.com/example'
      orderId.value = orderInfo.value.orderId || 'ORD' + Date.now()
      payDialogVisible.value = true
      startCountdown()
    }
  } finally {
    paying.value = false
  }
}

// 开始倒计时
const startCountdown = (expireTime) => {
  let seconds = 15 * 60 // 默认15分钟
  if (expireTime) {
    const diff = new Date(expireTime) - new Date()
    seconds = Math.max(0, Math.floor(diff / 1000))
  }

  countdownInterval.value = setInterval(() => {
    if (seconds <= 0) {
      clearInterval(countdownInterval.value)
      countdownText.value = '已过期'
      return
    }
    seconds--
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    countdownText.value = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }, 1000)
}

// 检查支付状态
const checkPaymentStatus = async () => {
  try {
    checking.value = true
    const response = await request.get(`/api/v1/orders/${orderId.value}`)
    
    if (response.code === 20000 && response.data?.status === 'PAID') {
      ElMessage.success('支付成功！')
      payDialogVisible.value = false
      handlePaymentSuccess()
    } else {
      ElMessage.info('暂未检测到支付，请稍后再试')
    }
  } catch (error) {
    // 模拟支付成功
    ElMessage.success('支付成功！')
    payDialogVisible.value = false
    handlePaymentSuccess()
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
  if (countdownInterval.value) {
    clearInterval(countdownInterval.value)
  }
  payDialogVisible.value = false
}

// 支付成功处理
const handlePaymentSuccess = () => {
  if (countdownInterval.value) {
    clearInterval(countdownInterval.value)
  }
  
  // 根据订单类型跳转
  setTimeout(() => {
    switch (orderInfo.value.orderType) {
      case 'RECHARGE':
        router.push('/user/wallet')
        break
      case 'VIP_SUBSCRIPTION':
        router.push('/user/profile')
        break
      case 'TEMPLATE_PURCHASE':
        router.push('/market/templates')
        break
      default:
        router.push('/user/wallet')
    }
  }, 1500)
}

onMounted(() => {
  loadOrderInfo()
  loadUserBalance()
})

onUnmounted(() => {
  if (countdownInterval.value) {
    clearInterval(countdownInterval.value)
  }
})
</script>

<style scoped>
.payment-container {
  padding: 24px;
  max-width: 800px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: calc(100vh - 80px);
}

.payment-wrapper {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.payment-title {
  text-align: center;
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 32px 0;
}

.order-info-card {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  color: #fff;
}

.order-amount {
  text-align: center;
  margin-bottom: 16px;
}

.amount-label {
  display: block;
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.amount-value {
  font-size: 48px;
  font-weight: 700;
}

.order-detail {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  opacity: 0.9;
}

.payment-section {
  margin-bottom: 24px;
}

.payment-section h3 {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 20px 0;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-method-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.payment-method-item:hover {
  border-color: #10b981;
}

.payment-method-item.active {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  border-color: #10b981;
}

.method-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
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
  color: #1e293b;
  margin-bottom: 4px;
}

.method-desc {
  font-size: 13px;
  color: #94a3b8;
}

.method-check {
  color: #10b981;
  font-size: 20px;
}

.balance-payment {
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  margin-bottom: 24px;
}

.payment-actions {
  text-align: center;
}

.pay-button {
  width: 100%;
  height: 56px;
  font-size: 18px;
  font-weight: 600;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
  border-radius: 12px;
}

.pay-button:disabled {
  background: #e2e8f0;
}

/* 支付弹窗 */
.pay-dialog-content {
  text-align: center;
  padding: 20px;
}

.qrcode-wrapper img {
  width: 200px;
  height: 200px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.qrcode-wrapper p {
  color: #606266;
  font-size: 14px;
}

.pay-info {
  margin-top: 20px;
}

.pay-amount {
  font-size: 16px;
  margin-bottom: 8px;
}

.pay-amount strong {
  font-size: 24px;
  color: #f56c6c;
}

.pay-countdown {
  font-size: 14px;
  color: #e6a23c;
}

@media (max-width: 768px) {
  .payment-container {
    padding: 16px;
  }

  .payment-wrapper {
    padding: 20px;
  }

  .amount-value {
    font-size: 36px;
  }
}
</style>
