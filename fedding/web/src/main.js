import './assets/main.css'
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createStore } from 'vuex'

const store = createStore({
  state() {
    return {
      userInfo: null
    }
  },
  mutations: {
    setUserInfo(state, userInfo) {
      state.userInfo = userInfo
    }
  },
  actions: {
    setUserInfo({ commit }, userInfo) {
      commit('setUserInfo', userInfo)
    }
  }
})

// 启动应用
async function startApp() {
  // 引入 MockJS（仅在开发环境）
  if (import.meta.env.DEV) {
    await import('./mock/index.js')
  }

  const app = createApp(App)
  app.use(router)
  app.use(ElementPlus)
  app.use(store)
  app.mount('#app')
}

startApp()
