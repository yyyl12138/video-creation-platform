<template>
  <div class="payment-result-container">
    <el-card class="result-card">
      <div v-if="verifying" class="verifying-state">
        <el-skeleton :rows="5" animated />
        <p>正在验证支付状态，请稍后...</p>
      </div>

      <div v-else-if="status === 'PAID'" class="status-content success">
        <el-result icon="success" title="支付成功" :sub-title="'订单号：' + orderNo">
          <template #extra>
            <div class="result-details">
              <p>您的积分已充值到账，请在钱包中查看。</p>
            </div>
            <div class="action-buttons">
              <el-button type="primary" @click="$router.push('/user/wallet')">返回钱包</el-button>
              <el-button @click="$router.push('/home')">前往首页</el-button>
            </div>
          </template>
        </el-result>
      </div>

      <div v-else class="status-content error">
        <el-result icon="warning" title="支付正在处理中或已取消" :sub-title="'订单号：' + orderNo">
          <template #extra>
            <p v-if="status === 'PENDING'">如果您的支付已扣款，请稍后刷新钱包查看。如有异常请联系客服。</p>
            <p v-else>订单状态：{{ statusText }}</p>
            <div class="action-buttons">
              <el-button type="primary" @click="$router.push('/user/wallet')">返回钱包</el-button>
              <el-button @click="checkStatus">重新检查</el-button>
            </div>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderStatus } from '@/api/user/orders'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const verifying = ref(true)
const status = ref('PENDING')
const orderNo = ref(route.query.out_trade_no || '')

const statusText = computed(() => {
  const map = {
    'PENDING': '待支付',
    'PAID': '已支付',
    'CANCELLED': '已取消',
    'REFUNDED': '已退款'
  }
  return map[status.value] || status.value
})

const checkStatus = async () => {
  if (!orderNo.value) {
    verifying.value = false
    status.value = 'ERROR'
    return
  }

  try {
    verifying.value = true
    const res = await getOrderStatus(orderNo.value)
    // 兼容 Result 包装或直接 Response
    const data = res.data || res
    status.value = data.status
  } catch (error) {
    console.error('查询订单状态失败:', error)
    ElMessage.error('查询订单状态失败')
  } finally {
    verifying.value = false
  }
}

onMounted(() => {
  // 从 URL query 中获取订单号 (支付宝 return_url 会携带 out_trade_no)
  if (route.query.out_trade_no) {
    orderNo.value = route.query.out_trade_no
    // 延迟 1 秒检查，给后端回调处理留点时间
    setTimeout(checkStatus, 1500)
  } else {
    verifying.value = false
    status.value = 'UNKNOWN'
  }
})
</script>

<style scoped>
.payment-result-container {
  padding: 80px 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
}

.result-card {
  max-width: 600px;
  width: 100%;
  border-radius: 16px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.verifying-state {
  padding: 40px;
}

.result-details {
  margin-bottom: 24px;
  color: #606266;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 20px;
}

:deep(.el-result__extra) {
  width: 100%;
}
</style>
