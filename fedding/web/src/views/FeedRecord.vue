<template>
  <div class="feed-record-container">
    <el-card class="feed-record-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="人员">
          <el-input v-model="searchForm.person" placeholder="请输入人员" clearable size="large" class="fixed-width-input" />
        </el-form-item>
        <el-form-item label="罐号">
          <el-input v-model="searchForm.tank" placeholder="请输入罐号" clearable size="large" class="fixed-width-input" />
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="searchForm.time"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px;"
            size="large"
          />
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
      <el-table :data="pagedRecords" style="width: 100%;" class="feed-record-table" v-loading="loading">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="person" label="人员" width="160" />
        <el-table-column prop="tank" label="罐号" width="160" />
        <el-table-column prop="baseWeight" label="罐底重量" width="160">
          <template #default="scope">
            {{ scope.row.baseWeight }} kg
          </template>
        </el-table-column>
        <el-table-column prop="feedWeight" label="加料重量" width="160">
          <template #default="scope">
            {{ scope.row.feedWeight }} kg
          </template>
        </el-table-column>
        <el-table-column prop="flameWeight" label="阻燃粉" width="160">
          <template #default="scope">
            {{ scope.row.flameWeight }} kg
          </template>
        </el-table-column>
        <el-table-column prop="time" label="时间" />
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { getFeedRecordList } from '../request/api'
import { ElMessage } from 'element-plus'
import { pageSizeCalculators } from '../utils/pagination'

const searchForm = reactive({
  person: '',
  tank: '',
  time: []
})

const records = ref([])
const loading = ref(false)
const total = ref(0)

// 动态计算每页显示条数
const pageSize = ref(5)
const currentPage = ref(1)

// 计算合适的每页显示条数
function calculatePageSize() {
  pageSize.value = pageSizeCalculators.feedRecord()
}

// 获取加料记录列表
async function fetchRecords() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      person: searchForm.person,
      tank: searchForm.tank,
      startTime: searchForm.time?.[0],
      endTime: searchForm.time?.[1]
    }
    const response = await getFeedRecordList(params)
    records.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取加料记录列表失败')
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
  searchForm.person = ''
  searchForm.tank = ''
  searchForm.time = []
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

// 页面加载时获取数据
onMounted(() => {
  calculatePageSize()
  fetchRecords()
  
  // 监听窗口大小变化
  window.addEventListener('resize', calculatePageSize)
  })
  
// 组件卸载时清理事件监听
onUnmounted(() => {
  window.removeEventListener('resize', calculatePageSize)
})
</script>

<style scoped>
.feed-record-container {
  margin: 20px;
  height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
}
.feed-record-card {
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
.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 24px;
  margin-bottom: 24px;
}
.table-header-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 12px;
}
.feed-record-table {
  margin-bottom: 24px;
  font-size: 20px;
  flex: 1;
  min-height: 0;
  max-height: calc(100vh - 300px);
  overflow: auto;
}

/* 增加表格行高，确保文字完整显示 */
:deep(.feed-record-table .el-table__row) {
  height: 60px !important;
  min-height: 60px !important;
}

:deep(.feed-record-table .el-table__cell) {
  padding: 12px 0 !important;
  line-height: 1.5 !important;
  height: 60px !important;
  min-height: 60px !important;
}

/* 确保表格内容垂直居中且完整显示 */
:deep(.feed-record-table .el-table) {
  --el-table-row-height: 60px !important;
}

:deep(.feed-record-table .el-table__cell) {
  vertical-align: middle !important;
}

/* 确保单元格内容不被截断 */
:deep(.feed-record-table .el-table__cell .cell) {
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

/* 隐藏分页组件的英文文本 */
:deep(.el-pagination .el-pagination__total) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__jump) {
  font-size: 16px;
}

/* 隐藏英文文本，只显示中文 */
:deep(.el-pagination .el-pagination__total) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__total::before) {
  content: "共 ";
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__total::after) {
  content: " 条";
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__total span) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner::after) {
  content: " 条/页";
  font-size: 16px;
  color: #606266;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner input) {
  font-size: 16px;
  padding-right: 50px;
}

/* 隐藏下拉选项中的英文文本 */
:deep(.el-pagination .el-pagination__sizes .el-select-dropdown .el-select-dropdown__item) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__sizes .el-select-dropdown .el-select-dropdown__item::after) {
  content: " 条/页";
  font-size: 16px;
  color: #606266;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto::before) {
  content: "前往第 ";
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto::after) {
  content: " 页";
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto input) {
  font-size: 16px;
}
</style> 