<template>
  <div class="return-manage-container">
    <DataTable
      :data="records"
      :total="total"
      :loading="loading"
      :columns="columns"
      :search-fields="searchFields"
      :action-buttons="actionButtons"
      :header-buttons="headerButtons"
      @search="handleSearch"
      @reset="handleReset"
      @page-change="handlePageChange"
      @size-change="handleSizeChange"
      @action="handleAction"
      @header-action="handleHeaderAction"
    />

    <!-- 退料操作对话框 -->
    <Dialog
      :visible="returnDialog.visible"
      :title="returnDialogConfig.title"
      :width="returnDialogConfig.width"
      @update:visible="val => returnDialog.visible = val"
    >
      <Form
        :fields="returnDialogConfig.fields"
        :rules="returnDialogConfig.rules"
        :steps="returnDialogConfig.steps"
        :current-step="returnDialog.step"
        :form-data="returnDialog.form"
        :loading="returnDialog.loading"
        @submit="handleReturnSubmit"
        @next-step="handleNextStep"
        @prev-step="handlePrevStep"
        @cancel="closeReturnDialog"
      />
    </Dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReturnManageList, submitReturnOperation, getTankWeightData } from '@/request/api'
import DataTable from '@/components/DataTable'
import { Dialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { searchFields, columns, actionButtons, headerButtons } from './config'
import { getReturnOperationConfig } from './formConfig'

// 数据
const records = ref([])
const loading = ref(false)
const total = ref(0)

// 退料对话框配置
const returnDialogConfig = getReturnOperationConfig()

// 退料对话框
const returnDialog = reactive({
  visible: false,
  step: 0,
  index: null,
  loading: false,
  form: { bucketNo: '', currentWeight: null, returnWeight: null }
})

// 定时器相关
const weightTimer = ref(null)
const currentTankId = ref(null)

// 获取重量数据的函数
async function fetchWeightData() {
  if (currentTankId.value) {
    try {
      const response = await getTankWeightData()
      if (response.data) {
        returnDialog.form.capacity = response.data
      }
    } catch (error) {
      ElMessage.error('获取重量数据失败')
    }
  }
}

// 启动重量数据定时器
function startWeightTimer() {
  stopWeightTimer() // 先停止之前的定时器
  
  // 立即获取一次数据
  fetchWeightData()
  
  // 启动定时器，每秒获取一次
  weightTimer.value = setInterval(() => {
    fetchWeightData()
  }, 1000)
}

// 停止重量数据定时器
function stopWeightTimer() {
  if (weightTimer.value) {
    clearInterval(weightTimer.value)
    weightTimer.value = null
  }
}

// 事件处理函数
async function handleSearch(params) {
  loading.value = true
  try {
    const response = await getReturnManageList(params)
    records.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取退料管理列表失败')
  } finally {
    loading.value = false
  }
}

async function handleReset(params) {
  await handleSearch(params)
}

async function handlePageChange(params) {
  await handleSearch(params)
}

async function handleSizeChange(params) {
  await handleSearch(params)
}

function handleAction({ action, row, index }) {
  if (action === 'return') {
    openReturnDialog(index)
  }
}

function handleHeaderAction({ action }) {
  if (action === 'export') {
    ElMessage.success('导出功能待实现')
  }
}

// 退料对话框相关函数
function openReturnDialog(index) {
  returnDialog.visible = true
  returnDialog.step = 0
  returnDialog.index = index
  if (index >= 0) {
    const record = records.value[index]
    returnDialog.form.bucketNo = record.bucketNo
    currentTankId.value = record.bucketNo // 设置当前罐号
    // 启动定时器，获取当前重量
    startWeightTimer()
  } else {
    returnDialog.form.bucketNo = ''
    currentTankId.value = null
  }
  returnDialog.form.capacity = null
}

function handleNextStep(step) {
  if (step === 1) {
    // 第0步：检查当前重量是否已获取
    if (!returnDialog.form.capacity) {
      ElMessage.warning('正在获取当前重量数据，请稍候')
    } else {
      // 当前重量确定，进入第二步
      stopWeightTimer()
      returnDialog.step = step
    }
  } 
}

function handlePrevStep(step) {
  returnDialog.step = step
  // 如果返回到需要获取数据的步骤，重新启动定时器
  if (step === 0) {
    startWeightTimer()
  }
}

function closeReturnDialog() {
  returnDialog.visible = false
  stopWeightTimer()
  returnDialog.step = 0
  returnDialog.form = { bucketNo: '', currentWeight: null, returnWeight: null }
}

async function handleReturnSubmit(formData) {
  if (returnDialog.loading) return // 防止重复提交
  
  try {
    returnDialog.loading = true
    // 确保清除定时器
    stopWeightTimer()
    await submitReturnOperation(formData)
    ElMessage.success('退料操作成功')
    closeReturnDialog()
    await handleSearch({ page: 1, pageSize: 10 }) // 重新获取列表
  } catch (error) {
    ElMessage.error('退料操作失败')
  } finally {
    returnDialog.loading = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  handleSearch({ page: 1, pageSize: 10 })
})

// 组件卸载时清理定时器
onUnmounted(() => {
  stopWeightTimer()
})
</script>

<style scoped>
.return-manage-container {
  height: 100vh;
}
</style> 