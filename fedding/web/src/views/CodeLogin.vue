<template>
  <div class="code-login-container">
    <div class="code-login-card">
      <h2 class="login-title">密码登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="login-form" @keyup.enter="login">
        <el-form-item label="工号" prop="account">
          <el-input v-model="form.account" placeholder="请输入工号" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" @click="login" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-button class="back-btn" size="large" @click="goHome">返回</el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { accountLogin, getCurrentUser } from '../request/api'
import { useStore } from 'vuex'
import { encryptPassword } from '../utils/http'

const router = useRouter()
const store = useStore()
const formRef = ref()
const loading = ref(false) // 添加loading状态
const form = ref({
  account: '',
  password: ''
})
const rules = {
  account: [ { required: true, message: '请输入工号', trigger: 'blur' } ],
  password: [ { required: true, message: '请输入密码', trigger: 'blur' } ]
}

async function login() {
  formRef.value.validate(async valid => {
    if (!valid) return
    if (loading.value) return // 防止重复提交
    
    try {
      loading.value = true
      // 前端AES加密
      const encrypted = encryptPassword(form.value.password)
      const res = await accountLogin(form.value.account, encrypted)
      if (res.code === 200) {
        localStorage.setItem('token', res.data)
        // 登录成功后获取用户信息
        try {
          const userInfo = await getCurrentUser()
          store.dispatch('setUserInfo', userInfo.data)
        } catch (e) {
          // 可选：处理获取用户信息失败
        }
        ElMessage.success('登录成功')
        setTimeout(() => {
          router.push('/app')
        }, 800)
      } else {
        ElMessage.error(res.message || '登录失败')
        loading.value = false // 登录失败时重置loading状态
      }
    } catch (e) {
      // 错误已由http拦截器处理，不再重复提示
      loading.value = false // 发生错误时重置loading状态
    }
  })
}

const goHome = () => {
  router.push('/')
}
</script>

<style scoped>
.code-login-container {
  min-width: 100vw;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
}
.code-login-card {
  background: rgba(255,255,255,0.95);
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  padding: 64px 96px 48px 96px;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 540px;
}
.login-title {
  font-size: 3em;
  font-weight: bold;
  color: #1976d2;
  margin-bottom: 48px;
  letter-spacing: 2px;
  text-align: center;
}
.login-form {
  width: 100%;
  font-size: 1.5em;
}
.el-form-item {
  margin-bottom: 36px;
}
.el-input__wrapper {
  font-size: 1.3em;
  padding: 12px 16px;
}

/* 表单组件宽度 */
:deep(.el-input) {
  width: 85% !important;
}

/* 文本域宽度 */
:deep(.el-textarea) {
  width: 85% !important;
}
.login-btn {
  width: 100%;
  font-size: 1.5em;
  padding: 18px 0;
  border-radius: 12px;
  letter-spacing: 2px;
}
.back-btn {
  position: fixed;
  right: 40px;
  bottom: 40px;
  z-index: 1000;
  font-size: 1.2em;
  padding: 16px 32px;
  border-radius: 12px;
}
@media (max-width: 600px) {
  .code-login-card {
    min-width: 0;
    width: 96vw;
    padding: 32px 8vw 24px 8vw;
  }
  .login-title {
    font-size: 2em;
    margin-bottom: 24px;
  }
  .login-form {
    font-size: 1em;
  }
  .login-btn {
    font-size: 1em;
    padding: 12px 0;
  }
}
</style> 