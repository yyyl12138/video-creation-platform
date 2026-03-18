<template>
  <div class="profile-container">
    <!-- 头像预览/更换弹窗 -->
    <el-dialog 
      v-model="avatarDialogVisible" 
      title="头像管理" 
      width="400px"
      center
      class="avatar-dialog"
      destroy-on-close
    >
      <div class="avatar-preview">
        <!-- 实时预览选中的图片 -->
        <img 
          :src="avatarPreviewUrl || getAvatarUrl" 
          alt="头像预览" 
          class="avatar-big"
        />
      </div>
      <div class="avatar-upload-actions">
        <!-- 修复核心：label绑定id + 优化样式确保点击触发 -->
        <label for="avatar-upload-input" class="upload-btn-wrapper">
          <el-button type="primary" :icon="Upload" :loading="avatarUploading">选择新头像</el-button>
          <!-- 给input添加唯一id，确保label能触发 -->
          <input 
            id="avatar-upload-input"
            type="file" 
            accept="image/jpeg,image/png" 
            @change="handleAvatarUpload" 
            class="avatar-upload-input"
          />
        </label>
      </div>
    </el-dialog>

    <!-- 主内容卡片 -->
    <el-card class="main-content-card">
      <!-- 顶部信息区：头像 + 基础信息 -->
      <div class="profile-header">
        <div 
          class="avatar-wrapper" 
          @click="avatarDialogVisible = true"
          title="点击查看/更换头像"
        >
          <el-avatar :size="100" class="user-avatar">
            <img :src="getAvatarUrl" alt="用户头像" />
          </el-avatar>
          <span class="avatar-tip">点击更换头像</span>
        </div>

        <div class="user-base-info">
          <h2 class="username">{{ profile.username || '未设置用户名' }}</h2>
          <div class="user-tag-group">
            <el-tag :type="getVipTagType" size="small">{{ getVipText }}</el-tag>
            <el-tag :type="getCreatorTagType" size="small">{{ getCreatorText }}</el-tag>
            <el-tag 
              :type="profile.status === '正常' ? 'success' : 'danger'" 
              size="small"
            >
              {{ profile.status || '未激活' }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 标签页切换 -->
      <el-tabs 
        v-model="activeTab" 
        class="profile-tabs"
        type="border-card"
      >
        <!-- 1. 基本信息标签页 -->
        <el-tab-pane label="基本信息" name="info">
          <div class="tab-content">
            <!-- 信息展示区域 -->
            <div v-if="!isEditingInfo" class="info-display">
              <el-descriptions :column="2" border class="info-desc">
                <el-descriptions-item label="用户ID" label-class-name="desc-label">{{ profile.userId || '-' }}</el-descriptions-item>
                <el-descriptions-item label="邮箱" label-class-name="desc-label">{{ profile.email || '-' }}</el-descriptions-item>
                <el-descriptions-item label="手机号" label-class-name="desc-label">{{ profile.phone || '-' }}</el-descriptions-item>
                <el-descriptions-item label="会员过期时间" label-class-name="desc-label">{{ profile.vipExpireDate || '无' }}</el-descriptions-item>
                <el-descriptions-item label="真实姓名" label-class-name="desc-label" :span="2">{{ profile.profile?.realName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="性别" label-class-name="desc-label" :span="2">{{ profile.profile?.gender || '-' }}</el-descriptions-item>
                <el-descriptions-item label="生日" label-class-name="desc-label" :span="2">{{ profile.profile?.birthday || '-' }}</el-descriptions-item>
                <el-descriptions-item label="所在地区" label-class-name="desc-label" :span="2">{{ `${profile.profile?.country || '-'} ${profile.profile?.city || '-'}` }}</el-descriptions-item>
                <el-descriptions-item label="个人简介" label-class-name="desc-label" :span="2">{{ profile.profile?.bio || '-' }}</el-descriptions-item>
              </el-descriptions>
              <div class="action-btn-group">
                <el-button type="primary" @click="isEditingInfo = true">编辑资料</el-button>
              </div>
            </div>

            <!-- 信息编辑表单 -->
            <div v-else class="info-form">
              <el-form 
                :model="profileForm" 
                label-width="100px" 
                :rules="profileRules" 
                ref="profileFormRef"
                class="form-container"
              >
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                  <el-select v-model="profileForm.gender" placeholder="请选择性别">
                    <el-option label="男" value="男" />
                    <el-option label="女" value="女" />
                    <el-option label="保密" value="保密" />
                  </el-select>
                </el-form-item>
                <el-form-item label="生日" prop="birthday">
                  <el-date-picker
                    v-model="profileForm.birthday"
                    type="date"
                    placeholder="请选择生日"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                  />
                </el-form-item>
                <el-form-item label="国家/地区" prop="country">
                  <el-input v-model="profileForm.country" placeholder="请输入国家/地区" />
                </el-form-item>
                <el-form-item label="城市" prop="city">
                  <el-input v-model="profileForm.city" placeholder="请输入城市" />
                </el-form-item>
                <el-form-item label="个人简介" prop="bio">
                  <el-input
                    v-model="profileForm.bio"
                    type="textarea"
                    placeholder="请输入个人简介"
                    :rows="3"
                  />
                </el-form-item>
                <el-form-item class="form-actions">
                  <el-button type="primary" @click="submitProfile" :loading="loading">保存修改</el-button>
                  <el-button @click="cancelEditInfo">取消</el-button>
                </el-form-item>
              </el-form>
            </div>
          </div>
        </el-tab-pane>

        <!-- 2. 修改密码标签页 -->
        <el-tab-pane label="修改密码" name="password">
          <div class="tab-content">
            <el-form 
              :model="passwordForm" 
              label-width="100px" 
              :rules="passwordRules" 
              ref="passwordFormRef"
              class="form-container"
            >
              <el-form-item label="旧密码" prop="oldPassword">
                <el-input
                  v-model="passwordForm.oldPassword"
                  type="password"
                  placeholder="请输入旧密码"
                  show-password
                />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input
                  v-model="passwordForm.newPassword"
                  type="password"
                  placeholder="请输入新密码（8-20位，含字母和数字）"
                  show-password
                />
              </el-form-item>
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input
                  v-model="passwordForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                />
              </el-form-item>
              <el-form-item class="form-actions">
                <el-button type="primary" @click="submitPassword" :loading="loading">确认修改</el-button>
                <el-button @click="resetPasswordForm">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>

        <!-- 3. 申请创作者标签页 -->
        <el-tab-pane label="申请成为模板创作者" name="creator">
          <div class="tab-content">
            <el-form
              :model="creatorForm"
              label-width="100px"
              :rules="creatorRules"
              ref="creatorFormRef"
              class="form-container"
            >
              <el-form-item label="创作简介" prop="introduction">
                <el-input
                  v-model="creatorForm.introduction"
                  type="textarea"
                  placeholder="请描述你的创作方向、经验等"
                  :rows="4"
                />
              </el-form-item>
              <el-form-item label="作品集链接" prop="portfolioUrl">
                <el-input
                  v-model="creatorForm.portfolioUrl"
                  placeholder="请输入作品集外部链接（如网盘、个人网站等）"
                />
              </el-form-item>
              <el-form-item class="form-actions">
                <el-button
                  type="primary"
                  @click="submitCreatorApply"
                  :loading="loading"
                  :disabled="profile.creatorStatus === 'APPLIED' || profile.creatorStatus === 'APPROVED'"
                >
                  提交申请
                </el-button>
                <el-button @click="resetCreatorForm">重置</el-button>
              </el-form-item>
            </el-form>

            <div v-if="profile.creatorStatus !== 'NONE'" class="creator-tip">
              <el-alert
                :title="getCreatorApplyTip"
                :type="profile.creatorStatus === 'APPROVED' ? 'success' : 'warning'"
                show-icon
                class="alert-tip"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- 4. 会员管理标签页 -->
        <el-tab-pane label="会员管理" name="vip">
          <div class="tab-content vip-management">
            <!-- VIP状态卡片 -->
            <div class="vip-status-card" :class="getVipCardClass">
              <div class="vip-card-bg">
                <div class="vip-card-pattern"></div>
              </div>
              <div class="vip-card-content">
                <div class="vip-card-header">
                  <div class="vip-icon-wrapper">
                    <el-icon :size="32"><Medal /></el-icon>
                  </div>
                  <div class="vip-info">
                    <h3 class="vip-title">{{ getVipText }}</h3>
                    <p class="vip-subtitle">{{ getVipSubtitle }}</p>
                  </div>
                </div>
                <div class="vip-stats">
                  <div class="vip-stat-item">
                    <div class="stat-icon">
                      <el-icon><Calendar /></el-icon>
                    </div>
                    <div class="stat-content">
                      <span class="stat-label">到期时间</span>
                      <span class="stat-value">{{ profile.vipExpireDate || '未开通' }}</span>
                    </div>
                  </div>
                  <div class="vip-stat-item">
                    <div class="stat-icon">
                      <el-icon><Clock /></el-icon>
                    </div>
                    <div class="stat-content">
                      <span class="stat-label">剩余天数</span>
                      <span class="stat-value">{{ getRemainingDays }}</span>
                    </div>
                  </div>
                  <div class="vip-stat-item">
                    <div class="stat-icon">
                      <el-icon><Star /></el-icon>
                    </div>
                    <div class="stat-content">
                      <span class="stat-label">会员特权</span>
                      <span class="stat-value">{{ getVipPrivileges }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- VIP套餐选择 -->
            <div class="vip-packages-section">
              <div class="section-title">
                <h4>选择会员套餐</h4>
                <p>升级会员，解锁更多专属特权</p>
              </div>

              <div class="vip-packages-grid">
                <div
                  v-for="pkg in vipPackages"
                  :key="pkg.type"
                  class="vip-package-card"
                  :class="{
                    recommended: pkg.recommended,
                    'svip-card': pkg.type.includes('SVIP'),
                    'year-card': pkg.type.includes('YEAR')
                  }"
                  @click="selectVipPackage(pkg)"
                >
                  <div v-if="pkg.recommended" class="recommend-badge">
                    <el-icon><Star /></el-icon>
                    <span>推荐</span>
                  </div>
                  <div v-if="pkg.type.includes('YEAR')" class="save-badge">省17%</div>

                  <div class="package-header">
                    <div class="package-icon">
                      <el-icon :size="28"><Medal /></el-icon>
                    </div>
                    <div class="package-name">{{ pkg.name }}</div>
                  </div>

                  <div class="package-pricing">
                    <div class="price-main">
                      <span class="currency">¥</span>
                      <span class="price-number">{{ pkg.price }}</span>
                    </div>
                    <div class="price-period">
                      <span v-if="pkg.type.includes('YEAR')">约¥{{ Math.round(pkg.price / 12) }}/月</span>
                      <span v-else>月付</span>
                    </div>
                  </div>

                  <div class="package-features">
                    <div class="feature-item" v-for="(feature, idx) in getPackageFeatures(pkg)" :key="idx">
                      <el-icon class="feature-icon"><Check /></el-icon>
                      <span>{{ feature }}</span>
                    </div>
                  </div>

                  <el-button
                    :type="pkg.recommended ? 'primary' : 'default'"
                    class="package-btn"
                    @click.stop="handlePurchaseVip(pkg)"
                  >
                    {{ pkg.recommended ? '立即开通' : '选择套餐' }}
                  </el-button>
                </div>
              </div>
            </div>

            <!-- 特权说明 -->
            <div class="vip-privileges-section">
              <h4>会员特权对比</h4>
              <el-table :data="privilegeData" style="width: 100%" class="privilege-table">
                <el-table-column prop="feature" label="特权功能" min-width="150" />
                <el-table-column prop="normal" label="普通用户" width="100" align="center">
                  <template #default="{ row }">
                    <el-icon v-if="row.normal === 'Y'" class="check-icon"><Check /></el-icon>
                    <el-icon v-else class="close-icon"><Close /></el-icon>
                  </template>
                </el-table-column>
                <el-table-column prop="vip" label="VIP会员" width="100" align="center">
                  <template #default="{ row }">
                    <el-icon v-if="row.vip === 'Y'" class="check-icon"><Check /></el-icon>
                    <el-icon v-else class="close-icon"><Close /></el-icon>
                  </template>
                </el-table-column>
                <el-table-column prop="svip" label="SVIP会员" width="100" align="center">
                  <template #default="{ row }">
                    <el-icon v-if="row.svip === 'Y'" class="check-icon"><Check /></el-icon>
                    <el-icon v-else class="close-icon"><Close /></el-icon>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-tab-pane>

        <!-- 5. 账户设置标签页 -->
        <el-tab-pane label="账户设置" name="settings">
          <div class="tab-content">
            <div class="settings-section">
              <h4>账户安全</h4>
              <div class="setting-item">
                <div class="setting-info">
                  <div class="setting-title">注销账户</div>
                  <div class="setting-desc">注销账户将永久删除您的所有数据，此操作不可恢复</div>
                </div>
                <el-button type="danger" @click="handleDeleteAccount">注销账户</el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Upload, Star, Check, Close, Clock, Calendar, Medal, Trophy } from '@element-plus/icons-vue'
import {
  getUserProfile,
  updateUserProfile,
  changePassword,
  applyCreator,
  uploadAvatar,
  purchaseVipSubscription,
  deleteAccount
} from '@/api/user/users'

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c908jpeg.jpeg'

// 状态管理
const activeTab = ref('info')
const loading = ref(false)
const profileLoading = ref(false)
const passwordLoading = ref(false)
const creatorLoading = ref(false)
const avatarUploading = ref(false)
const isEditingInfo = ref(false)
const profile = ref({})
const avatarDialogVisible = ref(false)
const avatarPreviewUrl = ref('')
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const creatorFormRef = ref(null)
const vipDialogVisible = ref(false)
const selectedVipPackage = ref(null)

// 表单数据
const profileForm = reactive({
  realName: '',
  gender: '',
  birthday: '',
  country: '',
  city: '',
  bio: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const creatorForm = reactive({
  introduction: '',
  portfolioUrl: ''
})

// VIP套餐数据
const vipPackages = ref([
  {
    type: 'VIP',
    name: 'VIP会员',
    price: 99,
    duration: 30,
    description: '月度会员，享受所有基础功能',
    recommended: false
  },
  {
    type: 'VIP_YEAR',
    name: 'VIP年费会员',
    price: 999,
    duration: 365,
    description: '年度会员，立省约17%',
    recommended: false
  },
  {
    type: 'SVIP',
    name: 'SVIP超级会员',
    price: 199,
    duration: 30,
    description: '月度超级会员，享受所有高级功能',
    recommended: true
  },
  {
    type: 'SVIP_YEAR',
    name: 'SVIP年费超级会员',
    price: 1999,
    duration: 365,
    description: '年度超级会员，立省约17%',
    recommended: false
  }
])

// 表单校验规则
const profileRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2-20个字符之间', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthday: [{ required: true, message: '请选择生日', trigger: 'change' }]
}

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入旧密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, max: 20, message: '密码长度需在8-20位之间', trigger: 'blur' },
    { pattern: /(?=.*[a-zA-Z])(?=.*\d)/, message: '密码必须包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) callback(new Error('两次输入的密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

const creatorRules = {
  introduction: [{ required: true, message: '请输入创作简介', trigger: 'blur' }],
  portfolioUrl: [{ required: true, message: '请输入作品集链接', trigger: 'blur' }]
}

// 计算属性 - 会员状态
const getVipTagType = computed(() => {
  const types = { VIP: 'primary', SVIP: 'danger', NORMAL: 'info' }
  return types[profile.value.vipStatus] || 'info'
})

const getVipText = computed(() => {
  const texts = { VIP: 'VIP会员', SVIP: 'SVIP会员', NORMAL: '普通用户' }
  return texts[profile.value.vipStatus] || '普通用户'
})

// 计算属性 - 创作者状态
const getCreatorTagType = computed(() => {
  const types = { APPLIED: 'warning', APPROVED: 'success', REJECTED: 'danger', NONE: 'info' }
  return types[profile.value.creatorStatus] || 'info'
})

const getCreatorText = computed(() => {
  const texts = { APPLIED: '申请中', APPROVED: '已认证', REJECTED: '申请驳回', NONE: '未申请' }
  return texts[profile.value.creatorStatus] || '未申请'
})

const getCreatorApplyTip = computed(() => {
  const tips = {
    APPLIED: '你已提交创作者申请，等待审核中',
    APPROVED: '恭喜！你已成为认证模板创作者',
    REJECTED: '你的创作者申请已驳回，可重新提交申请'
  }
  return tips[profile.value.creatorStatus] || ''
})

// 计算属性 - 剩余天数
const getRemainingDays = computed(() => {
  if (!profile.value.vipExpireDate) return '-'
  const now = new Date()
  const expireDate = new Date(profile.value.vipExpireDate)
  const diffTime = expireDate - now
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  if (diffDays < 0) return '已过期'
  if (diffDays === 0) return '今天到期'
  return `${diffDays}天`
})

// 计算属性 - VIP卡片样式
const getVipCardClass = computed(() => {
  const status = profile.value.vipStatus
  if (status === 'SVIP') return 'is-svip'
  if (status === 'VIP') return 'is-vip'
  return 'is-normal'
})

// 计算属性 - VIP副标题
const getVipSubtitle = computed(() => {
  const status = profile.value.vipStatus
  if (status === 'SVIP') return '尊享全部高级特权'
  if (status === 'VIP') return '解锁基础会员特权'
  return '升级会员解锁更多功能'
})

// 计算属性 - VIP特权描述
const getVipPrivileges = computed(() => {
  const status = profile.value.vipStatus
  if (status === 'SVIP') return '全部特权'
  if (status === 'VIP') return '基础特权'
  return '无'
})

// 获取套餐特性
const getPackageFeatures = (pkg) => {
  if (pkg.type.includes('SVIP')) {
    return [
      '无限视频生成次数',
      '高级模版免费使用',
      '优先渲染队列',
      '专属客服支持',
      '高级特效解锁'
    ]
  }
  return [
    '每月100次视频生成',
    '部分模版免费',
    '标准渲染速度',
    '工单支持'
  ]
}

// 特权对比数据
const privilegeData = ref([
  { feature: '视频生成次数', normal: '10次/月', vip: '100次/月', svip: 'Y' },
  { feature: '高级模版使用', normal: 'N', vip: '部分', svip: 'Y' },
  { feature: '渲染优先级', normal: '普通', vip: '标准', svip: '优先' },
  { feature: '特效解锁', normal: '基础', vip: '标准', svip: 'Y' },
  { feature: '专属客服', normal: 'N', vip: 'N', svip: 'Y' },
  { feature: '云存储空间', normal: '1GB', vip: '10GB', svip: '50GB' }
])

// 头像URL处理
const getAvatarUrl = computed(() => {
  return profile.value?.avatarUrl || defaultAvatar
})

// 监听编辑状态变化
watch(isEditingInfo, (newVal) => {
  if (newVal) {
    nextTick(() => {
      profileFormRef.value?.clearValidate()
    })
  }
})

// 初始化表单数据
const initProfileForm = () => {
  const profileData = profile.value.profile || {}
  profileForm.realName = profileData.realName || ''
  profileForm.gender = profileData.gender || ''
  profileForm.birthday = profileData.birthday || ''
  profileForm.country = profileData.country || ''
  profileForm.city = profileData.city || ''
  profileForm.bio = profileData.bio || ''
}

// 获取用户信息
const fetchProfile = async () => {
  try {
    profileLoading.value = true
    const res = await getUserProfile()
    profile.value = res.data || {}
    // 填充表单
    initProfileForm()
  } catch (error) {
    ElMessage.error('获取个人信息失败：' + (error.message || '网络错误'))
  } finally {
    profileLoading.value = false
  }
}

// 检测是否有实际修改
const hasProfileChanges = () => {
  const originalData = profile.value.profile || {}
  return Object.keys(profileForm).some(key => {
    const originalValue = originalData[key] || ''
    const newValue = profileForm[key] || ''
    
    // 处理null/undefined
    if (originalValue === null || originalValue === undefined) {
      return newValue !== ''
    }
    if (newValue === null || newValue === undefined) {
      return originalValue !== ''
    }
    
    // 去除首尾空格后比较
    return String(originalValue).trim() !== String(newValue).trim()
  })
}

// 提交个人资料修改
const submitProfile = async () => {
  console.group('🚀 开始个人资料修改流程')
  console.log('1️⃣ 当前表单数据:', JSON.parse(JSON.stringify(profileForm)))
  
  try {
    console.log('2️⃣ 开始表单验证...')
    await profileFormRef.value.validate()
    console.log('✅ 表单验证通过')
    
    // 检测是否有实际修改
    const originalData = profile.value.profile || {}
    console.log('3️⃣ 原始数据:', originalData)
    
    const hasChanges = Object.keys(profileForm).some(key => {
      const originalValue = originalData[key] || ''
      const newValue = profileForm[key] || ''
      const changed = String(originalValue).trim() !== String(newValue).trim()
      if (changed) console.log(`🔄 ${key} 有变化: "${originalValue}" → "${newValue}"`)
      return changed
    })
    
    console.log(`4️⃣ 检测到修改: ${hasChanges ? '是' : '否'}`)
    
    if (!hasChanges) {
      console.warn('⚠️ 没有检测到任何修改，取消提交')
      ElMessage.warning('没有检测到任何修改')
      isEditingInfo.value = false
      console.groupEnd()
      return
    }
    
    console.log('5️⃣ 开始调用API...')
    console.log('📤 发送的请求数据:', JSON.stringify(profileForm, null, 2))
    
    // 模拟API调用结果（临时注释掉真实调用）
    // loading.value = true
    // const result = await updateUserProfile(profileForm)
    
    // 暂时用这个模拟API响应
    const mockApiResponse = {
      code: 200,
      message: "用户资料更新成功",
      data: true
    }
    
    console.log('📥 模拟API响应:', mockApiResponse)
    
    if (mockApiResponse.code === 200) {
      console.log('✅ API调用成功')
      // 模拟更新本地数据
      if (!profile.value.profile) profile.value.profile = {}
      Object.keys(profileForm).forEach(key => {
        profile.value.profile[key] = profileForm[key]
      })
      console.log('🔄 本地数据已更新:', profile.value.profile)
      
      ElMessage.success('个人资料修改成功！')
      isEditingInfo.value = false
      console.log('🏁 流程完成')
    } else {
      console.error('❌ API返回失败')
      ElMessage.error('修改失败：' + (mockApiResponse.message || '未知错误'))
    }
    
  } catch (error) {
    console.error('❌ 流程出错:', error)
    if (error.name !== 'ValidationError') {
      ElMessage.error('修改失败：' + (error.message || '网络错误'))
    }
  } finally {
    // loading.value = false
    console.groupEnd()
  }
}
// 取消编辑个人资料
const cancelEditInfo = () => {
  isEditingInfo.value = false
  // 重置表单数据
  initProfileForm()
  // 清空校验状态
  nextTick(() => {
    profileFormRef.value?.clearValidate()
  })
}

// 提交密码修改
const submitPassword = async () => {
  try {
    await passwordFormRef.value.validate()
    
    // 二次确认
    await ElMessageBox.confirm(
      '确认修改密码？修改后请使用新密码登录',
      '密码修改确认',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    passwordLoading.value = true
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    ElMessage.success('密码修改成功！请重新登录')
    resetPasswordForm()
    
    // 这里应该触发登出逻辑，但需要根据你的登录系统实现
    // logout()
    
  } catch (error) {
    if (error.name !== 'ValidationError' && error !== 'cancel') {
      ElMessage.error('密码修改失败：' + (error.message || '旧密码错误或系统异常'))
    }
  } finally {
    passwordLoading.value = false
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordFormRef.value?.clearValidate()
}

// 提交创作者申请
const submitCreatorApply = async () => {
  try {
    await creatorFormRef.value.validate()
    
    await ElMessageBox.confirm(
      '确认提交创作者申请？提交后将进入审核流程',
      '申请确认',
      { type: 'info' }
    )
    
    creatorLoading.value = true
    await applyCreator(creatorForm)
    
    ElMessage.success('创作者申请提交成功！等待管理员审核')
    resetCreatorForm()
    await fetchProfile() // 刷新状态
    
  } catch (error) {
    if (error.name !== 'ValidationError' && error !== 'cancel') {
      ElMessage.error('申请提交失败：' + (error.message || '系统异常'))
    }
  } finally {
    creatorLoading.value = false
  }
}

// 重置创作者申请表单
const resetCreatorForm = () => {
  creatorForm.introduction = ''
  creatorForm.portfolioUrl = ''
  creatorFormRef.value?.clearValidate()
}

// 处理头像上传
const handleAvatarUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  // 预览选中的图片
  const reader = new FileReader()
  reader.onload = (event) => {
    avatarPreviewUrl.value = event.target.result
  }
  reader.readAsDataURL(file)

  // 校验文件类型和大小
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('仅支持上传JPG/PNG格式的图片！')
    avatarPreviewUrl.value = ''
    e.target.value = ''
    return
  }

  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过2MB！')
    avatarPreviewUrl.value = ''
    e.target.value = ''
    return
  }

  try {
    avatarUploading.value = true
    const formData = new FormData()
    formData.append('file', file)

    const res = await uploadAvatar(formData)
    const result = res.data || res

    if (result.avatarUrl) {
      ElMessage.success('头像上传成功！')
      avatarDialogVisible.value = false
      avatarPreviewUrl.value = ''

      // 更新本地头像URL
      profile.value.avatarUrl = result.avatarUrl

      // 可选：重新获取完整用户信息
      // await fetchProfile()

    } else {
      ElMessage.error('头像上传失败：返回数据异常')
    }

    e.target.value = ''
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败：' + (error.message || '网络错误或接口异常'))
    avatarPreviewUrl.value = ''
    e.target.value = ''
  } finally {
    avatarUploading.value = false
  }
}

// 选择VIP套餐
const selectVipPackage = (pkg) => {
  selectedVipPackage.value = pkg
}

// 购买VIP会员
const handlePurchaseVip = async (pkg) => {
  try {
    await ElMessageBox.confirm(
      `确认开通 ${pkg.name}？\n价格：${pkg.price}元\n时长：${pkg.duration}天`,
      '开通会员',
      {
        confirmButtonText: '确认开通',
        cancelButtonText: '取消',
        type: 'info'
      }
    )

    // 调用API（后端接口尚未实现，暂时用模拟数据）
    try {
      const res = await purchaseVipSubscription({
        vipType: pkg.type.includes('SVIP') ? 'SVIP' : 'VIP',
        duration: pkg.duration
      })

      if (res.code === 200 || res.success) {
        ElMessage.success(`${pkg.name}开通成功！`)
        await fetchProfile()
      } else {
        throw new Error(res.message || '开通失败')
      }
    } catch (apiError) {
      // 如果后端接口尚未实现，显示提示
      console.warn('会员开通接口尚未实现，使用模拟响应')
      ElMessage.info(`${pkg.name}开通成功！\n（模拟功能，后端接口待实现）`)
      // 模拟更新状态
      profile.value.vipStatus = pkg.type.includes('SVIP') ? 'SVIP' : 'VIP'
      const now = new Date()
      now.setDate(now.getDate() + pkg.duration)
      profile.value.vipExpireDate = now.toISOString().split('T')[0]
    }

  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('开通会员失败：' + (error.message || '系统异常'))
    }
  }
}

// 注销账户
const handleDeleteAccount = async () => {
  try {
    await ElMessageBox.prompt(
      '请输入 "确认注销" 以确认注销账户',
      '注销账户确认',
      {
        confirmButtonText: '确认注销',
        cancelButtonText: '取消',
        type: 'warning',
        inputPattern: /确认注销/,
        inputErrorMessage: '请输入正确的确认文本'
      }
    )

    await ElMessageBox.confirm(
      '注销账户将永久删除您的所有数据，此操作不可恢复！\n\n确定要继续吗？',
      '危险操作警告',
      {
        confirmButtonText: '确定注销',
        cancelButtonText: '再想想',
        type: 'error',
        distinguishCancelAndClose: true
      }
    )

    // 调用API（后端接口尚未实现，暂时用模拟数据）
    try {
      const res = await deleteAccount()

      if (res.code === 200 || res.success) {
        ElMessage.success('账户注销成功')
        // TODO: 跳转到登录页或首页
        // router.push('/login')
        // 或使用 window.location.href
        window.location.href = '/login'
      } else {
        throw new Error(res.message || '注销失败')
      }
    } catch (apiError) {
      // 如果后端接口尚未实现，显示提示
      console.warn('账户注销接口尚未实现，使用模拟响应')
      ElMessage.info('账户注销成功！\n（模拟功能，后端接口待实现）')
      // 模拟跳转
      window.location.href = '/login'
    }

  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error('注销账户失败：' + (error.message || '系统异常'))
    }
  }
}

// 页面挂载时获取用户信息
onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.profile-container {
  padding: 0;
  margin: 0;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
}

.profile-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 80%, rgba(167, 139, 250, 0.1) 0%, transparent 50%),
              radial-gradient(circle at 80% 20%, rgba(99, 102, 241, 0.1) 0%, transparent 50%);
  backdrop-filter: blur(20px);
  z-index: -1;
}


/* 主内容卡片样式 - 全屏现代化设计 */
.main-content-card {
  border-radius: 0;
  box-shadow: none;
  border: none;
  overflow: hidden;
  background: transparent;
  backdrop-filter: none;
  height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 个人信息头部（头像+基础信息） - 现代化设计 */
.profile-header {
  display: flex;
  align-items: center;
  padding: 60px 40px 40px;
  gap: 32px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 250, 252, 0.9) 100%);
  border-radius: 0;
  margin: 0;
  border: none;
  position: relative;
  backdrop-filter: blur(10px);
}

.profile-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 40px;
  right: 40px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0, 0, 0, 0.08), transparent);
}

.avatar-wrapper {
  cursor: pointer;
  text-align: center;
  transition: all 0.3s ease;
}

.avatar-wrapper:hover {
  transform: translateY(-2px);
}

.avatar-wrapper:hover .avatar-tip {
  color: #409eff;
}

.user-avatar {
  border: 4px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12), inset 0 0 0 1px rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.avatar-wrapper:hover .user-avatar {
  border-color: #409eff;
  box-shadow: 0 12px 32px rgba(64, 158, 255, 0.2), inset 0 0 0 1px rgba(255, 255, 255, 0.3);
}

.avatar-tip {
  margin-top: 12px;
  font-size: 13px;
  color: #606266;
  display: block;
  transition: all 0.3s ease;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.user-base-info {
  flex: 1;
}

.username {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 16px 0;
  background: linear-gradient(135deg, #1f2937 0%, #374151 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  letter-spacing: -0.5px;
}

.user-tag-group {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.user-tag-group .el-tag {
  font-weight: 600;
  font-size: 12px;
  padding: 6px 14px;
  border-radius: 16px;
  border: none;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.user-tag-group .el-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 标签页样式 - 现代化设计 */
.profile-tabs {
  margin: 0;
}

.profile-tabs :deep(.el-tabs__header) {
  background: linear-gradient(135deg, rgba(248, 250, 252, 0.95) 0%, rgba(241, 245, 249, 0.95) 100%);
  padding: 0 32px;
  border-bottom: none;
  backdrop-filter: blur(10px);
}

.profile-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.profile-tabs :deep(.el-tabs__item) {
  height: 56px;
  line-height: 56px;
  border: none;
  font-size: 15px;
  font-weight: 500;
  color: #64748b;
  padding: 0 24px;
  transition: all 0.3s ease;
  position: relative;
}

.profile-tabs :deep(.el-tabs__item:hover) {
  color: #3b82f6;
}

.profile-tabs :deep(.el-tabs__item.is-active) {
  background: transparent;
  color: #3b82f6;
  font-weight: 600;
}

.profile-tabs :deep(.el-tabs__item.is-active::after) {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20px;
  right: 20px;
  height: 3px;
  background: linear-gradient(90deg, #3b82f6, #6366f1);
  border-radius: 3px 3px 0 0;
}

/* 标签页内容样式 - 现代化设计 */
.tab-content {
  padding: 32px;
}

.info-display {
  margin-bottom: 32px;
}

.info-desc {
  --el-descriptions-item-label-color: #4b5563;
  --el-descriptions-item-content-color: #1f2937;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.info-desc:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.desc-label {
  font-weight: 600;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  padding: 12px 16px;
  border-right: 1px solid rgba(0, 0, 0, 0.05);
}

.action-btn-group {
  margin-top: 24px;
  text-align: right;
  padding-top: 16px;
  border-top: 1px solid rgba(0, 0, 0, 0.08);
}

.form-container {
  max-width: 640px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.form-actions {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid rgba(0, 0, 0, 0.06);
}

/* 创作者申请提示 */
.creator-tip {
  margin-top: 20px;
}

.alert-tip {
  --el-alert-padding: 12px 16px;
}

/* VIP会员管理样式 - 现代化升级 */
.vip-management {
  max-width: 1200px;
  margin: 0 auto;
}

.vip-status-card {
  position: relative;
  border-radius: 24px;
  padding: 32px;
  margin-bottom: 40px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.vip-status-card.is-normal {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
}

.vip-status-card.is-vip {
  background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);
}

.vip-status-card.is-svip {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.vip-card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  opacity: 0.1;
}

.vip-card-pattern {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(255,255,255,0.3) 0%, transparent 70%);
  border-radius: 50%;
}

.vip-card-content {
  position: relative;
  z-index: 1;
}

.vip-card-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 28px;
}

.vip-icon-wrapper {
  width: 72px;
  height: 72px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  color: #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.vip-info {
  flex: 1;
}

.vip-title {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 6px 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.vip-subtitle {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.85);
  margin: 0;
}

.vip-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 16px;
  padding: 24px;
  backdrop-filter: blur(10px);
}

.vip-stat-item {
  display: flex;
  align-items: center;
  gap: 14px;
}

.vip-stat-item .stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
}

.vip-stat-item .stat-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.vip-stat-item .stat-label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.75);
}

.vip-stat-item .stat-value {
  font-size: 17px;
  font-weight: 600;
  color: #fff;
}

/* VIP套餐区域 */
.vip-packages-section {
  margin-bottom: 40px;
}

.section-title {
  text-align: center;
  margin-bottom: 32px;
}

.section-title h4 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.section-title p {
  font-size: 15px;
  color: #6b7280;
  margin: 0;
}

.vip-packages-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.vip-package-card {
  background: #fff;
  border-radius: 20px;
  padding: 28px;
  border: 2px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.vip-package-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 12px 40px rgba(59, 130, 246, 0.15);
  transform: translateY(-4px);
}

.vip-package-card.recommended {
  border-color: #f59e0b;
  background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%);
}

.vip-package-card.recommended:hover {
  box-shadow: 0 12px 40px rgba(245, 158, 11, 0.2);
}

.vip-package-card.svip-card {
  border-color: #ec4899;
  background: linear-gradient(135deg, #fdf2f8 0%, #fce7f3 100%);
}

.vip-package-card.svip-card.recommended {
  border-color: #ec4899;
}

.recommend-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: #fff;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
}

.save-badge {
  position: absolute;
  top: 16px;
  left: 16px;
  background: #10b981;
  color: #fff;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
}

.package-header {
  text-align: center;
  margin-bottom: 20px;
  padding-top: 20px;
}

.package-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.vip-package-card.svip-card .package-icon {
  background: linear-gradient(135deg, #ec4899 0%, #f43f5e 100%);
}

.package-name {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
}

.package-pricing {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px dashed #e5e7eb;
}

.price-main {
  display: flex;
  align-items: baseline;
  justify-content: center;
  gap: 2px;
}

.currency {
  font-size: 20px;
  font-weight: 600;
  color: #f43f5e;
}

.price-number {
  font-size: 48px;
  font-weight: 700;
  color: #f43f5e;
  line-height: 1;
}

.price-period {
  margin-top: 8px;
  font-size: 14px;
  color: #6b7280;
}

.package-features {
  margin-bottom: 24px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  font-size: 14px;
  color: #374151;
}

.feature-icon {
  color: #10b981;
  font-size: 16px;
}

.package-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
}

/* 特权对比表格 */
.vip-privileges-section {
  background: #fff;
  border-radius: 20px;
  padding: 28px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.vip-privileges-section h4 {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 24px 0;
}

.privilege-table {
  border-radius: 12px;
  overflow: hidden;
}

.privilege-table :deep(.el-table__header th) {
  background: #f9fafb;
  font-weight: 600;
  color: #374151;
}

.privilege-table :deep(.el-table__row td) {
  padding: 16px 0;
}

.check-icon {
  color: #10b981;
  font-size: 20px;
}

.close-icon {
  color: #d1d5db;
  font-size: 20px;
}

/* 账户设置样式 */
.settings-section {
  max-width: 640px;
}

.settings-section h4 {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e7ed;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  margin-bottom: 12px;
  border: 1px solid #e4e7ed;
}

.setting-info {
  flex: 1;
}

.setting-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.setting-desc {
  font-size: 14px;
  color: #909399;
  line-height: 1.5;
}

/* 头像弹窗样式 - 重点修复 */
.avatar-dialog :deep(.el-dialog__body) {
  padding: 20px;
  text-align: center;
}

.avatar-preview {
  margin-bottom: 20px;
}

.avatar-big {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #f5f7fa;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-upload-actions {
  text-align: center;
}

/* 修复文件选择按钮的核心样式 */
.upload-btn-wrapper {
  display: inline-block;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  /* 确保点击穿透到input */
  pointer-events: auto;
}

/* 隐藏file input但保持可点击 */
.avatar-upload-input {
  position: absolute;
  top: 0;
  right: 0;
  opacity: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
  z-index: 10;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .profile-container::before {
    background: radial-gradient(circle at 10% 90%, rgba(167, 139, 250, 0.1) 0%, transparent 60%),
                radial-gradient(circle at 90% 10%, rgba(99, 102, 241, 0.1) 0%, transparent 60%);
  }
  
  .profile-header {
    gap: 24px;
    padding: 40px 30px;
  }
  
  .profile-header::after {
    left: 30px;
    right: 30px;
  }
  
  .username {
    font-size: 28px;
  }
  
  .user-tag-group {
    justify-content: center;
    gap: 8px;
  }
  
  .user-tag-group .el-tag {
    font-size: 12px;
    padding: 5px 12px;
  }
  
  .profile-tabs :deep(.el-tabs__header) {
    padding: 0 20px;
  }
  
  .profile-tabs :deep(.el-tabs__item) {
    height: 48px;
    padding: 0 16px;
    font-size: 14px;
  }
  
  .profile-tabs :deep(.el-tabs__item.is-active::after) {
    left: 16px;
    right: 16px;
  }
  
  .tab-content {
    padding: 24px;
  }
  
  .form-container {
    max-width: 100%;
    padding: 20px;
  }
  
  .avatar-big {
    width: 150px;
    height: 150px;
  }

  /* VIP管理响应式 */
  .vip-status-card {
    padding: 24px;
  }

  .vip-card-header {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .vip-title {
    font-size: 24px;
  }

  .vip-stats {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 20px;
  }

  .vip-packages-grid {
    grid-template-columns: 1fr;
  }

  .vip-package-card {
    padding: 24px;
  }

  .price-number {
    font-size: 40px;
  }
}

@media (max-width: 480px) {
  .profile-header {
    padding: 30px 20px;
  }
  
  .profile-header::after {
    left: 20px;
    right: 20px;
  }
  
  .profile-tabs :deep(.el-tabs__header) {
    padding: 0 16px;
  }
  
  .profile-tabs :deep(.el-tabs__item) {
    padding: 0 12px;
    font-size: 13px;
  }
  
  .tab-content {
    padding: 20px;
  }
  
  .form-container {
    padding: 16px;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 12px;
  }

  /* VIP管理小屏幕响应式 */
  .vip-icon-wrapper {
    width: 56px;
    height: 56px;
  }

  .vip-title {
    font-size: 20px;
  }

  .vip-stat-item .stat-icon {
    width: 40px;
    height: 40px;
  }

  .vip-privileges-section {
    padding: 20px;
  }

  .feature-item {
    font-size: 13px;
  }
}
</style>