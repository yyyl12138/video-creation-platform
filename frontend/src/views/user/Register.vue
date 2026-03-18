<template>
  <div class="register-container">
    <!-- 背景渐变效果 -->
    <div class="register-background">
      <div class="bg-gradient"></div>
      <div class="bg-glow bg-glow-1"></div>
      <div class="bg-glow bg-glow-2"></div>
    </div>
    
    <!-- 视频背景 -->
    <div class="video-background">
      <video 
        class="bg-video"
        autoplay
        muted
        loop
        playsinline
        src="/videos/8ad1a29fab8541b387f425c05b0d9801.mp4"
      ></video>
      <div class="video-overlay"></div>
    </div>
    
    <el-card class="register-card">
      <template #header>
        <h2 class="register-title">用户注册</h2>
      </template>
      
      <el-form :model="form" label-width="100px">
        <!-- 用户名 -->
        <el-form-item label="用户名" required>
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名（4-20位）"
            maxlength="20"
            @input="validateUsername"
            class="custom-input"
          />
          <div v-if="usernameError" class="error-text">{{ usernameError }}</div>
        </el-form-item>

        <!-- 手机号输入 -->
        <el-form-item label="手机号" required>
          <el-input 
            v-model="form.phone" 
            placeholder="请输入11位手机号"
            maxlength="11"
            @input="validatePhone"
            :class="{ 'is-success': isPhoneValid }"
            class="custom-input"
          />
          <div v-if="phoneError" class="error-text">{{ phoneError }}</div>
        </el-form-item>
        
        <!-- 验证码 -->
        <el-form-item label="验证码" required>
          <div class="code-input-group">
            <el-input 
              v-model="form.code" 
              placeholder="请输入6位验证码"
              maxlength="6"
              @input="validateCode"
              class="custom-input"
            />
            <el-button 
              type="primary" 
              :disabled="!isPhoneValid || countdown > 0"
              @click="sendVerificationCode"
              class="send-code-btn"
            >
              {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
            </el-button>
          </div>
          <div v-if="codeError" class="error-text">{{ codeError }}</div>
        </el-form-item>
        
        <!-- 密码设置 -->
        <el-form-item label="登录密码" required>
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请设置登录密码（8-20位，含字母和数字）"
            show-password
            @input="validatePassword"
            class="custom-input"
          />
          <div v-if="passwordError" class="error-text">{{ passwordError }}</div>
        </el-form-item>
        
        <!-- 用户协议 -->
        <el-form-item>
          <el-checkbox v-model="form.agreed" class="custom-checkbox">
            <span class="agreement-text">
              我已阅读并同意
              <el-link class="footer-link" @click="goToUserAgreement">《用户协议》</el-link>
              和
              <el-link class="footer-link" @click="goToPrivacyPolicy">《隐私政策》</el-link>
            </span>
          </el-checkbox>
          <div v-if="agreementError" class="error-text">{{ agreementError }}</div>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleRegister"
            :loading="loading"
            :disabled="!isFormValid"
            class="register-btn"
          >
            注册
          </el-button>
          <el-button @click="goToLogin" class="back-btn">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { sendAuthCode, registerUser } from '@/api/user/auth'

const router = useRouter()
const loading = ref(false)
const countdown = ref(0)
let countdownTimer = null

const form = reactive({
  username: '',
  phone: '',
  code: '',
  password: '',
  agreed: false
})

const usernameError = ref('')
const phoneError = ref('')
const codeError = ref('')
const passwordError = ref('')
const agreementError = ref('')

// 验证用户名
const validateUsername = () => {
  if (!form.username) {
    usernameError.value = '用户名不能为空'
    return
  }
  if (form.username.length < 4 || form.username.length > 20) {
    usernameError.value = '用户名长度必须在4-20之间'
  } else {
    usernameError.value = ''
  }
}

// 验证手机号格式
const validatePhone = () => {
  if (!form.phone) {
    phoneError.value = ''
    return
  }
  
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!phoneRegex.test(form.phone)) {
    phoneError.value = '请输入有效的11位手机号'
  } else {
    phoneError.value = ''
  }
}

// 验证码格式验证
const validateCode = () => {
  if (!form.code) {
    codeError.value = ''
    return
  }
  
  const codeRegex = /^\d{6}$/
  if (!codeRegex.test(form.code)) {
    codeError.value = '验证码必须为6位数字'
  } else {
    codeError.value = ''
  }
}

// 密码格式验证
const validatePassword = () => {
  if (!form.password) {
    passwordError.value = ''
    return
  }
  
  if (form.password.length < 8) {
    passwordError.value = '密码长度不能少于8位'
  } else if (form.password.length > 20) {
    passwordError.value = '密码长度不能超过20位'
  } else if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(form.password)) {
    passwordError.value = '密码必须包含字母和数字'
  } else if (/^\d+$/.test(form.password)) {
    passwordError.value = '密码不能为纯数字'
  } else if (/^[a-zA-Z]+$/.test(form.password)) {
    passwordError.value = '密码不能为纯字母'
  } else {
    passwordError.value = ''
  }
}

// 计算属性
const isPhoneValid = computed(() => {
  return /^1[3-9]\d{9}$/.test(form.phone)
})

const isFormValid = computed(() => {
  return isPhoneValid.value && 
         /^\d{6}$/.test(form.code) &&
         form.username.length >= 4 &&
         form.password.length >= 8 &&
         form.password.length <= 20 &&
         /(?=.*[a-zA-Z])(?=.*\d)/.test(form.password) &&
         form.agreed
})

// 发送验证码
const sendVerificationCode = async () => {
  if (!isPhoneValid.value) {
    ElMessage.warning('请输入有效的手机号')
    return
  }
  
  try {
    await sendAuthCode(form.phone, 'register')
    ElMessage.success('验证码已发送')
    startCountdown()
  } catch (error) {
    ElMessage.error(error.message || '验证码发送失败')
  }
}

// 开始倒计时
const startCountdown = (seconds = 60) => {
  countdown.value = seconds
  if (countdownTimer) clearInterval(countdownTimer)
  
  countdownTimer = setInterval(() => {
    if (countdown.value <= 0) {
      clearInterval(countdownTimer)
      return
    }
    countdown.value--
  }, 1000)
}

// 注册处理
const handleRegister = async () => {
  // 表单验证
  validateUsername()
  if (usernameError.value) return

  if (!form.agreed) {
    agreementError.value = '请同意用户协议和隐私政策'
    return
  }
  
  loading.value = true
  agreementError.value = ''
  
  try {
    const res = await registerUser({
      username: form.username,
      phone: form.phone,
      code: form.code,
      password: form.password
    })
    
    ElMessage.success('注册成功')
    // 自动登录并跳转首页
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('refreshToken', res.data.refreshToken)
    // token有效时间通常与后端一致 (2小时) or 使用 res.data.expireIn
    const expireIn = res.data.expireIn ? res.data.expireIn * 1000 : 2 * 60 * 60 * 1000
    localStorage.setItem('tokenExpiry', Date.now() + expireIn)
    
    localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo || res.data.user)) // Adapt to whatever field name is returned
    router.push('/home')
  } catch (error) {
    const message = error.message || '注册失败'
    if (message.includes('验证码')) {
      codeError.value = message
    } else if (message.includes('手机号')) {
      phoneError.value = message
    } else if (message.includes('密码')) {
      passwordError.value = message
    } else if (message.includes('用户名')) {
      usernameError.value = message
    } else {
      ElMessage.error(message)
    }
  } finally {
    loading.value = false
  }
}

// 页面跳转
const goToLogin = () => {
  router.push('/login')
}

const goToUserAgreement = () => {
  router.push('/user-agreement')
}

const goToPrivacyPolicy = () => {
  router.push('/privacy-policy')
}

// 清理倒计时
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped>
/* 全局容器 - 深色主题 */
.register-container {
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
.register-background {
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

/* 注册卡片 */
.register-card {
  width: 500px;
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  backdrop-filter: blur(20px);
  z-index: 2;
}

.register-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  padding: 20px;
  background: transparent;
}

.register-title {
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

/* 表单标签样式 */
.register-card :deep(.el-form-item__label) {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

/* 验证码输入组 */
.code-input-group {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.send-code-btn {
  min-width: 120px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 8px !important;
  transition: all 0.3s !important;
}

.send-code-btn:hover:not(:disabled) {
  transform: translateY(-2px) !important;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4) !important;
}

.send-code-btn:disabled {
  background: rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.5) !important;
}

/* 按钮样式 */
.register-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 8px !important;
  padding: 12px 20px !important;
  font-weight: 500;
  transition: all 0.3s !important;
  width: 120px;
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px) !important;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4) !important;
}

.register-btn:disabled {
  background: rgba(255, 255, 255, 0.1) !important;
  color: rgba(255, 255, 255, 0.5) !important;
}

.back-btn {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: #fff !important;
  border-radius: 8px !important;
  padding: 12px 20px !important;
  transition: all 0.3s !important;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.15) !important;
  border-color: rgba(255, 255, 255, 0.3) !important;
}

/* 复选框样式 */
.custom-checkbox :deep(.el-checkbox__label) {
  color: rgba(255, 255, 255, 0.7);
}

.custom-checkbox :deep(.el-checkbox__inner) {
  border-color: rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.05);
}

.custom-checkbox :deep(.el-checkbox__inner:hover) {
  border-color: #667eea;
}

.agreement-text {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

/* 链接样式 */
.footer-link {
  color: rgba(255, 255, 255, 0.6) !important;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #667eea !important;
}

/* 错误提示 */
.error-text {
  color: #ff6b6b;
  font-size: 12px;
  margin-top: 4px;
}

/* 响应式 */
@media (max-width: 600px) {
  .register-card {
    width: calc(100% - 40px);
    margin: 0 20px;
  }
  
  .code-input-group {
    flex-direction: column;
  }
  
  .send-code-btn {
    width: 100%;
    min-width: auto;
  }
  
  .register-card :deep(.el-form-item__label) {
    width: 80px !important;
  }
  
  .register-card :deep(.el-form-item__content) {
    margin-left: 80px !important;
  }
}
</style>