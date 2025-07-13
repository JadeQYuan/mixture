// 加料申请表单配置
export function getFeedApplyFormConfig() {
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
      },
      {
        prop: 'spec',
        label: '加料规格',
        type: 'select',
        placeholder: '请选择加料规格',
        required: true,
        options: [
          { label: '10KV', value: '10KV' },
          { label: '35KV', value: '35KV' }
        ],
        size: 'large',
        style: { width: '100%' }
      },
      {
        prop: 'capacity',
        label: '计划加料重量',
        type: 'number',
        placeholder: '请输入重量',
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
    
    // 表单验证规则
    rules: {
      bucketNo: [
        { required: true, message: '请选择料罐', trigger: 'change' }
      ],
      spec: [
        { required: true, message: '请选择加料规格', trigger: 'change' }
      ],
      capacity: [
        { required: true, message: '请输入计划加料重量', trigger: 'blur' }
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