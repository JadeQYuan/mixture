// 卡片显示字段配置
export const displayFields = [
  // {
  //   prop: 'tankNo',
  //   label: '料罐',
  // }
]

// 操作按钮配置
export const actionButtons = [
  {
    type: 'warning',
    text: '退料',
    action: 'return',
    size: 'large'
  }
]

// 头部按钮配置
export const headerButtons = []

// 退料操作对话框表单配置
export function getReturnOperationConfig() {
  return {
    title: '退料',
    width: '700px',
    fields: [
      {
        prop: 'tankNo',
        label: '料罐',
        type: 'input',
        disabled: true,
        size: 'large',
        style: { width: '100%' }
      },
      {
        prop: 'returnWeight',
        label: '退料',
        type: 'number',
        required: true,
        size: 'large',
        style: { width: '100%' },
        suffix: 'kg',
        inputType: 'number',
        props: {
          min: 0.01,
          step: 1
        }
      }
    ],
    rules: {
      returnWeight: [
        { required: true, message: '请输入退料重量', trigger: 'blur' }
      ]
    },
    steps: [
      {
        title: '底罐重量',
        description: '记录底罐重量'
      },
      {
        title: '确认提交',
        description: '确认信息并提交'
      }
    ]
  }
} 