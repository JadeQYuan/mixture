import axios from 'axios'

const http = axios.create({
  baseURL: import.meta.env.DEV ? '' : '/api', // 开发环境直接请求，生产环境使用 /api
  timeout: 10000
})

// 请求拦截器，可加token
http.interceptors.request.use(config => {
  // const token = localStorage.getItem('token')
  // if (token) config.headers.Authorization = `Bearer ${token}`
  return config
}, err => Promise.reject(err))

// 响应拦截器，统一错误处理
http.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response) {
      alert(err.response.data.message || '请求出错')
    } else {
      alert('网络错误')
    }
    return Promise.reject(err)
  }
)

export default http 