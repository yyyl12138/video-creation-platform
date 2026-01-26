<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <template #header>
        <h2>忘记密码</h2>
      </template>
      
      <el-form :model="form" label-width="100px">
        <!-- 手机号输入 -->
        <el-form-item label="手机号" required>
          <el-input 
            v-model="form.phone" 
            placeholder="请输入注册手机号"
            maxlength="11"
            @input="validatePhone"
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
          <el-button @click="goToLogin">返回登录</el-button>
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
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.auth-card {
  width: 500px;
}

.code-input-group {
  display: flex;
  gap: 10px;
}

.send-code-btn {
  min-width: 120px;
}

.reset-btn {
  width: 120px;
}

.error-text {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 4px;
}

:deep(.el-form-item__content) {
  line-height: 1.2;
}
</style>