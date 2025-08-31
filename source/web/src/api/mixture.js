import http from '@/utils/http'

// 获取加料管理列表
export function getFeedManageList(params) {
  return http.get('/api/mixture/list', { params })
}

// 底罐重量
export function getBottom(params) {
  return http.get('/api/mixture/bottom', { params })
}

// 保存底罐重量
export function saveBottomTankWeight(data) {
  return http.post('/api/mixture/bottom', data)
}

// 执行加料操作
export function submitFeedOperation(data) {
  return http.post('/api/mixture/feed', data)
}

// 执行备料操作
export function submitPrepare(data) {
  return http.post('/api/mixture/prepare', data)
}

// 执行领料操作
export function submitPicking(data) {
  return http.post('/api/mixture/picking', data)
}

// 执行退料操作
export function submitReturnOperation(data) {
  return http.post('/api/mixture/return', data)
}

// 获取底罐重量和加料重量
export function getTankWeightData() {
  return http.get(`/api/mixture/weight`)
}

// 获取领料记录列表
export function getTodoList() {
  return http.get('/api/mixture/todo')
}

// 获取领料记录列表
export function getFeedRecordList(params) {
  return http.get('/api/mixture/record', { params })
}

// 获取领料记录列表
export function getFeedStatsList(params) {
  return http.get('/api/mixture/stats', { params })
}

// 加料申请
export function feedApply(data) {
  return http.post('/api/mixture/apply', data)
}

// 获取退料罐列表
export function getReturnTankList() {
  return http.get('/api/mixture/return')
} 

// 保存备注
export function saveFeedRemark(data) {
  return http.post('/api/mixture/remark', data)
} 

// 获取加料申请用的料罐列表
export function getPickingTankList() {
  return http.get('/api/mixture/picking')
} 

// 获取加料申请用的料罐列表
export function cancelApply() {
  return http.post('/api/mixture/cancel')
}

// 获取加料阈值
export function getFeedThreshold() {
  return http.get('/api/mixture/feedThreshold')
} 

// 获取底罐阈值
export function getBottomThreshold() {
  return http.get('/api/mixture/bottomThreshold')
} 