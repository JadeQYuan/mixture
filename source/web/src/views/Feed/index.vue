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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedManageList, submitFeedOperation, getTankWeightData } from '@/api/mixture'
import DataTable from '@/components/DataTable'
import { Dialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { searchFields, columns, feedDialogConfig } from './config'

const table = ref()

const actionButtons = [
  {
    key: 'feed',
    text: '加料',
    type: 'primary',
    size: 'large',
    action: ( row ) => openFeedDialog(row)
  }
]

// 定时器相关
const weightTimer = ref(null)
const currentTankId = ref(null)
const weightTimerActive = ref(false)

// 修改后的递归定时器逻辑
async function fetchWeightDataWithDelay(step) {
  if (!weightTimerActive.value) return;
  if (currentTankId.value) {
    const response = await getTankWeightData()
    if (response) {
      if (step === 0) {
        feedDialog.form.bottomWeight = response
      } else if (step === 1) {
        feedDialog.form.fullWeight = response
      }
    }
  }
  if (weightTimerActive.value) {
    weightTimer.value = setTimeout(() => fetchWeightDataWithDelay(step), 1000)
  }
}

function startWeightTimer(step) {
  stopWeightTimer()
  weightTimerActive.value = true
  fetchWeightDataWithDelay(step)
}

function stopWeightTimer() {
  weightTimerActive.value = false
  if (weightTimer.value) {
    clearTimeout(weightTimer.value)
    weightTimer.value = null
  }
}

// 加料对话框
const feedDialog = reactive({
  visible: false,
  step: 0,
  loading: false,
  form: { id: null, tankId: null, tankNo: '', shiftType: '', materialName: '', productSpec: '', planWeight: null, bottomWeight: null, fullWeight: null, flameRetardantWeight: 0, actualWeight: null }
})

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
    visible: (step) => step < 3
  },
  {
    key: 'feed',
    text: '提交',
    type: 'success',
    loading: () => feedDialog.loading,
    action: ( row ) => handleFeedSubmit(row),
    visible: (step) => step === 3
  }
]

// 加料对话框相关函数
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
  currentTankId.value = row.tankNo // 设置当前罐号
  // 启动第一步定时器，获取底罐重量
  startWeightTimer(0)
  feedDialog.form.bottomWeight = null
  feedDialog.form.fullWeight = null
  feedDialog.form.flameRetardantWeight = 0
}

function handleNextStep() {
  const step = feedDialog.step + 1
  if (step === 1) {
    // 第0步：检查底罐重量是否已获取
    if (!feedDialog.form.bottomWeight) {
      ElMessage.warning('正在获取底罐重量数据，请稍候');
    } else {
      // 底罐重量确定，进入第二步
      stopWeightTimer()
      feedDialog.step = step
      // 启动第二步定时器，立即获取加料重量
      startWeightTimer(1)
    }
  } else if (step === 2) {
    if (!feedDialog.form.fullWeight) {
      ElMessage.warning('正在获取加料重量数据，请稍候');
    } else { 
      // 加料重量确定，进入第三步
      stopWeightTimer()
      feedDialog.step = step
      // 第三步不需要定时器，用户手动输入阻燃粉重量 
    }
  } else if (step === 3) {
    if (feedDialog.form.flameRetardantWeight === null || feedDialog.form.flameRetardantWeight === undefined || feedDialog.form.flameRetardantWeight < 0) {
      ElMessage.warning('请输入阻燃粉重量');
    } else {
      // 阻燃粉重量确定，进入第四步
      feedDialog.step = step
    }
  }
}

function handlePrevStep(step) {
  feedDialog.step = step
  // 如果返回到需要获取数据的步骤，重新启动对应步骤的定时器并立即获取数据
  if (step === 0) {
    // 返回第一步，立即启动底罐重量获取
    startWeightTimer(0)
  } else if (step === 1) {
    // 返回第二步，立即启动加料重量获取
    startWeightTimer(1)
  }
}

function closeFeedDialog() {
  feedDialog.visible = false
  stopWeightTimer()
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