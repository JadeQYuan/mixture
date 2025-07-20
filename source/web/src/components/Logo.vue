<template>
  <div class="logo-global">
    <img class="img-logo" src="/src/assets/img-logo.svg" alt="logo" />
    <img class="text-logo" src="/src/assets/text-logo.svg" alt="logo" />
  </div>
</template>

<style scoped>
.logo-global {
  position: fixed;
  top: 60px;
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
.img-logo {
  width: 72px;
  height: 72px;
  margin-bottom: 8px;
  display: block;
  margin-left: auto;
  margin-right: auto;
}
.text-logo {
  width: 260px;
  height: 72px;
  margin-bottom: 0;
  display: block;
  margin-left: auto;
  margin-right: auto;
}
@media (max-width: 900px) {
  .logo-global {
    width: 180px;
    height: 70px;
  }
  .img-logo {
    width: 40px;
    height: 40px;
  }
  .text-logo {
    width: 140px;
    height: 36px;
  }
}
@media (max-width: 600px) {
  .logo-global {
    width: 90px;
    height: 36px;
  }
  .img-logo {
    width: 20px;
    height: 20px;
  }
  .text-logo {
    width: 80px;
    height: 18px;
  }
}
</style> 