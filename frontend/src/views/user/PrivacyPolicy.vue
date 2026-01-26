<template>
  <div class="privacy-container">
    <el-card class="privacy-card">
      <template #header>
        <h2>隐私政策</h2>
      </template>
      
      <div class="privacy-content">
        <h3>1. 信息收集</h3>
        <p>我们收集您注册时提供的信息，以及使用服务时产生的数据。</p>
        
        <h3>2. 信息使用</h3>
        <p>您的信息将用于提供服务、改善用户体验和确保平台安全。</p>
        
        <h3>3. 信息共享</h3>
        <p>我们不会将您的信息出售给第三方，除非获得您的同意或法律要求。</p>
        
        <h3>4. 数据安全</h3>
        <p>我们采取严格的安全措施保护您的个人信息。</p>
        
        <h3>5. Cookies使用</h3>
        <p>我们使用cookies来提供更好的服务和用户体验。</p>
      </div>
      
      <div class="privacy-footer">
        <el-button type="primary" @click="goBack">返回</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.privacy-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
.privacy-card {
  margin-top: 20px;
}
.privacy-content {
  line-height: 1.6;
}
.privacy-content h3 {
  color: #409EFF;
  margin-top: 20px;
}
.privacy-footer {
  margin-top: 30px;
  text-align: center;
}
</style>