<template>
  <div class="home-container" @click="goToFace">
    <h2 class="welcome-title">欢迎使用智能加料管理系统</h2>
    <div class="click-hint" :style="hintStyle">
      请点击进入系统
    </div>
    <div class="login-link" @click.stop="goToLogin">密码登录</div>
    <el-button class="back-btn" size="large" @click.stop="goToLogin">返回</el-button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted, onUnmounted } from 'vue'

const router = useRouter()
const hintStyle = ref({
  transform: 'translate(0, 0)'
})

let animationId = null
let direction = { x: 1, y: 1 }
let position = { x: 0, y: 0 }
const speed = 0.5
const maxDistance = 20

function goToFace() {
  router.push('/face-login')
}

function goToLogin() {
  router.push('/login')
}

function animateHint() {
  // 更新位置
  position.x += direction.x * speed
  position.y += direction.y * speed
  
  // 检查边界，改变方向
  if (Math.abs(position.x) > maxDistance) {
    direction.x *= -1
  }
  if (Math.abs(position.y) > maxDistance) {
    direction.y *= -1
  }
  
  // 应用变换
  hintStyle.value = {
    transform: `translate(${position.x}px, ${position.y}px)`
  }
  
  // 继续动画
  animationId = requestAnimationFrame(animateHint)
}

onMounted(() => {
  // 启动动画
  animateHint()
})

onUnmounted(() => {
  // 清理动画
  if (animationId) {
    cancelAnimationFrame(animationId)
  }
})
</script>

<style scoped>
.home-container {
  min-width: 100vw;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  user-select: none;
  position: relative;
}
.welcome-title {
  text-align: center;
  font-size: 5em;
  font-weight: bold;
  margin-bottom: 60px;
  color: #1976d2;
  letter-spacing: 2px;
}

.click-hint {
  font-size: 2em;
  color: #409EFF;
  background: rgba(255, 255, 255, 0.9);
  padding: 16px 32px;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
  animation: pulse 2s infinite;
  margin-bottom: 40px;
}

.click-hint:hover {
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.3);
  transform: scale(1.05);
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}
.login-link {
  position: fixed;
  right: 40px;
  bottom: 40px;
  font-size: 1.4em;
  color: #1976d2;
  background: rgba(255,255,255,0.85);
  border-radius: 8px;
  padding: 10px 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  cursor: pointer;
  z-index: 1001;
  transition: background 0.2s;
}
.login-link:hover {
  background: #e3f0fd;
  color: #1256a6;
}
.back-btn {
  position: fixed;
  right: 40px;
  bottom: 40px;
  z-index: 1000;
  font-size: 1.2em;
  padding: 16px 32px;
  border-radius: 12px;
}
</style> 