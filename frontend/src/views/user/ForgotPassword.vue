<template>
  <div class="forgot-container">
    <!-- 背景渐变效果 -->
    <div class="forgot-background">
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
    
    <el-card class="forgot-card">
      <template #header>
        <h2 class="forgot-title">重置密码</h2>
      </template>
      
      <el-form :model="form" label-width="100px">
        <!-- 手机号输入 -->
        <el-form-item label="手机号" required>
          <el-input 
            v-model="form.phone" 
            placeholder="请输入注册手机号"
            maxlength="11"
            @input="validatePhone"
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
              @click="sendCode"
              class="send-code-btn"
            >
              {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
            </el-button>
          </div>
          <div v-if="codeError" class="error-text">{{ codeError }}</div>
        </el-form-item>
        
        <!-- 新密码 -->
        <el-form-item label="新密码" required>
          <el-input 
            v-model="form.newPassword" 
            type="password" 
            placeholder="请设置新密码（8-20位，含字母和数字）"
            show-password
            @input="validatePassword"
            class="custom-input"
          />
          <div v-if="passwordError" class="error-text">{{ passwordError }}</div>
        </el-form-item>
        
        <!-- 确认密码 -->
        <el-form-item label="确认密码" required>
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入新密码"
            show-password
            @input="validateConfirmPassword"
            class="custom-input"
          />
          <div v-if="confirmPasswordError" class="error-text">{{ confirmPasswordError }}</div>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleReset"
            :loading="loading"
            :disabled="!isFormValid"
            class="reset-btn"
          >
            重置密码
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
import { sendAuthCode, resetPassword } from '@/api/user/auth'

const router = useRouter()
const loading = ref(false)
const countdown = ref(0)
let countdownTimer = null

const form = reactive({
  phone: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

const phoneError = ref('')
const codeError = ref('')
const passwordError = ref('')
const confirmPasswordError = ref('')

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

// 密码格式验证（与注册页面一致）
const validatePassword = () => {
  if (!form.newPassword) {
    passwordError.value = ''
    return
  }
  
  if (form.newPassword.length < 8) {
    passwordError.value = '密码长度不能少于8位'
  } else if (form.newPassword.length > 20) {
    passwordError.value = '密码长度不能超过20位'
  } else if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(form.newPassword)) {
    passwordError.value = '密码必须包含字母和数字'
  } else if (/^\d+$/.test(form.newPassword)) {
    passwordError.value = '密码不能为纯数字'
  } else if (/^[a-zA-Z]+$/.test(form.newPassword)) {
    passwordError.value = '密码不能为纯字母'
  } else {
    passwordError.value = ''
  }
}

// 确认密码验证
const validateConfirmPassword = () => {
  if (!form.confirmPassword) {
    confirmPasswordError.value = ''
    return
  }
  
  if (form.confirmPassword !== form.newPassword) {
    confirmPasswordError.value = '两次输入的密码不一致'
  } else {
    confirmPasswordError.value = ''
  }
}

// 计算属性
const isPhoneValid = computed(() => {
  return /^1[3-9]\d{9}$/.test(form.phone)
})

const isFormValid = computed(() => {
  return isPhoneValid.value && 
         /^\d{6}$/.test(form.code) &&
         form.newPassword.length >= 8 &&
         form.newPassword.length <= 20 &&
         /(?=.*[a-zA-Z])(?=.*\d)/.test(form.newPassword) &&
         form.confirmPassword === form.newPassword
})

// 发送验证码
const sendCode = async () => {
  if (!isPhoneValid.value) {
    ElMessage.warning('请输入有效的手机号')
    return
  }
  
  try {
    await sendAuthCode(form.phone, 'reset')
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

// 重置密码
const handleReset = async () => {
  if (!isFormValid.value) {
    ElMessage.warning('请填写完整的重置信息')
    return
  }
  
  loading.value = true
  
  try {
    await resetPassword({
      phone: form.phone,
      code: form.code,
      password: form.newPassword
    })
    
    ElMessage.success('密码重置成功')
    // 延迟跳转，让用户看到成功提示
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } catch (error) {
    const message = error.message || '重置失败'
    if (message.includes('验证码')) {
      codeError.value = message
    } else if (message.includes('手机号')) {
      phoneError.value = message
    } else if (message.includes('密码')) {
      passwordError.value = message
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

// 清理倒计时
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>

<style scoped>
/* 全局容器 - 深色主题 */
.forgot-container {
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
.forgot-background {
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

/* 重置密码卡片 */
.forgot-card {
  width: 500px;
  background: rgba(255, 255, 255, 0.05) !important;
  border: 1px solid rgba(255, 255, 255, 0.08) !important;
  backdrop-filter: blur(20px);
  z-index: 2;
}

.forgot-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  padding: 20px;
  background: transparent;
}

.forgot-title {
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
  content: rgba(255, 255, 255, 0.5);
}

/* 表单标签样式 */
.forgot-card :deep(.el-form-item__label) {
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
.reset-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  border: none !important;
  border-radius: 8px !important;
  padding: 12px 20px !important;
  font-weight: 500;
  transition: all 0.3s !important;
  width: 120px;
}

.reset-btn:hover:not(:disabled) {
  transform: translateY(-2px) !important;
  box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4) !important;
}

.reset-btn:disabled {
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

/* 错误提示 */
.error-text {
  color: #ff6b6b;
  font-size: 12px;
  margin-top: 4px;
}

/* 响应式 */
@media (max-width: 600px) {
  .forgot-card {
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
  
  .forgot-card :deep(.el-form-item__label) {
    width: 80px !important;
  }
  
  .forgot-card :deep(.el-form-item__content) {
    margin-left: 80px !important;
  }
}
</style>