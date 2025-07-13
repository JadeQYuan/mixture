// FeedManage 页面表单配置

// 加料操作步骤配置
export const feedOperationSteps = [
  {
    title: '底罐重量',
    description: '记录底罐重量'
  },
  {
    title: '加料重量',
    description: '记录加料重量'
  },
  {
    title: '阻燃粉重量',
    description: '输入阻燃粉重量'
  },
  {
    title: '确认提交',
    description: '确认所有信息并提交'
  }
]

// 加料操作字段配置
export const feedOperationFields = [
  {
    prop: 'bucketNo',
    label: '罐号',
    type: 'input',
    disabled: true,
    size: 'large'
  },
  {
    prop: 'spec',
    label: '加料规格',
    type: 'input',
    disabled: true,
    size: 'large'
  },
  {
    prop: 'capacity',
    label: '底罐重量',
    type: 'input',
    disabled: true,
    size: 'large',
    suffix: 'kg',
    style: { width: '100%' }
  },
  {
    prop: 'capacityAdd',
    label: '加料重量',
    type: 'input',
    disabled: true,
    size: 'large',
    suffix: 'kg',
    style: { width: '100%' },
    // 只在第1步（加料重量步骤）显示
    visible: (currentStep) => currentStep >= 1
  },
  {
    prop: 'abs',
    label: '阻燃粉重量',
    type: 'number',
    size: 'large',
    suffix: 'kg',
    style: { width: '100%' },
    min: 0,
    precision: 2,
    // 只在第2步（阻燃粉重量步骤）显示和可编辑
    visible: (currentStep) => currentStep >= 2,
    disabled: (currentStep) => currentStep !== 2
  }
]

// 加料操作验证规则
export const feedOperationRules = {
  abs: [
    { required: true, message: '请输入阻燃粉重量', trigger: 'blur' }
  ]
}

// 获取加料操作表单配置
export function getFeedOperationConfig() {
  return {
    title: '加料操作',
    width: '700px',
    fields: feedOperationFields,
    rules: feedOperationRules,
    steps: feedOperationSteps
  }
} 