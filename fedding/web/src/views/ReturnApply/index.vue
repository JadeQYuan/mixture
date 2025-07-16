<template>
  <div class="return-container">
    <CardGrid
      :data="records"
      :loading="loading"
      :page-loading="pageLoading"
      :display-fields="displayFields"
      :action-buttons="actionButtons"
      :header-buttons="headerButtons"
      :auto-refresh="true"
      :refresh-interval="5000"
      @refresh="handleRefresh"
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
import { submitReturnOperation, getTankWeightData, getReturnTankList } from '@/request/api'
import CardGrid from '@/components/CardGrid'
import { Dialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { displayFields, actionButtons, headerButtons } from './config'
import { getReturnOperationConfig } from './formConfig'

// 数据
const records = ref([])
const loading = ref(false)
const pageLoading = ref(false)

// 退料对话框配置
const returnDialogConfig = getReturnOperationConfig()

// 退料对话框
const returnDialog = reactive({
  visible: false,
  step: 0,
  index: null,
  loading: false,
  form: { id: null, tankId: null, tankNo: '', returnWeight: null }
})

// 定时器相关
const weightTimer = ref(null)
const currentTankId = ref(null)
const weightTimerActive = ref(false)

// 修改后的递归定时器逻辑
async function fetchWeightDataWithDelay() {
  if (!weightTimerActive.value) return;
  if (currentTankId.value) {
    try {
      const response = await getTankWeightData()
      if (response) {
        returnDialog.form.returnWeight = response
      }
    } catch (error) {
      ElMessage.error('获取重量数据失败')
    }
  }
  if (weightTimerActive.value) {
    weightTimer.value = setTimeout(fetchWeightDataWithDelay, 1000)
  }
}

function startWeightTimer() {
  stopWeightTimer()
  weightTimerActive.value = true
  fetchWeightDataWithDelay()
}

function stopWeightTimer() {
  weightTimerActive.value = false
  if (weightTimer.value) {
    clearTimeout(weightTimer.value)
    weightTimer.value = null
  }
}

// 事件处理函数
async function handleRefresh() {
  loading.value = true
  try {
    const response = await getReturnTankList()
    records.value = response || []
  } catch (error) {
    ElMessage.error('获取退料列表失败')
  } finally {
    loading.value = false
  }
}

// 页面初始化加载
async function initPage() {
  pageLoading.value = true
  try {
    await handleRefresh()
  } finally {
    pageLoading.value = false
  }
}

function handleAction({ action, row, index }) {
  if (action === 'return') {
    openReturnDialog(index)
  }
}

function handleHeaderAction({ action }) {
  // 移除导出功能处理
}

// 退料对话框相关函数
function openReturnDialog(index) {
  returnDialog.visible = true
  returnDialog.step = 0
  returnDialog.index = index
  if (index >= 0) {
    const record = records.value[index]
    returnDialog.form.id = record.id
    returnDialog.form.tankId = record.tankId
    returnDialog.form.tankNo = record.tankNo
    currentTankId.value = record.tankNo // 设置当前罐号
    // 启动定时器，获取当前重量
    startWeightTimer()
  } else {
    returnDialog.form.tankNo = ''
    currentTankId.value = null
  }
}

function handleNextStep(step) {
  if (step === 1) {
    // 第0步：检查当前重量是否已获取
    if (!returnDialog.form.returnWeight) {
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
  returnDialog.form = { id: null, tankId: null, tankNo: '', returnWeight: null }
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
    await handleRefresh() // 重新获取列表
  } catch (error) {
    ElMessage.error('退料操作失败')
  } finally {
    returnDialog.loading = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  initPage()
})

// 组件卸载时清理定时器
onUnmounted(() => {
  stopWeightTimer()
})
</script>

<style scoped>
.return-container {
  height: 100vh;
}
</style> 