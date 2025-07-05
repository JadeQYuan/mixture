<template>
  <div class="face-login-container">
    <div class="face-login-card">
      <video ref="videoRef" autoplay playsinline class="face-video" />
    </div>
    
    <!-- 临时使用的右侧控制面板 -->
    <div class="temp-control-panel">
      <div class="temp-panel-header">
        <span class="temp-label">临时功能</span>
      </div>
      <div class="temp-control-content">
        <div class="role-selection">
          <div class="role-label">请选择识别角色：</div>
          <el-radio-group v-model="selectedRole" class="temp-role-radio">
            <el-radio label="物料员" size="large">物料员</el-radio>
            <el-radio label="高级操作员" size="large">高级操作员</el-radio>
            <el-radio label="操作员" size="large">操作员</el-radio>
          </el-radio-group>
        </div>
        <el-button type="success" size="large" class="temp-login-btn" :disabled="!selectedRole" @click="facePass">
          识别通过
        </el-button>
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
  position: relative;
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
/* 临时控制面板样式 */
.temp-control-panel {
  position: fixed;
  top: 50%;
  right: 100px;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  padding: 20px;
  min-width: 280px;
  z-index: 1000;
  border: 2px solid #409EFF;
}

.temp-panel-header {
  text-align: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
}

.temp-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
  background: #f0f9ff;
  padding: 4px 12px;
  border-radius: 12px;
  border: 1px solid #409EFF;
}

.temp-control-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.role-selection {
  width: 100%;
}

.role-label {
  font-size: 14px;
  color: #333;
  margin-bottom: 12px;
  font-weight: 500;
}

.temp-role-radio {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.temp-role-radio .el-radio {
  margin-right: 0;
  margin-bottom: 0;
  padding: 8px 12px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.temp-role-radio .el-radio:hover {
  background-color: #f0f9ff;
}

.temp-role-radio .el-radio.is-checked {
  background-color: #e3f0fd;
  border: 1px solid #409EFF;
}

.temp-login-btn {
  width: 100%;
  padding: 12px 24px;
  font-size: 16px;
  border-radius: 12px;
  height: 48px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .temp-control-panel {
    right: 10px;
    min-width: 260px;
  }
}

@media (max-width: 768px) {
  .temp-control-panel {
    position: fixed;
    bottom: 20px;
    right: 20px;
    top: auto;
    transform: none;
    min-width: 240px;
  }
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