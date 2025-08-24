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

    <!-- 加料申请对话框 -->
    <Dialog
      :visible="applyDialog.visible"
      :title="applyDialogConfig.title"
      :width="applyDialogConfig.width"
      @update:visible="handleDialogVisibleUpdate"
    >
      <Form
        ref="applyForm"
        :fields="applyDialogConfig.fields"
        :rules="applyDialogConfig.rules"
        :form-data="applyDialog.form"
        :footer-buttons="applyDialogButtons"
      />
    </Dialog>

    <!-- 领料对话框 -->
    <ConfirmDialog
      :visible="pickingDialog.visible"
      title="确认领料"
      message="确定要领取该料罐吗？"
      icon="Warning"
      icon-type="danger"
      confirm-text="确认"
      confirm-type="primary"
      @update:visible="val => pickingDialog.visible = val"
      @confirm="confirmPicking"
      @cancel="val => pickingDialog.visible = val"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { feedApply } from '@/api/mixture'
import { getApplyTankList } from '@/api/tank'
import { submitPicking } from '@/api/mixture'
import CardGrid from '@/components/CardGrid'
import { Dialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { applyDialogConfig } from './config'
import ConfirmDialog from '@/components/Dialog/ConfirmDialog.vue'
import { dialogConfig } from '../TankManage/config'

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
  {
    prop: 'fullWeight',
    label: '满罐重量',
    visible: (row) => row.picking,
    render: (row) => row.fullWeight ? `${row.fullWeight} kg` : ''
  }
]

// 操作按钮配置
const actionButtons = [
  {
    key: 'primary',
    text: '申请',
    action: 'apply',
    size: 'large',
    visible: (row) => !row.picking,
    action: ( row ) => openApplyDialog(row)
  },
  {
    key: 'primary',
    text: '领料',
    action: 'picking',
    size: 'large',
    visible: (row) => row.picking,
    action: ( row ) => openPickingDialog(row)
  }
]

// 加料申请对话框
const applyDialog = reactive({
  visible: false,
  loading: false,
  form: { tankId: null, tankNo: '', shiftType: '', materialName: '', productSpec: '', planWeight: null }
})

const applyDialogButtons = [
  {
    key: 'cancel',
    text: '取消',
    action: () => closeApplyDialog()
  },
  {
    key: 'submit',
    text: '提交',
    type: 'primary',
    validate: true,
    loading: () => applyDialog.loading,
    action: (formData) => handleApplySubmit(formData)
  }
]

// 加料申请对话框相关函数
function openApplyDialog(row) {
  // autoRefresh.value = false // 打开弹窗时关闭自动刷新
  applyDialog.visible = true
  applyDialog.form = {
    tankId: row.id,
    tankNo: row.tankNo,
    shiftType: '',
    materialName: '',
    productSpec: '',
    planWeight: null
  }
}

function closeApplyDialog() {
  // autoRefresh.value = true // 关闭弹窗时恢复自动刷新
  applyDialog.visible = false
  applyDialog.form = { tankId: null, tankNo: '', shiftType: '', materialName: '', productSpec: '', planWeight: null }
  // 清空校验状态
  applyForm.value?.resetFields?.()
}

async function handleApplySubmit(formData) {
  if (applyDialog.loading) return // 防止重复提交
  
  try {
    applyDialog.loading = true
    await feedApply(formData)
    ElMessage.success('加料申请已提交！')
    closeApplyDialog()
    await card.value.search() // 重新获取列表
  } finally {
    applyDialog.loading = false
  }
}

// 新增：Dialog关闭时处理
function handleDialogVisibleUpdate(val) {
  if (!val) {
    closeApplyDialog()
  } else {
    applyDialog.visible = true
  }
}

// 领料对话框
const pickingDialog = reactive({
  visible: false,
  form: {id: null, tankId: null, tankNo: ''}
})

function openPickingDialog(row) {
  pickingDialog.visible = true
  pickingDialog.form.id = row.mixtureId
  pickingDialog.form.tankId = row.id
  pickingDialog.form.tankNo = row.tankNo
}

async function confirmPicking() {
  if (pickingDialog.loading) return // 防止重复提交
  try {
    pickingDialog.loading = true
    await submitPicking(pickingDialog.form)
    ElMessage.success('领料申请已提交！')
    await card.value.search() // 重新获取列表
  } finally {
    pickingDialog.loading = false
  }
}

</script>

<style scoped>
.feed-apply-container {
}
</style> 