import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 引入 MockJS（仅在开发环境）
if (import.meta.env.DEV) {
  import('./mock')
}

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
