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
  await table.value.search();
}
</script>

<style scoped>
.feed-record-container {
}
</style> 