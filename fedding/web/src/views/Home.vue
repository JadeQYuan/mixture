<template>
  <div class="home-container" @click="goToFace">
    <h2 class="welcome-title">欢迎使用智能加料管理系统</h2>
    <div class="login-link" @click.stop="goToLogin">密码登录</div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function goToFace() {
  router.push('/face-login')
}
function goToLogin() {
  router.push('/login')
}
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
</style> 