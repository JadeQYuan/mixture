<template>
  <div class="feed-manage-container">
    <DataTable
      ref="table"
      :columns="columns"
      :search-fields="searchFields"
      :action-buttons="actionButtons"
      :request="getFeedManageList"
    />

    <!-- 加料操作对话框 -->
    <Dialog
      :visible="feedDialog.visible"
      :title="feedDialogConfig.title"
      :width="feedDialogConfig.width"
      @update:visible="closeFeedDialog"
    >
      <Form
        ref="feedForm"
        :fields="feedDialogConfig.fields"
        :rules="feedDialogConfig.rules"
        :form-data="feedDialog.form"
        :extends="feedDialog.step"
        :footerButtons="footerButtons"
      >
        <template #form-top>
           <!-- 步骤条 -->
          <el-steps 
            v-if="feedDialogConfig.steps && feedDialogConfig.steps.length > 0" 
            :active="feedDialog.step" 
            finish-status="success" 
            align-center 
            style="margin-bottom: 24px; margin-top: 32px;"
          >
            <el-step 
              v-for="step in feedDialogConfig.steps" 
              :key="step.title" 
              :title="step.title" 
              :description="step.description"
            />
          </el-steps>
        </template>
      </Form>
    </Dialog>

    <!-- 底罐操作对话框 -->
    <Dialog
      :visible="bottomTankDialog.visible"
      :title="bottomTankConfig.title"
      :width="bottomTankConfig.width"
      @update:visible="closeBottomTankDialog"
    >
      <Form
        ref="bottomTankForm"
        :fields="bottomTankConfig.fields"
        :rules="bottomTankConfig.rules"
        :form-data="bottomTankDialog.form"
        :extends="bottomTankDialog.step"
        :footer-buttons="bottomTankButtons"
      >
        <template #form-top>
          <!-- 步骤条 -->
          <el-steps 
            :active="bottomTankDialog.step" 
            finish-status="success" 
            align-center 
            style="margin-bottom: 24px; margin-top: 32px;"
          >
            <el-step 
              v-for="step in bottomTankConfig.steps" 
              :key="step.title" 
              :title="step.title" 
              :description="step.description"
            />
          </el-steps>
        </template>
      </Form>
    </Dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedManageList, submitFeedOperation, getTankWeightData, saveBottomTankWeight } from '@/api/mixture'
import DataTable from '@/components/DataTable'
import { Dialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { searchFields, columns, feedDialogConfig, bottomTankConfig } from './config'

const table = ref()
const feedForm = ref()
const bottomTankForm = ref()

const actionButtons = [
  {
    key: 'bottomTank',
    text: '底罐',
    type: 'warning',
    size: 'large',
    action: ( row ) => openBottomTankDialog(row),
    visible: (row) => !row.bottomWeight // 没有底罐重量时显示
  },
  {
    key: 'feed',
    text: '加料',
    type: 'primary',
    size: 'large',
    action: ( row ) => openFeedDialog(row),
    visible: (row) => row.bottomWeight // 有底罐重量时显示
  }
]

// 定时器相关
const weightTimer = ref(null)
const currentTankId = ref(null)
const weightTimerActive = ref(false)

// 修改后的递归定时器逻辑
async function fetchWeightDataWithDelay(callback) {
  if (!weightTimerActive.value) return;
  if (currentTankId.value) {
    const response = await getTankWeightData()
    if (response !== null && response !== undefined) {
      callback(response)
    }
  }
  if (weightTimerActive.value) {
    weightTimer.value = setTimeout(() => fetchWeightDataWithDelay(callback), 1000)
  }
}

function startWeightTimer(callback) {
  stopWeightTimer()
  weightTimerActive.value = true
  fetchWeightDataWithDelay(callback)
}

function stopWeightTimer() {
  weightTimerActive.value = false
  if (weightTimer.value) {
    clearTimeout(weightTimer.value)
    weightTimer.value = null
  }
}

// 底罐操作对话框
const bottomTankDialog = reactive({
  visible: false,
  step: 0,
  loading: false,
  form: { id: null, tankId: null, tankNo: '', bottomWeight: null }
})

// 加料操作对话框
const feedDialog = reactive({
  visible: false,
  step: 0,
  loading: false,
  form: { id: null, tankId: null, tankNo: '', shiftType: '', materialName: '', productSpec: '', planWeight: null, bottomWeight: null, fullWeight: null, flameRetardantWeight: 0, actualWeight: null }
})

const bottomTankButtons = [
  {
    key: 'cancel',
    text: '取消',
    action: () => closeBottomTankDialog()
  },
  {
    key: 'prev',
    text: '上一步',
    type: 'info',
    action: () => handleBottomTankPrev(),
    visible: (step) => step > 0
  },
  {
    key: 'next',
    text: '下一步',
    type: 'primary',
    validate: true,
    action: () => handleBottomTankNext(),
    visible: (step) => step === 0
  },
  {
    key: 'confirm',
    text: '确认',
    type: 'success',
    loading: () => bottomTankDialog.loading,
    action: () => handleBottomTankSubmit(),
    visible: (step) => step === 1
  }
]

const footerButtons = [
  {
    key: 'cancel',
    text: '取消',
    action: ( row ) => closeFeedDialog(row)
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
    visible: (step) => step < 2
  },
  {
    key: 'feed',
    text: '提交',
    type: 'success',
    loading: () => feedDialog.loading,
    action: ( row ) => handleFeedSubmit(row),
    visible: (step) => step === 2
  }
]

// 底罐操作相关函数
function openBottomTankDialog(row) {
  bottomTankDialog.visible = true
  bottomTankDialog.step = 0
  bottomTankDialog.form.id = row.id
  bottomTankDialog.form.tankId = row.tankId
  bottomTankDialog.form.tankNo = row.tankNo
  currentTankId.value = row.tankNo // 设置当前罐号
  bottomTankDialog.form.bottomWeight = null
  // 启动定时器，获取底罐重量
  startWeightTimer(val => bottomTankDialog.form.bottomWeight = val)
}

function handleBottomTankPrev() {
  const step = bottomTankDialog.step - 1
  // 如果返回到需要获取数据的步骤，重新启动对应步骤的定时器并立即获取数据
  if (step === 0) {
    // 返回第一步，立即启动底罐重量获取
    startWeightTimer(val => bottomTankDialog.form.bottomWeight = val)
  }
  bottomTankDialog.step = step
}

function handleBottomTankNext() {
  // 检查底罐重量是否已获取
  if (!bottomTankDialog.form.bottomWeight) {
    ElMessage.warning('正在获取底罐重量数据，请稍候')
    return
  }
  // 底罐重量确定，进入确认步骤
  stopWeightTimer()
  bottomTankDialog.step = 1
}

async function handleBottomTankSubmit() {
  if (bottomTankDialog.loading) return
  
  try {
    bottomTankDialog.loading = true
    await saveBottomTankWeight(bottomTankDialog.form)
    ElMessage.success('底罐重量已保存！')
    closeBottomTankDialog()
    await table.value.search() // 重新获取列表
  } finally {
    bottomTankDialog.loading = false
  }
}

function closeBottomTankDialog() {
  bottomTankDialog.visible = false
  stopWeightTimer()
  bottomTankDialog.step = 0
  bottomTankDialog.form = { id: null, tankId: null, tankNo: '', bottomWeight: null }
  // 清空校验状态
  bottomTankForm.value?.resetFields?.()
}

// 加料操作相关函数
function openFeedDialog(row) {
  feedDialog.visible = true
  feedDialog.step = 0
  feedDialog.form.id = row.id
  feedDialog.form.tankId = row.tankId
  feedDialog.form.tankNo = row.tankNo
  feedDialog.form.shiftType = row.shiftType
  feedDialog.form.materialName = row.materialName
  feedDialog.form.productSpec = row.productSpec
  feedDialog.form.planWeight = row.planWeight
  feedDialog.form.bottomWeight = row.bottomWeight // 使用已有的底罐重量
  currentTankId.value = row.tankNo // 设置当前罐号
  feedDialog.form.fullWeight = null
  feedDialog.form.flameRetardantWeight = 0
  // 启动定时器，获取满罐重量
  startWeightTimer(val => feedDialog.form.fullWeight = val)
}

function handleNextStep() {
  const step = feedDialog.step + 1
  if (step === 1) {
    // 第0步：检查满罐重量是否已获取
    if (!feedDialog.form.fullWeight) {
      ElMessage.warning('正在获取满罐重量数据，请稍候');
    } else {
      // 满罐重量确定，进入第二步
      stopWeightTimer()
      feedDialog.step = step
    }
  } else if (step === 2) {
    if (feedDialog.form.flameRetardantWeight === null || feedDialog.form.flameRetardantWeight === undefined || feedDialog.form.flameRetardantWeight < 0) {
      ElMessage.warning('请输入阻燃粉重量');
    } else {
      // 阻燃粉重量确定，进入第三步
      feedDialog.step = step
    }
  }
}

function handlePrevStep() {
  const step = feedDialog.step - 1
  // 如果返回到需要获取数据的步骤，重新启动对应步骤的定时器并立即获取数据
  if (step === 0) {
    // 返回第一步，立即启动满罐重量获取
    startWeightTimer(val => feedDialog.form.fullWeight = val)
  }
  feedDialog.step = step
}

function closeFeedDialog() {
  feedDialog.visible = false
  stopWeightTimer()
  // 清空校验状态
  feedForm.value?.resetFields?.()
}

async function handleFeedSubmit(formData) {
  if (feedDialog.loading) return // 防止重复提交
  
  try {
    feedDialog.loading = true
    // 确保清除定时器
    stopWeightTimer()
    await submitFeedOperation(formData)
    ElMessage.success('加料数据已提交！')
    feedDialog.visible = false
    await table.value.search() // 重新获取列表
  } finally {
    feedDialog.loading = false
  }
}

onUnmounted(() => {
  stopWeightTimer()
})
</script>

<style scoped>
.feed-manage-container {
}
</style> 