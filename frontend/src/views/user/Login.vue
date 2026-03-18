<template>
  <div class="login-container">
    <!-- 背景渐变效果 -->
    <div class="login-background">
      <div class="bg-gradient"></div>
      <div class="bg-glow bg-glow-1"></div>
      <div class="bg-glow bg-glow-2"></div>
    </div>
    
    <!-- 视频背景 -->
    <div class="video-background">
      <video 
        ref="bgVideo"
        class="bg-video"
        autoplay
        muted
        loop
        playsinline
        src="/videos/8ad1a29fab8541b387f425c05b0d9801.mp4"
      ></video>
      <div class="video-overlay"></div>
    </div>
    
    <el-card class="login-card">
      <template #header>
        <h2 class="login-title">系统登录</h2>
      </template>
      
      <!-- 登录方式切换 -->
      <el-tabs v-model="activeTab" class="login-tabs">
        <el-tab-pane label="密码登录" name="password">
          <el-form :model="passwordForm" label-width="0">
            <el-form-item>
              <el-input 
                v-model="passwordForm.username" 
                placeholder="用户名/邮箱/手机号" 
                prefix-icon="User"
                class="custom-input"
              />
            </el-form-item>
            <el-form-item>
              <el-input 
                v-model="passwordForm.password" 
                type="password" 
                placeholder="密码" 
                prefix-icon="Lock"
                show-password
                class="custom-input"
              />
            </el-form-item>
            <el-form-item>
              <div class="login-options">
                <el-checkbox v-model="rememberMe">
                  记住密码
                </el-checkbox>
                <el-link type="primary" :underline="false" @click="goToForgotPassword">
                  忘记密码?
                </el-link>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="w-100 login-btn" @click="handlePasswordLogin">
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="验证码登录" name="sms">
          <el-form :model="smsForm" label-width="0">
            <el-form-item>
              <el-input 
                v-model="smsForm.phone" 
                placeholder="请输入手机号" 
                prefix-icon="Phone"
                class="custom-input"
              />
            </el-form-item>
            <el-form-item>
              <VerificationCode 
                v-model="smsForm.code"
                placeholder="请输入验证码"
                :disabled="!isPhoneValid"
                @send="handleSendCode"
                class="custom-input"
              />
            </el-form-item>
            <el-form-item>
              <el-button 
                type="primary" 
                class="w-100 login-btn" 
                @click="handleSmsLogin"
                :disabled="!isSmsFormValid"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      
      <div class="login-footer">
        <el-link class="footer-link" :underline="false" @click="goToForgotPassword">
          忘记密码?
        </el-link>
        <el-link class="footer-link" :underline="false" @click="goToRegister">
          没有账号?立即注册
        </el-link>
      </div>

      <div class="agreement-footer">
        <span>登录即代表同意</span>
        <el-link class="footer-link" :underline="false" @click="goToUserAgreement">
          用户协议
        </el-link>
        <span>和</span>
        <el-link class="footer-link" :underline="false" @click="goToPrivacyPolicy">
          隐私政策
        </el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone } from '@element-plus/icons-vue'
import VerificationCode from '@/components/VerificationCode.vue'
import { loginByPassword, loginBySms, sendAuthCode } from '@/api/user/auth'

const router = useRouter()
const activeTab = ref('password')
const rememberMe = ref(false)

// 密码登录表单
const passwordForm = reactive({
  username: '',
  password: ''
})

// 验证码登录表单
const smsForm = reactive({
  phone: '',
  code: ''
})

// 验证手机号格式
const isValidPhone = (phone) => {
  return /^1[3-9]\d{9}$/.test(phone)
}

// 手机号验证
const isPhoneValid = computed(() => {
  return smsForm.phone && isValidPhone(smsForm.phone)
})

// 验证码登录表单验证
const isSmsFormValid = computed(() => {
  return isPhoneValid.value && smsForm.code.length === 6
})

// 发送验证码
const handleSendCode = async () => {
  if (!isPhoneValid.value) {
    ElMessage.warning('请输入有效的手机号')
    return
  }
  
  try {
    // ✅ 调用 API 函数
    await sendAuthCode(smsForm.phone, 'login')
    ElMessage.success('验证码发送成功')
  } catch (error) {
    ElMessage.error(error.message || '验证码发送失败')
  }
}

// 验证码登录
const handleSmsLogin = async () => {
  if (!isSmsFormValid.value) {
    ElMessage.warning('请填写完整的登录信息')
    return
  }
  
  try {
    // ✅ 调用 API 函数
    const res = await loginBySms(smsForm.phone, smsForm.code)
    ElMessage.success('登录成功')
    // token已在API函数中设置，无需重复设置
    router.push('/dashboard')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  }
}

// 页面初始化 - 检查保存的登录信息
onMounted(() => {
  const savedLogin = localStorage.getItem('rememberedLogin')
  if (savedLogin) {
    try {
      const { username, password } = JSON.parse(savedLogin)
      passwordForm.username = username
      passwordForm.password = password
      rememberMe.value = true
    } catch (error) {
      console.error('解析保存的登录信息失败:', error)
      localStorage.removeItem('rememberedLogin')
    }
  }
})

// 密码登录处理（包含记住密码功能）
const handlePasswordLogin = async () => {
  if (!passwordForm.username || !passwordForm.password) {
    ElMessage.warning('请填写完整的登录信息')
    return
  }
  
  try {
    const res = await loginByPassword(passwordForm.username, passwordForm.password)
    ElMessage.success('登录成功')

    // 处理记住密码功能
    if (rememberMe.value) {
      localStorage.setItem('rememberedLogin', JSON.stringify({
        username: passwordForm.username,
        password: passwordForm.password
      }))
    } else {
      localStorage.removeItem('rememberedLogin')
    }

    // token已在API函数中设置，无需重复设置
    router.push('/dashboard')
  } catch (error) {
    ElMessage.error(error.message || '登录失败')
  }
}

// 页面跳转
const goToRegister = () => {
  router.push('/register')
}

const goToForgotPassword = () => {
  router.push('/forgot-password')
}

const goToUserAgreement = () => {
  router.push('/user-agreement')
}

const goToPrivacyPolicy = () => {
  router.push('/privacy-policy')
}
</script>

<style scoped>
/* 全局容器 - 深色主题 */
.login-container {
  min-height: 100vh;
  background: #0a0a0f;
  color: #fff;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

/* 背景效果 */
.login-background {
  position: absolute;
  inset: 0;
  z-index: 1;
}

.bg-gradient {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse 80% 50% at 50% -20%, rgba(120, 119, 198, 0.3), transparent);
}

.bg-glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.3;
}

.bg-glow-1 {
  width: 600px;
  height: 600px;
  background: rgba(102, 126, 234, 0.4);
  top: -200px;
  left: -100px;
  animation: float 20s ease-in-out infinite;
}

.bg-glow-2 {
  width: 500px;
  height: 500px;
  background: rgba(118, 75, 162, 0.4);
  bottom: -150px;
  right: -100px;
  animation: float 25s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(30px, -30px); }
}

/* 视频背景容器 */
.video-background {
  position: absolute;
  inset: 0;
  z-index: 0;
  overflow: hidden;
}

.bg-video {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0.6;
  filter: brightness(0.7) contrast(1.1) blur(1px);
  transition: all 0.5s;
}

.video-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    45deg, 
    rgba(10, 10, 15, 0.4) 0%, 
    rgba(102, 126, 234, 0.1) 50%, 
    rgba(118, 75, 162, 0.1) 100%
  );
}

/* 登录卡片 */
.login-card {
  width: 400px;
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  backdrop-filter: blur(20px);
  z-index: 2;
}

.login-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  padding: 20px;
  background: transparent;
}

.login-title {
  text-align: center;
  color: #fff;
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 标签页样式 */
.login-tabs {
  margin-bottom: 20px;
}

.login-tabs :deep(.el-tabs__nav-wrap::after) {
  background-color: rgba(255, 255, 255, 0.08);
}

.login-tabs :deep(.el-tabs__item) {
  color: rgba(255, 255, 255, 0.7);
}

.login-tabs :deep(.el-tabs__item.is-active) {
  color: #667eea;
}

.login-tabs :deep(.el-tabs__active-bar) {
  background-color: #667eea;
}

/* 输入框样式 */
.custom-input :deep(.el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: none;
  border-radius: 8px;
  transition: all 0.3s;
}

.custom-input :deep(.el-input__wrapper:hover),
.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 1px #667eea inset;
}

.custom-input :deep(.el-input__inner) {
  color: #fff;
}

.custom-input :deep(.el-input__inner::placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

.custom-input :deep(.el-input__prefix) {
  color: rgba(255, 255, 255, 0.5);
}

/* 按钮样式 */
.login-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 8px !important;
  padding: 12px 20px !important;
  font-weight: 500;
  transition: all 0.3s !important;
}

.login-btn:hover {
  transform: translateY(-2px) !important;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4) !important;
}

.login-btn:disabled {
  background: rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.5) !important;
  transform: none !important;
  box-shadow: none !important;
}

.w-100 {
  width: 100%;
}

/* 选项区域 */
.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-options :deep(.el-checkbox__label) {
  color: rgba(255, 255, 255, 0.7);
}

.login-options :deep(.el-checkbox__inner) {
  border-color: rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.05);
}

.login-options :deep(.el-checkbox__inner:hover) {
  border-color: #667eea;
}

/* 底部链接 */
.login-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
  font-size: 14px;
}

.footer-link {
  color: rgba(255, 255, 255, 0.6) !important;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #667eea !important;
}

/* 协议底部 */
.agreement-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
}

.agreement-footer span {
  margin: 0 4px;
}

/* 响应式 */
@media (max-width: 480px) {
  .login-card {
    width: calc(100% - 40px);
    margin: 0 20px;
  }
  
  .login-footer {
    flex-direction: column;
    gap: 8px;
    align-items: center;
  }
}
</style>
