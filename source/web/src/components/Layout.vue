<template>
  <div class="layout global-bg">
    <div class="sidebar-wrapper">
      <el-menu
        class="el-menu-vertical-demo"
        router
        background-color="#f5f5f5"
        text-color="#333"
        active-text-color="#1976d2"
        :default-active="activeMenu"
        v-if="currentRole"
      >
        <el-menu-item
          v-for="route in filteredMenuRoutes"
          :key="route.path"
          :index="'/app/' + route.path"
          class="custom-menu-item"
          @click="handleMenuClick(route)"
        >
          <span class="menu-item-label">{{ route.meta && route.meta.title ? route.meta.title : route.name }}</span>
        </el-menu-item>

      </el-menu>
      <div class="user-section">
        <div class="user-info-box">
          <img class="user-avatar" :src="userAvatar" alt="头像" />
          <div class="user-account">{{ userAccount }}</div>
          <div class="user-name">{{ userName }}</div>
        </div>
        <div class="logout-btn-box">
          <el-button color="rgba(140, 166, 191, 1)" size="large" @click="logout" style="width: 100%;">退出登录</el-button>
        </div>
      </div>
    </div>
    <div class="main-content">
      <div class="content-inner">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import activityMonitor from '../utils/activityMonitor'
import { useStore } from 'vuex'
import { getCurrentUser } from '@/api/user'
import defaultAvatar from '@/assets/avatar.svg'

const router = useRouter()
const route = useRoute()

const store = useStore()

const userInfo = computed(() => store.state.userInfo || {})
const userAvatar = computed(() => userInfo.value.facePath ? "data:image/png;base64," + userInfo.value.facePath :defaultAvatar)
const userAccount = computed(() => store.state.userInfo?.account || '')
const userName = computed(() => store.state.userInfo?.userName || '')
const currentRole = computed(() => userInfo.value.roleCode)

const filteredMenuRoutes = computed(() => {
  if (!currentRole.value) return []
  // 找到SidebarLayout一级路由
  const sidebarRoute = router.getRoutes().find(r => r.path === '/app')
  if (!sidebarRoute || !sidebarRoute.children) return []
  // 只显示有meta且有roles的子路由，且当前角色在roles中
  return sidebarRoute.children.filter(r => {
    return r.meta && r.meta.roles && r.meta.roles.includes(currentRole.value)
  })
})

const activeMenu = computed(() => route.path)

onMounted(async () => {
  // 如果有token，每次刷新自动获取用户信息
  if (localStorage.getItem('token')) {
    try {
      const userInfo = await getCurrentUser()
      store.dispatch('setUserInfo', userInfo)
    } catch (e) {}
  }
  
  // 如果当前正好在/app，自动跳转到第一个有权限的子路由
  if (route.path === '/app' && filteredMenuRoutes.value.length > 0) {
    router.replace('/app/' + filteredMenuRoutes.value[0].path)
  }
  
  // 启动活动监测
  activityMonitor.start(() => {
    // 2分钟无活动时的回调
    ElMessage.warning('由于长时间无操作，已自动退出登录')
    localStorage.clear()
    router.push('/')
  })
})

onUnmounted(() => {
  // 组件卸载时停止活动监测
  activityMonitor.stop()
})

function handleMenuClick(route) {
  router.push('/app/' + route.path)
}

function logout() {
  localStorage.clear()
  router.push('/')
}
</script>

<style scoped>
.global-bg {
  min-height: 100vh;
  min-width: 100vw;
}
.layout {
  display: flex;
  min-height: 100vh;
  width: 100vw;
  height: 100vh;
  font-size: 2.2em;
  background: transparent;
}
.sidebar-wrapper {
  display: flex;
  flex-direction: column;
  width: 320px;
  min-width: 200px;
  max-width: 400px;
  min-height: 100vh;
  background: #f5f5f5;
  font-size: 1.6em;
  padding-top: 180px;
  align-items: stretch;
}
.el-menu-vertical-demo {
  flex: 1;
  border-right: none;
  font-size: 2.2em;
  margin-top: 32px;
}
.custom-menu-item {
  display: flex !important;
  justify-content: center;
  align-items: center;
  height: 100px !important;
}
.menu-item-label {
  font-size: 2.4em;
  font-weight: bold;
  text-align: center;
  width: 100%;
  color: inherit;
}
.el-menu-vertical-demo .el-menu-item.is-active {
  background: #e3f0fd !important;
  color: #1976d2 !important;
}
.user-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: auto;
  margin-bottom: 0;
}
.user-info-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 5px 0 5px 0;
}
.user-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 16px;
  border: 2px solid #e0e0e0;
  background: #fff;
}
.user-account {
  font-size: 0.6em;
  color: #333;
  text-align: center;
  font-weight: bold;
}
.user-name {
  font-size: 0.6em;
  color: #333;
  text-align: center;
  font-weight: bold;
}
.logout-btn-box {
  display: flex;
  justify-content: center;
  background: #f5f5f5;
  width: 100%;
}
.logout-btn-box .el-button {
  font-size: 0.8em !important;
  height: 60px;
}
.main-content {
  flex: 1;
  background: transparent;
  font-size: 2.2em;
  min-width: 0;
  overflow-x: auto;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  height: 100vh !important;
  box-sizing: border-box;
}
.content-inner {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  min-height: 0;
  height: 100%;
}
.content-inner > * {
  flex: 1 1 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
.content-inner .el-card,
.content-inner .el-form,
.content-inner .el-table {
  flex: 1 1 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
}
@media (max-width: 900px) {
  .layout {
    flex-direction: column;
    font-size: 1.4em;
  }
  .sidebar-wrapper {
    flex-direction: row;
    width: 100vw;
    max-width: 100vw;
    min-width: 0;
    min-height: unset;
    height: auto;
    font-size: 1.1em;
  }
  .el-menu-vertical-demo {
    width: 100vw;
    min-width: 0;
    font-size: 1.5em;
    margin-top: 12px;
  }
  .custom-menu-item {
    height: 56px !important;
  }
  .main-content {
    font-size: 1.3em;
    height: auto;
  }
  .content-inner {
    height: auto;
  }
  .user-info-box {
    margin: 12px 0 6px 0;
  }
  .user-avatar {
    width: 70px;
    height: 70px;
  }
  .logout-btn-box {
    padding: 12px 0 10px 0;
  }
  .logout-btn-box .el-button {
    font-size: 1.1em !important;
    height: 40px;
  }
}
@media (max-width: 600px) {
  .layout {
    font-size: 1em;
  }
  .sidebar-wrapper {
    font-size: 0.95em;
    width: 100vw;
    max-width: 100vw;
  }
  .main-content {
    font-size: 1em;
  }
  .user-avatar {
    width: 40px;
    height: 40px;
  }
  .el-menu-vertical-demo {
    font-size: 1.1em;
  }
  .custom-menu-item {
    height: 40px !important;
  }
  .logout-btn-box .el-button {
    font-size: 1em !important;
    height: 32px;
  }
}
</style> 