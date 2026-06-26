<template>
  <div class="home-container" @click="goToFace">
    <h2 class="welcome-title">欢迎使用</h2>
    <h2 class="welcome-title">原材料智能管理系统</h2>
    <div class="click-hint" :style="hintStyle">
      请点击进入系统
    </div>
    <el-card class="todo-content" v-if="todo.list.length > 0">
      <template v-for="item in todo.list">
        <el-row :class="{ 'todo-row-warning': item.overdue }">
          <el-space size="large">
            <el-icon v-if="item.overdue" class="todo-warning-icon"><WarningFilled /></el-icon>
            <el-text class="todo-info">{{ item.applyUserName }}({{item.applyUserAccount}}) </el-text>
            <el-text class="todo-info">{{ item.status == 0 ? '申请加料' : '申请备料' }} </el-text>
            <el-text class="todo-info">{{ item.materialName }} </el-text>
            <el-text type="primary" class="todo-info">{{ item.planWeight }} kg</el-text>
          </el-space>
        </el-row>
      </template>
    </el-card>
    <div class="login-link" @click.stop="goToLogin">密码登录</div>
    <el-button class="back-btn" size="large" @click.stop="goToLogin">返回</el-button>
  </div>
</template>

<script setup>
import { getTodoList } from '@/api/mixture'
import { ref, onMounted, onUnmounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { WarningFilled } from '@element-plus/icons-vue'

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
  router.push('/login/face')
}

function goToLogin() {
  router.push('/login/code')
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


const todo = reactive({
  list: []
})

async function getTodo() {
  todo.list = await getTodoList()
}

onMounted(async () => {
  // 启动动画
  animateHint()
  await getTodo()
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
  margin-top: 60px;
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

.todo-content {
  position: fixed;
  top: 30px;
  right: 30px;
  width: 580px;
  max-height: 650px;
  opacity: 0.5;
  overflow: auto;
}
.todo-info {
  font-size: 24px;
  font-weight: 600;
  line-height: 48px;
}

.todo-row-warning {
  background-color: #fef0f0;
  border-left: 4px solid #f56c6c;
  border-radius: 4px;
  padding: 0 8px;
  animation: warning-blink 1.5s ease-in-out infinite;
}

.todo-warning-icon {
  font-size: 28px;
  color: #f56c6c;
  animation: warning-icon-pulse 1s ease-in-out infinite;
}

@keyframes warning-blink {
  0%, 100% { background-color: #fef0f0; }
  50% { background-color: #fde2e2; }
}

@keyframes warning-icon-pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}
</style>