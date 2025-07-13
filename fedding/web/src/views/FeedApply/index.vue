<template>
  <div class="feed-apply-container">
    <div class="feed-apply-card">
      <h2 class="page-title">加料申请</h2>
      <Form
        ref="formRef"
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
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { feedApply, getFeedTankList } from '../../request/api'
import Form from '@/components/Form'
import { getFeedApplyFormConfig } from './formConfig'

// 表单配置
const formConfig = getFeedApplyFormConfig()

// 表单数据
const form = reactive({
  bucketNo: '',
  spec: '',
  capacity: null
})

const loading = ref(false)
const formRef = ref()

// 加载料罐选项
async function loadTankOptions() {
  try {
    const res = await getFeedTankList()
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

// 重置表单
function resetForm() {
  // 清空表单数据
  Object.assign(form, {
    bucketNo: '',
    spec: '',
    capacity: null
  })
  
  // 重置表单验证状态
  nextTick(() => {
    formRef.value?.formRef?.clearValidate()
  })
}

// 提交处理
async function handleSubmit(formData) {
  if (loading.value) return
  
  try {
    loading.value = true
    const res = await feedApply(formData)
      ElMessage.success('申请已提交！')
      // 重置表单
      resetForm()
      // 重新查询料罐列表
      await loadTankOptions()
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

.page-title {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  margin-bottom: 24px;
  text-align: center;
}

@media (max-width: 600px) {
  .feed-apply-card {
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