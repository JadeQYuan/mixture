import http from '@/utils/http'

// 密码登录
export function accountLogin(account, password) {
  return http.post('/api/login/account', { account, password })
}

// 人脸登录
export function faceLogin(imageFile) {
  const formData = new FormData()
  formData.append('imageFile', imageFile)
  return http.post('/api/login/face', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
} 