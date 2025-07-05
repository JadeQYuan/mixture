/**
 * 根据窗口高度计算合适的每页显示条数
 * @param {Object} options 配置选项
 * @param {number} options.cardPadding 卡片内边距 (默认: 160)
 * @param {number} options.searchFormHeight 搜索表单高度 (默认: 80)
 * @param {number} options.tableHeaderHeight 表格头部高度 (默认: 50)
 * @param {number} options.paginationHeight 分页组件高度 (默认: 50)
 * @param {number} options.tableRowHeight 每行表格高度 (默认: 60)
 * @param {number} options.extraSpace 额外预留空间 (默认: 100)
 * @param {number} options.minRows 最少显示行数 (默认: 3)
 * @param {number} options.maxRows 最多显示行数 (默认: 15)
 * @returns {number} 计算得出的每页显示条数
 */
export function calculatePageSize(options = {}) {
  const {
    cardPadding = 160, // 48 + 40 + 32 + 40
    searchFormHeight = 80,
    tableHeaderHeight = 50,
    paginationHeight = 50,
    tableRowHeight = 60,
    extraSpace = 100,
    minRows = 3,
    maxRows = 15
  } = options

  const windowHeight = window.innerHeight
  
  // 计算可用高度
  const availableHeight = windowHeight - cardPadding - searchFormHeight - tableHeaderHeight - paginationHeight - extraSpace
  
  // 计算可以显示的行数
  const maxRowsCalculated = Math.floor(availableHeight / tableRowHeight)
  
  // 确保在最小和最大行数范围内
  return Math.max(minRows, Math.min(maxRowsCalculated, maxRows))
}

/**
 * 创建分页条数计算器的工厂函数
 * @param {Object} options 配置选项
 * @returns {Function} 返回一个计算函数
 */
export function createPageSizeCalculator(options = {}) {
  return () => calculatePageSize(options)
}

/**
 * 默认的分页条数计算器
 */
export const defaultPageSizeCalculator = createPageSizeCalculator()

/**
 * 为不同页面预设的计算器
 */
export const pageSizeCalculators = {
  // 用户管理页面
  userManage: createPageSizeCalculator({
    tableRowHeight: 60,
    minRows: 3,
    maxRows: 15
  }),
  
  // 料罐管理页面
  tankManage: createPageSizeCalculator({
    tableRowHeight: 60,
    minRows: 3,
    maxRows: 15
  }),
  
  // 加料管理页面
  feedManage: createPageSizeCalculator({
    tableRowHeight: 60,
    minRows: 3,
    maxRows: 15
  }),
  
  // 退料管理页面
  returnManage: createPageSizeCalculator({
    tableRowHeight: 60,
    minRows: 3,
    maxRows: 15
  }),
  
  // 加料记录页面
  feedRecord: createPageSizeCalculator({
    tableRowHeight: 60,
    minRows: 3,
    maxRows: 15
  })
}

/**
 * 设置分页组件的中文文本
 * @param {HTMLElement} paginationElement 分页组件元素
 */
export function setPaginationChineseText(paginationElement) {
  if (!paginationElement) return
  
  // 设置总数文本
  const totalElement = paginationElement.querySelector('.el-pagination__total')
  if (totalElement) {
    const text = totalElement.textContent
    if (text.includes('Total')) {
      totalElement.textContent = text.replace('Total', '共').replace('items', '条')
    }
  }
  
  // 设置每页条数文本
  const sizesElement = paginationElement.querySelector('.el-pagination__sizes')
  if (sizesElement) {
    const text = sizesElement.textContent
    if (text.includes('items per page')) {
      sizesElement.textContent = text.replace('items per page', '条/页')
    }
  }
  
  // 设置跳转文本
  const jumpElement = paginationElement.querySelector('.el-pagination__jump')
  if (jumpElement) {
    const text = jumpElement.textContent
    if (text.includes('Go to')) {
      jumpElement.textContent = text.replace('Go to', '前往第').replace('page', '页')
    }
  }
} 