<template>
  <div class="dashboard-container">
    <!-- 可爱的欢迎卡片 -->
    <div class="welcome-card">
      <div class="card-decoration top-left">
        <span>✨</span>
      </div>
      <div class="card-decoration top-right">
        <span>🌟</span>
      </div>
      <div class="card-decoration bottom-left">
        <span>🎬</span>
      </div>
      <div class="card-decoration bottom-right">
        <span>💫</span>
      </div>
      
      <div class="card-content">
        <div class="mascot">
          <div class="mascot-face">
            <div class="eyes">
              <div class="eye left"></div>
              <div class="eye right"></div>
            </div>
            <div class="mouth"></div>
            <div class="cheeks">
              <div class="cheek"></div>
              <div class="cheek"></div>
            </div>
          </div>
        </div>
        
        <h1>欢迎使用</h1>
        <h2>智能视频创作平台</h2>
        <p class="slogan">让创意起飞，精彩视频由此开始 ✨</p>
      </div>
    </div>

    <!-- 卡通风格提示 -->
    <div class="hint-bubble">
      <div class="bubble-arrow">◀</div>
      <div class="bubble-content">
        <span class="bubble-icon">👉</span>
        <span>从左边菜单开始创作之旅吧！</span>
      </div>
    </div>

    <!-- 装饰元素 -->
    <div class="decoration-stars">
      <span class="star" style="--delay: 0s">⭐</span>
      <span class="star" style="--delay: 0.5s">🌟</span>
      <span class="star" style="--delay: 1s">✨</span>
      <span class="star" style="--delay: 1.5s">💫</span>
      <span class="star" style="--delay: 2s">⭐</span>
    </div>
  </div>
</template>

<script setup>
// 简洁的欢迎页面，无需额外脚本
</script>

<style scoped>
.dashboard-container {
  padding: 60px 20px;
  max-width: 700px;
  margin: 0 auto;
  position: relative;
  overflow: hidden;
}

/* 可爱的欢迎卡片 */
.welcome-card {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%);
  border-radius: 32px;
  padding: 60px 40px;
  text-align: center;
  position: relative;
  box-shadow: 
    0 20px 60px rgba(255, 154, 158, 0.4),
    0 0 0 8px #fff,
    0 0 0 12px #ffecd2;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

/* 卡片装饰角标 */
.card-decoration {
  position: absolute;
  font-size: 24px;
  animation: spin 4s linear infinite;
}

.card-decoration.top-left {
  top: 20px;
  left: 20px;
}

.card-decoration.top-right {
  top: 20px;
  right: 20px;
  animation-direction: reverse;
}

.card-decoration.bottom-left {
  bottom: 20px;
  left: 20px;
  animation-direction: reverse;
}

.card-decoration.bottom-right {
  bottom: 20px;
  right: 20px;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 卡片内容 */
.card-content h1 {
  margin: 0 0 8px 0;
  font-size: 2.2rem;
  font-weight: 700;
  color: #fff;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: 4px;
}

.card-content h2 {
  margin: 0 0 16px 0;
  font-size: 1.4rem;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.95);
}

.slogan {
  margin: 0;
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.9);
}

/* 卡通吉祥物 */
.mascot {
  margin-bottom: 24px;
}

.mascot-face {
  width: 100px;
  height: 100px;
  background: #fff;
  border-radius: 50%;
  margin: 0 auto 20px;
  position: relative;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

.eyes {
  display: flex;
  justify-content: center;
  gap: 24px;
  padding-top: 28px;
}

.eye {
  width: 16px;
  height: 20px;
  background: #333;
  border-radius: 50%;
  position: relative;
  animation: blink 3s infinite;
}

.eye::after {
  content: '';
  position: absolute;
  width: 6px;
  height: 6px;
  background: #fff;
  border-radius: 50%;
  top: 4px;
  right: 3px;
}

@keyframes blink {
  0%, 90%, 100% { transform: scaleY(1); }
  95% { transform: scaleY(0.1); }
}

.mouth {
  width: 30px;
  height: 15px;
  background: #ff6b6b;
  border-radius: 0 0 30px 30px;
  margin: 12px auto 0;
}

.cheeks {
  position: absolute;
  bottom: 20px;
  width: 100%;
  display: flex;
  justify-content: space-between;
  padding: 0 15px;
  box-sizing: border-box;
}

.cheek {
  width: 16px;
  height: 10px;
  background: #ffb6c1;
  border-radius: 50%;
  opacity: 0.7;
}

/* 卡通提示气泡 */
.hint-bubble {
  display: flex;
  align-items: center;
  margin-top: 40px;
  justify-content: center;
}

.bubble-arrow {
  color: #67c23a;
  font-size: 24px;
  margin-right: 8px;
  animation: pointLeft 1s ease-in-out infinite;
}

@keyframes pointLeft {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(-5px); }
}

.bubble-content {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 28px;
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  border-radius: 50px;
  box-shadow: 0 8px 24px rgba(103, 194, 58, 0.3);
  color: #fff;
  font-size: 1rem;
  font-weight: 500;
}

.bubble-icon {
  font-size: 1.2rem;
}

/* 装饰星星 */
.decoration-stars {
  position: absolute;
  width: 100%;
  top: 40px;
  left: 0;
  display: flex;
  justify-content: space-between;
  padding: 0 60px;
  pointer-events: none;
  opacity: 0.6;
}

.star {
  font-size: 20px;
  animation: twinkle 1.5s ease-in-out infinite;
  animation-delay: var(--delay);
}

@keyframes twinkle {
  0%, 100% { opacity: 0.4; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

/* 响应式 */
@media (max-width: 600px) {
  .dashboard-container {
    padding: 40px 15px;
  }
  
  .welcome-card {
    padding: 40px 24px;
    border-radius: 24px;
  }
  
  .card-content h1 {
    font-size: 1.8rem;
  }
  
  .card-content h2 {
    font-size: 1.2rem;
  }
  
  .mascot-face {
    width: 80px;
    height: 80px;
  }
  
  .eyes {
    gap: 18px;
    padding-top: 22px;
  }
  
  .eye {
    width: 14px;
    height: 18px;
  }
  
  .decoration-stars {
    padding: 0 30px;
  }
}
</style>
