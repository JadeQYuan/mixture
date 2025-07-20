<script setup>
import Logo from './components/Logo.vue'
import { onMounted, onBeforeUnmount } from 'vue'
import { startGlobalCamera, stopGlobalCamera } from './utils/camera'

onMounted(() => {
  // 应用启动时预加载并启动全局摄像头
  startGlobalCamera()
})

onBeforeUnmount(() => {
  stopGlobalCamera()
})
</script>

<template>
  <Logo />
  <router-view />
</template>

<style scoped>
header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}

.app-layout {
  display: flex;
}
.main-content {
  width: 100%;
}
</style>
