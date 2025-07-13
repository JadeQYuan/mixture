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

    <!-- 罐管理对话框 -->
    <el-dialog v-model="dialog.visible" :title="dialog.mode === 'add' ? '新增料罐' : '编辑料罐'" width="700px" :close-on-click-modal="false">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          {{ dialog.mode === 'add' ? '新增料罐' : '编辑料罐' }}
        </div>
      </template>
      <el-form :model="dialog.form" :rules="dialog.rules" ref="dialogFormRef" label-width="180px" style="margin-top: 32px;">
        <el-form-item label="编号" prop="bucketNo">
          <el-input v-model="dialog.form.bucketNo" placeholder="请输入编号" size="large" />
        </el-form-item>
        <el-form-item label="描述" prop="remark">
          <el-input v-model="dialog.form.remark" placeholder="请输入描述" type="textarea" :rows="3" size="large" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="padding: 0;">
          <el-button @click="dialog.visible = false" size="large">取消</el-button>
          <el-button type="primary" @click="handleDialogOk" size="large" :loading="dialog.loading">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog v-model="deleteDialog.visible" title="确认删除" width="400px">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          确认删除
        </div>
      </template>
      <span style="font-size: 18px; margin-top: 32px; display: block;">确定要删除该料罐吗？</span>
      <template #footer>
        <div style="padding: 0;">
          <el-button @click="deleteDialog.visible = false" size="large">取消</el-button>
          <el-button type="danger" @click="handleDeleteTank" size="large">删除</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTankList, createTank, updateTank, deleteTank } from '@/request/api'
import DataTable from '@/components/DataTable'
import { searchFields, columns, actionButtons, headerButtons } from './config'

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
  form: { bucketNo: '', remark: '' },
  rules: {
    bucketNo: [ { required: true, message: '请输入编号', trigger: 'blur' } ],
    remark: [ { required: false, message: '请输入描述', trigger: 'blur' } ]
  }
})

const deleteDialog = reactive({
  visible: false,
  index: null
})

const dialogFormRef = ref()

// 事件处理函数
async function handleSearch(params) {
  loading.value = true
  try {
    const response = await getTankList(params)
    records.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取料罐列表失败')
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
      ElMessage.info(`查看料罐: ${row.bucketNo}`)
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
  if (mode === 'edit' && index !== null) {
    Object.assign(dialog.form, records.value[index])
  } else {
    dialog.form.bucketNo = ''
    dialog.form.remark = ''
  }
}

async function handleDialogOk() {
  dialogFormRef.value.validate(async (valid) => {
    if (valid) {
      dialog.loading = true
      try {
        if (dialog.mode === 'add') {
          await createTank(dialog.form)
          ElMessage.success('新增料罐成功')
        } else {
          // 确保编辑时包含ID
          const updateData = { ...dialog.form }
          if (dialog.index !== null && records.value[dialog.index]) {
            updateData.id = records.value[dialog.index].id
          }
          await updateTank(updateData)
          ElMessage.success('编辑料罐成功')
        }
        dialog.visible = false
        await handleSearch({ page: 1, pageSize: 10 })
      } catch (error) {
        ElMessage.error(dialog.mode === 'add' ? '新增料罐失败' : '编辑料罐失败')
      } finally {
        dialog.loading = false
      }
    }
  })
}

function confirmDelete(index) {
  deleteDialog.index = index
  deleteDialog.visible = true
}

async function handleDeleteTank() {
  const index = deleteDialog.index
  try {
    await deleteTank(records.value[index].id)
    ElMessage.success('删除料罐成功')
    deleteDialog.visible = false
    await handleSearch({ page: 1, pageSize: 10 })
  } catch (error) {
    ElMessage.error('删除料罐失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  handleSearch({ page: 1, pageSize: 10 })
})
</script>

<style scoped>
.tank-manage-container {
  height: 100vh;
}
</style> 