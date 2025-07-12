import { ElMessage } from 'element-plus'

// 替换全局ElMessage
export function setupMessage() {
  // 配置全局默认值
  ElMessage.defaults = {
    ...ElMessage.defaults,
    duration: 2000, // 消息显示2秒
    showClose: true, // 显示关闭按钮
    offset: 20 // 消息距离顶部的距离
  }
} 