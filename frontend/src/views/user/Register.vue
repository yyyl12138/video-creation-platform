<template>
  <div class="auth-container">
    <el-card class="auth-card">
      <template #header>
        <h2>手机号注册</h2>
      </template>
      
      <el-form :model="form" label-width="100px">
        <!-- 用户名 -->
        <el-form-item label="用户名" required>
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名（4-20位）"
            maxlength="20"
            @input="validateUsername"
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
          />
          <div v-if="passwordError" class="error-text">{{ passwordError }}</div>
        </el-form-item>
        
        <!-- 用户协议 -->
        <el-form-item>
          <el-checkbox v-model="form.agreed">
            我已阅读并同意
            <el-link type="primary" @click="goToUserAgreement">《用户协议》</el-link>
            和
            <el-link type="primary" @click="goToPrivacyPolicy">《隐私政策》</el-link>
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

.register-btn {
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