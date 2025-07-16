import http from '@/utils/http'

// 获取料罐列表
export function getTankList(params) {
  return http.get('/service/tank/list', { params })
}

// 新增料罐
export function createTank(data) {
  return http.post('/service/tank/add', data)
}

// 更新料罐信息
export function updateTank(data) {
  return http.put(`/service/tank/update`, data)
}

// 删除料罐
export function deleteTank(id) {
  return http.delete(`/service/tank/delete`, { data: { id } })
}

// 获取加料申请用的料罐列表
export function getApplyTankList() {
  return http.get('/service/tank/apply')
} 