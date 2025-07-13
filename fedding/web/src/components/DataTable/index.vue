<template>
  <div class="data-table-container">
    <el-card class="data-card">
      <!-- 搜索表单 -->
      <el-form v-if="showSearch" :inline="true" :model="searchForm" class="search-form">
        <el-form-item 
          v-for="field in searchFields" 
          :key="field.key" 
          :label="field.label"
        >
          <!-- 时间范围选择器 -->
          <el-date-picker
            v-if="field.type === 'datetimerange'"
            v-model="searchForm[field.key]"
            type="datetimerange"
            :placeholder="field.placeholder"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            :default-value="field.defaultValue"
            size="large"
            class="fixed-width-input"
            style="width: 400px;"
          />
          <!-- 日期范围选择器 -->
          <el-date-picker
            v-else-if="field.type === 'daterange'"
            v-model="searchForm[field.key]"
            type="daterange"
            :placeholder="field.placeholder"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            :default-value="field.defaultValue"
            size="large"
            class="fixed-width-input"
            style="width: 300px;"
          />
          <!-- 下拉选择框 -->
          <el-select
            v-else-if="field.type === 'select'"
            v-model="searchForm[field.key]"
            :placeholder="field.placeholder"
            size="large"
            class="fixed-width-input"
            style="width: 180px;"
            clearable
          >
            <el-option
              v-for="opt in field.options || []"
              :key="opt.value"
              :label="opt.label"
              :value="opt.value"
            />
          </el-select>
          <!-- 默认输入框 -->
          <el-input 
            v-else
            v-model="searchForm[field.key]" 
            :placeholder="field.placeholder" 
            clearable 
            size="large" 
            class="fixed-width-input" 
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleSearch">查询</el-button>
          <el-button size="large" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 表格头部操作栏 -->
      <div class="table-header-bar">
        <div style="flex:1"></div>
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
      
      <!-- 数据表格 -->
      <el-table 
        :data="data" 
        style="width: 100%;" 
        :class="tableClass" 
        v-loading="loading"
        :row-key="rowKey"
      >
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column 
          v-for="column in columns" 
          :key="column.prop || column.label"
          :prop="column.prop" 
          :label="column.label" 
          :width="column.width"
          show-overflow-tooltip
          :resizable="true"
        >
          <template #default="scope">
            <!-- 操作按钮列 -->
            <template v-if="column.type === 'actions'">
              <div class="action-buttons">
                <el-button
                  v-for="button in actionButtons"
                  :key="button.action"
                  :type="button.type || 'primary'"
                  :size="button.size || 'large'"
                  :disabled="button.disabled ? button.disabled(scope.row, scope.$index) : false"
                  :loading="button.loading ? button.loading(scope.row, scope.$index) : false"
                  @click="handleActionButtonClick(button.action, scope.row, scope.$index)"
                >
                  {{ button.text }}
                </el-button>
              </div>
            </template>
            <!-- 自定义模板 -->
            <template v-else-if="column.template">
              <component 
                :is="column.template" 
                :row="scope.row" 
                :index="scope.$index"
                @action="handleAction"
              />
            </template>
            <!-- 自定义渲染函数 -->
            <template v-else-if="column.render">
              <span v-if="column.renderHtml" v-html="column.render(scope.row, scope.$index)" @click="handleCustomClick($event, scope.row, scope.$index)"></span>
              <span v-else @click="handleCustomClick($event, scope.row, scope.$index)">{{ column.render(scope.row, scope.$index) }}</span>
            </template>
            <!-- 自定义渲染函数（返回VNode） -->
            <template v-else-if="column.renderFn">
              <component :is="column.renderFn(scope.row, scope.$index, handleAction)" />
            </template>
            <!-- 自定义渲染函数（返回组件配置） -->
            <template v-else-if="column.renderComponent">
              <component 
                :is="column.renderComponent(scope.row, scope.$index).component" 
                v-bind="column.renderComponent(scope.row, scope.$index).props"
                v-on="column.renderComponent(scope.row, scope.$index).events"
              >
                <template v-if="column.renderComponent(scope.row, scope.$index).slots?.default">
                  {{ column.renderComponent(scope.row, scope.$index).slots.default }}
                </template>
              </component>
            </template>
            <!-- 默认显示 -->
            <template v-else-if="column.prop">
              {{ scope.row[column.prop] }}
            </template>
            <!-- 自定义插槽 -->
            <template v-else-if="column.slot">
              <slot :name="column.slot" :row="scope.row" :index="scope.$index"></slot>
            </template>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页组件 -->
      <div class="pagination-box">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          :page-sizes="pageSizes"
          size="large"
          prev-text="上一页"
          next-text="下一页"
          :pager-count="7"
          :hide-on-single-page="false"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { pageSizeCalculators } from '../../utils/pagination'

// Props 定义
const props = defineProps({
  // 数据源
  data: {
    type: Array,
    default: () => []
  },
  // 总数
  total: {
    type: Number,
    default: 0
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false
  },
  // 表格列配置
  columns: {
    type: Array,
    default: () => []
  },
  // 搜索字段配置
  searchFields: {
    type: Array,
    default: () => []
  },
  // 是否显示搜索表单
  showSearch: {
    type: Boolean,
    default: true
  },
  // 表格CSS类名
  tableClass: {
    type: String,
    default: 'data-table'
  },
  // 行键
  rowKey: {
    type: String,
    default: 'id'
  },
  // 分页大小选项
  pageSizes: {
    type: Array,
    default: () => [10, 15, 20, 25, 30, 50, 100]
  },
  // 页面大小计算器名称
  pageSizeCalculator: {
    type: String,
    default: 'default'
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
  }
})

// Emits 定义
const emit = defineEmits([
  'search',
  'reset',
  'page-change',
  'size-change',
  'action',
  'header-action',
  'link-click'
])

// 响应式数据
const searchForm = reactive({})
const pageSize = ref(5)
const currentPage = ref(1)

// 初始化搜索表单
const initSearchForm = () => {
  props.searchFields.forEach(field => {
    // 如果有默认值，使用默认值；否则使用空字符串
    searchForm[field.key] = field.defaultValue || ''
  })
}

// 计算合适的每页显示条数
function calculatePageSize() {
  if (props.pageSizeCalculator && pageSizeCalculators[props.pageSizeCalculator]) {
    pageSize.value = pageSizeCalculators[props.pageSizeCalculator]()
  }
}

// 搜索处理
function handleSearch() {
  currentPage.value = 1
  
  // 处理搜索参数，特别是时间范围
  const searchParams = { ...searchForm, page: currentPage.value, pageSize: pageSize.value }
  
  // 处理时间范围参数
  props.searchFields.forEach(field => {
    if (field.type === 'datetimerange' || field.type === 'daterange') {
      const timeValue = searchForm[field.key]
      if (Array.isArray(timeValue) && timeValue.length === 2) {
        // 将时间范围数组转换为开始和结束时间
        searchParams[`startTime`] = timeValue[0]
        searchParams[`endTime`] = timeValue[1]
        // 保留原始时间范围参数
        searchParams[field.key] = timeValue
      }
    }
  })
  
  emit('search', searchParams)
}

// 重置搜索
function resetSearch() {
  initSearchForm()
  currentPage.value = 1
  
  // 处理重置参数，特别是时间范围
  const resetParams = { page: currentPage.value, pageSize: pageSize.value }
  
  // 处理时间范围参数
  props.searchFields.forEach(field => {
    if (field.type === 'datetimerange' || field.type === 'daterange') {
      const timeValue = searchForm[field.key]
      if (Array.isArray(timeValue) && timeValue.length === 2) {
        // 将时间范围数组转换为开始和结束时间
        resetParams[`startTime`] = timeValue[0]
        resetParams[`endTime`] = timeValue[1]
        // 保留原始时间范围参数
        resetParams[field.key] = timeValue
      }
    }
  })
  
  emit('reset', resetParams)
}

// 页码变化
function handlePageChange(page) {
  currentPage.value = page
  
  // 处理分页参数，特别是时间范围
  const pageParams = { ...searchForm, page: currentPage.value, pageSize: pageSize.value }
  
  // 处理时间范围参数
  props.searchFields.forEach(field => {
    if (field.type === 'datetimerange' || field.type === 'daterange') {
      const timeValue = searchForm[field.key]
      if (Array.isArray(timeValue) && timeValue.length === 2) {
        // 将时间范围数组转换为开始和结束时间
        pageParams[`startTime`] = timeValue[0]
        pageParams[`endTime`] = timeValue[1]
        // 保留原始时间范围参数
        pageParams[field.key] = timeValue
      }
    }
  })
  
  emit('page-change', pageParams)
}

// 每页大小变化
function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
  
  // 处理分页参数，特别是时间范围
  const sizeParams = { ...searchForm, page: currentPage.value, pageSize: pageSize.value }
  
  // 处理时间范围参数
  props.searchFields.forEach(field => {
    if (field.type === 'datetimerange' || field.type === 'daterange') {
      const timeValue = searchForm[field.key]
      if (Array.isArray(timeValue) && timeValue.length === 2) {
        // 将时间范围数组转换为开始和结束时间
        sizeParams[`startTime`] = timeValue[0]
        sizeParams[`endTime`] = timeValue[1]
        // 保留原始时间范围参数
        sizeParams[field.key] = timeValue
      }
    }
  })
  
  emit('size-change', sizeParams)
}

// 处理自定义操作
function handleAction(action, row, index) {
  emit('action', { action, row, index })
}

// 处理自定义点击事件
function handleCustomClick(event, row, index) {
  if (event.target.tagName === 'BUTTON') {
    const action = event.target.getAttribute('data-action')
    const dataIndex = event.target.getAttribute('data-index')
    if (action) {
      emit('action', { action, row, index: dataIndex ? parseInt(dataIndex) : index })
    }
  } else if (event.target.tagName === 'A') {
    // 处理链接点击事件
    const rowId = event.target.getAttribute('data-row-id')
    if (rowId) {
      emit('link-click', { row, index })
    }
  }
}

// 处理操作按钮点击
function handleActionButtonClick(action, row, index) {
  emit('action', { action, row, index })
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

// 处理头部按钮点击
function handleHeaderButtonClick(action) {
  emit('header-action', { action })
}



// 监听数据变化，重新计算分页
watch(() => props.total, () => {
  if (currentPage.value > 1 && props.total === 0) {
    currentPage.value = 1
  }
})

// 生命周期
onMounted(() => {
  initSearchForm()
  calculatePageSize()
  
  // 监听窗口大小变化
  window.addEventListener('resize', calculatePageSize)
})

onUnmounted(() => {
  window.removeEventListener('resize', calculatePageSize)
})
</script>

<style scoped>
.data-table-container {
  margin: 20px;
  height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
}

.data-card {
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

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 24px;
  margin-bottom: 24px;
}

.table-header-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 12px;
}

.data-table {
  margin-bottom: 24px;
  font-size: 18px !important;
  flex: 1;
  min-height: 0;
  max-height: calc(100vh - 300px);
  overflow: auto;
}

/* 表格基础样式 - 使用Element Plus默认样式 */

.pagination-box {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  flex-shrink: 0;
  padding: 10px 0;
}

/* 字体大小设置 */
:deep(.el-form-item__label),
:deep(.search-form .el-input__inner),
:deep(.data-table .el-table__cell),
:deep(.feed-table .el-table__cell),
:deep(.return-table .el-table__cell),
:deep(.tank-table .el-table__cell) {
  font-size: 18px !important;
}

/* 确保表格行高足够显示完整内容 */
:deep(.el-table .el-table__cell) {
  padding: 8px 0 !important;
  line-height: 1.8 !important;
  min-height: 50px !important;
}

/* 调整span的行高 */
:deep(.el-table .el-table__cell span) {
  display: inline-block !important;
  vertical-align: middle !important;
  overflow: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
  max-width: 100% !important;
}

/* 按钮样式 */
:deep(.action-buttons .el-button),
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

/* 分页组件样式 */
:deep(.el-pagination .el-pagination__total),
:deep(.el-pagination .el-pagination__total span),
:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner),
:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner input),
:deep(.el-pagination .el-pagination__jump),
:deep(.el-pagination .el-pagination__jump .el-pagination__goto),
:deep(.el-pagination .el-pagination__jump .el-pagination__goto input),
:deep(.el-pagination .el-pagination__sizes .el-select-dropdown .el-select-dropdown__item) {
  font-size: 16px;
}

/* 按钮容器样式 */
.action-buttons,
.header-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.header-buttons {
  margin-right: 8px;
}
</style> 