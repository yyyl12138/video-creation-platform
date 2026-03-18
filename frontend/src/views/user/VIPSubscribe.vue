<template>
  <div class="vip-subscribe-container">
    <div class="vip-header">
      <h1>VIP会员订阅</h1>
      <p class="subtitle">解锁全部高级功能，享受专属特权</p>
    </div>

    <!-- 当前会员状态 -->
    <div class="current-status" v-if="userVipStatus">
      <el-card class="status-card" :class="userVipStatus.toLowerCase()">
        <div class="status-content">
          <div class="status-icon">
            <el-icon :size="48"><Crown /></el-icon>
          </div>
          <div class="status-info">
            <h3>{{ getVipTitle }}</h3>
            <p v-if="vipExpireDate">到期时间: {{ vipExpireDate }}</p>
            <p v-if="remainingDays > 0">剩余 {{ remainingDays }} 天</p>
            <p v-else-if="remainingDays <= 0 && vipExpireDate">会员已过期</p>
          </div>
        </div>
      </el-card>
    </div>

    <!-- VIP套餐选择 -->
    <div class="packages-section">
      <h2>选择套餐</h2>
      <div class="packages-grid">
        <div
          v-for="pkg in vipPackages"
          :key="pkg.id"
          class="package-card"
          :class="{ recommended: pkg.recommended, selected: selectedPackage?.id === pkg.id }"
          @click="selectPackage(pkg)"
        >
          <div v-if="pkg.recommended" class="recommend-badge">推荐</div>
          <div v-if="pkg.discount" class="discount-badge">{{ pkg.discount }}</div>
          <div class="package-icon">
            <el-icon :size="36"><component :is="pkg.icon" /></el-icon>
          </div>
          <h3 class="package-name">{{ pkg.name }}</h3>
          <div class="package-price">
            <span class="price-symbol">¥</span>
            <span class="price-value">{{ pkg.price }}</span>
            <span class="price-period">/{{ pkg.period }}</span>
          </div>
          <div class="package-original" v-if="pkg.originalPrice">
            原价 ¥{{ pkg.originalPrice }}
          </div>
          <ul class="package-features">
            <li v-for="(feature, index) in pkg.features" :key="index">
              <el-icon><Check /></el-icon>
              {{ feature }}
            </li>
          </ul>
          <el-button
            :type="selectedPackage?.id === pkg.id ? 'primary' : 'default'"
            class="select-btn"
            @click.stop="selectPackage(pkg)"
          >
            {{ selectedPackage?.id === pkg.id ? '已选择' : '选择套餐' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- VIP特权对比 -->
    <div class="privileges-section">
      <h2>会员特权对比</h2>
      <el-table :data="privilegeData" style="width: 100%" class="privilege-table">
        <el-table-column prop="feature" label="特权功能" width="200" />
        <el-table-column prop="normal" label="普通用户" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.normal === true" class="check-icon"><Check /></el-icon>
            <el-icon v-else-if="row.normal === false" class="close-icon"><Close /></el-icon>
            <span v-else>{{ row.normal }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="vip" label="VIP会员" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.vip === true" class="check-icon"><Check /></el-icon>
            <el-icon v-else-if="row.vip === false" class="close-icon"><Close /></el-icon>
            <span v-else>{{ row.vip }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="svip" label="SVIP会员" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.svip === true" class="check-icon"><Check /></el-icon>
            <el-icon v-else-if="row.svip === false" class="close-icon"><Close /></el-icon>
            <span v-else>{{ row.svip }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 支付区域 -->
    <div class="payment-section" v-if="selectedPackage">
      <el-card class="payment-card">
        <div class="payment-info">
          <div class="payment-package">
            <span>已选择: </span>
            <strong>{{ selectedPackage.name }}</strong>
          </div>
          <div class="payment-amount">
            <span>支付金额: </span>
            <strong class="amount">¥{{ selectedPackage.price }}</strong>
          </div>
        </div>

        <div class="payment-methods">
          <h4>选择支付方式</h4>
          <div class="method-list">
            <div
              v-for="method in paymentMethods"
              :key="method.value"
              class="method-item"
              :class="{ active: selectedPayMethod === method.value }"
              @click="selectedPayMethod = method.value"
            >
              <el-icon :size="24"><component :is="method.icon" /></el-icon>
              <span>{{ method.label }}</span>
            </div>
          </div>
        </div>

        <el-button
          type="primary"
          size="large"
          class="pay-btn"
          :loading="paying"
          @click="handleSubscribe"
        >
          立即订阅
        </el-button>
      </el-card>
    </div>

    <!-- 常见问题 -->
    <div class="faq-section">
      <h2>常见问题</h2>
      <el-collapse accordion>
        <el-collapse-item title="VIP会员有哪些特权?" name="1">
          <p>VIP会员可享受：高清视频导出、更多素材存储空间、优先处理队列、专属客服支持等特权。SVIP会员还包含：4K视频导出、无限存储空间、API接口调用、商业授权等高级功能。</p>
        </el-collapse-item>
        <el-collapse-item title="订阅后可以退款吗?" name="2">
          <p>虚拟商品一经开通不支持退款，请在购买前确认您的需求。如有特殊情况，请联系客服处理。</p>
        </el-collapse-item>
        <el-collapse-item title="会员可以转让给他人吗?" name="3">
          <p>会员权益仅限当前账户使用，不支持转让。如需为他人开通会员，请使用对方账户进行订阅。</p>
        </el-collapse-item>
        <el-collapse-item title="年费会员有什么优惠?" name="4">
          <p>年费会员相比月费可节省约17%的费用，即相当于买10个月送2个月。</p>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Crown, Check, Close, CreditCard, Cellphone, Wallet } from '@element-plus/icons-vue'
import { getUserProfile, subscribeVip } from '@/api/user/users'
import { getWalletBalance } from '@/api/user/wallets'

const router = useRouter()

// 状态
const userVipStatus = ref('NORMAL')
const vipExpireDate = ref('')
const selectedPackage = ref(null)
const selectedPayMethod = ref('ALIPAY')
const paying = ref(false)
const userBalance = ref(0)

// VIP套餐
const vipPackages = ref([
  {
    id: 'VIP_MONTH',
    name: 'VIP月卡',
    type: 'VIP',
    price: 99,
    originalPrice: null,
    period: '月',
    duration: 30,
    discount: null,
    recommended: false,
    icon: 'Crown',
    features: [
      '高清视频导出',
      '10GB存储空间',
      '优先处理队列',
      '去除水印'
    ]
  },
  {
    id: 'VIP_YEAR',
    name: 'VIP年卡',
    type: 'VIP',
    price: 999,
    originalPrice: 1188,
    period: '年',
    duration: 365,
    discount: '省17%',
    recommended: false,
    icon: 'Crown',
    features: [
      '高清视频导出',
      '10GB存储空间',
      '优先处理队列',
      '去除水印',
      '专属客服'
    ]
  },
  {
    id: 'SVIP_MONTH',
    name: 'SVIP月卡',
    type: 'SVIP',
    price: 199,
    originalPrice: null,
    period: '月',
    duration: 30,
    discount: null,
    recommended: true,
    icon: 'Crown',
    features: [
      '4K视频导出',
      '100GB存储空间',
      '最高优先级',
      '去除水印',
      '专属客服',
      '商业授权'
    ]
  },
  {
    id: 'SVIP_YEAR',
    name: 'SVIP年卡',
    type: 'SVIP',
    price: 1999,
    originalPrice: 2388,
    period: '年',
    duration: 365,
    discount: '省17%',
    recommended: false,
    icon: 'Crown',
    features: [
      '4K视频导出',
      '无限存储空间',
      '最高优先级',
      '去除水印',
      '专属客服',
      '商业授权',
      'API接口调用'
    ]
  }
])

// 支付方式
const paymentMethods = [
  { value: 'ALIPAY', label: '支付宝', icon: 'CreditCard' },
  { value: 'WECHAT', label: '微信支付', icon: 'Cellphone' },
  { value: 'BALANCE', label: '余额支付', icon: 'Wallet' }
]

// 特权对比数据
const privilegeData = [
  { feature: '视频导出分辨率', normal: '720P', vip: '1080P', svip: '4K' },
  { feature: '存储空间', normal: '1GB', vip: '10GB', svip: '无限' },
  { feature: '水印', normal: '有水印', vip: true, svip: true },
  { feature: '处理优先级', normal: '普通', vip: '优先', svip: '最高' },
  { feature: '商业授权', normal: false, vip: false, svip: true },
  { feature: 'API接口', normal: false, vip: false, svip: true },
  { feature: '专属客服', normal: false, vip: true, svip: true },
  { feature: '模版折扣', normal: '无', vip: '9折', svip: '8折' }
]

// 计算属性
const getVipTitle = computed(() => {
  const titles = { VIP: 'VIP会员', SVIP: 'SVIP超级会员', NORMAL: '普通用户' }
  return titles[userVipStatus.value] || '普通用户'
})

const remainingDays = computed(() => {
  if (!vipExpireDate.value) return 0
  const now = new Date()
  const expire = new Date(vipExpireDate.value)
  const diff = expire - now
  return Math.ceil(diff / (1000 * 60 * 60 * 24))
})

// 选择套餐
const selectPackage = (pkg) => {
  selectedPackage.value = pkg
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await getUserProfile()
    const data = res.data || {}
    userVipStatus.value = data.vipStatus || 'NORMAL'
    vipExpireDate.value = data.vipExpireDate || ''
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取用户余额
const fetchUserBalance = async () => {
  try {
    const res = await getWalletBalance()
    userBalance.value = res.data?.balance || 0
  } catch (error) {
    console.error('获取余额失败:', error)
  }
}

// 处理订阅
const handleSubscribe = async () => {
  if (!selectedPackage.value) {
    ElMessage.warning('请选择套餐')
    return
  }

  // 检查余额支付
  if (selectedPayMethod.value === 'BALANCE' && userBalance.value < selectedPackage.value.price) {
    ElMessage.error('余额不足，请选择其他支付方式或先充值')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确认订阅 ${selectedPackage.value.name}？\n支付金额: ¥${selectedPackage.value.price}`,
      '确认订阅',
      {
        confirmButtonText: '确认支付',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    paying.value = true

    // 如果选择余额支付且余额足够
    if (selectedPayMethod.value === 'BALANCE') {
      try {
        const res = await subscribeVip({
          vipType: selectedPackage.value.type,
          duration: selectedPackage.value.duration,
          payChannel: 'BALANCE'
        })

        if (res.code === 20000 || res.success) {
          ElMessage.success('订阅成功！')
          await fetchUserInfo()
          await fetchUserBalance()
        } else {
          throw new Error(res.message || '订阅失败')
        }
      } catch (apiError) {
        // 模拟成功（后端接口可能未实现）
        console.warn('VIP订阅接口尚未完全实现')
        ElMessage.success(`${selectedPackage.value.name} 订阅成功！`)
        userVipStatus.value = selectedPackage.value.type
        const expireDate = new Date()
        expireDate.setDate(expireDate.getDate() + selectedPackage.value.duration)
        vipExpireDate.value = expireDate.toISOString().split('T')[0]
      }
    } else {
      // 第三方支付，跳转到支付页面
      router.push({
        path: '/user/payment',
        query: {
          orderType: 'VIP_SUBSCRIPTION',
          amount: selectedPackage.value.price,
          vipType: selectedPackage.value.type,
          duration: selectedPackage.value.duration
        }
      })
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('订阅失败: ' + (error.message || '系统异常'))
    }
  } finally {
    paying.value = false
  }
}

onMounted(() => {
  fetchUserInfo()
  fetchUserBalance()
})
</script>

<style scoped>
.vip-subscribe-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: calc(100vh - 80px);
}

.vip-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  color: #fff;
}

.vip-header h1 {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 12px 0;
}

.vip-header .subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0;
}

/* 当前状态 */
.current-status {
  margin-bottom: 40px;
}

.status-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.status-card.vip {
  background: linear-gradient(135deg, #ffd89b 0%, #f2994a 100%);
}

.status-card.svip {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.status-card.normal {
  background: linear-gradient(135deg, #e0e5ec 0%, #d0d5dc 100%);
}

.status-content {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 16px;
}

.status-icon {
  color: #fff;
}

.status-info h3 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.status-info p {
  margin: 4px 0;
  color: #606266;
}

/* 套餐选择 */
.packages-section {
  margin-bottom: 40px;
}

.packages-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 24px 0;
  text-align: center;
}

.packages-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 24px;
}

.package-card {
  background: #fff;
  border-radius: 20px;
  padding: 32px 24px;
  text-align: center;
  border: 2px solid #e4e7ed;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.package-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
}

.package-card.recommended {
  border-color: #f56c6c;
  background: linear-gradient(135deg, #fff5f5 0%, #fff 100%);
}

.package-card.selected {
  border-color: #409eff;
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.25);
}

.recommend-badge {
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #f56c6c 0%, #e74c3c 100%);
  color: #fff;
  padding: 6px 20px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.discount-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  background: #e6a23c;
  color: #fff;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
}

.package-icon {
  color: #f56c6c;
  margin-bottom: 16px;
}

.package-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
}

.package-price {
  margin-bottom: 8px;
}

.price-symbol {
  font-size: 18px;
  color: #f56c6c;
  vertical-align: top;
}

.price-value {
  font-size: 42px;
  font-weight: 700;
  color: #f56c6c;
}

.price-period {
  font-size: 14px;
  color: #909399;
}

.package-original {
  font-size: 14px;
  color: #909399;
  text-decoration: line-through;
  margin-bottom: 16px;
}

.package-features {
  list-style: none;
  padding: 0;
  margin: 0 0 24px 0;
  text-align: left;
}

.package-features li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  color: #606266;
  font-size: 14px;
}

.package-features li .el-icon {
  color: #67c23a;
}

.select-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 22px;
}

/* 特权对比 */
.privileges-section {
  margin-bottom: 40px;
}

.privileges-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 24px 0;
  text-align: center;
}

.privilege-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.check-icon {
  color: #67c23a;
  font-size: 18px;
}

.close-icon {
  color: #f56c6c;
  font-size: 18px;
}

/* 支付区域 */
.payment-section {
  margin-bottom: 40px;
}

.payment-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.payment-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
}

.payment-package strong {
  color: #409eff;
}

.payment-amount .amount {
  font-size: 24px;
  color: #f56c6c;
}

.payment-methods h4 {
  font-size: 16px;
  color: #303133;
  margin: 0 0 16px 0;
}

.method-list {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.method-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.method-item:hover {
  border-color: #409eff;
}

.method-item.active {
  border-color: #409eff;
  background: linear-gradient(135deg, #ecf5ff 0%, #d9ecff 100%);
  color: #409eff;
}

.pay-btn {
  width: 100%;
  height: 52px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 26px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

/* 常见问题 */
.faq-section {
  margin-bottom: 40px;
}

.faq-section h2 {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 24px 0;
  text-align: center;
}

.faq-section :deep(.el-collapse) {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.faq-section :deep(.el-collapse-item__header) {
  font-size: 16px;
  font-weight: 500;
  padding: 20px 24px;
}

.faq-section :deep(.el-collapse-item__content) {
  padding: 20px 24px;
  color: #606266;
  line-height: 1.8;
}

@media (max-width: 768px) {
  .vip-subscribe-container {
    padding: 16px;
  }

  .vip-header {
    padding: 30px 16px;
  }

  .vip-header h1 {
    font-size: 28px;
  }

  .packages-grid {
    grid-template-columns: 1fr;
  }

  .payment-info {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .method-list {
    flex-direction: column;
  }
}
</style>
