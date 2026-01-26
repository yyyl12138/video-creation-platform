<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>系统登录</h2>
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
              />
            </el-form-item>
            <el-form-item>
              <el-input 
                v-model="passwordForm.password" 
                type="password" 
                placeholder="密码" 
                prefix-icon="Lock"
                show-password
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
              <el-button type="primary" class="w-100" @click="handlePasswordLogin">
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
              />
            </el-form-item>
            <el-form-item>
              <VerificationCode 
                v-model="smsForm.code"
                placeholder="请输入验证码"
                :disabled="!isPhoneValid"
                @send="handleSendCode"
              />
            </el-form-item>
            <el-form-item>
              <el-button 
                type="primary" 
                class="w-100" 
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
        <el-link type="primary" :underline="false" @click="goToForgotPassword">
          忘记密码?
        </el-link>
        <el-link type="primary" :underline="false" @click="goToRegister">
          没有账号?立即注册
        </el-link>
      </div>

      <div class="agreement-footer">
        <span>登录即代表同意</span>
        <el-link type="primary" :underline="false" @click="goToUserAgreement">
          用户协议
        </el-link>
        <span>和</span>
        <el-link type="primary" :underline="false" @click="goToPrivacyPolicy">
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
    localStorage.setItem('token', res.token)
    router.push('/home')
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
    
    localStorage.setItem('token', res.token)
    router.push('/home')
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
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-card {
  width: 400px;
}

.login-tabs {
  margin-bottom: 20px;
}

.w-100 {
  width: 100%;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 16px;
  font-size: 14px;
}

.agreement-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 12px;
  color: #666;
}

.agreement-footer span {
  margin: 0 4px;
}
</style>
