// 页面通用工具函数

/**
 * 创建通用的页面数据状态
 * @returns {Object} 包含records、loading、total的响应式对象
 */
export function createPageData() {
  return {
    records: ref([]),
    loading: ref(false),
    total: ref(0)
  }
}

/**
 * 创建通用的搜索处理函数
 * @param {Function} apiFunction - API调用函数
 * @param {Object} pageData - 页面数据对象
 * @param {string} errorMessage - 错误消息
 * @returns {Function} 搜索处理函数
 */
export function createSearchHandler(apiFunction, pageData, errorMessage) {
  return async function(params) {
    pageData.loading.value = true
    try {
      const response = await apiFunction(params)
      pageData.records.value = response.data || []
      pageData.total.value = response.total || 0
    } catch (error) {
      ElMessage.error(errorMessage)
    } finally {
      pageData.loading.value = false
    }
  }
}

/**
 * 创建通用的对话框状态
 * @param {Object} config - 对话框配置
 * @returns {Object} 对话框状态对象
 */
export function createDialogState(config = {}) {
  return reactive({
    visible: false,
    mode: 'add',
    index: null,
    loading: false,
    form: config.form || {},
    config: config
  })
}

/**
 * 创建通用的删除确认对话框状态
 * @returns {Object} 删除对话框状态对象
 */
export function createDeleteDialogState() {
  return reactive({
    visible: false,
    index: null
  })
}

/**
 * 创建通用的对话框处理函数
 * @param {Object} dialogState - 对话框状态
 * @param {Function} createApi - 创建API函数
 * @param {Function} updateApi - 更新API函数
 * @param {Function} searchHandler - 搜索处理函数
 * @returns {Object} 对话框处理函数对象
 */
export function createDialogHandlers(dialogState, createApi, updateApi, searchHandler) {
  return {
    openDialog(mode, index = null) {
      dialogState.mode = mode
      dialogState.visible = true
      dialogState.index = index
      
      if (mode === 'edit' && index !== null) {
        Object.assign(dialogState.form, dialogState.records.value[index])
      } else {
        dialogState.form = { ...dialogState.config.form }
      }
    },

    async handleSubmit(formData) {
      if (dialogState.loading) return
      
      dialogState.loading = true
      try {
        if (dialogState.mode === 'add') {
          await createApi(formData)
          ElMessage.success('新增成功')
        } else {
          const updateData = { ...formData }
          if (dialogState.index !== null && dialogState.records.value[dialogState.index]) {
            updateData.id = dialogState.records.value[dialogState.index].id
          }
          await updateApi(updateData)
          ElMessage.success('编辑成功')
        }
        dialogState.visible = false
        await searchHandler({ page: 1, pageSize: 10 })
      } catch (error) {
        ElMessage.error(dialogState.mode === 'add' ? '新增失败' : '编辑失败')
      } finally {
        dialogState.loading = false
      }
    },

    closeDialog() {
      dialogState.visible = false
    }
  }
}

/**
 * 创建通用的删除处理函数
 * @param {Object} deleteDialogState - 删除对话框状态
 * @param {Function} deleteApi - 删除API函数
 * @param {Function} searchHandler - 搜索处理函数
 * @returns {Object} 删除处理函数对象
 */
export function createDeleteHandlers(deleteDialogState, deleteApi, searchHandler) {
  return {
    confirmDelete(index) {
      deleteDialogState.index = index
      deleteDialogState.visible = true
    },

    async handleDelete() {
      const index = deleteDialogState.index
      try {
        await deleteApi(deleteDialogState.records.value[index].id)
        ElMessage.success('删除成功')
        deleteDialogState.visible = false
        await searchHandler({ page: 1, pageSize: 10 })
      } catch (error) {
        ElMessage.error('删除失败')
      }
    },

    closeDeleteDialog() {
      deleteDialogState.visible = false
    }
  }
} 