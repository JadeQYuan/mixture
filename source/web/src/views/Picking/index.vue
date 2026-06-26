<template>
  <div class="picking-container">
    <CardGrid
      ref="card"
      :display-fields="displayFields"
      :header-buttons="headerButtons"
      :action-buttons="actionButtons"
      :header-render="headerRender"
      :request="getPickingTankList"
      :card-style="getCardStyle"
    />

    <!-- 领料提示对话框（需阻燃粉时先弹出） -->
    <ConfirmDialog
      :visible="flameTipDialog.visible"
      title="领料提示"
      :message="flameTipDialog.message"
      icon="Warning"
      icon-type="warning"
      confirm-text="确定"
      confirm-type="primary"
      @update:visible="val => flameTipDialog.visible = val"
      @confirm="openPickingStepDialog"
      @cancel="() => flameTipDialog.visible = false"
    />

    <!-- 领料操作步骤对话框 -->
    <Dialog
      :visible="pickingDialog.visible"
      :title="pickingDialogConfig.title"
      :width="pickingDialogConfig.width"
      @update:visible="closePickingDialog"
    >
      <Form
        ref="pickingForm"
        :fields="pickingDialogConfig.fields"
        :rules="pickingDialogConfig.rules"
        :form-data="pickingDialog.form"
        :extends="pickingDialog.step"
        :footer-buttons="footerButtons"
        style="padding-right: 30px;"
      >
        <template #form-top>
          <el-steps 
            v-if="pickingDialogConfig.steps && pickingDialogConfig.steps.length > 0" 
            :active="pickingDialog.step" 
            finish-status="success" 
            align-center 
            style="margin-bottom: 24px; margin-top: 32px;"
          >
            <el-step 
              v-for="step in pickingDialogConfig.steps" 
              :key="step.title" 
              :title="step.title" 
              :description="step.description"
            />
          </el-steps>
          <el-divider content-position="center" style="font-size: 136px;">
            <span style="font-size: 21px;font-weight: bold;color: #FF8C00;">需添加阻燃粉 {{pickingDialog.form.suggestWeight}} kg</span> 
          </el-divider>
        </template>
      </Form>
    </Dialog>

    <!-- 底罐重量确认对话框 -->
    <ConfirmDialog
      :visible="bottomConfirmDialog.visible"
      title="确认"
      message="底罐重量与满罐重量相差较大，确认继续？"
      icon="Warning"
      icon-type="danger"
      confirm-text="确认"
      confirm-type="primary"
      @update:visible="val => bottomConfirmDialog.visible = val"
      @confirm="confirmBottom"
      @cancel="() => bottomConfirmDialog.visible = false"
    />

    <!-- 阻燃粉比例确认对话框 -->
    <ConfirmDialog
      :visible="ratioConfirmDialog.visible"
      title="确认"
      :message="ratioConfirmDialog.message"
      icon="Warning"
      icon-type="danger"
      confirm-text="确认"
      confirm-type="primary"
      @update:visible="val => ratioConfirmDialog.visible = val"
      @confirm="confirmRatio"
      @cancel="() => ratioConfirmDialog.visible = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPickingTankList, submitPicking, getTankWeightData, getPickingConfig } from '@/api/mixture'
import CardGrid from '@/components/CardGrid'
import { Dialog, ConfirmDialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { displayFields, pickingDialogConfig } from './config'

const card = ref()
const pickingForm = ref()

const headerButtons = [
  {
    key: 'refresh',
    text: '刷新',
    type: 'primary',
    size: 'large',
    action: () => card.value.search()
  }
]

const actionButtons = [
  {
    key: 'picking',
    text: '领料',
    type: 'success',
    size: 'large',
    action: (row) => handlePicking(row)
  }
]

// 判断型号是否需要阻燃粉（不区分大小写）
function needFlameRetardant(productSpec) {
  return productSpec && productSpec.toUpperCase().includes('V')
}

// 卡片header渲染
function headerRender(item) {
  return `料罐：${item.tankNo}`
}

// 根据状态返回卡片样式
function getCardStyle(item) {
  const needFlame = needFlameRetardant(item.productSpec)
  if (item.status === 1) {
    return needFlame
      ? { backgroundColor: 'rgb(201, 231, 255)', borderLeft: '4px solid #FF8C00' }
      : { backgroundColor: 'rgb(217, 236, 255)' }
  } else if (item.status === 4) {
    return needFlame
      ? { backgroundColor: 'rgb(209, 237, 204)', borderLeft: '4px solid #FF8C00' }
      : { backgroundColor: 'rgb(225, 243, 216)' }
  }
  return {}
}

// 定时器相关
const weightTimer = ref(null)
const currentTankId = ref(null)
const weightTimerActive = ref(false)

async function fetchWeightDataWithDelay(callback) {
  if (!weightTimerActive.value) return
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

// 阻燃粉提示对话框
const flameTipDialog = reactive({
  visible: false,
  message: '',
  pendingRow: null
})

// 底罐重量确认对话框
const bottomConfirmDialog = reactive({
  visible: false
})

// 阻燃粉比例确认对话框
const ratioConfirmDialog = reactive({
  visible: false,
  message: ''
})

// 领料步骤对话框
const pickingDialog = reactive({
  visible: false,
  step: 0,
  loading: false,
  bottomThreshold: 5,
  ratioMin: 25,
  ratioMax: 35,
  form: {
    id: null, tankId: null, tankNo: '', shiftType: '', materialName: '', productSpec: '',
    planWeight: null, fullWeight: null, needFlameRetardant: false,
    pickingBottomWeight: null, pickingTotalWeight: null, flameRetardantWeight: 0
  }
})

const footerButtons = [
  {
    key: 'cancel',
    text: '取消',
    action: () => closePickingDialog()
  },
  {
    key: 'prev',
    text: '上一步',
    type: 'info',
    action: () => handlePrevStep(),
    visible: (step) => step > 0
  },
  {
    key: 'next',
    text: '下一步',
    type: 'primary',
    validate: true,
    action: () => handleNextStep(false),
    visible: (step) => step < 2
  },
  {
    key: 'submit',
    text: '提交',
    type: 'success',
    validate: true,
    loading: () => pickingDialog.loading,
    action: () => handlePickingSubmit(),
    visible: (step) => step === 2
  }
]

// 点击领料按钮
async function handlePicking(row) {
  const needFlame = needFlameRetardant(row.productSpec)
  if (needFlame) {
    // 需阻燃粉：先弹出提示对话框（只显示添加重量）
    const config = await getPickingConfig()
    const ratioTarget = ((config.flameRetardantRatioMin + config.flameRetardantRatioMax) / 2).toFixed(0)
    const actualWeight = row.fullWeight && row.bottomWeight ? (row.fullWeight - row.bottomWeight).toFixed(2) : 0
    const suggestWeight = actualWeight > 0 ? (actualWeight / ratioTarget).toFixed(2) : 0
    flameTipDialog.message = `需添加阻燃粉 ${suggestWeight} kg`
    flameTipDialog.pendingRow = row
    flameTipDialog.visible = true
  } else {
    // 不需阻燃粉：直接打开步骤对话框（仅确认信息）
    openPickingStepDialogWithRow(row)
  }
}

// 从提示对话框确认后打开步骤对话框
function openPickingStepDialog() {
  flameTipDialog.visible = false
  openPickingStepDialogWithRow(flameTipDialog.pendingRow)
}

// 打开领料步骤对话框
async function openPickingStepDialogWithRow(row) {
  const needFlame = needFlameRetardant(row.productSpec)
  // 获取阈值和比例配置
  const config = await getPickingConfig()
  const ratioTarget = ((config.flameRetardantRatioMin + config.flameRetardantRatioMax) / 2).toFixed(0)
  const actualWeight = row.fullWeight && row.bottomWeight ? (row.fullWeight - row.bottomWeight).toFixed(2) : 0
  const suggestWeight = actualWeight > 0 ? (actualWeight / ratioTarget).toFixed(2) : 0
  pickingDialog.visible = true
  pickingDialog.step = 0
  pickingDialog.form = {
    id: row.id,
    tankId: row.tankId,
    tankNo: row.tankNo,
    shiftType: row.shiftType,
    materialName: row.materialName,
    productSpec: row.productSpec,
    planWeight: row.planWeight,
    fullWeight: row.fullWeight ? row.fullWeight.toFixed(2) : null,
    needFlameRetardant: needFlame,
    pickingBottomWeight: null,
    pickingTotalWeight: null,
    flameRetardantWeight: 0,
    suggestWeight: suggestWeight  
  }
  currentTankId.value = row.tankNo
  pickingDialog.bottomThreshold = config.bottomThreshold
  pickingDialog.ratioMin = config.flameRetardantRatioMin
  pickingDialog.ratioMax = config.flameRetardantRatioMax
  // 需阻燃粉时，第一步启动底罐称重
  if (needFlame) {
    startWeightTimer(val => pickingDialog.form.pickingBottomWeight = val)
  }
}

function handleNextStep(confirmed) {
  const step = pickingDialog.step + 1
  if (step === 1) {
    // Step 0→1：底罐称重 → 阻燃粉称重
    if (!pickingDialog.form.pickingBottomWeight) {
      ElMessage.warning('正在获取称重数据，请稍候')
      return
    }
    // 校验：满罐与底罐重量差超出阈值，提示确认
    const fullWeight = parseFloat(pickingDialog.form.fullWeight)
    const bottomWeight = parseFloat(pickingDialog.form.pickingBottomWeight)
    if (fullWeight && bottomWeight && Math.abs(fullWeight - bottomWeight) > pickingDialog.bottomThreshold && !confirmed) {
      bottomConfirmDialog.visible = true
      return
    }
    stopWeightTimer()
    startWeightTimer(val => {
      pickingDialog.form.pickingTotalWeight = val
      pickingDialog.form.flameRetardantWeight = (val - pickingDialog.form.pickingBottomWeight).toFixed(2)
    })
    pickingDialog.step = step
  } else if (step === 2) {
    // Step 1→2：阻燃粉称重 → 信息确认
    if (!pickingDialog.form.pickingTotalWeight) {
      ElMessage.warning('正在获取称重数据，请稍候')
      return
    }
    // 校验：阻燃粉比例不在区间范围内，提示确认
    const flameWeight = parseFloat(pickingDialog.form.flameRetardantWeight)
    const fullWeight = parseFloat(pickingDialog.form.fullWeight)
    const bottomWeight = parseFloat(pickingDialog.form.pickingBottomWeight)
    const actualWeight = fullWeight - bottomWeight
    if (flameWeight > 0 && actualWeight > 0) {
      const ratio = actualWeight / flameWeight
      if ((ratio < pickingDialog.ratioMin || ratio > pickingDialog.ratioMax) && !confirmed) {
        ratioConfirmDialog.message = `阻燃粉重量不合格，建议添加重量：${suggestWeight} kg，确认继续？`
        ratioConfirmDialog.visible = true
        return
      }
    }
    stopWeightTimer()
    pickingDialog.step = step
  }
}

function handlePrevStep() {
  const step = pickingDialog.step - 1
  if (step === 0) {
    // 返回底罐称重步骤
    startWeightTimer(val => pickingDialog.form.pickingBottomWeight = val)
  } else if (step === 1) {
    // 返回阻燃粉称重步骤
    stopWeightTimer()
    startWeightTimer(val => {
      pickingDialog.form.pickingTotalWeight = val
      pickingDialog.form.flameRetardantWeight = (val - pickingDialog.form.pickingBottomWeight).toFixed(2)
    })
  }
  pickingDialog.step = step
}

function closePickingDialog() {
  pickingDialog.visible = false
  stopWeightTimer()
  pickingDialog.step = 0
  pickingForm.value?.resetFields?.()
}

async function handlePickingSubmit() {
  if (pickingDialog.loading) return
  try {
    pickingDialog.loading = true
    stopWeightTimer()
    await submitPicking(pickingDialog.form)
    ElMessage.success('领料成功！')
    pickingDialog.visible = false
    await card.value.search()
  } finally {
    setTimeout(() => { pickingDialog.loading = false }, 300)
  }
}

// 底罐重量确认
function confirmBottom() {
  bottomConfirmDialog.visible = false
  handleNextStep(true)
}

// 阻燃粉比例确认
function confirmRatio() {
  ratioConfirmDialog.visible = false
  handleNextStep(true)
}

onUnmounted(() => {
  stopWeightTimer()
})
</script>

<style scoped>
.picking-container {
}
</style>
