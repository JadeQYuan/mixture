<template>
  <div class="logo-global">
    <img class="logo" src="/src/assets/logo.png" alt="logo" />
  </div>
</template>

<style scoped>
.logo-global {
  position: fixed;
  top: 50px;
  left: 0;
  z-index: 1000;
  width: 320px;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0;
  background: transparent;
  pointer-events: auto;
}
.logo {
  width: 185px;
  height: 170px;
  margin: 15px auto;
  display: block;
}
</style> 