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


export default store