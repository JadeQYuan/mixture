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

// 通用：由 MAP 生成 options
export function getOptions(map) {
  return Object.entries(map).map(([value, label]) => ({ label, value }))
}

// 通用：由 MAP 获取 label
export function getLabel(map, value) {
  return map[value] || '-'
}
