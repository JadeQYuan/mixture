import http from '@/utils/http'

// 用户管理接口
export function getUserList(params) {
  return http.get('/service/user/list', { params })
}

export function createUser(data) {
  return http.post('/service/user/add', data)
}

export function updateUser(id, data) {
  return http.put(`/service/user/updateUserInfo`, data)
}

export function updateUserPhoto(userId, imageFile) {
  const formData = new FormData()
  formData.append('imageFile', imageFile)
  formData.append('userId', userId)
  return http.post(`/service/user/uploadPhoto`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function deleteUser(id) {
  return http.delete(`/service/user/delete`, { data: { userId: id } })
}

// 修改密码接口
export function updateUserPassword(data) {
  return http.put('/service/user/updatePassword', data)
}

// 料罐管理接口
export function getTankList(params) {
  return http.get('/service/tank/list', { params })
}

export function createTank(data) {
  return http.post('/service/tank/add', data)
}

export function updateTank(data) {
  return http.put(`/service/tank/update`, data)
}

export function deleteTank(id) {
  return http.delete(`/service/tank/delete`, { data: { id } })
}

export function getAvailableTankList( ) {
  return http.get('/service/tank/available')
}

export function getMyTankList() {
  return http.get('/service/tank/my')
}

// 加料管理接口
export function getFeedManageList(params) {
  return http.get('/service/feeding/list', { params })
}

export function submitFeedOperation(data) {
  return http.post('/service/feeding/feed', data)
}

// 退料管理接口
export function getReturnManageList(params) {
  return http.get('/service/feeding/list', { params })
}

export function submitReturnOperation(data) {
  return http.post('/service/feeding/return', data)
}

// 获取底罐重量和加料重量接口
export function getTankWeightData() {
  return http.get(`/service/feeding/weight`)
}

// 领料记录接口
export function getFeedRecordList(params) {
  return http.get('/service/feeding/recordList', { params })
}

// 加料申请接口
export function feedApply(data) {
  return http.post('/service/feeding/apply', data)
}

// 退料申请接口
export function returnApply(data) {
  return http.post('/service/feeding/returnApply', data)
}

// 获取当前用户信息接口
export function getCurrentUser() {
  return http.get('/service/user/info')
}

// 密码登录接口
export function accountLogin(account, password) {
  return http.post('/service/login/account', { account, password })
}

// 人脸登录接口
export function faceLogin(imageFile) {
  const formData = new FormData()
  formData.append('imageFile', imageFile)
  return http.post('/service/face/faceLogin', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
} 