<template>
  <div class="verification-code">
    <el-input
      v-model="code"
      :placeholder="placeholder"
      :maxlength="length"
      @input="handleInput"
      class="code-input"
    >
      <template #append>
        <el-button
          type="primary"
          :disabled="disabled || countdown > 0"
          @click="handleSend"
          class="send-btn"
        >
          {{ countdown > 0 ? `${countdown}s后重发` : '获取验证码' }}
        </el-button>
      </template>
    </el-input>
  </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '请输入验证码'
  },
  length: {
    type: Number,
    default: 6
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'send'])

const code = ref(props.modelValue)
const countdown = ref(0)

let timer = null

const handleInput = (value) => {
  // 只保留数字
  code.value = value.replace(/\D/g, '')
  emit('update:modelValue', code.value)
}

const handleSend = () => {
  emit('send')
}

const startCountdown = (seconds = 60) => {
  countdown.value = seconds
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const resetCountdown = () => {
  countdown.value = 0
  if (timer) {
    clearInterval(timer)
  }
}

// 暴露方法给父组件
defineExpose({
  startCountdown,
  resetCountdown
})
</script>

<style scoped>
.verification-code {
  width: 100%;
}

.code-input {
  width: 100%;
}

.send-btn {
  min-width: 100px;
}
</style>