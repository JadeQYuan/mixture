// 摄像头管理工具
// 统一管理摄像头配置和权限

/**
 * 摄像头配置常量
 * 
 * 配置说明：
 * - BLURAY: 蓝光配置，最高清晰度（最高）
 * - HD: 高清配置，用于用户管理拍照（高）
 * - MEDIUM: 中等分辨率配置，用于全局摄像头和人脸识别（中）
 * - LOW: 低分辨率配置，快速启动（低）
 * - PRELOAD: 预加载配置，极低分辨率，快速获取权限（最低）
 */
export const CAMERA_CONFIG = {
  // 蓝光配置 - 最高清晰度（最高）
  // BLURAY: {
  //   width: { ideal: 1920, max: 2560 },
  //   height: { ideal: 1080, max: 1440 },
  //   facingMode: 'user',
  //   frameRate: { ideal: 30, max: 60 }
  // },

  // 高清配置 - 用于用户管理拍照（高）
  HD: {
    width: { ideal: 1280, max: 1920 },
    height: { ideal: 720, max: 1080 },
    facingMode: 'user',
    frameRate: { ideal: 24, max: 30 }
  },

  // 中等分辨率配置 - 用于全局摄像头和人脸识别（中）
  MEDIUM: {
    width: { ideal: 640, max: 1280 },
    height: { ideal: 480, max: 720 },
    facingMode: 'user',
    frameRate: { ideal: 15, max: 24 }
  },

  // 低分辨率配置 - 快速启动（低）
  LOW: {
    width: { ideal: 480, max: 640 },
    height: { ideal: 360, max: 480 },
    facingMode: 'user',
    frameRate: { ideal: 12, max: 15 }
  },

  // 预加载配置 - 极低分辨率，快速获取权限（最低）
  PRELOAD: {
    width: { ideal: 40, max: 80 },
    height: { ideal: 30, max: 60 },
    facingMode: 'user',
    frameRate: { ideal: 1, max: 3 }
  }
}

/**
 * 获取摄像头配置
 * @param {string} type 配置类型
 * @param {object} customOptions 自定义选项
 * @returns {object} 摄像头配置
 */
export function getCameraConfig(type = 'MEDIUM', customOptions = {}) {
  const baseConfig = CAMERA_CONFIG[type] || CAMERA_CONFIG.MEDIUM
  
  // 合并自定义选项
  return {
    ...baseConfig,
    ...customOptions
  }
}

// ==================== 摄像头权限管理 ====================

let cameraPermissionGranted = false
let cameraStream = null
let preloadPromise = null
let globalCameraStream = null
let isStartingCamera = false // 添加摄像头启动状态标记

/**
 * 检测设备是否有摄像头
 */
async function checkCameraAvailability() {
  try {
    const devices = await navigator.mediaDevices.enumerateDevices()
    const videoDevices = devices.filter(device => device.kind === 'videoinput')
    return videoDevices.length > 0
  } catch (error) {
    console.error('无法检测摄像头设备:', error)
    return false
  }
}

/**
 * 预加载摄像头权限
 * 在应用启动时调用，避免使用时调用慢
 */
export async function preloadCameraPermission() {
  // 如果已经在预加载中，返回现有的Promise
  if (preloadPromise) {
    return preloadPromise
  }
  
  // 如果已经预加载成功，直接返回
  if (cameraPermissionGranted) {
    return true
  }
  
  preloadPromise = (async () => {
    try {
      // 检查是否有摄像头设备
      const hasCamera = await checkCameraAvailability()
      if (!hasCamera) {
        console.log('未检测到摄像头设备')
        cameraPermissionGranted = false
        return false
      }

      // 请求摄像头权限但不显示视频，使用极低配置快速获取权限
      const preloadConfig = getCameraConfig('PRELOAD')
      cameraStream = await navigator.mediaDevices.getUserMedia({ 
        video: preloadConfig
      })
      
      // 立即停止摄像头，只保留权限
      if (cameraStream) {
        cameraStream.getTracks().forEach(track => track.stop())
        cameraStream = null
      }
      
      cameraPermissionGranted = true
      console.log('摄像头权限加载成功')
      return true
    } catch (error) {
      console.log('摄像头权限加载失败:', error)
      cameraPermissionGranted = false
      return false
    } finally {
      // 清理Promise引用
      preloadPromise = null
    }
  })()
  
  return preloadPromise
}

/**
 * 智能启动摄像头
 * 从最高配置开始尝试，获取不到自动降级到下一配置
 * @param {object} customOptions 自定义选项
 * @returns {Promise<MediaStream>} 摄像头流
 */
async function startCamera(customOptions = {}) {
  const configs = ['HD', 'MEDIUM', 'LOW', 'PRELOAD']
  
  for (const configType of configs) {
    try {
      console.log(`尝试启动摄像头 (${configType})...`)
      
      // 其他配置使用对应配置
      const config = getCameraConfig(configType, customOptions)
      const stream = await navigator.mediaDevices.getUserMedia({ 
        video: config
      })
      console.log(`摄像头启动成功 (${configType})`)
      console.log(getCurrentCameraConfig(stream))
      return stream
    } catch (error) {
      console.log(`摄像头启动失败 (${configType}):`, error)
      
      // 如果是最后一个配置也失败了，则抛出错误
      if (configType === 'PRELOAD') {
        console.error('所有摄像头配置都启动失败')
        throw error
      }
      
      // 否则继续尝试下一个配置
      console.log(`尝试下一个配置...`)
    }
  }
}

/**
 * 分步升级摄像头质量：先用最低配置启动，逐步提升到目标配置
 * @param {string} type 目标配置类型（如 'HD'、'BLURAY'，默认 'MEDIUM'）
 * @param {object} customOptions 额外自定义配置
 * @returns {Promise<MediaStream>} 最终达到目标配置的摄像头流
 */
export async function startCameraWithUpgrade(type = 'MEDIUM', customOptions = {}) {
  // 配置升级顺序
  const configOrder = ['PRELOAD', 'LOW', 'MEDIUM', 'HD', 'BLURAY']
  // 目标配置在顺序中的索引
  const targetIndex = configOrder.indexOf(type)
  const finalIndex = targetIndex >= 0 ? targetIndex : configOrder.indexOf('MEDIUM')

  let stream = null
  let lastError = null

  // 1. 先用最低配置启动
  try {
    const preloadConfig = getCameraConfig('PRELOAD')
    stream = await navigator.mediaDevices.getUserMedia({ video: preloadConfig })
  } catch (e) {
    lastError = e
    // PRELOAD 都失败直接抛出
    throw e
  }

  // 2. 逐步升级到目标配置
  for (let i = 1; i <= finalIndex; i++) {
    const configType = configOrder[i]
    try {
      // 关闭上一个流
      if (stream) {
        stream.getTracks().forEach(track => track.stop())
      }
      // 合并自定义配置
      const config = getCameraConfig(configType, customOptions)
      stream = await navigator.mediaDevices.getUserMedia({ video: config })
    } catch (e) {
      // 升级失败就停在当前配置，返回当前流
      break
    }
  }
  return stream
}

/**
 * 启动全局摄像头
 * 在应用启动时调用，供整个应用使用
 */
export async function startGlobalCamera() {
  // 如果正在启动中，等待启动完成
  if (isStartingCamera) {
    console.log('摄像头正在启动中，等待启动完成...')
    // 等待启动完成，最多等待10秒
    let waitCount = 0
    while (isStartingCamera && waitCount < 100) {
      await new Promise(resolve => setTimeout(resolve, 100))
      waitCount++
    }
    
    // 如果等待超时，抛出错误
    if (isStartingCamera) {
      throw new Error('摄像头启动超时')
    }
    
    // 如果启动成功，返回现有的流
    if (globalCameraStream && globalCameraStream.active) {
      return globalCameraStream
    }
  }
  
  // 如果已经有活跃的全局摄像头，直接返回
  if (globalCameraStream && globalCameraStream.active) {
    console.log('全局摄像头已存在且活跃，直接返回')
    return globalCameraStream
  }
  
  // 设置启动状态
  isStartingCamera = true
  
  try {
    // 如果全局摄像头存在但已停止，先清理
    if (globalCameraStream) {
      console.log('清理已停止的全局摄像头')
      stopGlobalCamera()
    }
    
    // 预加载权限
    const preloadSuccess = await preloadCameraPermission()
    if (!preloadSuccess) {
      throw new Error('摄像头权限加载失败，请检查设备')
    }
    
    // 使用智能启动方法启动全局摄像头
    globalCameraStream = await startCamera()
    
    console.log('全局摄像头启动成功')
    return globalCameraStream
  } catch (error) {
    console.error('全局摄像头启动失败:', error)
    throw error
  } finally {
    // 无论成功失败，都要重置启动状态
    isStartingCamera = false
  }
}

/**
 * 停止全局摄像头
 */
export function stopGlobalCamera() {
  if (globalCameraStream) {
    globalCameraStream.getTracks().forEach(track => track.stop())
    globalCameraStream = null
  }
  // 重置启动状态
  isStartingCamera = false
}

/**
 * 获取摄像头错误信息
 * @param {Error} error 错误对象
 * @returns {string} 错误信息
 */
export function getCameraErrorMessage(error) {
  if (!error) return '未知错误'
  
  const errorMessages = {
    'NotAllowedError': '摄像头权限被拒绝，请允许访问摄像头',
    'NotFoundError': '未找到摄像头设备',
    'NotReadableError': '摄像头被其他应用占用',
    'OverconstrainedError': '摄像头配置不兼容',
    'TypeError': '摄像头参数错误',
    'AbortError': '摄像头启动被中断',
    'NotSupportedError': '浏览器不支持摄像头功能',
    'SecurityError': '摄像头访问被安全策略阻止'
  }
  
  return errorMessages[error.name] || error.message || '摄像头启动失败'
}

/**
 * 获取摄像头流的实际配置信息（分辨率、帧率等）
 * @param {MediaStream} stream 摄像头流
 * @returns {object|null} 配置信息，如 { width, height, frameRate, facingMode }
 */
export function getCurrentCameraConfig(stream) {
  if (!stream) return null
  const videoTrack = stream.getVideoTracks && stream.getVideoTracks()[0]
  if (!videoTrack) return null
  const settings = videoTrack.getSettings ? videoTrack.getSettings() : {}
  return {
    width: settings.width,
    height: settings.height,
    frameRate: settings.frameRate,
    facingMode: settings.facingMode
  }
}

/**
 * 使用示例：
 * 
 * // 导入所需函数
 * import { 
 *   getCameraConfig,
 *   startGlobalCamera,
 *   stopGlobalCamera,
 *   getCameraErrorMessage
 * } from './camera'
 * 
 * // 全局摄像头使用
 * const globalStream = await startGlobalCamera()
 * 
 * // 停止全局摄像头
 * stopGlobalCamera()
 * 
 * // 错误处理
 * try {
 *   const stream = await startGlobalCamera()
 * } catch (error) {
 *   const message = getCameraErrorMessage(error)
 *   ElMessage.error(message)
 * }
 */ 