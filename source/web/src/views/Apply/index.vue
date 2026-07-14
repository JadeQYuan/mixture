<template>
  <div class="feed-apply-container">
    <CardGrid
      ref="card"
      :display-fields="displayFields"
      :headerButtons="headerButtons"
      :action-buttons="actionButtons"
      :request="getApplyTankList"
      :header-render="item => ('料罐：' + item.tankNo)"
    />

    <!-- 加料/备料申请对话框 -->
    <Dialog
      :visible="dialog.visible"
      :title="currentDialogConfig.title"
      :width="currentDialogConfig.width"
      @update:visible="handleDialogVisibleUpdate"
    >
      <Form
        ref="applyForm"
        :fields="currentDialogConfig.fields"
        :rules="currentDialogConfig.rules"
        :form-data="dialog.form"
        :footer-buttons="dialogButtons"
        style="padding-right: 30px;"
      />
    </Dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { feedApply, submitPrepare } from '@/api/mixture'
import { getApplyTankList } from '@/api/tank'
import { COLOR_MAP } from '@/utils/constant'
import CardGrid from '@/components/CardGrid'
import { Dialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { applyDialogConfig, prepareDialogConfig } from './config'

const card = ref()
const applyForm = ref()

const headerButtons = [
  {
    key: 'refresh',
    text: '刷新',
    type: 'primary',
    size: 'large',
    action: () => card.value.search()
  }
]

// 卡片显示字段配置
const displayFields = [
]

// 操作按钮配置：加料申请 + 备料申请
const actionButtons = [
  {
    key: 'apply',
    text: '加料申请',
    color: COLOR_MAP.BTN_BLUE1,
    size: 'large',
    action: (row) => openDialog(row, 'apply')
  },
  {
    key: 'prepare',
    text: '备料申请',
    color: COLOR_MAP.BTN_GREEN1,
    size: 'large',
    action: (row) => openDialog(row, 'prepare')
  }
]

// 对话框状态
const dialog = reactive({
  visible: false,
  loading: false,
  mode: 'apply', // 'apply' 或 'prepare'
  form: { tankId: null, tankNo: '', shiftType: '', materialName: '', productSpec: '', planWeight: null }
})

const currentDialogConfig = computed(() => {
  return dialog.mode === 'prepare' ? prepareDialogConfig : applyDialogConfig
})

const dialogButtons = [
  {
    key: 'cancel',
    text: '取消',
    action: () => closeDialog()
  },
  {
    key: 'submit',
    text: '提交',
    type: 'primary',
    validate: true,
    loading: () => dialog.loading,
    action: (formData) => handleSubmit(formData)
  }
]

function openDialog(row, mode) {
  dialog.visible = true
  dialog.mode = mode
  dialog.form = {
    tankId: row.id,
    tankNo: row.tankNo,
    shiftType: '',
    materialName: '',
    productSpec: '',
    planWeight: null
  }
}

function closeDialog() {
  dialog.visible = false
  dialog.form = { tankId: null, tankNo: '', shiftType: '', materialName: '', productSpec: '', planWeight: null }
  applyForm.value?.resetFields?.()
}

async function handleSubmit(formData) {
  if (dialog.loading) return

  try {
    dialog.loading = true
    if (dialog.mode === 'prepare') {
      await submitPrepare(formData)
      ElMessage.success('备料申请已提交！')
    } else {
      await feedApply(formData)
      ElMessage.success('加料申请已提交！')
    }
    closeDialog()
    await card.value.search()
  } finally {
    setTimeout(() => {
      dialog.loading = false
    }, 300)
  }
}

function handleDialogVisibleUpdate(val) {
  if (!val) {
    closeDialog()
  } else {
    dialog.visible = true
  }
}
</script>

<style scoped>
.feed-apply-container {
}
</style>
