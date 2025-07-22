<template>
  <div class="feed-record-container">
    <DataTable
      ref="table"
      :columns="columns"
      :search-fields="searchFields"
      :action-buttons="actionButtons"
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
        :form-data="remarkForm"
        :footer-buttons="dialogFormButtons"
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

// 备注弹窗相关
const remarkDialogVisible = ref(false)
const remarkForm = ref({ id: null, remark: '' })

function openRemarkDialog(row) {
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