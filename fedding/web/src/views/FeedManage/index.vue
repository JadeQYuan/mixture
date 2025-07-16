<template>
  <div class="feed-manage-container">
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
    />

    <!-- 加料操作对话框 -->
    <Dialog
      :visible="feedDialog.visible"
      :title="feedDialogConfig.title"
      :width="feedDialogConfig.width"
      @update:visible="val => feedDialog.visible = val"
    >
      <Form
        :fields="feedDialogConfig.fields"
        :rules="feedDialogConfig.rules"
        :steps="feedDialogConfig.steps"
        :current-step="feedDialog.step"
        :form-data="feedDialog.form"
        :loading="feedDialog.loading"
        @submit="handleFeedSubmit"
        @next-step="handleNextStep"
        @prev-step="handlePrevStep"
        @cancel="closeFeedDialog"
      />
    </Dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedManageList, submitFeedOperation, getTankWeightData } from '@/request/api'
import DataTable from '@/components/DataTable'
import { Dialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { searchFields, columns, actionButtons } from './config'
import { getFeedOperationConfig } from './formConfig'

// 数据
const records = ref([])
const loading = ref(false)
const total = ref(0)

// 加料对话框配置
const feedDialogConfig = getFeedOperationConfig()

// 加料对话框
const feedDialog = reactive({
  visible: false,
  step: 0,
  index: null,
  loading: false,
  form: { id: null, tankId: null, tankNo: '', shiftType: '', materialName: '', productSpec: '', planWeight: null, bottomWeight: null, fullWeight: null, flameRetardantWeight: null, actualWeight: null }
})

// 定时器相关
const weightTimer = ref(null)
const currentTankId = ref(null)
const currentStep = ref(0)
const weightTimerActive = ref(false)

// 修改后的递归定时器逻辑
async function fetchWeightDataWithDelay(step) {
  if (!weightTimerActive.value) return;
  if (currentTankId.value) {
    try {
      const response = await getTankWeightData()
      if (response) {
        if (step === 0) {
          feedDialog.form.bottomWeight = response
        } else if (step === 1) {
          feedDialog.form.fullWeight = response
        }
      }
    } catch (error) {
      ElMessage.error('获取重量数据失败')
    }
  }
  if (weightTimerActive.value) {
    weightTimer.value = setTimeout(() => fetchWeightDataWithDelay(step), 1000)
  }
}

function startWeightTimer(step) {
  stopWeightTimer()
  currentStep.value = step
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

// 事件处理函数
async function handleSearch(params) {
  loading.value = true
  try {
    const response = await getFeedManageList(params)
    records.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取加料管理列表失败')
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
  if (action === 'feed') {
    openFeedDialog(index)
  }
}

function handleHeaderAction({ action }) {
  if (action === 'add') {
    ElMessage.success('新增功能待实现')
  }
}





// 加料对话框相关函数
function openFeedDialog(index) {
  feedDialog.visible = true
  feedDialog.step = 0
  currentStep.value = 0
  feedDialog.index = index
  if (index >= 0) {
    const record = records.value[index]
    feedDialog.form.id = record.id
    feedDialog.form.tankId = record.tankId
    feedDialog.form.tankNo = record.tankNo
    feedDialog.form.shiftType = record.shiftType
    feedDialog.form.materialName = record.materialName
    feedDialog.form.productSpec = record.productSpec
    feedDialog.form.planWeight = record.planWeight
    currentTankId.value = record.tankNo // 设置当前罐号
    // 启动第一步定时器，获取底罐重量
    startWeightTimer(0)
  } else {
    feedDialog.form.tankNo = ''
    feedDialog.form.shiftType = ''
    feedDialog.form.materialName = ''
    feedDialog.form.productSpec = ''
    feedDialog.form.planWeight = null
    currentTankId.value = null
  }
  feedDialog.form.bottomWeight = null
  feedDialog.form.fullWeight = null
  feedDialog.form.flameRetardantWeight = null
}

function handleNextStep(step) {
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
    if (!feedDialog.form.flameRetardantWeight) {
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
  currentStep.value = 0
}

async function handleFeedSubmit(formData) {
  if (feedDialog.loading) return // 防止重复提交
  
  try {
    feedDialog.loading = true
    // 确保清除定时器
    stopWeightTimer()
    currentStep.value = 0
    await submitFeedOperation(formData)
    ElMessage.success('加料数据已提交！')
    feedDialog.visible = false
    await handleSearch({ page: 1, pageSize: 10 }) // 重新获取列表
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    feedDialog.loading = false
  }
}

// 初始化
onMounted(() => {
  handleSearch({ page: 1, pageSize: 10 })
})

onUnmounted(() => {
  stopWeightTimer()
})
</script>

<style scoped>
.feed-manage-container {
  height: 100vh;
}
</style> 