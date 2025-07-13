// 退料申请表单配置
export function getReturnApplyFormConfig() {
  return {
    // 表单字段配置
    fields: [
      {
        prop: 'bucketNo',
        label: '料罐',
        type: 'select',
        placeholder: '请选择料罐',
        required: true,
        options: [], // 动态加载
        size: 'large',
        style: { width: '100%' }
      }
    ],
    
    // 表单验证规则
    rules: {
      bucketNo: [
        { required: true, message: '请选择料罐', trigger: 'change' }
      ]
    },
    
    // 表单样式配置
    labelWidth: '120px',
    labelPosition: 'left',
    
    // 底部按钮配置
    footerButtons: [
      {
        action: 'submit',
        text: '提交申请',
        type: 'primary',
        size: 'large'
      }
    ]
  }
} 