import axios from 'axios'
import { ElMessage } from 'element-plus'

const http = axios.create({
  baseURL: import.meta.env.DEV ? 'http://localhost:8090' : '', // 开发环境直接请求，生产环境使用 /api
  timeout: 10000
})

// 请求拦截器，可加token
http.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `${token}`
  return config
}, err => Promise.reject(err))

// 响应拦截器，统一错误处理
http.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response) {
      ElMessage.error(err.response.data.message || '请求出错')
    } else {
      ElMessage.error('网络错误')
    }
    return Promise.reject(err)
  }
)

// 移除接口实现，只保留http实例和拦截器
export default http 