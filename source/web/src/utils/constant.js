// 角色常量映射表
export const ROLE_MAP = {
  Admin: '管理员',
  MaterialClerk: '物料员',
  SeniorOperator: '高级操作员',
  Operator: '操作员',
  Accountant: '会计'
}

// 班次常量映射表
export const SHIFT_TYPE_MAP = {
  day: '白班',
  night: '夜班'
}

// 材料常量映射表
export const MATERIAL_MAP = {
  '10KV': '10KV',
  '35KV': '35KV'
}

// 加料状态映射表
export const STATUS_MAP = {
  '-1': '撤销',
  '0': '申请加料',
  '1': '已加料',
  '2': '已退料',
  '3': '备料',
  '4': '已备料',
  '5': '已领料',
  '6': '待加阻燃粉'
}

// 通用：由 MAP 生成 options
export function getOptions(map) {
  return Object.entries(map).map(([value, label]) => ({ label, value }))
}

// 通用：由 MAP 获取 label
export function getLabel(map, value) {
  return map[value] || '-'
}

export const COLOR_MAP = {  
  BTN_BLUE1: 'rgb(51, 126, 204)',
  BTN_BLUE2: 'rgb(121, 187, 255)',
  BTN_GREEN1: 'rgb(82, 155, 46)',
  BTN_GREEN2: 'rgb(149, 212, 117)',
  BTN_RED: '#f56c6c',
  BTN_YELLOW: '#e6a23c',
  BG_BLUE: 'rgb(217, 236, 255)',
  BG_GREEN: 'rgb(225, 243, 216)',
  BG_RED: 'rgb(248, 152, 152)',
  BG_YELLOW: 'rgb(250, 236, 216)',
}