import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 启动应用
async function startApp() {
  // 引入 MockJS（仅在开发环境）
  if (import.meta.env.DEV) {
    await import('./mock/index.js')
  }

  const app = createApp(App)
  app.use(router)
  app.use(ElementPlus)
  app.mount('#app')
}

startApp()
