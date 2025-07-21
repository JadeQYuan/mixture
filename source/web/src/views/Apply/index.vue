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
        :fields="applyDialogConfig.fields"
        :rules="applyDialogConfig.rules"
        :form-data="applyDialog.form"
        :loading="applyDialog.loading"
        :label-width="applyDialogConfig.labelWidth"
        :label-position="applyDialogConfig.labelPosition"
        :footer-buttons="applyDialogConfig.footerButtons"
        @submit="handleApplySubmit"
        @cancel="closeApplyDialog"
      />
    </Dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { feedApply } from '@/api/mixture'
import { getApplyTankList } from '@/api/tank'
import CardGrid from '@/components/CardGrid'
import { Dialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { displayFields, getFeedApplyFormConfig } from './config'

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
    key: 'primary',
    text: '申请',
    action: 'apply',
    size: 'large',
    action: ( row ) => openApplyDialog(row)
  }
]

// 加料申请对话框配置
const applyDialogConfig = getFeedApplyFormConfig()

// 加料申请对话框
const applyDialog = reactive({
  visible: false,
  loading: false,
  form: { tankId: null, tankNo: '', shiftType: '', materialName: '', productSpec: '', planWeight: null }
})

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
</script>

<style scoped>
.feed-apply-container {
}
</style> 