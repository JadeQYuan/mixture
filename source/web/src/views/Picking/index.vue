<template>
  <div class="picking-container">
    <CardGrid
      ref="card"
      :display-fields="pickingConfig.displayFields"
      :headerButtons="headerButtons"
      :action-buttons="actionButtons"
      :request="getPickingTankList"
      :header-render="item => ('料罐：' + item.tankNo)"
    />

    <!-- 领料确认对话框 -->
    <ConfirmDialog
      :visible="pickingDialog.visible"
      title="确认领料"
      message="确定要领取该料罐吗？"
      icon="Warning"
      icon-type="danger"
      confirm-text="确认"
      confirm-type="primary"
      @update:visible="val => pickingDialog.visible = val"
      @confirm="confirmPicking"
      @cancel="() => pickingDialog.visible = false"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getPickingTankList, submitPicking } from '@/api/mixture'
import CardGrid from '@/components/CardGrid'
import ConfirmDialog from '@/components/Dialog/ConfirmDialog.vue'
import { pickingConfig } from './config'

const card = ref()

const headerButtons = [
  {
    key: 'refresh',
    text: '刷新',
    type: 'primary',
    size: 'large',
    action: () => card.value.search()
  }
] 

// 操作按钮配置 - 只显示领料按钮
const actionButtons = [
  {
    key: 'picking',
    text: '领料',
    type: 'success',
    size: 'large',
    action: ( row ) => openPickingDialog(row)
  }
]

// 领料对话框
const pickingDialog = reactive({
  visible: false,
  loading: false,
  form: {id: null, tankId: null, tankNo: ''}
})

function openPickingDialog(row) {
  pickingDialog.visible = true
  pickingDialog.form.id = row.id
  pickingDialog.form.tankId = row.tankId
  pickingDialog.form.tankNo = row.tankNo
}

async function confirmPicking() {
  if (pickingDialog.loading) return // 防止重复提交
  try {
    pickingDialog.loading = true
    await submitPicking(pickingDialog.form)
    ElMessage.success('领料申请已提交！')
    pickingDialog.visible = false 
    await card.value.search() // 重新获取列表
  } finally {
    setTimeout(() => {
      pickingDialog.loading = false
    }, 300)
  }
}
</script>

<style scoped>
.picking-container {
}
</style>
