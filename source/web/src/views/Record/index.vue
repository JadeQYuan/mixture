<template>
  <div class="feed-record-container">
    <DataTable
      :data="records"
      :total="total"
      :loading="loading"
      :columns="columns"
      :search-fields="searchFields"
      :action-buttons="actionButtons"
      @search="handleSearch"
      @reset="handleReset"
      @page-change="handlePageChange"
      @size-change="handleSizeChange"
      @action="handleAction"
    />
    <Dialog
      :visible="remarkDialog"
      :title="remarkDialogConfig.title"
      :width="remarkDialogConfig.width"
      @update:visible="val => remarkDialog = val"
    >
      <Form
        :fields="remarkDialogConfig.fields"
        :form-data="remarkForm"
        :footer-buttons="remarkDialogConfig.footerButtons"
        @submit="saveRemark"
        @cancel="closeRemarkDialog"
      />
    </Dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedRecordList, saveFeedRemark } from '@/api/mixture'
import DataTable from '@/components/DataTable'
import {Dialog} from '@/components/Dialog'
import Form from '@/components/Form'
import { searchFields, columns, actionButtons, remarkDialogConfig } from './config'

// 数据
const records = ref([])
const loading = ref(false)
const total = ref(0)

// 备注弹窗相关
const remarkDialog = ref(false)
const remarkRow = ref(null)
const remarkText = ref('')
// 删除本地 remarkDialogConfig 定义，直接使用导入的配置
const remarkForm = ref({ remark: '' })

function openRemarkDialog(row) {
  remarkRow.value = row
  remarkForm.value.remark = row.remark || ''
  remarkDialog.value = true
}

function closeRemarkDialog() {
  remarkDialog.value = false
}

async function saveRemark() {
  await saveFeedRemark({ id: remarkRow.value.id, remark: remarkForm.value.remark })
  ElMessage.success('保存成功')
  remarkDialog.value = false
}

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
    const response = await getFeedRecordList(query)
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

// 页面加载时获取数据
onMounted(() => {
  handleSearch({ page: 1, pageSize: 10 })
})
</script>

<style scoped>
.feed-record-container {
}
</style> 