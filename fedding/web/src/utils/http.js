import axios from 'axios'
import CryptoJS from 'crypto-js'
import { ElMessage } from 'element-plus'

const http = axios.create({
  baseURL: import.meta.env.DEV ? 'http://localhost:8090' : '/api', // 开发环境直接请求，生产环境使用 /api
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
    // 登录接口不弹窗，交由页面处理
    if (err.response) {
      ElMessage.error(err.response.data.message || '请求出错')
    } else {
      ElMessage.error('网络错误')
    }
    return Promise.reject(err)
  }
)

// 密码加密工具方法（反序+两次base64）
export function encryptPassword(password) {
  // 第一步：反序
  let reversed = password.split('').reverse().join('');
  // 第二步：base64加密，去掉末尾=
  let firstBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(reversed)).replace(/=+$/, '');
  // 第三步：再base64加密，去掉末尾=
  let secondBase64 = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(firstBase64)).replace(/=+$/, '');
  return secondBase64;
}

// 移除接口实现，只保留http实例和拦截器
export default http 