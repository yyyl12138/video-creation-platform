<template>
  <div class="agreement-container">
    <el-card class="agreement-card">
      <template #header>
        <h2>用户协议</h2>
      </template>
      
      <div class="agreement-content">
        <h3>1. 服务内容</h3>
        <p>本平台为用户提供短视频创作、编辑、发布等相关服务。</p>
        
        <h3>2. 用户权利与义务</h3>
        <p>用户有权使用平台提供的各项服务，同时需遵守相关法律法规和平台规则。</p>
        
        <h3>3. 知识产权</h3>
        <p>用户创作的内容版权归用户所有，平台享有使用权。</p>
        
        <h3>4. 隐私保护</h3>
        <p>平台承诺保护用户隐私，详见隐私政策。</p>
        
        <h3>5. 免责声明</h3>
        <p>平台不对用户行为承担法律责任。</p>
      </div>
      
      <div class="agreement-footer">
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
.agreement-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}
.agreement-card {
  margin-top: 20px;
}
.agreement-content {
  line-height: 1.6;
}
.agreement-content h3 {
  color: #409EFF;
  margin-top: 20px;
}
.agreement-footer {
  margin-top: 30px;
  text-align: center;
}
</style>