<template>
  <div class="feed-apply-container">
    <div class="feed-apply-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" class="feed-form">
        <el-form-item label="料罐" prop="tank">
          <el-select v-model="form.tank" placeholder="请选择料罐" size="large">
            <el-option v-for="tank in tankOptions" :key="tank" :label="tank" :value="tank" />
          </el-select>
        </el-form-item>
        <el-form-item label="加料规格" prop="spec">
          <el-select v-model="form.spec" placeholder="请选择加料规格" size="large">
            <el-option v-for="spec in specOptions" :key="spec" :label="spec" :value="spec" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划加料重量" prop="weight">
          <el-input-number v-model="form.weight" :min="0.01" :precision="2" placeholder="请输入重量" size="large" style="width: 100%;" />
        </el-form-item>
        <el-form-item class="form-btn-item">
          <el-button type="primary" size="large" class="submit-btn" @click="submit">提交申请</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { feedApply } from '../request/api'

const tankOptions = ['T001', 'T002', 'T003'] // 可根据实际料罐动态获取
const specOptions = ['10kg', '20kg', '50kg', '100kg']

const formRef = ref()
const form = reactive({
  tank: '',
  spec: '',
  weight: null
})
const rules = {
  tank: [ { required: true, message: '请选择料罐', trigger: 'change' } ],
  spec: [ { required: true, message: '请选择加料规格', trigger: 'change' } ],
  weight: [ { required: true, message: '请输入计划加料重量', trigger: 'blur' } ]
}

function submit() {
  formRef.value.validate(async valid => {
    if (valid) {
      try {
        const res = await feedApply(form)
        if (res.code === 0) {
          ElMessage.success('申请已提交！')
        } else {
          ElMessage.error(res.message || '提交失败')
        }
      } catch (e) {
        // 错误已由http拦截器处理
      }
    }
  })
}
</script>

<style scoped>
.feed-apply-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
}
.feed-apply-card {
  background: rgba(255,255,255,0.7);
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  padding: 64px 56px 48px 56px;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 480px;
}
.feed-form {
  width: 100%;
  font-size: 2.6em;
}
.el-form-item {
  margin-bottom: 36px;
}
.el-form-item__label {
  font-size: 1.5em;
}
.form-btn-item {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  margin-bottom: 0;
}
.submit-btn {
  min-width: 220px;
  font-size: 2em;
  padding: 24px 0;
  border-radius: 12px;
  letter-spacing: 2px;
}
.el-input__wrapper,
.el-select__wrapper {
  font-size: 2em;
  padding: 18px 20px;
}

/* 表单组件宽度 */
:deep(.el-input) {
  width: 85% !important;
}

:deep(.el-select) {
  width: 85% !important;
}

:deep(.el-input-number) {
  width: 85% !important;
}

/* 文本域宽度 */
:deep(.el-textarea) {
  width: 85% !important;
}
@media (max-width: 600px) {
  .feed-apply-card {
    min-width: 0;
    width: 96vw;
    padding: 32px 8vw 24px 8vw;
  }
  .feed-form {
    font-size: 1.2em;
  }
  .submit-btn {
    font-size: 1em;
    padding: 12px 0;
    min-width: 120px;
  }
}
</style> 