<template>
  <div class="face-login-container">
    <div class="face-login-card">
      <video ref="videoRef" autoplay playsinline class="face-video" />
      <div class="face-role-select">
        <el-select v-model="selectedRole" placeholder="请选择识别角色" style="margin: 32px 0 0 0; width: 240px; font-size: 1.3em;">
          <el-option label="物料员" value="物料员" />
          <el-option label="高级操作员" value="高级操作员" />
          <el-option label="操作员" value="操作员" />
        </el-select>
        <el-button type="success" size="large" style="margin-top: 32px; font-size: 1.3em; padding: 16px 48px; border-radius: 16px;" :disabled="!selectedRole" @click="facePass">识别通过</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const selectedRole = ref('')
const router = useRouter()
const videoRef = ref(null)
let stream = null

function facePass() {
  if (!selectedRole.value) return
  localStorage.setItem('role', selectedRole.value)
  ElMessage.success('识别通过，欢迎 ' + selectedRole.value)
  setTimeout(() => {
    router.push('/app')
  }, 800)
}

onMounted(async () => {
  try {
    stream = await navigator.mediaDevices.getUserMedia({ video: true })
    if (videoRef.value) {
      videoRef.value.srcObject = stream
    }
  } catch (e) {
    ElMessage.error('无法访问摄像头，请检查权限')
  }
})

onBeforeUnmount(() => {
  if (stream) {
    stream.getTracks().forEach(track => track.stop())
  }
})
</script>

<style scoped>
.face-login-container {
  min-width: 100vw;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
}
.face-login-card {
  background: rgba(255,255,255,0.8);
  border-radius: 50%;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  padding: 5px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.face-video {
  width: 900px;
  height: 850px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 4px 32px rgba(25, 118, 210, 0.18);
  background: #222;
}
.face-role-select {
  display: flex;
  flex-direction: column;
  align-items: center;
}
@media (max-width: 900px) {
  .face-video {
    width: 90vw;
    height: 50vw;
    min-width: 220px;
    min-height: 140px;
    border-radius: 40px;
  }
}
@media (max-width: 600px) {
  .face-login-card {
    padding: 24px 4vw 16px 4vw;
  }
  .face-video {
    width: 96vw;
    height: 48vw;
    min-width: 120px;
    min-height: 80px;
    border-radius: 20px;
  }
}
</style> 