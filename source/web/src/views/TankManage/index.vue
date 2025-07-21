<template>
  <div class="tank-manage-container">
    <DataTable
      ref="table"
      :columns="columns"
      :search-fields="searchFields"
      :action-buttons="actionButtons"
      :header-buttons="headerButtons"
      :request="getTankList"
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
import { searchFields, columns, getTankFormConfig } from './config'

const table = ref()

const headerButtons = [
  {
    key: 'add',
    text: '新增',
    type: 'primary',
    size: 'large',
    action: () => openDialog('add')
  }
] 

const actionButtons = [
  {
    key: 'edit',
    text: '编辑',
    type: 'primary',
    size: 'large',
    action: (row) => openDialog('edit', row)
  },
  {
    key: 'delete',
    text: '删除',
    type: 'danger',
    size: 'large',
    disabled: (row) => row.status === 'active', // 正常状态禁用删除
    action: (row) => confirmDelete(row)
  }
]

// 对话框
const dialog = reactive({
  visible: false,
  mode: 'add', // add/edit
  loading: false,
  form: { tankNo: '', remark: '' },
  config: getTankFormConfig()
})

// 对话框相关函数
function openDialog(mode, row) {
  dialog.mode = mode
  dialog.visible = true
  
  // 更新对话框标题
  dialog.config.title = mode === 'add' ? '新增料罐' : '编辑料罐'
  
  if (mode === 'edit' && row !== null) {
    Object.assign(dialog.form, row)
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
    await table.value.search()
  } finally {
    dialog.loading = false
  }
}

const deleteDialog = reactive({
  visible: false,
  id: null
})

function confirmDelete(row) {
  deleteDialog.id = row.id
  deleteDialog.visible = true
}

function closeDeleteDialog() {
  deleteDialog.visible = false
}

async function handleDeleteTank() {
  try {
    await deleteTank(deleteDialog.id)
    ElMessage.success('删除料罐成功')
  } finally {
    deleteDialog.id = null
    deleteDialog.visible = false
    await table.value.search()
  }
}

</script>

<style scoped>
.tank-manage-container {
}
</style> 