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
      title="退料"
      :visible="returnDialog.visible"
      @update:visible="handleDialogVisibleUpdate"
    >
      <Form
        ref="returnForm"
        :fields="returnDialogConfig.fileds"
        :rules="returnDialogConfig.rules"
        :form-data="returnDialog.form"
        :extends="returnDialog.step"
        :footer-buttons="footerButtons"
      >
        <template #form-top>
           <!-- 步骤条 -->
          <el-steps 
            v-if="returnDialogConfig.steps && returnDialogConfig.steps.length > 0" 
            :active="returnDialog.step" 
            finish-status="success" 
            align-center 
            style="margin-bottom: 24px; margin-top: 32px;"
          >
            <el-step 
              v-for="step in returnDialogConfig.steps" 
              :key="step.title" 
              :title="step.title" 
              :description="step.description"
            />
          </el-steps>
        </template>
      </Form>
    </Dialog>

    <!-- 撤销确认对话框 -->
    <ConfirmDialog
      :visible="cancelDialog.visible"
      title="撤销申请"
      message="确定要撤销该申请吗？"
      icon="Warning"
      icon-type="danger"
      confirm-text="确认"
      confirm-type="primary"
      @update:visible="val => cancelDialog.visible = val"
      @confirm="confirmCancel"
      @cancel="() => cancelDialog.visible = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { submitReturnOperation, getTankWeightData, getReturnTankList, cancelApply } from '@/api/mixture'
import CardGrid from '@/components/CardGrid'
import { Dialog, ConfirmDialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { displayFields, returnDialogConfig } from './config'

const card = ref()
const returnForm = ref()

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
    disabled: (row) => row.status !== 1,
    action: ( row ) => openReturnDialog(row)
  },
  {
    key: 'cancel',
    text: '撤销',
    type: 'danger',
    size: 'large',
    disabled: (row) => row.status !== 0 && row.status !== 3,
    action: ( row ) => openCancelDialog(row)
  }
]

const footerButtons = [
  {
    key: 'cancel',
    text: '取消',
    action: ( row ) => closeReturnDialog(row)
  },
  {
    key: 'prev',
    text: '上一步',
    type: 'info',
    action: ( row ) => handlePrevStep(row),
    visible: (step) => step > 0
  },
  {
    key: 'next',
    text: '下一步',
    type: 'primary',
    validate: true,
    action: ( row ) => handleNextStep(row),
    visible: (step) => step < 1
  },
  {
    key: 'feed',
    text: '提交',
    type: 'success',
    validate: true,
    loading: () => returnDialog.loading,
    action: ( row ) => handleReturnSubmit(row),
    visible: (step) => step === 1
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
    if (response !== null && response !== undefined) {
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

function handleNextStep() {
  const step = returnDialog.step + 1
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

function handlePrevStep() {
  const step = returnDialog.step - 1
  // 如果返回到需要获取数据的步骤，重新启动定时器
  if (step === 0) {
    startWeightTimer()
  }
  returnDialog.step = step
}

function closeReturnDialog() {
  card.value.search()
  returnDialog.visible = false
  stopWeightTimer()
  returnDialog.step = 0
  returnDialog.form = { id: null, tankId: null, tankNo: '', returnWeight: null }
  // 清空校验状态
  returnForm.value?.resetFields?.()
}

async function handleReturnSubmit(formData) {
  if (returnDialog.loading) return // 防止重复提交
  
  try {
    returnDialog.loading = true
    // 确保清除定时器
    stopWeightTimer()
    await submitReturnOperation(formData)
    ElMessage.success('退料成功! ')
    closeReturnDialog()
    await card.value.search() // 重新获取列表
  } finally {
    setTimeout(() => {
      returnDialog.loading = false
    }, 300)
  }
}

// Dialog关闭时处理
function handleDialogVisibleUpdate(val) {
  if (!val) {
    closeReturnDialog()
  } else {
    returnDialog.visible = true
  }
}

// 撤销对话框
const cancelDialog = reactive({
  visible: false,
  loading: false,
  form: {id: null, tankId: null, tankNo: ''}
})

function openCancelDialog(row) {
  cancelDialog.visible = true
  cancelDialog.form.id = row.id
  cancelDialog.form.tankId = row.tankId
  cancelDialog.form.tankNo = row.tankNo
}

async function confirmCancel() {
  if (cancelDialog.loading) return // 防止重复提交
  try {
    cancelDialog.loading = true
    await cancelApply(cancelDialog.form)
    ElMessage.success('加料申请已取消！')
    cancelDialog.visible = false 
    await card.value.search() // 重新获取列表
  } finally {
    setTimeout(() => {
      cancelDialog.loading = false
    }, 300)
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