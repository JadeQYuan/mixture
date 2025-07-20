<template>
  <div class="tank-manage-container">
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
      @header-action="handleHeaderAction"
    />

    <!-- 料罐管理对话框 -->
    <Dialog
      :visible="dialog.visible"
      :title="dialog.config.title"
      :width="dialog.config.width"
      :header-config="dialog.config.headerConfig"
      @update:visible="val => dialog.visible = val"
    >
      <Form
        :fields="dialog.config.fields"
        :rules="dialog.config.rules"
        :form-data="dialog.form"
        :loading="dialog.loading"
        :footer-buttons="dialog.config.buttons"
        @submit="handleDialogOk"
        @cancel="dialog.visible = false"
        @footer-action="handleFooterAction"
      />
    </Dialog>

    <!-- 删除确认对话框 -->
    <ConfirmDialog
      :visible="deleteDialog.visible"
      title="确认删除"
      message="确定要删除该料罐吗？删除后无法恢复。"
      icon="Warning"
      icon-type="danger"
      confirm-text="删除"
      confirm-type="danger"
      @update:visible="val => deleteDialog.visible = val"
      @confirm="handleDeleteTank"
      @cancel="closeDeleteDialog"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTankList, createTank, updateTank, deleteTank } from '@/api/tank'
import DataTable from '@/components/DataTable'
import { Dialog, ConfirmDialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { searchFields, columns, actionButtons, headerButtons, getTankFormConfig } from './config'

// 数据
const records = ref([])
const loading = ref(false)
const total = ref(0)

// 对话框
const dialog = reactive({
  visible: false,
  mode: 'add', // add/edit
  index: null,
  loading: false,
  form: { tankNo: '', remark: '' },
  config: getTankFormConfig()
})

const deleteDialog = reactive({
  visible: false,
  index: null
})

// 事件处理函数
async function handleSearch(params) {
  loading.value = true
  try {
    const response = await getTankList(params)
    records.value = response.data || []
    total.value = response.total || 0
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
  switch (action) {
    case 'edit':
      openDialog('edit', index)
      break
    case 'delete':
      confirmDelete(index)
      break
    case 'view':
      ElMessage.info(`查看料罐: ${row.tankNo}`)
      break
  }
}

function handleHeaderAction({ action }) {
  switch (action) {
    case 'add':
      openDialog('add')
      break
    case 'import':
      ElMessage.success('导入功能待实现')
      break
    case 'export':
      ElMessage.success('导出功能待实现')
      break
  }
}

// 对话框相关函数
function openDialog(mode, index = null) {
  dialog.mode = mode
  dialog.visible = true
  dialog.index = index
  
  // 更新对话框标题
  dialog.config.title = mode === 'add' ? '新增料罐' : '编辑料罐'
  
  if (mode === 'edit' && index !== null) {
    Object.assign(dialog.form, records.value[index])
  } else {
    dialog.form = { tankNo: '', remark: '' }
  }
}

// 处理底部按钮点击事件
function handleFooterAction({ action, formData }) {
  if (action === 'submit') {
    handleDialogOk(formData)
  } else if (action === 'cancel') {
    dialog.visible = false
  }
}

async function handleDialogOk(formData) {
  try {
    dialog.loading = true
    if (dialog.mode === 'add') {
      await createTank(formData)
      ElMessage.success('新增料罐成功')
    } else {
      // 确保编辑时包含ID
      const updateData = { ...formData }
      if (dialog.index !== null && records.value[dialog.index]) {
        updateData.id = records.value[dialog.index].id
      }
      await updateTank(updateData)
      ElMessage.success('编辑料罐成功')
    }
    dialog.visible = false
    await handleSearch({ page: 1, pageSize: 10 })
  } finally {
    dialog.loading = false
  }
}

function confirmDelete(index) {
  deleteDialog.index = index
  deleteDialog.visible = true
}

function closeDeleteDialog() {
  deleteDialog.visible = false
}

async function handleDeleteTank() {
  const index = deleteDialog.index
  try {
    await deleteTank(records.value[index].id)
    ElMessage.success('删除料罐成功')
  } finally {
    deleteDialog.visible = false
    await handleSearch({ page: 1, pageSize: 10 })
  }
}

// 页面加载时获取数据
onMounted(() => {
  handleSearch({ page: 1, pageSize: 10 })
})
</script>

<style scoped>
.tank-manage-container {
}
</style> 