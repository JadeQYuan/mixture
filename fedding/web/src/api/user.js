import http from '@/utils/http'

// 获取用户列表
export function getUserList(params) {
  return http.get('/service/user/list', { params })
}

// 新增用户
export function createUser(data) {
  return http.post('/service/user/add', data)
}

// 修改用户信息
export function updateUser(id, data) {
  return http.put(`/service/user/updateUserInfo`, data)
}

// 上传用户照片
export function updateUserPhoto(id, imageFile) {
  const formData = new FormData()
  formData.append('imageFile', imageFile)
  formData.append('id', id)
  return http.post(`/service/user/uploadPhoto`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 删除用户
export function deleteUser(id) {
  return http.delete(`/service/user/delete`, { data: { userId: id } })
}

// 修改密码
export function updateUserPassword(data) {
  return http.put('/service/user/updatePassword', data)
}

// 获取当前用户信息
export function getCurrentUser() {
  return http.get('/service/user/info')
} 