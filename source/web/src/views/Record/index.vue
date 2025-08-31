<template>
  <div class="feed-record-container">
    <DataTable
      ref="table"
      :columns="columns"
      :search-fields="searchFields"
      :action-buttons="actionButtons"
      :beforeRequest="handlerParams"
      :request="getFeedRecordList"
    />
    <Dialog
      :title="remarkDialogConfig.title"
      :width="remarkDialogConfig.width"
      :visible="remarkDialogVisible"
      @update:visible="val => remarkDialogVisible = val"
    >
      <Form
        :fields="remarkDialogConfig.fields"
        :rules="remarkDialogConfig.rules"
        :form-data="remarkForm"
        :footer-buttons="dialogFormButtons"
        style="padding-right: 30px;"
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
import { searchFields, columns, remarkDialogConfig } from './config'

const table = ref()

const actionButtons = [
  {
    key: 'remark',
    text: '备注',
    type: 'primary',
    size: 'large',
    action: ( row ) => openRemarkDialog(row)
  }
]

const dialogFormButtons = [
  {
    key: 'cancel',
    text: '取消',
    action: () => closeRemarkDialog()
  },
  {
    key: 'feed',
    text: '提交',
    type: 'primary',
    action: ( formdata ) => saveRemark(formdata),
  }
]

// 处理动态时间类型和范围
function handlerParams(params) {
  const { timeType, timeRange, startTime, endTime, ...rest } = params
  let query = { ...rest }
  if (timeType && Array.isArray(timeRange) && timeRange.length === 2) {
    const [start, end] = timeRange
    if (timeType === 'applyTime') {
      query.applyStartTime = start
      query.applyEndTime = end
    } else if (timeType === 'pickingTime') {
      query.pickingStartTime = start
      query.pickingEndTime = end
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

// 备注弹窗相关
const remarkDialogVisible = ref(false)
const remarkForm = ref({ id: null, remark: '', materialName: '', productSpec: '' })

function openRemarkDialog(row) {
  remarkForm.value.materialName = row.materialName
  remarkForm.value.productSpec = row.productSpec
  remarkForm.value.remark = row.remark || ''
  remarkForm.value.id = row.id
  remarkDialogVisible.value = true
}

function closeRemarkDialog() {
  remarkDialogVisible.value = false
}

async function saveRemark(formdata) {
  await saveFeedRemark(formdata)
  ElMessage.success('保存成功')
  remarkDialogVisible.value = false
  await table.value.search();
}
</script>

<style scoped>
.feed-record-container {
}
</style> 