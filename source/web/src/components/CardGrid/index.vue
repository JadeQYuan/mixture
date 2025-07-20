<template>
  <div class="card-grid-container" v-loading="pageLoading" element-loading-text="页面加载中...">
    <el-card class="card-grid-card">
      <!-- 头部操作栏 -->
      <div class="card-header-bar">
        <div style="flex:1"></div>
        <!-- 刷新按钮 -->
        <el-button
          type="primary"
          size="large"
          :loading="loading"
          @click="handleRefresh"
        >
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <!-- 配置化的头部操作按钮 -->
        <div v-if="headerButtons.length > 0" class="header-buttons">
          <el-button
            v-for="button in headerButtons"
            :key="button.action"
            v-show="hasButtonPermission(button)"
            :type="button.type || 'primary'"
            :size="button.size || 'large'"
            :disabled="button.disabled"
            :loading="button.loading"
            @click="handleHeaderButtonClick(button.action)"
          >
            {{ button.text }}
          </el-button>
        </div>
        <!-- 自定义头部操作插槽 -->
        <slot name="header-actions"></slot>
      </div>
      
      <!-- 卡片网格 -->
      <div class="card-grid" v-loading="loading">
        <el-card
          v-for="(item, index) in data"
          :key="item.id || index"
          class="grid-card"
          shadow="hover"
          :header="getHeader(item)"
        >
          <!-- 卡片内容 -->
          <div class="card-content">
            <!-- 信息展示区域 -->
            <div class="card-info">
              <div 
                v-for="field in displayFields" 
                :key="field.prop"
                class="info-item"
              >
                <span class="info-label">{{ field.label }}:</span>
                <span class="info-value">{{ item[field.prop] || '-' }}</span>
              </div>
            </div>
            
            <!-- 操作按钮区域 -->
            <div class="card-actions">
              <el-button
                v-for="button in actionButtons"
                :key="button.action"
                :type="button.type || 'primary'"
                :size="button.size || 'large'"
                :disabled="button.disabled ? button.disabled(item, index) : false"
                :loading="button.loading ? button.loading(item, index) : false"
                @click="handleActionButtonClick(button.action, item, index)"
              >
                {{ button.text }}
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 空状态 -->
      <div v-if="!loading && data.length === 0" class="empty-state">
        <el-empty description="暂无数据" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { Refresh } from '@element-plus/icons-vue'

// Props 定义
const props = defineProps({
  // 数据源
  data: {
    type: Array,
    default: () => []
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 页面加载状态
  pageLoading: {
    type: Boolean,
    default: false
  },
  // 显示字段配置
  displayFields: {
    type: Array,
    default: () => []
  },
  // 操作按钮配置
  actionButtons: {
    type: Array,
    default: () => []
  },
  // 头部操作按钮配置
  headerButtons: {
    type: Array,
    default: () => []
  },
  // 当前用户角色
  userRole: {
    type: String,
    default: ''
  },
  // 是否启用自动刷新
  autoRefresh: {
    type: Boolean,
    default: false
  },
  // 自动刷新间隔（毫秒）
  refreshInterval: {
    type: Number,
    default: 5000
  },
  // 卡片header字段名
  headerField: {
    type: String,
    default: ''
  },
  // 卡片header渲染函数
  headerRender: {
    type: Function,
    default: null
  }
})

// Emits 定义
const emit = defineEmits([
  'refresh',
  'action',
  'header-action'
])

// 处理刷新按钮点击
function handleRefresh() {
  emit('refresh')
}

// 处理操作按钮点击
function handleActionButtonClick(action, item, index) {
  emit('action', { action, row: item, index })
}

// 检查按钮权限
function hasButtonPermission(button) {
  // 如果按钮没有设置角色限制，则所有角色都可以访问
  if (!button.roles || button.roles.length === 0) {
    return true
  }
  
  // 检查当前用户角色是否在允许的角色列表中
  return button.roles.includes(props.userRole)
}

// 自动刷新定时器
const autoRefreshTimer = ref(null)

// 启动自动刷新
function startAutoRefresh() {
  if (props.autoRefresh && !autoRefreshTimer.value) {
    autoRefreshTimer.value = setInterval(() => {
      emit('refresh')
    }, props.refreshInterval)
  }
}

// 停止自动刷新
function stopAutoRefresh() {
  if (autoRefreshTimer.value) {
    clearInterval(autoRefreshTimer.value)
    autoRefreshTimer.value = null
  }
}

// 处理头部按钮点击
function handleHeaderButtonClick(action) {
  emit('header-action', { action })
}

// 获取卡片header
function getHeader(item) {
  if (props.headerRender) {
    return props.headerRender(item)
  }
  if (props.headerField) {
    return item[props.headerField] || ''
  }
  return ''
}

// 生命周期
onMounted(() => {
  startAutoRefresh()
})

onUnmounted(() => {
  stopAutoRefresh()
})

// 新增：监听autoRefresh和refreshInterval变化，动态启停定时器
watch(
  () => [props.autoRefresh, props.refreshInterval],
  ([newAuto, newInterval], [oldAuto, oldInterval]) => {
    stopAutoRefresh()
    if (newAuto) {
      startAutoRefresh()
    }
  }
)
</script>

<style scoped>
.card-grid-container {
  margin: 20px;
  height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
}

.card-grid-card {
  background: rgba(255,255,255,0.8);
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  padding: 48px 40px 32px 40px;
  font-size: 1.2em;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.card-header-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 24px;
  gap: 12px;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  flex: 1;
  overflow-y: auto;
  padding: 0;
  max-height: 650px;
}

.grid-card {
  background: rgba(255,255,255,0.9);
  border-radius: 16px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.grid-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0,0,0,0.15);
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;
}

.info-label {
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

.info-value {
  color: #333;
  font-size: 14px;
  text-align: right;
  word-break: break-all;
}

.card-actions {
  display: flex;
  gap: 8px;
  justify-content: center;
  margin-top: 8px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  flex: 1;
  min-height: 200px;
}

/* 字体大小设置 */
:deep(.el-form-item__label),
:deep(.search-form .el-input__inner) {
  font-size: 18px !important;
}

/* 按钮样式 */
:deep(.card-actions .el-button),
:deep(.header-buttons .el-button) {
  font-size: 16px !important;
  height: 40px !important;
  padding: 0 16px !important;
  margin: 0;
}

/* 查询表单按钮样式 */
:deep(.search-form .el-button) {
  font-size: 16px !important;
  height: 40px !important;
  padding: 0 16px !important;
  margin: 0 8px 0 0 !important;
}

/* 固定输入框宽度，避免清除按钮导致宽度变化 */
.fixed-width-input {
  width: 180px !important;
}

.fixed-width-input .el-input__wrapper {
  width: 100% !important;
}

/* 按钮容器样式 */
.header-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-right: 8px;
}

/* 卡片header字体大小设置 */
:deep(.grid-card .el-card__header) {
  font-size: 32px !important;
  font-weight: 600 !important;
  color: #333 !important;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
  
  .card-grid-container {
    margin: 10px;
  }
  
  .card-grid-card {
    padding: 24px 20px 16px 20px;
  }
  
  /* 移动端卡片header字体大小 */
  :deep(.grid-card .el-card__header) {
    font-size: 16px !important;
  }
}
</style> 