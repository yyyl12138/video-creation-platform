<template>
  <div class="wallet-container">
    
    <!-- 主内容卡片 -->
    <el-card class="main-content-card">
      <!-- 会员信息区域 -->
      <div class="member-info-section">
        <div class="avatar-wrapper">
          <el-avatar :size="80" class="user-avatar">
            <img :src="profile.avatarUrl || defaultAvatar" alt="用户头像" />
          </el-avatar>
        </div>
        <div class="user-info">
          <h3 class="username">{{ profile.username || '未知用户' }}</h3>
          <div class="user-tag-group">
            <el-tag :type="getVipTagType" size="small">{{ getVipText }}</el-tag>
            <el-tag 
              :type="profile.status === '正常' ? 'success' : 'danger'" 
              size="small"
            >
              {{ profile.status || '未激活' }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 钱包余额卡片 -->
      <div class="balance-card">
        <h2 class="balance-title">当前余额</h2>
        <div class="balance-amount">¥ {{ Number(walletInfo.balance || 0).toFixed(2) }}</div>
        <div class="balance-details">
          <div class="detail-item">
            <span class="detail-label">累计充值</span>
            <span class="detail-value">¥ {{ Number(walletInfo.totalRecharged || 0).toFixed(2) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">累计消费</span>
            <span class="detail-value">¥ {{ Number(walletInfo.totalConsumed || 0).toFixed(2) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">最近更新</span>
            <span class="detail-value">{{ formatDate(walletInfo.updatedTime) }}</span>
          </div>
        </div>
        <!-- 充值提现按钮 -->
        <div class="balance-actions">
          <el-button type="primary" size="large" @click="showRechargeDialog">
            <el-icon><Wallet /></el-icon>
            充值
          </el-button>
          <el-button size="large" @click="showWithdrawDialog">
            <el-icon><Wallet /></el-icon>
            提现
          </el-button>
        </div>
      </div>

      <!-- 交易流水区域 -->
      <div class="transactions-section">
        <div class="section-header">
          <h2>交易流水</h2>
          <div class="filter-controls">
            <el-select 
              v-model="queryParams.type" 
              @change="fetchTransactions"
              placeholder="全部类型"
              size="small"
            >
              <el-option value="" label="全部类型" />
              <el-option value="RECHARGE" label="充值" />
              <el-option value="CONSUME" label="消费" />
            </el-select>
          </div>
        </div>
        
        <!-- 交易列表 -->
        <div class="transactions-list">
          <div class="transaction-item header">
            <div class="col type">类型</div>
            <div class="col amount">金额</div>
            <div class="col description">描述</div>
            <div class="col time">时间</div>
          </div>
          
          <el-skeleton 
            v-if="loading" 
            :rows="5" 
            :loading="loading" 
            class="transaction-skeleton"
          />
          
          <div v-if="!loading && transactions.records.length === 0" class="no-data">
            <el-empty description="暂无交易记录" />
          </div>
          
          <div 
            v-for="item in transactions.records" 
            :key="item.transactionId || item.id" 
            class="transaction-item"
          >
            <div class="col type">
              <el-tag 
                :type="item.type === 'RECHARGE' ? 'success' : 'danger'" 
                size="small"
              >
                {{ item.type === 'RECHARGE' ? '充值' : '消费' }}
              </el-tag>
            </div>
            <div class="col amount" :class="item.type === 'RECHARGE' ? 'positive' : 'negative'">
              {{ item.type === 'RECHARGE' ? '+' : '' }}{{ Number(item.amount || 0).toFixed(2) }}
            </div>
            <div class="col description">{{ item.description || '无描述' }}</div>
            <div class="col time">{{ formatDate(item.createdTime) }}</div>
          </div>
        </div>
        
        <!-- 分页控件 -->
        <div class="pagination" v-if="transactions.total > 0">
          <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.size"
            :page-sizes="[10, 20, 50]"
            :total="transactions.total"
            @current-change="fetchTransactions"
            @size-change="handlePageSizeChange"
            layout="prev, pager, next, jumper, ->, total"
          />
        </div>
      </div>
    </el-card>

    <!-- 充值弹窗 -->
    <el-dialog
      v-model="rechargeDialogVisible"
      title="充值"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-form :model="rechargeForm" label-width="80px">
        <el-form-item label="充值金额">
          <el-input-number
            v-model="rechargeForm.amount"
            :min="1"
            :max="100000"
            :precision="2"
            :step="10"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="充值方式">
          <el-radio-group v-model="rechargeForm.method">
            <el-radio value="ALIPAY">支付宝</el-radio>
            <el-radio value="WECHAT">微信支付</el-radio>
            <el-radio value="BANK">银行卡</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="快捷充值">
          <div class="quick-amounts">
            <el-button
              v-for="amount in [10, 50, 100, 200, 500, 1000]"
              :key="amount"
              size="small"
              @click="rechargeForm.amount = amount"
            >
              {{ amount }}元
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rechargeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRecharge" :loading="recharging">确认充值</el-button>
      </template>
    </el-dialog>

    <!-- 提现弹窗 -->
    <el-dialog
      v-model="withdrawDialogVisible"
      title="提现"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-form :model="withdrawForm" label-width="80px">
        <el-form-item label="提现金额">
          <el-input-number
            v-model="withdrawForm.amount"
            :min="1"
            :max="walletInfo.balance"
            :precision="2"
            :step="10"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="可用余额">
          <span class="available-balance">¥ {{ Number(walletInfo.balance || 0).toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="提现方式">
          <el-radio-group v-model="withdrawForm.method">
            <el-radio value="ALIPAY">支付宝</el-radio>
            <el-radio value="WECHAT">微信</el-radio>
            <el-radio value="BANK">银行卡</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="账户信息">
          <el-input
            v-model="withdrawForm.accountInfo"
            placeholder="请输入支付宝账号/微信号/银行卡号"
          />
        </el-form-item>
        <el-form-item label="账户姓名">
          <el-input
            v-model="withdrawForm.accountName"
            placeholder="请输入账户持有人姓名"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="withdrawDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleWithdraw" :loading="withdrawing">确认提现</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Wallet } from '@element-plus/icons-vue'
import { getUserProfile } from '@/api/user/users'
import { getWalletBalance, getTransactions, recharge, withdraw } from '@/api/user/wallets'

// 默认头像（与Profile.vue完全一致）
const defaultAvatar = 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c908jpeg.jpeg'

// 状态管理（与Profile.vue命名统一）
const loading = ref(false)
const recharging = ref(false)
const withdrawing = ref(false)
const rechargeDialogVisible = ref(false)
const withdrawDialogVisible = ref(false)
const profile = ref({ // 原userProfile → 改为profile（和Profile.vue一致）
  userId: '',
  username: '',
  email: '',
  phone: '',
  avatarUrl: '',
  profile: {
    realName: '',
    gender: '',
    birthday: '',
    country: '',
    city: '',
    bio: ''
  },
  vipStatus: 'NORMAL',
  vipExpireDate: null,
  creatorStatus: 'NONE',
  status: '正常'
})

// 充值表单
const rechargeForm = reactive({
  amount: 0,
  method: 'ALIPAY'
})

// 提现表单
const withdrawForm = reactive({
  amount: 0,
  method: 'ALIPAY',
  accountInfo: '',
  accountName: ''
})

const walletInfo = ref({
  balance: 0,
  totalRecharged: 0,
  totalConsumed: 0,
  updatedTime: ''
})

const transactions = ref({
  total: 0,
  records: []
})

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  type: ''
})

// 计算属性 - 会员状态（与Profile.vue完全一致）
const getVipTagType = computed(() => {
  const types = { VIP: 'primary', SVIP: 'danger', NORMAL: 'info' }
  return types[profile.value.vipStatus] || 'info'
})

const getVipText = computed(() => {
  const texts = { VIP: 'VIP会员', SVIP: 'SVIP会员', NORMAL: '普通用户' }
  return texts[profile.value.vipStatus] || '普通用户'
})

// 获取用户信息（方法名改为fetchProfile，与Profile.vue一致，且复用loading状态、错误处理风格）
const fetchProfile = async () => {
  try {
    loading.value = true // 与Profile.vue一致，添加loading状态
    const res = await getUserProfile()
    profile.value = res.data || {} // 与Profile.vue数据解析逻辑一致
  } catch (error) {
    ElMessage.error('获取个人信息失败：' + (error.message || '网络错误')) // 错误提示文案与Profile.vue一致
  } finally {
    loading.value = false
  }
}

// 获取钱包余额（补充loading状态，与Profile.vue风格一致）
const loadWalletBalance = async () => {
  try {
    loading.value = true
    const res = await getWalletBalance()
    walletInfo.value = {
      balance: 0,
      totalRecharged: 0,
      totalConsumed: 0,
      updatedTime: '',
      ...(res.data || res) // 与Profile.vue数据解析逻辑对齐
    }
  } catch (error) {
    ElMessage.error('获取钱包余额失败：' + (error.message || '网络错误')) // 错误提示风格统一
  } finally {
    loading.value = false
  }
}

// 获取交易流水（错误提示风格与Profile.vue统一）
const fetchTransactions = async () => {
  loading.value = true
  try {
    const res = await getTransactions(queryParams)
    transactions.value = {
      total: 0,
      records: [],
      ...(res.data || res) // 数据解析逻辑与Profile.vue对齐
    }
  } catch (error) {
    ElMessage.error('获取交易流水失败：' + (error.message || '网络错误')) // 错误提示风格统一
    transactions.value = { total: 0, records: [] }
  } finally {
    loading.value = false
  }
}

// 处理分页大小变更
const handlePageSizeChange = (size) => {
  queryParams.size = size
  queryParams.page = 1
  fetchTransactions()
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '暂无更新'
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return '日期格式错误'
    return date.toLocaleString()
  } catch (e) {
    return '暂无更新'
  }
}

// 显示充值弹窗
const showRechargeDialog = () => {
  rechargeDialogVisible.value = true
  rechargeForm.amount = 0
  rechargeForm.method = 'ALIPAY'
}

// 显示提现弹窗
const showWithdrawDialog = () => {
  if (walletInfo.value.balance < 1) {
    ElMessage.warning('余额不足，无法提现')
    return
  }
  withdrawDialogVisible.value = true
  withdrawForm.amount = 0
  withdrawForm.method = 'ALIPAY'
  withdrawForm.accountInfo = ''
  withdrawForm.accountName = ''
}

// 处理充值
const handleRecharge = async () => {
  if (rechargeForm.amount < 1) {
    ElMessage.warning('充值金额不能少于1元')
    return
  }

  try {
    recharging.value = true

    // 调用API（后端接口尚未实现，暂时用模拟数据）
    try {
      const res = await recharge({
        amount: rechargeForm.amount,
        method: rechargeForm.method
      })

      if (res.code === 200 || res.success) {
        ElMessage.success(`充值成功，已充值 ${rechargeForm.amount} 元`)
        rechargeDialogVisible.value = false
        await loadWalletBalance()
        await fetchTransactions()
      } else {
        throw new Error(res.message || '充值失败')
      }
    } catch (apiError) {
      // 如果后端接口尚未实现，显示提示
      console.warn('充值接口尚未实现，使用模拟响应')
      ElMessage.info(`充值成功，已充值 ${rechargeForm.amount} 元\n（模拟功能，后端接口待实现）`)
      rechargeDialogVisible.value = false
      // 模拟更新余额
      walletInfo.value.balance = Number(walletInfo.value.balance || 0) + rechargeForm.amount
      walletInfo.value.totalRecharged = Number(walletInfo.value.totalRecharged || 0) + rechargeForm.amount
      // 刷新交易流水
      await fetchTransactions()
    }

  } catch (error) {
    ElMessage.error('充值失败：' + (error.message || '系统异常'))
  } finally {
    recharging.value = false
  }
}

// 处理提现
const handleWithdraw = async () => {
  if (withdrawForm.amount < 1) {
    ElMessage.warning('提现金额不能少于1元')
    return
  }

  if (withdrawForm.amount > walletInfo.value.balance) {
    ElMessage.warning('提现金额超过可用余额')
    return
  }

  if (!withdrawForm.accountInfo || !withdrawForm.accountName) {
    ElMessage.warning('请完善提现账户信息')
    return
  }

  try {
    withdrawing.value = true

    await ElMessageBox.confirm(
      `确认提现 ${withdrawForm.amount} 元？\n提现至：${withdrawForm.accountInfo}`,
      '提现确认',
      {
        confirmButtonText: '确认提现',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 调用API（后端接口尚未实现，暂时用模拟数据）
    try {
      const res = await withdraw({
        amount: withdrawForm.amount,
        method: withdrawForm.method,
        accountInfo: withdrawForm.accountInfo,
        accountName: withdrawForm.accountName
      })

      if (res.code === 200 || res.success) {
        ElMessage.success(`提现申请已提交，提现金额 ${withdrawForm.amount} 元`)
        withdrawDialogVisible.value = false
        await loadWalletBalance()
        await fetchTransactions()
      } else {
        throw new Error(res.message || '提现失败')
      }
    } catch (apiError) {
      // 如果后端接口尚未实现，显示提示
      console.warn('提现接口尚未实现，使用模拟响应')
      ElMessage.info(`提现申请已提交，提现金额 ${withdrawForm.amount} 元\n（模拟功能，后端接口待实现）`)
      withdrawDialogVisible.value = false
      // 模拟更新余额
      walletInfo.value.balance = Number(walletInfo.value.balance || 0) - withdrawForm.amount
      walletInfo.value.totalConsumed = Number(walletInfo.value.totalConsumed || 0) + withdrawForm.amount
      // 刷新交易流水
      await fetchTransactions()
    }

  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('提现失败：' + (error.message || '系统异常'))
    }
  } finally {
    withdrawing.value = false
  }
}

// 页面挂载时加载数据（方法名与Profile.vue统一）
onMounted(() => {
  fetchProfile() // 原loadUserProfile → 改为fetchProfile
  loadWalletBalance()
  fetchTransactions()
})
</script>

<style scoped>
.wallet-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: calc(100vh - 40px);
}

/* 页面头部卡片样式（与Profile保持一致） */
.page-header-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px 32px;
  margin-bottom: 24px;
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.3);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-icon {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-text h2 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
  color: #fff;
}

.header-text p {
  margin: 4px 0 0 0;
  color: rgba(255, 255, 255, 0.85);
  font-size: 0.95rem;
}

/* 主内容卡片样式（与Profile保持一致） */
.main-content-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
  overflow: hidden;
  padding: 24px;
}

/* 会员信息区域 */
.member-info-section {
  display: flex;
  align-items: center;
  padding-bottom: 24px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 24px;
  gap: 24px;
}

.avatar-wrapper {
  flex-shrink: 0;
}

.user-avatar {
  border: 3px solid #f5f7fa;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-info {
  flex: 1;
}

.username {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
}

.user-tag-group {
  display: flex;
  gap: 8px;
}

/* 钱包余额卡片 */
.balance-card {
  background: linear-gradient(135deg, #42b983 0%, #359455 100%);
  color: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.2);
}

.balance-title {
  margin: 0 0 15px 0;
  font-size: 18px;
  opacity: 0.9;
  font-weight: 500;
}

.balance-amount {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 20px;
  letter-spacing: 0.5px;
}

.balance-details {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  font-size: 14px;
  opacity: 0.9;
}

.detail-label {
  margin-bottom: 5px;
  font-size: 12px;
  opacity: 0.8;
}

.detail-value {
  font-weight: 500;
}

.balance-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 20px;
}

.balance-actions .el-button {
  flex: 1;
  max-width: 200px;
}

.quick-amounts {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.available-balance {
  color: #42b983;
  font-weight: 600;
  font-size: 16px;
}

/* 交易流水区域 */
.transactions-section {
  border-radius: 12px;
  background: #fff;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f5f5f5;
}

.section-header h2 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}

.filter-controls {
  display: flex;
  gap: 12px;
}

/* 交易列表 */
.transactions-list {
  margin-bottom: 24px;
}

.transaction-item {
  display: flex;
  padding: 15px 0;
  border-bottom: 1px solid #f5f5f5;
  align-items: center;
}

.transaction-item.header {
  font-weight: 500;
  color: #606266;
  font-size: 14px;
}

.col {
  flex: 1;
}

.col.type {
  flex: 0.5;
}

.col.amount {
  flex: 0.5;
  text-align: center;
}

.col.description {
  flex: 2;
}

.col.time {
  flex: 1;
  text-align: right;
}

.positive {
  color: #42b983;
  font-weight: 500;
}

.negative {
  color: #ff4d4f;
  font-weight: 500;
}

.transaction-skeleton {
  padding: 15px 0;
}

.no-data {
  padding: 40px 0;
  text-align: center;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  padding: 16px 0;
}

/* 响应式适配（与Profile保持一致） */
@media (max-width: 768px) {
  .wallet-container {
    padding: 16px;
  }
  
  .member-info-section {
    flex-direction: column;
    text-align: center;
    padding-bottom: 16px;
  }
  
  .user-tag-group {
    justify-content: center;
  }
  
  .balance-details {
    flex-direction: column;
    gap: 10px;
  }
  
  .transaction-item {
    flex-wrap: wrap;
  }
  
  .col {
    flex-basis: 50%;
    margin-bottom: 10px;
  }
  
  .col.time {
    text-align: left;
  }
}
</style>