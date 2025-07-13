// ReturnManage 页面表单配置

// 退料操作步骤配置
export const returnOperationSteps = [
  {
    title: '底罐重量',
    description: '记录底罐重量'
  },
  {
    title: '确认提交',
    description: '确认所有信息并提交'
  }
]

// 退料操作字段配置
export const returnOperationFields = [
  {
    prop: 'bucketNo',
    label: '罐号',
    type: 'input',
    disabled: true,
    size: 'large'
  },
  {
    prop: 'capacity',
    label: '当前重量',
    type: 'input',
    disabled: true,
    size: 'large',
    suffix: 'kg',
    style: { width: '100%' }
  },
]

// 获取退料操作表单配置
export function getReturnOperationConfig() {
  return {
    title: '退料操作',
    width: '700px',
    fields: returnOperationFields,
    steps: returnOperationSteps
  }
} 