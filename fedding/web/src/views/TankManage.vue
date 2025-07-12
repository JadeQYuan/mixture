<template>
  <div class="tank-manage-container">
    <el-card class="tank-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="编号">
          <el-input v-model="searchForm.bucketNo" placeholder="请输入编号" clearable size="large" class="fixed-width-input" />
        </el-form-item>
        <el-form-item label="用户">
          <el-input v-model="searchForm.userKey" placeholder="请输入人员姓名/工号" clearable size="large" class="fixed-width-input" />
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
        <el-table-column prop="bucketNo" label="编号" width="160" />
        <el-table-column label="当前人员" width="160">
          <template #default="scope">
            <template v-if="scope.row.userName && scope.row.account">
              {{ scope.row.userName }}({{ scope.row.account }})
            </template>
            <template v-else>
              -
            </template>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="描述" width="500" show-overflow-tooltip>
          <template #default="scope">
            <div class="desc-cell">{{ scope.row.remark || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="修改时间" width="240" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="large" @click="openDialog('edit', scope.$index)"
              :disabled="scope.row.userName && scope.row.account"
            >编辑</el-button>
            <el-button size="large" type="danger" @click="confirmDelete(scope.$index)"
              :disabled="scope.row.person && scope.row.account"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-box">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          :page-sizes="[10, 15, 20, 25, 30, 50, 100]"
          size="large"
          prev-text="上一页"
          next-text="下一页"
          :pager-count="7"
          :hide-on-single-page="false"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
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
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getTankList, createTank, updateTank, deleteTank } from '../request/api'
import { pageSizeCalculators } from '../utils/pagination'

const currentPerson = localStorage.getItem('username') || '张三'

const searchForm = reactive({
  bucketNo: '',
  userKey: ''
})

const tanks = ref([])
const loading = ref(false)
const total = ref(0)

// 动态计算每页显示条数
const pageSize = ref(5)
const currentPage = ref(1)

// 计算合适的每页显示条数
function calculatePageSize() {
  pageSize.value = pageSizeCalculators.tankManage()
}

// 获取料罐列表
async function fetchTanks() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      bucketNo: searchForm.bucketNo,
      userKey: searchForm.userKey
    }
    const response = await getTankList(params)
    tanks.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取料罐列表失败')
  } finally {
    loading.value = false
  }
}

// 直接使用后端返回的数据，不再进行客户端分页
const pagedTanks = computed(() => {
  return tanks.value
})

async function handleSearch() {
  currentPage.value = 1
  await fetchTanks()
}
async function resetSearch() {
  searchForm.bucketNo = ''
  searchForm.userkey = ''
  currentPage.value = 1
  await fetchTanks()
}
async function handlePageChange(page) {
  currentPage.value = page
  await fetchTanks()
}

async function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
  await fetchTanks()
}

const dialog = reactive({
  visible: false,
  mode: 'add', // add/edit
  index: null,
  loading: false, // 添加loading状态
  form: { bucketNo: '', remark: '' },
  rules: {
    bucketNo: [ { required: true, message: '请输入编号', trigger: 'blur' } ],
    remark: [ { required: false, message: '请输入描述', trigger: 'blur' } ]
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
    dialog.form.bucketNo = ''
    dialog.form.remark = ''
  }
}

async function handleDialogOk() {
  if (dialog.loading) return // 防止重复提交
  
  dialogFormRef.value.validate(async valid => {
    if (!valid) return
    
    try {
      dialog.loading = true
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
      ElMessage.error('操作失败')
    } finally {
      dialog.loading = false
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
      ElMessage.error('删除失败')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  calculatePageSize()
  fetchTanks()
  
  // 监听窗口大小变化
  window.addEventListener('resize', calculatePageSize)
  })
  
// 组件卸载时清理事件监听
onUnmounted(() => {
  window.removeEventListener('resize', calculatePageSize)
})
</script>

<style scoped>
.tank-manage-container {
  margin: 20px;
  height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
}
.tank-card {
  background: rgba(255,255,255,0.8);
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  padding: 48px 40px 32px 40px;
  font-size: 1.2em;
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
  font-size: 20px;
  flex: 1;
  min-height: 0;
  max-height: calc(100vh - 300px);
  overflow: auto;
}

/* 增加表格行高，确保文字完整显示 */
:deep(.tank-table .el-table__row) {
  height: 60px !important;
  min-height: 60px !important;
}

:deep(.tank-table .el-table__cell) {
  padding: 12px 0 !important;
  line-height: 1.5 !important;
  height: 60px !important;
  min-height: 60px !important;
}

/* 确保表格内容垂直居中且完整显示 */
:deep(.tank-table .el-table) {
  --el-table-row-height: 60px !important;
}

:deep(.tank-table .el-table__cell) {
  vertical-align: middle !important;
}

/* 确保单元格内容不被截断 */
:deep(.tank-table .el-table__cell .cell) {
  height: 100% !important;
  min-height: 36px !important;
  line-height: 1.5 !important;
  overflow: visible !important;
}

/* 描述列样式 */
.desc-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 480px;
}

/* 分页组件中文样式 */
:deep(.el-pagination .el-pagination__total) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__jump) {
  font-size: 16px;
}

/* 分页组件样式 */
:deep(.el-pagination .el-pagination__total) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__jump) {
  font-size: 16px;
}

/* 分页组件按钮样式 */
:deep(.el-pagination .el-pagination__total) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__total span) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner input) {
  font-size: 16px;
}

/* 分页组件下拉选项样式 */
:deep(.el-pagination .el-pagination__sizes .el-select-dropdown .el-select-dropdown__item) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto input) {
  font-size: 16px;
}
.pagination-box {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  flex-shrink: 0;
  padding: 10px 0;
}
/* 使用更高优先级的选择器确保表单标签字体生效 */
:deep(.el-form-item__label) {
  font-size: 20px !important;
}

:deep(.search-form .el-form-item__label),
:deep(.el-dialog .el-form-item__label) {
  font-size: 20px !important;
}

/* 确保所有表单标签字体生效 */
:deep(.el-form-item) .el-form-item__label {
  font-size: 20px !important;
}

/* 固定输入框宽度，避免清除按钮导致宽度变化 */
.fixed-width-input {
  width: 180px !important;
}

.fixed-width-input .el-input__wrapper {
  width: 100% !important;
}

/* 弹窗表单输入框宽度 */
:deep(.el-dialog .el-input) {
  width: 85% !important;
}

:deep(.el-dialog .el-input-number) {
  width: 85% !important;
}

/* 弹窗文本域宽度 */
:deep(.el-dialog .el-textarea) {
  width: 85% !important;
}
</style> 