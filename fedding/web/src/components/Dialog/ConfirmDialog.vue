<template>
  <Dialog
    :visible="visible"
    @update:visible="val => emit('update:visible', val)"
    :title="title"
    :width="width"
    @close="handleClose"
  >
    <div class="confirm-content">
      <el-icon class="confirm-icon" :class="iconClass">
        <component :is="icon" />
      </el-icon>
      <span class="confirm-text">{{ message }}</span>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button size="large" @click="handleCancel">{{ cancelText }}</el-button>
        <el-button 
          :type="confirmType" 
          size="large" 
          @click="handleConfirm"
          :loading="loading"
        >
          {{ confirmText }}
        </el-button>
      </div>
    </template>
  </Dialog>
</template>

<script setup>
import Dialog from './index.vue'
import { computed } from 'vue'

// Props 定义
const props = defineProps({
  // 是否可见
  visible: {
    type: Boolean,
    default: false
  },
  // 标题
  title: {
    type: String,
    default: '确认'
  },
  // 宽度
  width: {
    type: String,
    default: '400px'
  },
  // 消息内容
  message: {
    type: String,
    default: '确定要执行此操作吗？'
  },
  // 图标
  icon: {
    type: String,
    default: 'Warning'
  },
  // 图标类型
  iconType: {
    type: String,
    default: 'warning' // warning, danger, info, success
  },
  // 取消按钮文本
  cancelText: {
    type: String,
    default: '取消'
  },
  // 确认按钮文本
  confirmText: {
    type: String,
    default: '确定'
  },
  // 确认按钮类型
  confirmType: {
    type: String,
    default: 'primary'
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  }
})

// Emits 定义
const emit = defineEmits([
  'update:visible',
  'confirm',
  'cancel',
  'close'
])

// 计算图标样式类
const iconClass = computed(() => {
  const typeMap = {
    warning: 'warning-icon',
    danger: 'danger-icon',
    info: 'info-icon',
    success: 'success-icon'
  }
  return typeMap[props.iconType] || 'warning-icon'
})

// 处理确认
function handleConfirm() {
  emit('confirm')
}

// 处理取消
function handleCancel() {
  emit('cancel')
  emit('update:visible', false)
}

// 处理关闭
function handleClose() {
  emit('close')
  emit('update:visible', false)
}
</script>

<style scoped>
.confirm-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 20px 0;
}

.confirm-icon {
  font-size: 24px;
}

.warning-icon {
  color: #e6a23c;
}

.danger-icon {
  color: #f56c6c;
}

.info-icon {
  color: #909399;
}

.success-icon {
  color: #67c23a;
}

.confirm-text {
  font-size: 16px;
  color: #606266;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style> 