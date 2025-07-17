<template>
  <div class="feed-apply-container">
    <CardGrid
      :data="records"
      :loading="loading"
      :page-loading="pageLoading"
      :display-fields="displayFields"
      :action-buttons="actionButtons"
      :header-buttons="headerButtons"
      :auto-refresh="autoRefresh"
      :header-render="item => ('料罐：' + item.tankNo)"
      @refresh="handleRefresh"
      @action="handleAction"
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
import { feedApply } from '@/api/mixes'
import { getApplyTankList } from '@/api/tank'
import CardGrid from '@/components/CardGrid'
import { Dialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { displayFields, actionButtons, headerButtons, getFeedApplyFormConfig } from './config'

// 数据
const records = ref([])
const loading = ref(false)
const pageLoading = ref(false)

// 自动刷新配置
const autoRefresh = ref(true)

// 加料申请对话框配置
const applyDialogConfig = getFeedApplyFormConfig()

// 加料申请对话框
const applyDialog = reactive({
  visible: false,
  loading: false,
  form: { tankId: null, tankNo: '', shiftType: '', materialName: '', productSpec: '', planWeight: null }
})

// 事件处理函数
async function handleRefresh() {
  loading.value = true
  try {
    const response = await getApplyTankList()
    records.value = response || []
  } finally {
    loading.value = false
  }
}

// 页面初始化加载
async function initPage() {
  pageLoading.value = true
  try {
    await handleRefresh()
  } finally {
    pageLoading.value = false
  }
}

function handleAction({ action, row, index }) {
  if (action === 'apply') {
    openApplyDialog(row)
  }
}

// 加料申请对话框相关函数
function openApplyDialog(row) {
  autoRefresh.value = false // 打开弹窗时关闭自动刷新
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
  autoRefresh.value = true // 关闭弹窗时恢复自动刷新
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
    await handleRefresh() // 重新获取列表
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

// 页面加载时获取数据
onMounted(() => {
  initPage()
})
</script>

<style scoped>
.feed-apply-container {
}
</style> 