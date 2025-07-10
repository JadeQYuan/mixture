import http from '../utils/http'

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

export function deleteUser(id) {
  return http.delete(`/service/user/delete`, { data: { userId: id } })
}

// 修改密码接口
export function updateUserPassword(data) {
  return http.put('/service/user/updatePassword', data)
}

// 料罐管理接口
export function getTankList(params) {
  return http.get('/service/bucket/addBucketList', { params })
}

export function createTank(data) {
  return http.post('/service/bucket/addBucket', data)
}

export function updateTank(id, data) {
  return http.put(`/service/bucket/updateBucket`, data)
}

export function deleteTank(id) {
  return http.delete(`/service/bucket/deleteBucket`, { data: { id } })
}

export function getFeedTankList() {
  return http.get('/service/bucket/list')
}

export function getMyTankList() {
  return http.get('/service/bucket/listMy')
}

// 加料管理接口
export function getFeedManageList(params) {
  return http.get('/service/bucket/bucketList', { params })
}

export function submitFeedOperation(data) {
  return http.post('/feed-operation', data)
}

// 获取底罐重量和加料重量接口
export function getTankWeightData(tankId) {
  return http.get(`/tank-weight/${tankId}`)
}

// 退料管理接口
export function getReturnManageList(params) {
  return http.get('/service/bucket/delBucketList', { params })
}

export function submitReturnOperation(data) {
  return http.post('/return-operation', data)
}

// 加料记录接口
export function getFeedRecordList(params) {
  return http.get('/feed-records', { params })
}

// 加料申请接口
export function feedApply(data) {
  return http.post('/service/bucket/bucketApplyAdd', data)
}

// 退料申请接口
export function returnApply(data) {
  return http.post('/service/bucket/bucketApplyDel', data)
}

// 获取当前用户信息接口
export function getCurrentUser() {
  return http.get('/service/user/info')
}

// 密码登录接口
export function accountLogin(account, password) {
  return http.post('/service/user/accountLogin', { account, password })
}

// 人脸登录接口
export function faceLogin(imageFile) {
  const formData = new FormData()
  formData.append('imageFile', imageFile)
  return http.post('/service/face/faceLogin', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
} 