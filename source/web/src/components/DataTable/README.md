# DataTable 通用列表组件

一个功能完整的通用数据表格组件，支持搜索、分页、自定义列渲染和操作按钮配置。

## 功能特性

- ✅ 搜索表单（可配置字段）
- ✅ 数据表格（支持多种渲染方式）
- ✅ 分页组件
- ✅ 操作按钮配置
- ✅ 自定义列渲染
- ✅ 响应式设计
- ✅ 字体大小统一（20px）

## 基本用法

```vue
<template>
  <DataTable
    :data="records"
    :total="total"
    :loading="loading"
    :columns="columns"
    :search-fields="searchFields"
    :action-buttons="actionButtons"
    @search="handleSearch"
    @reset="handleReset"
    @page-change="handlePageChange"
    @size-change="handleSizeChange"
    @action="handleAction"
  />
</template>
```

## Props 配置

### 基础配置

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| data | Array | [] | 表格数据 |
| total | Number | 0 | 数据总数 |
| loading | Boolean | false | 加载状态 |
| columns | Array | [] | 表格列配置 |
| searchFields | Array | [] | 搜索字段配置 |
| actionButtons | Array | [] | 操作按钮配置 |
| headerButtons | Array | [] | 头部操作按钮配置 |
| showSearch | Boolean | true | 是否显示搜索表单 |
| tableClass | String | 'data-table' | 表格CSS类名 |
| rowKey | String | 'id' | 行键字段 |
| pageSizes | Array | [10,15,20,25,30,50,100] | 分页大小选项 |
| pageSizeCalculator | String | 'default' | 页面大小计算器名称 |

## 搜索字段配置

```javascript
const searchFields = [
  {
    key: 'userKey',        // 字段键名
    label: '人员',          // 标签文本
    placeholder: '请输入人员姓名/工号'  // 占位符
  },
  {
    key: 'bucketNo',
    label: '罐号',
    placeholder: '请输入罐号'
  }
]
```

## 表格列配置

### 基础列配置

```javascript
const columns = [
  {
    prop: 'userName',      // 数据字段名
    label: '人员',          // 列标题
    width: '160'           // 列宽度
  },
  {
    prop: 'bucketNo',
    label: '罐号',
    width: '160'
  }
]
```

### 列配置选项

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| prop | String | - | 数据字段名 |
| label | String | - | 列标题 |
| width | String | - | 列宽度 |
| render | Function | - | 自定义渲染函数 |
| renderHtml | Boolean | false | 渲染函数是否返回HTML内容 |
| type | String | - | 特殊列类型（如'actions'） |

### 默认功能

组件默认启用以下功能：
- `show-overflow-tooltip`: 文本溢出时显示tooltip
- `resizable`: 列宽可调整

### 自定义渲染列

```javascript
const columns = [
  {
    prop: 'userName',
    label: '人员',
    width: '160',
    render: (row) => `${row.userName}(${row.account})`  // 自定义渲染函数
  },
  {
    prop: 'capacity',
    label: '重量',
    width: '160',
    render: (row) => `${row.capacity} kg`
  },
  {
    prop: 'photo',
    label: '照片',
    width: '120',
    renderHtml: true,  // 返回HTML内容
    render: (row) => {
      if (row.photo) {
        return `<a href="javascript:void(0)" style="color: #409eff;">查看</a>`
      } else {
        return `<span style="color: #999;">-</span>`
      }
    }
  }
]
```

### 操作按钮列

```javascript
const columns = [
  // ... 其他列
  {
    type: 'actions',       // 操作按钮列类型
    label: '操作',
    width: '300'
  }
]
```

## 操作按钮配置

### 基础按钮配置

```javascript
const actionButtons = [
  {
    action: 'edit',        // 操作标识
    text: '编辑',          // 按钮文本
    type: 'primary',       // 按钮类型
    size: 'large'          // 按钮大小
  },
  {
    action: 'delete',
    text: '删除',
    type: 'danger',
    size: 'large'
  }
]
```

### 高级按钮配置

```javascript
const actionButtons = [
  {
    action: 'edit',
    text: '编辑',
    type: 'primary',
    size: 'large'
  },
  {
    action: 'delete',
    text: '删除',
    type: 'danger',
    size: 'large',
    disabled: (row) => row.status === 'inactive'  // 条件禁用
  },
  {
    action: 'process',
    text: '处理',
    type: 'success',
    size: 'large',
    loading: (row) => row.processing  // 条件加载状态
  }
]
```

### 按钮配置选项

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| action | String | - | 操作标识（必填） |
| text | String | - | 按钮文本（必填） |
| type | String | 'primary' | 按钮类型：primary/success/warning/danger/info |
| size | String | 'large' | 按钮大小：large/default/small |
| disabled | Function | - | 禁用条件函数，接收(row, index)参数 |
| loading | Function | - | 加载状态函数，接收(row, index)参数 |

## 头部操作按钮配置

### 基础头部按钮配置

```javascript
const headerButtons = [
  {
    action: 'add',         // 操作标识
    text: '新增',          // 按钮文本
    type: 'primary',       // 按钮类型
    size: 'large'          // 按钮大小
  },
  {
    action: 'export',
    text: '导出',
    type: 'success',
    size: 'large'
  },
  {
    action: 'import',
    text: '导入',
    type: 'warning',
    size: 'large'
  }
]
```

### 高级头部按钮配置

```javascript
const headerButtons = [
  {
    action: 'add',
    text: '新增',
    type: 'primary',
    size: 'large'
  },
  {
    action: 'export',
    text: '导出',
    type: 'success',
    size: 'large',
    disabled: false,       // 禁用状态
    loading: false         // 加载状态
  },
  {
    action: 'batch-delete',
    text: '批量删除',
    type: 'danger',
    size: 'large',
    disabled: true         // 条件禁用
  }
]
```

### 头部按钮配置选项

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| action | String | - | 操作标识（必填） |
| text | String | - | 按钮文本（必填） |
| type | String | 'primary' | 按钮类型：primary/success/warning/danger/info |
| size | String | 'large' | 按钮大小：large/default/small |
| disabled | Boolean | false | 禁用状态 |
| loading | Boolean | false | 加载状态 |

## 事件处理

### 搜索事件

```javascript
async function handleSearch(params) {
  // params 包含搜索条件和分页信息
  const { userKey, bucketNo, page, pageSize } = params
  // 调用API获取数据
  const response = await getData(params)
  records.value = response.data
  total.value = response.total
}
```

### 操作事件

```javascript
function handleAction({ action, row, index }) {
  switch (action) {
    case 'edit':
      // 处理编辑操作
      openEditDialog(row)
      break
    case 'delete':
      // 处理删除操作
      handleDelete(row)
      break
    case 'view':
      // 处理查看操作
      openViewDialog(row)
      break
  }
}
```

### 头部操作事件

```javascript
function handleHeaderAction({ action }) {
  switch (action) {
    case 'add':
      // 处理新增操作
      openAddDialog()
      break
    case 'export':
      // 处理导出操作
      handleExport()
      break
    case 'import':
      // 处理导入操作
      handleImport()
      break
  }
}
```

## 插槽

### header-actions 插槽

用于在表格头部添加自定义操作按钮（与配置化按钮并存）：

```vue
<DataTable :columns="columns" :data="data" :header-buttons="headerButtons">
  <template #header-actions>
    <el-button type="info" size="large" @click="handleCustomAction">自定义操作</el-button>
  </template>
</DataTable>
```

## 样式定制

组件内置了统一的字体大小（20px）和表格样式，支持以下自定义：

### 自定义表格类名

```vue
<DataTable 
  :table-class="'my-custom-table'"
  :columns="columns" 
  :data="data" 
/>
```

### 自定义样式

```css
/* 自定义表格样式 */
:deep(.my-custom-table .el-table__cell) {
  font-size: 18px !important;
}

/* 自定义操作按钮样式 */
:deep(.my-custom-table .action-buttons .el-button) {
  margin-right: 8px;
}
```

## 配置文件使用

### 使用页面配置文件（推荐）

每个页面都有独立的配置文件，与Vue文件放在同一目录下：

```vue
<template>
  <DataTable
    :data="records"
    :total="total"
    :loading="loading"
    :columns="columns"
    :search-fields="searchFields"
    :action-buttons="actionButtons"
    :header-buttons="headerButtons"
    :table-class="tableConfig.tableClass"
    :page-size-calculator="tableConfig.pageSizeCalculator"
    @search="handleSearch"
    @reset="handleReset"
    @page-change="handlePageChange"
    @size-change="handleSizeChange"
    @action="handleAction"
    @header-action="handleHeaderAction"
  />
</template>

<script setup>
import { ref } from 'vue'
import DataTable from '../components/DataTable.vue'
import { getConfig } from './页面名.config'

// 数据
const records = ref([])
const total = ref(0)
const loading = ref(false)

// 从配置文件获取表格配置
const tableConfig = getConfig()
const { searchFields, columns, actionButtons, headerButtons } = tableConfig

// 事件处理
async function handleSearch(params) {
  loading.value = true
  try {
    const response = await getData(params)
    records.value = response.data
    total.value = response.total
  } finally {
    loading.value = false
  }
}

function handleAction({ action, row, index }) {
  if (action === 'feed') {
    openFeedDialog(index)
  }
}

function handleHeaderAction({ action }) {
  if (action === 'add') {
    openAddDialog()
  }
}
</script>
```

### 手动配置（传统方式）

```vue
<template>
  <DataTable
    :data="records"
    :total="total"
    :loading="loading"
    :columns="columns"
    :search-fields="searchFields"
    :action-buttons="actionButtons"
    :header-buttons="headerButtons"
    :table-class="'feed-table'"
    :page-size-calculator="'feedManage'"
    @search="handleSearch"
    @reset="handleReset"
    @page-change="handlePageChange"
    @size-change="handleSizeChange"
    @action="handleAction"
    @header-action="handleHeaderAction"
  />
</template>

<script setup>
import { ref } from 'vue'
import DataTable from '../components/DataTable.vue'

// 数据
const records = ref([])
const total = ref(0)
const loading = ref(false)

// 搜索字段配置
const searchFields = [
  {
    key: 'userKey',
    label: '人员',
    placeholder: '请输入人员姓名/工号'
  },
  {
    key: 'bucketNo',
    label: '罐号',
    placeholder: '请输入罐号'
  }
]

// 表格列配置
const columns = [
  {
    prop: 'userName',
    label: '人员',
    width: '160',
    render: (row) => `${row.userName}(${row.account})`
  },
  {
    prop: 'bucketNo',
    label: '罐号',
    width: '160'
  },
  {
    prop: 'updateTime',
    label: '时间'
  },
  {
    type: 'actions',
    label: '操作',
    width: '240'
  }
]

// 操作按钮配置
const actionButtons = [
  {
    action: 'feed',
    text: '加料',
    type: 'primary',
    size: 'large'
  }
]

// 头部操作按钮配置
const headerButtons = [
  {
    action: 'add',
    text: '新增',
    type: 'primary',
    size: 'large'
  }
]

// 事件处理
async function handleSearch(params) {
  loading.value = true
  try {
    const response = await getData(params)
    records.value = response.data
    total.value = response.total
  } finally {
    loading.value = false
  }
}

function handleAction({ action, row, index }) {
  if (action === 'feed') {
    openFeedDialog(index)
  }
}

function handleHeaderAction({ action }) {
  if (action === 'add') {
    openAddDialog()
  }
}
</script>
```

## 配置文件

### 配置文件位置
- 每个页面都有独立的配置文件，与Vue文件放在同一目录下
- 配置文件命名格式：`页面名.config.js`
- 例如：`FeedManage.vue` 对应 `FeedManage.config.js`

### 配置文件结构
每个配置文件包含以下导出：

```javascript
// 搜索字段配置
export const searchFields = [...]

// 表格列配置
export const columns = [...]

// 操作按钮配置
export const actionButtons = [...]

// 头部按钮配置
export const headerButtons = [...]

// 表格类名
export const tableClass = 'my-table'

// 分页计算器名称
export const pageSizeCalculator = 'default'

// 获取完整配置的函数
export function getConfig() {
  return {
    searchFields,
    columns,
    actionButtons,
    headerButtons,
    tableClass,
    pageSizeCalculator
  }
}
```

### 使用方法
```javascript
import { getConfig } from './页面名.config'

// 获取完整配置
const tableConfig = getConfig()
const { searchFields, columns, actionButtons, headerButtons } = tableConfig
```

## 注意事项

1. **字体大小**：组件统一使用20px字体大小，确保界面一致性
2. **按钮配置**：操作按钮通过配置数组定义，支持条件禁用和加载状态
3. **事件处理**：所有操作通过统一的action事件处理，便于维护
4. **响应式**：组件支持响应式设计，自动适应不同屏幕尺寸
5. **样式穿透**：使用`:deep()`确保样式正确应用到Element Plus组件
6. **配置管理**：推荐使用配置文件统一管理表格配置，便于维护和复用 