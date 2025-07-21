<template>
  <div class="return-container">
    <CardGrid
      ref="card"
      :display-fields="displayFields"
      :header-buttons="headerButtons"
      :action-buttons="actionButtons"
      :header-render="item => ('料罐：' + item.tankNo)"
      :request="getReturnTankList"
    />

    <!-- 退料操作对话框 -->
    <Dialog
      :visible="returnDialog.visible"
      :title="returnDialogConfig.title"
      :width="returnDialogConfig.width"
      @update:visible="handleDialogVisibleUpdate"
    >
      <Form
        :fields="returnDialogConfig.fields"
        :rules="returnDialogConfig.rules"
        :steps="returnDialogConfig.steps"
        :current-step="returnDialog.step"
        :form-data="returnDialog.form"
        :loading="returnDialog.loading"
        :footer-buttons="returnDialogConfig.footerButtons"
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
import { submitReturnOperation, getTankWeightData, getReturnTankList } from '@/api/mixture'
import CardGrid from '@/components/CardGrid'
import { Dialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { displayFields, getReturnOperationConfig } from './config'

const card = ref()

const headerButtons = [
  {
    key: 'refresh',
    text: '刷新',
    type: 'primary',
    size: 'large',
    action: () => card.value.search()
  }
] 

// 操作按钮配置
const actionButtons = [
  {
    key: 'return',
    text: '退料',
    type: 'warning',
    size: 'large',
    action: ( row ) => openReturnDialog(row)
  }
]

// 定时器相关
const weightTimer = ref(null)
const currentTankId = ref(null)
const weightTimerActive = ref(false)

// 修改后的递归定时器逻辑
async function fetchWeightDataWithDelay() {
  if (!weightTimerActive.value) return;
  if (currentTankId.value) {
    const response = await getTankWeightData()
    if (response) {
      returnDialog.form.returnWeight = response
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

// 退料对话框配置
const returnDialogConfig = getReturnOperationConfig()

// 退料对话框
const returnDialog = reactive({
  visible: false,
  step: 0,
  loading: false,
  form: { id: null, tankId: null, tankNo: '', returnWeight: null }
})

// 退料对话框相关函数
function openReturnDialog(row) {
  returnDialog.visible = true
  returnDialog.step = 0
  returnDialog.form.id = row.id
  returnDialog.form.tankId = row.tankId
  returnDialog.form.tankNo = row.tankNo
  currentTankId.value = row.tankNo // 设置当前罐号
  // 启动定时器，获取当前重量
  startWeightTimer()
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
  card.value.search()
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
    await card.value.search() // 重新获取列表
  } finally {
    returnDialog.loading = false
  }
}

// 新增：Dialog关闭时处理
function handleDialogVisibleUpdate(val) {
  if (!val) {
    closeReturnDialog()
  } else {
    returnDialog.visible = true
  }
}

// 组件卸载时清理定时器
onUnmounted(() => {
  stopWeightTimer()
})
</script>

<style scoped>
.return-container {
}
</style> 