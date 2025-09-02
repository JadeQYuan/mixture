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
      :visible="remarkDialog.visible"
      @update:visible="handleDialogVisibleUpdate"
    >
      <Form
        ref="remarkForm"
        :fields="remarkDialogConfig.fields"
        :rules="remarkDialogConfig.rules"
        :form-data="remarkDialog.form"
        :footer-buttons="dialogFormButtons"
        style="padding-right: 30px;"
      />
    </Dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedRecordList, saveFeedRemark } from '@/api/mixture'
import DataTable from '@/components/DataTable'
import {Dialog} from '@/components/Dialog'
import Form from '@/components/Form'
import { searchFields, columns, remarkDialogConfig } from './config'

const table = ref()
const remarkForm = ref()

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
    validate: true,
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
const remarkDialog = reactive({
  visible: false,
  form: { id: null, remark: '', materialName: '', productSpec: '', flameRetardantWeight: '' }
})

function openRemarkDialog(row) {
  remarkDialog.form.id = row.id
  remarkDialog.form.materialName = row.materialName
  remarkDialog.form.productSpec = row.productSpec
  remarkDialog.form.flameRetardantWeight = row.flameRetardantWeight
  remarkDialog.form.remark = row.remark || ''
  remarkDialog.visible = true
}

function closeRemarkDialog() {
  remarkDialog.visible = false
  // 重置表单数据与校验状态
  remarkForm.value?.resetFields?.()
}

async function saveRemark(formdata) {
  await saveFeedRemark(formdata)
  ElMessage.success('保存成功')
  closeRemarkDialog()
  await table.value.search();
}

// Dialog关闭时处理
function handleDialogVisibleUpdate(val) {
  if (!val) {
    closeRemarkDialog()
  } else {
    remarkDialog.visible = true
  }
}
</script>

<style scoped>
.feed-record-container {
}
</style> 