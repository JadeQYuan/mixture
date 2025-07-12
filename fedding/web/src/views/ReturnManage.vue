<template>
  <div class="return-manage-container">
    <el-card class="return-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="人员">
          <el-input v-model="searchForm.userKey" placeholder="请输入人员姓名/工号" clearable size="large" class="fixed-width-input" />
        </el-form-item>
        <el-form-item label="罐号">
          <el-input v-model="searchForm.bucketNo" placeholder="请输入罐号" clearable size="large" class="fixed-width-input" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleSearch">查询</el-button>
          <el-button size="large" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="table-header-bar">
        <div style="flex:1"></div>
        <div style="width: 120px;"></div>
      </div>
      <el-table :data="pagedRecords" style="width: 100%;" class="return-table" v-loading="loading">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column label="人员" width="160">
          <template #default="scope">
            {{ scope.row.userName }}({{ scope.row.account }})
          </template>
        </el-table-column>
        <el-table-column prop="bucketNo" label="罐号" width="160" />
        <el-table-column prop="updateTime" label="时间" />
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <el-button size="large" type="primary" @click="openReturnDialog(scope.$index)">退料</el-button>
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
    <el-dialog v-model="returnDialog.visible" title="退料操作" width="700px" :close-on-click-modal="false">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          退料操作
        </div>
      </template>
      <el-steps :active="returnDialog.step" finish-status="success" align-center style="margin-bottom: 24px; margin-top: 32px;">
        <el-step title="获取底罐重量" />
        <el-step title="确认提交" />
      </el-steps>
      <el-form :model="returnDialog.form" :rules="returnDialog.rules" ref="returnFormRef" label-width="180px" style="margin-top: 32px;">
        <template v-if="returnDialog.step === 0">
          <el-form-item label="罐号">
            <el-input v-model="returnDialog.form.bucketNo" disabled size="large" />
          </el-form-item>
          <el-form-item label="底罐重量">
            <el-input v-model="returnDialog.form.capacity" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
        </template>
        <template v-else-if="returnDialog.step === 1">
          <el-form-item label="罐号">
            <el-input v-model="returnDialog.form.bucketNo" disabled size="large" />
          </el-form-item>
          <el-form-item label="底罐重量">
            <el-input v-model="returnDialog.form.capacity" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <div style="padding: 0;">
          <el-button @click="closeReturnDialog" size="large">取消</el-button>
          <el-button v-if="returnDialog.step > 0" @click="prevStep" size="large">上一步</el-button>
          <el-button v-if="returnDialog.step < 1" type="primary" @click="nextStep" size="large">下一步</el-button>
          <el-button v-else type="success" @click="submitReturn" size="large" :loading="returnDialog.loading">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReturnManageList, submitReturnOperation, getTankWeightData } from '../request/api'
import { pageSizeCalculators } from '../utils/pagination'

const searchForm = reactive({
  person: '',
  bucketNo: ''
})

const records = ref([])
const loading = ref(false)
const total = ref(0)

// 动态计算每页显示条数
const pageSize = ref(5)
const currentPage = ref(1)

// 计算合适的每页显示条数
function calculatePageSize() {
  pageSize.value = pageSizeCalculators.returnManage()
}

// 获取退料管理列表
async function fetchRecords() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      userKey: searchForm.userKey,
      bucketNo: searchForm.bucketNo
    }
    const response = await getReturnManageList(params)
    records.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取退料管理列表失败')
  } finally {
    loading.value = false
  }
}

// 直接使用后端返回的数据，不再进行客户端分页
const pagedRecords = computed(() => {
  return records.value
})

async function handleSearch() {
  currentPage.value = 1
  await fetchRecords()
}
async function resetSearch() {
  searchForm.userKey = ''
  searchForm.bucketNo = ''
  currentPage.value = 1
  await fetchRecords()
}
async function handlePageChange(page) {
  currentPage.value = page
  await fetchRecords()
}

async function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
  await fetchRecords()
}

const returnDialog = reactive({
  visible: false,
  step: 0,
  index: null,
  loading: false, // 添加loading状态
  form: { bucketNo: '', capacity: null },
  rules: {
    capacity: [ { required: true, message: '请输入底罐重量', trigger: 'blur' } ]
  }
})
const returnFormRef = ref()

// 定时器相关
const weightTimer = ref(null)
const currentTankId = ref(null)

// 获取重量数据的函数
async function fetchWeightData() {
  if (currentTankId.value) {
    try {
      const response = await getTankWeightData()
      if (response.data) {
        returnDialog.form.capacity = response.data
      }
    } catch (error) {
      ElMessage.error('获取重量数据失败')
    }
  }
}

// 启动重量数据定时器
function startWeightTimer() {
  stopWeightTimer() // 先停止之前的定时器
  
  // 立即获取一次数据
  fetchWeightData()
  
  // 启动定时器，每秒获取一次
  weightTimer.value = setInterval(() => {
    fetchWeightData()
  }, 1000)
}

// 停止重量数据定时器
function stopWeightTimer() {
  if (weightTimer.value) {
    clearInterval(weightTimer.value)
    weightTimer.value = null
  }
}

function openReturnDialog(index) {
  returnDialog.visible = true
  returnDialog.step = 0
  returnDialog.index = index
  if (index >= 0) {
    const record = records.value[index]
    returnDialog.form.bucketNo = record.bucketNo
    currentTankId.value = record.id // 设置当前罐号ID
    // 启动定时器，立即获取底罐重量
    startWeightTimer()
  } else {
    returnDialog.form.bucketNo = ''
    currentTankId.value = null
  }
  returnDialog.form.capacity = null
}
function nextStep() {
  // 第0步：检查底罐重量是否已获取
  if (returnDialog.form.capacity !== null && returnDialog.form.capacity !== undefined) {
    // 底罐重量确定，进入下一步
    stopWeightTimer()
    returnDialog.step++
  } else {
    // 如果数据还未获取，立即获取一次
    fetchWeightData().then(() => {
      if (returnDialog.form.capacity !== null && returnDialog.form.capacity !== undefined) {
        stopWeightTimer()
        returnDialog.step++
      } else {
        ElMessage.warning('正在获取底罐重量数据，请稍候')
      }
    })
  }
}

function prevStep() {
  if (returnDialog.step > 0) {
    returnDialog.step--
    // 如果返回到需要获取数据的步骤，重新启动定时器并立即获取数据
    if (returnDialog.step === 0) {
      // 返回第一步，立即启动底罐重量获取
      startWeightTimer()
    }
  }
}

function closeReturnDialog() {
  returnDialog.visible = false
  stopWeightTimer()
}

async function submitReturn() {
  if (returnDialog.loading) return // 防止重复提交
  
  try {
    returnDialog.loading = true
    // 确保清除定时器
    stopWeightTimer()
    await submitReturnOperation(returnDialog.form)
    ElMessage.success('退料数据已提交！')
    returnDialog.visible = false
    await fetchRecords() // 重新获取列表
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    returnDialog.loading = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  calculatePageSize()
  fetchRecords()
  
  // 监听窗口大小变化
  window.addEventListener('resize', calculatePageSize)
  })
  
// 组件卸载时清理事件监听和定时器
onUnmounted(() => {
  window.removeEventListener('resize', calculatePageSize)
  stopWeightTimer()
})
</script>

<style scoped>
.return-manage-container {
  margin: 20px;
  height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
}
.return-card {
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
.return-table {
  margin-bottom: 24px;
  font-size: 20px;
  flex: 1;
  min-height: 0;
  max-height: calc(100vh - 300px);
  overflow: auto;
}

/* 增加表格行高，确保文字完整显示 */
:deep(.return-table .el-table__row) {
  height: 60px !important;
  min-height: 60px !important;
}

:deep(.return-table .el-table__cell) {
  padding: 12px 0 !important;
  line-height: 1.5 !important;
  height: 60px !important;
  min-height: 60px !important;
}

/* 确保表格内容垂直居中且完整显示 */
:deep(.return-table .el-table) {
  --el-table-row-height: 60px !important;
}

:deep(.return-table .el-table__cell) {
  vertical-align: middle !important;
}

/* 确保单元格内容不被截断 */
:deep(.return-table .el-table__cell .cell) {
  height: 100% !important;
  min-height: 36px !important;
  line-height: 1.5 !important;
  overflow: visible !important;
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

:deep(.el-input-number .el-input__suffix),
:deep(.el-input .el-input__suffix) {
  padding-left: 0 !important;
  margin-left: -6px !important;
  color: #333;
  font-size: 1em;
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
</style> 