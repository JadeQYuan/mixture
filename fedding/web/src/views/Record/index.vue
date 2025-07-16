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
      :user-role="userRole"
      @search="handleSearch"
      @reset="handleReset"
      @page-change="handlePageChange"
      @size-change="handleSizeChange"
      @action="handleAction"
      @header-action="handleHeaderAction"
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
import { getFeedRecordList, saveFeedRemark } from '@/api/mixes'
import DataTable from '@/components/DataTable'
import {Dialog} from '@/components/Dialog'
import Form from '@/components/Form'
import { searchFields, columns, actionButtons, headerButtons, remarkDialogConfig } from './config'
import { useStore } from 'vuex'
import * as XLSX from 'xlsx'
import { getLabel, SHIFT_TYPE_MAP, MATERIAL_MAP } from '@/utils/constant'

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

// 获取用户角色信息
const store = useStore()
const userRole = computed(() => {
  const userInfo = store.state.userInfo
  return userInfo ? userInfo.roleCode : ''
})

// 查询参数
const searchParams = ref({})

// 事件处理函数
async function handleSearch(params) {
  loading.value = true
  try {
    searchParams.value = { ...params } // 保存当前查询参数
    const response = await getFeedRecordList(params)
    records.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取领料记录列表失败')
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
    try {
      // 1. 直接用当前查询参数和total
      const params = { ...searchParams.value, page: 1, pageSize: total.value }
      if (!total.value) {
        ElMessage.warning('无可导出数据')
        return
      }
      // 2. 拉取所有数据
      const allRes = await getFeedRecordList(params)
      const list = allRes.data || []
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
.feed-record-container {
  height: 100vh;
}
</style> 