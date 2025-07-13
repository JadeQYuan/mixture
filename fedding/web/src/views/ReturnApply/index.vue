<template>
  <div class="return-apply-container">
    <div class="return-apply-card">
      <h2 class="page-title">退料申请</h2>
      <Form
        :fields="formConfig.fields"
        :rules="formConfig.rules"
        :form-data="form"
        :loading="loading"
        :label-width="formConfig.labelWidth"
        :label-position="formConfig.labelPosition"
        :footer-buttons="formConfig.footerButtons"
        @submit="handleSubmit"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { returnApply, getMyTankList } from '@/request/api'
import Form from '@/components/Form'
import { getReturnApplyFormConfig } from './formConfig'

// 表单配置
const formConfig = getReturnApplyFormConfig()

// 表单数据
const form = reactive({
  bucketNo: ''
})

const loading = ref(false)

// 加载料罐选项
async function loadTankOptions() {
  try {
    const res = await getMyTankList()
    const tankOptions = (res.data || []).map(item => ({
      label: item.bucketNo,
      value: item.id
    }))
    
    // 更新表单配置中的料罐选项
    const bucketField = formConfig.fields.find(field => field.prop === 'bucketNo')
    if (bucketField) {
      bucketField.options = tankOptions
    }
  } catch (error) {
    console.error('加载料罐选项失败:', error)
  }
}

// 提交处理
async function handleSubmit(formData) {
  if (loading.value) return
  
  try {
    loading.value = true
    const res = await returnApply(formData)
      ElMessage.success('退料申请已提交！')
      // 重置表单
      Object.assign(form, {
        bucketNo: ''
      })
  } catch (error) {
    // 错误已由http拦截器处理
  } finally {
    loading.value = false
  }
}

// 页面加载时获取料罐选项
onMounted(() => {
  loadTankOptions()
})
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

.page-title {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
  text-align: center;
}

@media (max-width: 600px) {
  .return-apply-card {
    min-width: 0;
    width: 96vw;
    padding: 32px 8vw 24px 8vw;
  }
  
  .page-title {
    font-size: 24px;
    margin-bottom: 20px;
  }
}
</style> 