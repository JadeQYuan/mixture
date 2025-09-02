import http from '@/utils/http'

// 获取检查列表
export function getCheckList(params) {
  return http.get('/api/check/list', { params })
}

// 处理检查记录
export function processCheck(data) {
  return http.put('/api/check/process', data)
}
