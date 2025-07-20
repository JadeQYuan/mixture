import http from '@/utils/http'

// 获取料罐列表
export function getTankList(params) {
  return http.get('/api/tank/list', { params })
}

// 新增料罐
export function createTank(data) {
  return http.post('/api/tank/add', data)
}

// 更新料罐信息
export function updateTank(data) {
  return http.put(`/api/tank/update`, data)
}

// 删除料罐
export function deleteTank(id) {
  return http.delete(`/api/tank/delete`, { data: { id } })
}

// 获取加料申请用的料罐列表
export function getApplyTankList() {
  return http.get('/api/tank/apply')
} 