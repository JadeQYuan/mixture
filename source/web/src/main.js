import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { setupMessage } from './utils/message'
import { ElMessage } from 'element-plus'

// 启动应用
async function startApp() {

  const app = createApp(App)
  app.use(router)
  app.use(ElementPlus, {
    locale: zhCn,
  })
  app.use(store)
  
  // 设置消息管理
  setupMessage()
  
  // 全局异常处理
  app.config.errorHandler = (err, vm, info) => {
    // 控制台输出
    console.error('全局异常:', err, info)
    // 判断是否为 HTTP 异常（如 axios/fetch）
    if (
      (err && err.isAxiosError) ||
      (err && err.response && err.config)
    ) {
      // HTTP异常，交由拦截器处理，这里不弹窗
      return
    }
    // 其他异常弹窗
    ElMessage.error(err?.message || '发生未知错误')
  }
  
  app.mount('#app')
}

startApp()
