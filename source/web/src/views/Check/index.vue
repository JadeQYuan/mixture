<template>
  <div class="check-container">
    <DataTable
      ref="table"
      :columns="columns"
      :search-fields="searchFields"
      :action-buttons="actionButtons"
      :header-buttons="headerButtons"
      :request="getCheckList"
      :rowStyle="rowStyle"
    />

    <!-- 数据检查对话框 -->
    <Dialog
      :visible="processDialog.visible"
      :title="processDialog.title"
      :width="dialogConfig.width"
      @update:visible="handleDialogVisibleUpdate"
    >
      <Form
        ref="processForm"
        :fields="dialogConfig.fields"
        :rules="dialogConfig.rules"
        :form-data="processDialog.form"
        :footer-buttons="dialogFormButtons"
        style="padding-right: 30px;"
      />
    </Dialog>

  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getCheckList, processCheck } from '@/api/check'
import DataTable from '@/components/DataTable'
import { Dialog, ConfirmDialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { searchFields, columns, dialogConfig } from './config'

const table = ref()
const processForm = ref()

const headerButtons = [
] 

function rowStyle({row, rowIndex}) {
  if (row.status == 1) {
    return {backgroundColor: 'rgb(250, 236, 216)'}
  } else if (row.status == 2) {
    return {backgroundColor: 'rgb(225, 243, 216)'}
  }
  return {backgroundColor: 'rgb(248, 152, 152)'}
}

const actionButtons = [
  {
    key: 'process',
    text: '处理',
    type: 'primary',
    size: 'large',
    action: (row) => openProcessDialog(row),
    disabled: (row) => row.status !== 0 // 已完成状态禁用处理
  }
]

// 对话框
const processDialog = reactive({
  visible: false,
  title: "数据处理",
  id: null,
  loading: false,
  form: { 
    id: null,
    tankNo: '', 
    returnWeight: '', 
    bottomWeight: '', 
    opinion: '', 
    status: '',
    adminOpinion: '',
    corrrectWeight: null
  }
})

const dialogFormButtons = [
  {
    key: 'cancel',
    text: '取消',
    action: () => closeProcessDialog()
  },
  {
    key: 'submit',
    text: '保存',
    type: 'primary',
    validate: true,
    loading: () => processDialog.loading,
    action: (formData) => handleDialogOk(formData)
  }
]

// 对话框相关函数
function openProcessDialog(row) {
  processDialog.visible = true
  processDialog.form.id = row.id
  processDialog.form.tankNo = row.tankNo
  processDialog.form.returnWeight = row.returnWeight
  processDialog.form.bottomWeight = row.bottomWeight
  processDialog.form.opinion = row.opinion
    // 清空校验状态
  processForm.value?.resetFields?.()
}

async function handleDialogOk(formData) {
  try {
    processDialog.loading = true
    await processCheck(formData)
    ElMessage.success('处理成功')
    closeProcessDialog()
    await table.value.search()
  } finally {
    setTimeout(() => {
      processDialog.loading = false
    }, 300)
  }
}

function closeProcessDialog() {
  processDialog.visible = false
  processForm.value?.resetFields?.()
}

function handleDialogVisibleUpdate(val) {
  if (!val) {
    closeProcessDialog()
  } else {
    processDialog.visible = true
  }
}

</script>

<style scoped>
.check-container {
}
</style>
