<template>
  <div class="return-apply-container">
    <div class="return-apply-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" class="return-form">
        <el-form-item label="料罐" prop="bucketNo">
          <el-select v-model="form.bucketNo" placeholder="请选择料罐" size="large">
            <el-option v-for="tank in tankOptions" :key="tank.value" :label="tank.label" :value="tank.value" />
          </el-select>
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
import { returnApply, getMyTankList } from '../request/api'
import { onMounted } from 'vue'

const tankOptions = ref([])

const formRef = ref()
const form = reactive({
  bucketNo: ''
})
const rules = {
  bucketNo: [ { required: true, message: '请选择料罐', trigger: 'change' } ]
}

onMounted(async () => {
  try {
    const res = await getMyTankList()
    tankOptions.value = (res.data || []).map(item => ({
      label: item.bucketNo,
      value: item.id
    }))
  } catch (e) {}
})

function submit() {
  formRef.value.validate(async valid => {
    if (valid) {
      try {
        const res = await returnApply(form)
        if (res.code === 0) {
          ElMessage.success('退料申请已提交！')
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
.return-apply-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
}
.return-apply-card {
  background: rgba(255,255,255,0.8);
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  padding: 64px 56px 48px 56px;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 480px;
}
.return-form {
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
  .return-apply-card {
    min-width: 0;
    width: 96vw;
    padding: 32px 8vw 24px 8vw;
  }
  .return-form {
    font-size: 1.2em;
  }
  .submit-btn {
    font-size: 1em;
    padding: 12px 0;
    min-width: 120px;
  }
}
</style> 