<template>
  <div class="tank-manage-container">
    <el-card class="tank-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="编号">
          <el-input v-model="searchForm.code" placeholder="请输入编号" clearable size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleSearch">查询</el-button>
          <el-button size="large" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="table-header-bar">
        <div style="flex:1"></div>
        <el-button type="primary" size="large" class="add-btn" @click="openDialog('add')">新增料罐</el-button>
      </div>
      <el-table :data="pagedTanks" style="width: 100%;" class="tank-table" v-loading="loading">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="code" label="编号" width="160" />
        <el-table-column prop="desc" label="描述" />
        <el-table-column prop="person" label="当前人员" width="160" />
        <el-table-column prop="updatedAt" label="修改时间" width="180" />
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <el-button size="large" @click="openDialog('edit', scope.$index)">编辑</el-button>
            <el-button size="large" type="danger" @click="confirmDelete(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-box">
        <el-pagination
          background
          layout="prev, pager, next, jumper, total"
          :total="filteredTanks.length"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    <el-dialog v-model="dialog.visible" :title="dialog.mode === 'add' ? '新增料罐' : '编辑料罐'" width="400px">
      <template #header>
        <div style="text-align: center; font-size: 18px; font-weight: 900; color: #000;">
          {{ dialog.mode === 'add' ? '新增料罐' : '编辑料罐' }}
        </div>
      </template>
      <el-form :model="dialog.form" :rules="dialog.rules" ref="dialogFormRef" label-width="80px">
        <el-form-item label="编号" prop="code">
          <el-input v-model="dialog.form.code" placeholder="请输入编号" />
        </el-form-item>
        <el-form-item label="描述" prop="desc">
          <el-input v-model="dialog.form.desc" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="padding: 8px 0;">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="handleDialogOk">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="deleteDialog.visible" title="确认删除" width="300px">
      <template #header>
        <div style="text-align: center; font-size: 18px; font-weight: 900; color: #000;">
          确认删除
        </div>
      </template>
      <span>确定要删除该料罐吗？</span>
      <template #footer>
        <div style="padding: 8px 0;">
          <el-button @click="deleteDialog.visible = false">取消</el-button>
          <el-button type="danger" @click="handleDeleteTank">删除</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTankList, createTank, updateTank, deleteTank } from '../request/api'

const currentPerson = localStorage.getItem('username') || '张三'

const searchForm = reactive({
  code: ''
})

const tanks = ref([])
const loading = ref(false)

const pageSize = 5
const currentPage = ref(1)

// 获取料罐列表
async function fetchTanks() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize,
      code: searchForm.code
    }
    const response = await getTankList(params)
    tanks.value = response.data || []
  } catch (error) {
    console.error('获取料罐列表失败:', error)
    ElMessage.error('获取料罐列表失败')
  } finally {
    loading.value = false
  }
}

const filteredTanks = computed(() => {
  return tanks.value.filter(t => {
    return !searchForm.code || t.code.includes(searchForm.code)
  })
})

const pagedTanks = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredTanks.value.slice(start, start + pageSize)
})

async function handleSearch() {
  currentPage.value = 1
  await fetchTanks()
}
async function resetSearch() {
  searchForm.code = ''
  currentPage.value = 1
  await fetchTanks()
}
async function handlePageChange(page) {
  currentPage.value = page
  await fetchTanks()
}

const dialog = reactive({
  visible: false,
  mode: 'add', // add/edit
  index: null,
  form: { code: '', desc: '' },
  rules: {
    code: [ { required: true, message: '请输入编号', trigger: 'blur' } ],
    desc: [ { required: true, message: '请输入描述', trigger: 'blur' } ]
  }
})
const dialogFormRef = ref()

function openDialog(mode, index = null) {
  dialog.mode = mode
  dialog.visible = true
  dialog.index = index
  if (mode === 'edit' && index !== null) {
    Object.assign(dialog.form, tanks.value[index])
  } else {
    dialog.form.code = ''
    dialog.form.desc = ''
  }
}

async function handleDialogOk() {
  dialogFormRef.value.validate(async valid => {
    if (!valid) return
    
    try {
      const now = new Date().toLocaleString()
      if (dialog.mode === 'add') {
        await createTank({
          ...dialog.form,
          person: currentPerson,
          updatedAt: now
        })
        ElMessage.success('新增成功')
      } else if (dialog.mode === 'edit' && dialog.index !== null) {
        const tank = tanks.value[dialog.index]
        await updateTank(tank.id, {
          ...dialog.form,
          person: currentPerson,
          updatedAt: now
        })
        ElMessage.success('编辑成功')
      }
      dialog.visible = false
      await fetchTanks() // 重新获取列表
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  })
}

const deleteDialog = reactive({ visible: false, index: null })
function confirmDelete(index) {
  deleteDialog.visible = true
  deleteDialog.index = index
}

async function handleDeleteTank() {
  if (deleteDialog.index !== null) {
    try {
      const tank = tanks.value[deleteDialog.index]
      await deleteTank(tank.id)
      ElMessage.success('删除成功')
      deleteDialog.visible = false
      deleteDialog.index = null
      await fetchTanks() // 重新获取列表
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchTanks()
})
</script>

<style scoped>
.tank-manage-container {
  margin: 20px;
}
.tank-card {
  background: rgba(255,255,255,0.8);
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  padding: 48px 40px 32px 40px;
  font-size: 1.08em;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}
.table-header-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 12px;
}
.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 24px;
  margin-bottom: 24px;
}
.add-btn {
  margin-left: 24px;
}
.tank-table {
  margin-bottom: 24px;
  font-size: 14px;
}
.pagination-box {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
.el-form-item__label {
  font-size: 1em;
}
</style> 