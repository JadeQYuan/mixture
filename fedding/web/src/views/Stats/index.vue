<template>
  <div class="feed-stats-container">
    <DataTable
      :data="records"
      :total="total"
      :loading="loading"
      :columns="columns"
      :search-fields="searchFields"
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
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedStatsList } from '@/api/mixes'
import DataTable from '@/components/DataTable'
import { searchFields, columns, headerButtons } from './config'
import * as XLSX from 'xlsx'

// 数据
const records = ref([])
const loading = ref(false)
const total = ref(0)

// 查询参数
const searchParams = ref({})

// 事件处理函数
async function handleSearch(params) {
  loading.value = true
  try {
    // 处理动态时间类型和范围
    const { timeType, timeRange, ...rest } = params
    let query = { ...rest }
    if (timeType && Array.isArray(timeRange) && timeRange.length === 2) {
      const [start, end] = timeRange
      if (timeType === 'applyTime') {
        query.applyStartTime = start
        query.applyEndTime = end
      } else if (timeType === 'feedingTime') {
        query.feedingStartTime = start
        query.feedingEndTime = end
      } else if (timeType === 'returnTime') {
        query.returnStartTime = start
        query.returnEndTime = end
      }
    }
    searchParams.value = { ...params } // 保存当前查询参数
    const response = await getFeedStatsList(query)
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
  if (action === 'remark') {
    openRemarkDialog(row)
  }
}

async function handleHeaderAction({ action }) {
  if (action === 'export') {
  // 1. 直接用当前查询参数和total
  const params = { ...searchParams.value, page: 1, pageSize: total.value }
  if (!total.value) {
    ElMessage.warning('无可导出数据')
    return
  }
  // 2. 拉取所有数据
  const allRes = await getFeedStatsList(params)
  const list = allRes.data || []
    try {
      // 3. 组装导出数据和表头，顺序与columns一致，内容优先用render
      const exportColumns = columns.filter(col => col.type !== 'actions')
      const headers = exportColumns.map(col => col.label)
      const exportData = list.map(row => {
        const item = {}
        exportColumns.forEach(col => {
          if (typeof col.render === 'function') {
            item[col.label] = col.render(row)
          } else {
            item[col.label] = row[col.prop]
          }
        })
        return item
      })
      // 4. 生成并下载xlsx，设置表头宽度
      const ws = XLSX.utils.json_to_sheet(exportData, { header: headers })
      ws['!cols'] = exportColumns.map(col => ({ wch: Math.round(Number(col.width || 120) / 7.5) }))
      const wb = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(wb, ws, '加料记录')
      XLSX.writeFile(wb, '加料记录.xlsx')
      ElMessage.success('导出成功')
    } catch (e) {
      ElMessage.error('导出失败')
    }
  }
}

// 页面加载时获取数据
onMounted(() => {
  handleSearch({ page: 1, pageSize: 10 })
})
</script>

<style scoped>
.feed-stats-container {
}
</style> 