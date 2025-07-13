<template>
  <div class="face-login-container">
    <!-- 左侧提示信息面板 -->
    <div class="info-panel">
      <div class="info-header">
        <h2 class="info-title">人脸识别登录</h2>
      </div>
      <div class="info-content">
        <div class="recognition-tips">
          <div class="tips-title">识别提示：</div>
          <ul class="tips-list">
            <li>请确保光线充足</li>
            <li>面部正对摄像头</li>
            <li>保持适当距离（30-50cm）</li>
            <li>避免遮挡面部</li>
          </ul>
        </div>
      </div>
    </div>
    
    <!-- 中间摄像头区域 -->
    <div class="camera-section">
      <div class="face-login-card">
        <video ref="videoRef" autoplay playsinline class="face-video" />
        <canvas ref="canvasRef" style="display: none;"></canvas>
      </div>
    </div>
    <el-button class="back-btn" size="large" @click="goHome">返回</el-button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { faceLogin, getCurrentUser } from '@/request/api'
import { useStore } from 'vuex'
import { startGlobalCamera, getCameraErrorMessage } from '@/utils/camera'

const router = useRouter()
const store = useStore()
const videoRef = ref(null)
const canvasRef = ref(null)
const isCameraActive = ref(false)
const isProcessing = ref(false)
const maxRetryCount = 100 // 最大重试次数
let retryCount = 0 // 当前重试次数


// 启动摄像头（智能策略）
async function startCameraStream() {
  try {
    // 使用智能摄像头启动，自动从最高配置开始尝试
    ElMessage.info('正在启动摄像头...')
    if (videoRef.value) {
      videoRef.value.srcObject = await startGlobalCamera()
      isCameraActive.value = true
      ElMessage.success('摄像头已启动，正在准备识别...')
    }
  } catch (error) {
    console.error('摄像头启动失败:', error)
    // 使用统一的错误信息处理
    const errorMessage = getCameraErrorMessage(error)
    ElMessage.error(errorMessage)
    isCameraActive.value = false
  }
}

// 拍照并转换为File对象
function capturePhoto() {
  const video = videoRef.value
  const canvas = canvasRef.value
  
  if (!video || !canvas) return null
  
  // 设置canvas尺寸与video一致
  canvas.width = video.videoWidth
  canvas.height = video.videoHeight
  
  // 在canvas上绘制video帧
  const ctx = canvas.getContext('2d')
  ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
  
  // 将canvas转换为blob，再转换为File对象
  return new Promise((resolve) => {
    canvas.toBlob((blob) => {
      const file = new File([blob], 'face_capture.jpg', { type: 'image/jpeg' })
      resolve(file)
    }, 'image/jpeg', 0.8)
  })
}

// 人脸识别
async function captureAndRecognize() {
  if (!isCameraActive.value || isProcessing.value) return
  
  // 检查重试次数
  if (retryCount >= maxRetryCount) {
    ElMessage.error('识别失败次数过多，请检查摄像头或网络连接，或联系管理员')
    isProcessing.value = false
    return
  }
  
  try {
    isProcessing.value = true
    
    // 拍照
    const imageFile = await capturePhoto()
    if (!imageFile) {
      retryCount++
      isProcessing.value = false
      // 减少延迟，立即重试
      setTimeout(() => {
        captureAndRecognize()
      }, 50)
      return
    }
    
    ElMessage.info('正在识别中...')
    
    // 调用人脸识别接口
    const response = await faceLogin(imageFile)
    
    if (response.code === 200) {
      // 识别成功，与密码登录逻辑保持一致
      const token = response.data
      if (token) {
        localStorage.setItem('token', token)
        
        // 登录成功后获取用户信息
        try {
          const userInfo = await getCurrentUser()
          store.dispatch('setUserInfo', userInfo.data)
        } catch (error) {
          console.error('获取用户信息失败:', error)
        }
        
        ElMessage.success('识别成功，正在跳转...')
        
        // 减少延迟跳转时间
        setTimeout(() => {
          router.push('/app')
        }, 500)
      } else {
        ElMessage.error('识别成功但token无效，1秒后重试...')
        retryCount++
        isProcessing.value = false
        setTimeout(() => {
          captureAndRecognize()
        }, 1000)
      }
    } else {
      // 识别失败，减少等待时间后重新识别
      ElMessage.error(response.message || '人脸识别失败，1秒后重试')
      retryCount++
      isProcessing.value = false
      setTimeout(() => {
        captureAndRecognize()
      }, 1000)
    }
  } catch (error) {
    // 错误已由http拦截器处理，不再重复提示
    retryCount++
    isProcessing.value = false
    setTimeout(() => {
      captureAndRecognize()
    }, 1000)
  }
}

// 自动开始人脸识别
async function autoStartRecognition() {
  // 进一步减少等待时间，立即开始识别
  await new Promise(resolve => setTimeout(resolve, 10))
  
  if (isCameraActive.value && !isProcessing.value) {
    // 重置重试次数
    retryCount = 0
    await captureAndRecognize()
  }
}

const goHome = () => {
  router.push('/')
}

onMounted(async () => {
  
  // 如果使用全局摄像头，检查其状态并设置视频源
  if (videoRef.value) {
    await startCameraStream()
  }
  
  // 监听视频加载完成事件
  if (videoRef.value) {
    videoRef.value.addEventListener('loadeddata', () => {
      // 视频数据加载完成后立即开始识别
      autoStartRecognition()
    })
    
    // 如果视频已经加载完成，直接开始识别
    if (videoRef.value.readyState >= 2) {
      autoStartRecognition()
    }
    
    // 备用策略：如果1秒后还没有开始识别，强制开始（进一步减少等待时间）
    setTimeout(() => {
      if (!isProcessing.value && isCameraActive.value) {
        autoStartRecognition()
      }
    }, 1000)
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
  padding: 0 60px;
}
/* 左侧信息面板 */
.info-panel {
  position: fixed;
  left: 60px;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255,255,255,0.95);
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  padding: 48px 40px;
  min-width: 400px;
  max-width: 500px;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 1000;
}

.info-header {
  text-align: center;
  margin-bottom: 40px;
}

.info-title {
  font-size: 2.5em;
  font-weight: bold;
  color: #1976d2;
  margin: 0;
  letter-spacing: 2px;
}

.info-content {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 32px;
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
/* 中间摄像头区域 */
.camera-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 24px;
}

.face-video {
  width: 900px;
  height: 850px;
  border-radius: 50%;
  object-fit: cover;
  box-shadow: 0 4px 32px rgba(25, 118, 210, 0.18);
  background: #222;
}


.recognition-tips {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e9ecef;
}

.tips-title {
  font-size: 18px;
  color: #333;
  font-weight: 600;
  margin-bottom: 16px;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  font-size: 16px;
  color: #666;
  line-height: 1.6;
}

.tips-list li {
  margin-bottom: 6px;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .face-login-container {
    padding: 0 40px;
  }
  
  .info-panel {
    left: 40px;
    min-width: 350px;
    padding: 40px 32px;
  }
  
  .face-video {
    width: 800px;
    height: 750px;
  }
}

@media (max-width: 1200px) {
  .face-login-container {
    padding: 0 20px;
  }
  
  .info-panel {
    left: 20px;
    min-width: 300px;
    padding: 32px 24px;
  }
  
  .face-video {
    width: 600px;
    height: 550px;
  }
}

@media (max-width: 768px) {
  .face-login-container {
    padding: 20px 10px;
  }
  
  .info-panel {
    position: relative;
    left: auto;
    top: auto;
    transform: none;
    min-width: 0;
    width: 100%;
    max-width: 100%;
    padding: 32px 24px;
    margin-bottom: 20px;
  }
  
  .info-title {
    font-size: 2.2em;
  }
  
  .tips-title {
    font-size: 16px;
  }
  
  .tips-list {
    font-size: 14px;
  }
  
  .face-video {
    width: 90vw;
    height: 50vw;
    min-width: 300px;
    min-height: 300px;
    border-radius: 40px;
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