// TankManage 页面表单配置

// 料罐表单字段配置
export const tankFormFields = [
  {
    prop: 'tankNo',
    label: '料罐编号',
    type: 'input',
    placeholder: '请输入料罐编号',
    size: 'large'
  },
  {
    prop: 'remark',
    label: '描述',
    type: 'textarea',
    placeholder: '请输入描述',
    rows: 3,
    size: 'large',
    // 示例：根据编号是否填写来决定是否显示描述字段
    visible: (currentStep, formData) => formData.tankNo && formData.tankNo.trim() !== ''
  }
]

// 料罐表单验证规则
export const tankFormRules = {
  tankNo: [
    { required: true, message: '请输入料罐编号', trigger: 'blur' }
  ],
  remark: [
    { required: false, message: '请输入描述', trigger: 'blur' }
  ]
}

// 获取料罐表单配置
export function getTankFormConfig() {
  return {
    title: '料罐信息',
    width: '700px',
    fields: tankFormFields,
    rules: tankFormRules,
    buttons: [
      { text: '取消', type: 'default', action: 'cancel' },
      { text: '确定', type: 'primary', action: 'submit' }
    ]
  }
} 