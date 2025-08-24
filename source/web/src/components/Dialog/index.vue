<template>
  <el-dialog 
    :model-value="visible"
    @update:model-value="handleVisibleChange"
    :width="width" 
    :close-on-click-modal="closeOnClickModal"
    :before-close="handleBeforeClose"
    style="max-height: 850px; overflow-y: auto;"
    center
    align-center
  >
    <!-- 自定义头部 -->
    <template #header>
      <div class="dialog-header">
        <div class="header-title">
          {{ title }}
        </div>
      </div>
    </template>
    
    <!-- 内容插槽 -->
    <slot></slot>
    
    <!-- 底部插槽 -->
    <template #footer>
      <slot name="footer"></slot>
    </template>
  </el-dialog>
</template>

<script setup>


// Props 定义
const props = defineProps({
  // 对话框是否可见
  visible: {
    type: Boolean,
    default: false
  },
  // 标题
  title: {
    type: String,
    default: '对话框'
  },
  // 宽度
  width: {
    type: String,
    default: '700px'
  },
  // 是否点击遮罩关闭
  closeOnClickModal: {
    type: Boolean,
    default: false
  },

})

// Emits 定义
const emit = defineEmits([
  'update:visible',
  'close'
])

// 处理可见性变化
function handleVisibleChange(value) {
  emit('update:visible', value)
}

// 处理对话框关闭
function handleBeforeClose(done) {
  emit('update:visible', false)
  emit('close')
  done()
}
</script>

<style scoped>
/* 对话框样式 */
:deep(.el-dialog) {
  border-radius: 16px;
}

:deep(.el-dialog__header) {
  padding: 16px 24px 0 24px;
}

:deep(.el-dialog__body) {
  padding: 16px 24px;
}

:deep(.el-dialog__footer) {
  padding: 0 24px 24px 24px;
}

/* 自定义头部样式 */
.dialog-header {
  display: flex;
  justify-content: center;
  align-items: center;
}

.header-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  text-align: center;
}
</style> 