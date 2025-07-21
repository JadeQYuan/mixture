<template>
  <div class="feed-stats-container">
    <DataTable
      ref="table"
      :columns="columns"
      :search-fields="searchFields"
      :header-buttons="headerButtons"
      :beforeRequest="handlerParams"
      :request="getFeedStatsList"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedStatsList } from '@/api/mixture'
import DataTable from '@/components/DataTable'
import { searchFields, columns } from './config'
import * as XLSX from 'xlsx'

const table = ref()

const headerButtons = [
  {
    key: 'export',
    text: '导出',
    type: 'success',
    size: 'large',
    action: () => handleExport()
  }
] 

// 处理动态时间类型和范围
function handlerParams(params) {
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
  return query
}

async function handleExport() {
  // 1. 拉取所有数据
  const list = await table.value.search()
  if (list.length == 0) {
    ElMessage.warning('无可导出数据')
    return
  }
  try {
    // 2. 组装导出数据和表头，顺序与columns一致，内容优先用render
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
    // 3. 生成并下载xlsx，设置表头宽度
    const ws = XLSX.utils.json_to_sheet(exportData, { header: headers })
    ws['!cols'] = exportColumns.map(col => ({ wch: Math.round(Number(col.width || 120) / 7.5) }))
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, '领料统计')
    XLSX.writeFile(wb, '领料统计.xlsx')
    ElMessage.success('导出成功')
  } catch (e) {
    console.error('导出失败', e)
    ElMessage.error('导出失败')
  }
}
</script>

<style scoped>
.feed-stats-container {
}
</style> 