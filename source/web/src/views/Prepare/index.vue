<template>
  <div class="prepare-container">
    <CardGrid
      ref="card"
      :display-fields="displayFields"
      :headerButtons="headerButtons"
      :action-buttons="actionButtons"
      :request="getApplyTankList"
      :header-render="item => ('料罐：' + item.tankNo)"
    />

    <!-- 备料申请对话框 -->
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
        style="padding-right: 30px;"
      />
    </Dialog>

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
]

// 操作按钮配置
const actionButtons = [
  {
    key: 'prepare',
    text: '备料',
    type: 'warning',
    size: 'large',
    action: ( row ) => openApplyDialog(row)
  }
]

// 备料申请对话框
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

// 备料申请对话框相关函数
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
    ElMessage.success('备料申请已提交！')
    closeApplyDialog()
    await card.value.search() // 重新获取列表
  } finally {
    setTimeout(() => {
      applyDialog.loading = false
    }, 300)
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

</script>

<style scoped>
.prepare-container {
}
</style> 