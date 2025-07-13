<template>
  <div class="feed-record-container">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedRecordList } from '@/request/api'
import DataTable from '@/components/DataTable'
import { searchFields, columns, actionButtons, headerButtons } from './config'

// 数据
const records = ref([])
const loading = ref(false)
const total = ref(0)

// 事件处理函数
async function handleSearch(params) {
  loading.value = true
  try {
    const response = await getFeedRecordList(params)
    records.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取加料记录列表失败')
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
  // FeedRecord页面没有操作按钮
}

function handleHeaderAction({ action }) {
  if (action === 'export') {
    ElMessage.success('导出功能待实现')
  }
}

// 页面加载时获取数据
onMounted(() => {
  handleSearch({ page: 1, pageSize: 10 })
})
</script>

<style scoped>
.feed-record-container {
  height: 100vh;
}
</style> 