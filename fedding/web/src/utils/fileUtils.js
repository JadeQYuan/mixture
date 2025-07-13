/**
 * 文件工具类
 * 处理文件相关的转换和操作
 */

/**
 * 将base64字符串转换为File对象
 * @param {string} base64Data - base64字符串（不包含data:image/png;base64,前缀）
 * @param {string} filename - 文件名
 * @param {string} mimeType - MIME类型，默认为'image/png'
 * @returns {File} File对象
 */
export function base64ToFile(base64Data, filename = 'file.png', mimeType = 'image/png') {
  try {
    // 解码base64字符串
    const byteCharacters = atob(base64Data)
    const byteNumbers = new Array(byteCharacters.length)
    
    // 转换为字节数组
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i)
    }
    
    // 创建Uint8Array
    const byteArray = new Uint8Array(byteNumbers)
    
    // 创建Blob对象
    const blob = new Blob([byteArray], { type: mimeType })
    
    // 创建File对象
    const file = new File([blob], filename, { type: mimeType })
    
    return file
  } catch (error) {
    console.error('base64转File失败:', error)
    throw new Error('base64转File失败: ' + error.message)
  }
}

/**
 * 将File对象转换为base64字符串
 * @param {File} file - File对象
 * @returns {Promise<string>} base64字符串（不包含前缀）
 */
export function fileToBase64(file) {
  return new Promise((resolve, reject) => {
    try {
      const reader = new FileReader()
      
      reader.onload = () => {
        const result = reader.result
        // 移除data:image/png;base64,前缀
        const base64Data = result.split(',')[1]
        resolve(base64Data)
      }
      
      reader.onerror = () => {
        reject(new Error('文件读取失败'))
      }
      
      reader.readAsDataURL(file)
    } catch (error) {
      console.error('File转base64失败:', error)
      reject(new Error('File转base64失败: ' + error.message))
    }
  })
}

/**
 * 将base64字符串转换为Blob对象
 * @param {string} base64Data - base64字符串（不包含前缀）
 * @param {string} mimeType - MIME类型，默认为'image/png'
 * @returns {Blob} Blob对象
 */
export function base64ToBlob(base64Data, mimeType = 'image/png') {
  try {
    // 解码base64字符串
    const byteCharacters = atob(base64Data)
    const byteNumbers = new Array(byteCharacters.length)
    
    // 转换为字节数组
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i)
    }
    
    // 创建Uint8Array
    const byteArray = new Uint8Array(byteNumbers)
    
    // 创建Blob对象
    const blob = new Blob([byteArray], { type: mimeType })
    
    return blob
  } catch (error) {
    console.error('base64转Blob失败:', error)
    throw new Error('base64转Blob失败: ' + error.message)
  }
}

/**
 * 将Blob对象转换为base64字符串
 * @param {Blob} blob - Blob对象
 * @returns {Promise<string>} base64字符串（不包含前缀）
 */
export function blobToBase64(blob) {
  return new Promise((resolve, reject) => {
    try {
      const reader = new FileReader()
      
      reader.onload = () => {
        const result = reader.result
        // 移除data:image/png;base64,前缀
        const base64Data = result.split(',')[1]
        resolve(base64Data)
      }
      
      reader.onerror = () => {
        reject(new Error('Blob读取失败'))
      }
      
      reader.readAsDataURL(blob)
    } catch (error) {
      console.error('Blob转base64失败:', error)
      reject(new Error('Blob转base64失败: ' + error.message))
    }
  })
}

/**
 * 将canvas转换为base64字符串
 * @param {HTMLCanvasElement} canvas - Canvas元素
 * @param {string} format - 图片格式，默认为'image/png'
 * @param {number} quality - 图片质量（0-1），仅对JPEG有效
 * @returns {string} base64字符串（不包含前缀）
 */
export function canvasToBase64(canvas, format = 'image/png', quality = 0.8) {
  try {
    const dataURL = canvas.toDataURL(format, quality)
    // 移除data:image/png;base64,前缀
    const base64Data = dataURL.split(',')[1]
    return base64Data
  } catch (error) {
    console.error('Canvas转base64失败:', error)
    throw new Error('Canvas转base64失败: ' + error.message)
  }
}

/**
 * 将base64字符串转换为canvas
 * @param {string} base64Data - base64字符串（不包含前缀）
 * @param {string} mimeType - MIME类型，默认为'image/png'
 * @returns {HTMLCanvasElement} Canvas元素
 */
export function base64ToCanvas(base64Data, mimeType = 'image/png') {
  try {
    // 创建Image对象
    const img = new Image()
    
    return new Promise((resolve, reject) => {
      img.onload = () => {
        // 创建canvas
        const canvas = document.createElement('canvas')
        const ctx = canvas.getContext('2d')
        
        // 设置canvas尺寸
        canvas.width = img.width
        canvas.height = img.height
        
        // 绘制图片
        ctx.drawImage(img, 0, 0)
        
        resolve(canvas)
      }
      
      img.onerror = () => {
        reject(new Error('图片加载失败'))
      }
      
      // 设置图片源
      img.src = `data:${mimeType};base64,${base64Data}`
    })
  } catch (error) {
    console.error('base64转Canvas失败:', error)
    throw new Error('base64转Canvas失败: ' + error.message)
  }
}

/**
 * 获取文件的MIME类型
 * @param {string} filename - 文件名
 * @returns {string} MIME类型
 */
export function getMimeType(filename) {
  const ext = filename.split('.').pop().toLowerCase()
  const mimeTypes = {
    'png': 'image/png',
    'jpg': 'image/jpeg',
    'jpeg': 'image/jpeg',
    'gif': 'image/gif',
    'webp': 'image/webp',
    'bmp': 'image/bmp',
    'svg': 'image/svg+xml'
  }
  return mimeTypes[ext] || 'application/octet-stream'
}

/**
 * 生成随机文件名
 * @param {string} extension - 文件扩展名，默认为'png'
 * @returns {string} 随机文件名
 */
export function generateRandomFilename(extension = 'png') {
  const timestamp = Date.now()
  const random = Math.random().toString(36).substring(2, 8)
  return `file_${timestamp}_${random}.${extension}`
}

/**
 * 使用示例：
 * 
 * // base64转File
 * const file = base64ToFile(base64Data, 'photo.png', 'image/png')
 * 
 * // File转base64
 * const base64 = await fileToBase64(file)
 * 
 * // Canvas转base64
 * const base64 = canvasToBase64(canvas, 'image/png', 0.8)
 * 
 * // base64转Canvas
 * const canvas = await base64ToCanvas(base64Data, 'image/png')
 * 
 * // 获取MIME类型
 * const mimeType = getMimeType('photo.png') // 'image/png'
 * 
 * // 生成随机文件名
 * const filename = generateRandomFilename('png') // 'file_1234567890_abc123.png'
 */ 