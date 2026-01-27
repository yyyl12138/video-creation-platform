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
          :src="avatarPreviewUrl || (profile.avatarUrl || defaultAvatar)" 
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

    <!-- 页面标题卡片 -->
    <div class="page-header-card">
      <div class="header-content">
        <div class="header-icon">
          <el-icon size="32" color="#fff"><User /></el-icon>
        </div>
        <div class="header-text">
          <h2>个人中心</h2>
          <p>管理您的个人信息和账户设置</p>
        </div>
      </div>
    </div>

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
            <img :src="profile.avatarUrl || defaultAvatar" alt="用户头像" />
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
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Upload } from '@element-plus/icons-vue'
import {
  getUserProfile,
  updateUserProfile,
  changePassword,
  applyCreator,
  uploadAvatar
} from '@/api/user/users'

// 默认头像
const defaultAvatar = 'https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c908jpeg.jpeg'

// 状态管理
const activeTab = ref('info')
const loading = ref(false)
const avatarUploading = ref(false) // 单独的头像上传loading
const isEditingInfo = ref(false)
const profile = ref({})
const avatarDialogVisible = ref(false)
const avatarPreviewUrl = ref('') // 头像上传预览URL
const profileFormRef = ref(null)
const passwordFormRef = ref(null)
const creatorFormRef = ref(null)

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

// 表单校验规则
const profileRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  birthday: [{ required: true, message: '请选择生日', trigger: 'change' }],
  country: [{ required: true, message: '请输入国家/地区', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }]
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

// 获取用户信息
const fetchProfile = async () => {
  try {
    loading.value = true
    const res = await getUserProfile()
    profile.value = res.data || {}
    // 填充表单
    const profileData = profile.value.profile || {}
    Object.keys(profileForm).forEach(key => {
      profileForm[key] = profileData[key] || ''
    })
  } catch (error) {
    ElMessage.error('获取个人信息失败：' + (error.message || '网络错误'))
  } finally {
    loading.value = false
  }
}

// 提交个人资料修改
const submitProfile = async () => {
  try {
    await profileFormRef.value.validate()
    loading.value = true
    await updateUserProfile(profileForm)
    ElMessage.success('个人资料修改成功！')
    isEditingInfo.value = false
    await fetchProfile()
  } catch (error) {
    if (error.name !== 'ValidationError') {
      ElMessage.error('修改失败：' + (error.message || '网络错误'))
    }
  } finally {
    loading.value = false
  }
}

// 取消编辑个人资料
const cancelEditInfo = () => {
  isEditingInfo.value = false
  // 重置表单数据
  const profileData = profile.value.profile || {}
  Object.keys(profileForm).forEach(key => {
    profileForm[key] = profileData[key] || ''
  })
  // 清空校验状态
  profileFormRef.value?.clearValidate()
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
    
    loading.value = true
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功！请重新登录')
    resetPasswordForm()
    // 这里可根据实际需求添加登出逻辑
    // logout()
  } catch (error) {
    if (error.name !== 'ValidationError' && error !== 'cancel') {
      ElMessage.error('密码修改失败：' + (error.message || '旧密码错误或系统异常'))
    }
  } finally {
    loading.value = false
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
    
    loading.value = true
    await applyCreator(creatorForm)
    ElMessage.success('创作者申请提交成功！等待管理员审核')
    resetCreatorForm()
    await fetchProfile() // 刷新状态
  } catch (error) {
    if (error.name !== 'ValidationError' && error !== 'cancel') {
      ElMessage.error('申请提交失败：' + (error.message || '系统异常'))
    }
  } finally {
    loading.value = false
  }
}

// 重置创作者申请表单
const resetCreatorForm = () => {
  creatorForm.introduction = ''
  creatorForm.portfolioUrl = ''
  creatorFormRef.value?.clearValidate()
}

// 处理头像上传 - 完全重构，确保文件选择和上传正常
const handleAvatarUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  // 1. 预览选中的图片（提升体验）
  const reader = new FileReader()
  reader.onload = (event) => {
    avatarPreviewUrl.value = event.target.result
  }
  reader.readAsDataURL(file)

  // 2. 校验文件类型和大小
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('仅支持上传JPG/PNG格式的图片！')
    avatarPreviewUrl.value = '' // 清空预览
    e.target.value = '' // 清空文件选择
    return
  }

  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过2MB！')
    avatarPreviewUrl.value = '' // 清空预览
    e.target.value = '' // 清空文件选择
    return
  }

  try {
    avatarUploading.value = true
    // 3. 构建FormData（字段名严格匹配接口要求的file）
    const formData = new FormData()
    formData.append('file', file)

    // 4. 调用上传接口（适配通用的res.data返回格式）
    const res = await uploadAvatar(formData)
    const result = res.data || res // 兼容两种返回格式

    if (result.avatarUrl) {
      ElMessage.success('头像上传成功！')
      avatarDialogVisible.value = false
      avatarPreviewUrl.value = '' // 清空预览
      await fetchProfile() // 刷新用户信息，更新头像显示
    } else {
      ElMessage.error('头像上传失败：返回数据异常')
    }

    // 5. 清空文件选择，避免重复选择同一文件不触发change事件
    e.target.value = ''
  } catch (error) {
    ElMessage.error('头像上传失败：' + (error.message || '网络错误或接口异常'))
    avatarPreviewUrl.value = '' // 清空预览
    e.target.value = '' // 清空文件选择
  } finally {
    avatarUploading.value = false
  }
}

// 页面挂载时获取用户信息
onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.profile-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  min-height: calc(100vh - 40px);
}

/* 页面头部卡片样式 */
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

/* 主内容卡片样式 */
.main-content-card {
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
  overflow: hidden;
}

/* 个人信息头部（头像+基础信息） */
.profile-header {
  display: flex;
  align-items: center;
  padding: 30px;
  border-bottom: 1px solid #ebeef5;
  gap: 40px;
}

.avatar-wrapper {
  cursor: pointer;
  text-align: center;
}

.user-avatar {
  border: 4px solid #f5f7fa;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-tip {
  margin-top: 8px;
  font-size: 12px;
  color: #909399;
  display: block;
}

.user-base-info {
  flex: 1;
}

.username {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px 0;
}

.user-tag-group {
  display: flex;
  gap: 8px;
}

/* 标签页样式 */
.profile-tabs {
  margin: 0;
}

.profile-tabs :deep(.el-tabs__header) {
  background: #fafafa;
  padding: 8px 8px 0;
  border-bottom: none;
}

.profile-tabs :deep(.el-tabs__nav-wrap::after) {
  display: none;
}

.profile-tabs :deep(.el-tabs__item) {
  height: 48px;
  line-height: 48px;
  border: none;
  font-size: 14px;
  font-weight: 500;
}

.profile-tabs :deep(.el-tabs__item.is-active) {
  background: #fff;
  border-radius: 8px 8px 0 0;
}

/* 标签页内容样式 */
.tab-content {
  padding: 24px;
}

.info-display {
  margin-bottom: 20px;
}

.info-desc {
  --el-descriptions-item-label-color: #606266;
  --el-descriptions-item-content-color: #303133;
}

.desc-label {
  font-weight: 500;
}

.action-btn-group {
  margin-top: 20px;
  text-align: right;
}

.form-container {
  max-width: 600px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 创作者申请提示 */
.creator-tip {
  margin-top: 20px;
}

.alert-tip {
  --el-alert-padding: 12px 16px;
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
  .profile-container {
    padding: 16px;
  }
  
  .profile-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
    padding: 20px;
  }
  
  .user-tag-group {
    justify-content: center;
  }
  
  .form-container {
    max-width: 100%;
  }
  
  .avatar-big {
    width: 140px;
    height: 140px;
  }
}
</style>