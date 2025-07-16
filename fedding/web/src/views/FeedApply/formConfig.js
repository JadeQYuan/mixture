// 加料申请表单配置
export function getFeedApplyFormConfig() {
  return {
    title: '加料申请',
    width: '600px',
    // 表单字段配置
    fields: [
      {
        prop: 'tankNo',
        label: '料罐编号',
        type: 'input',
        placeholder: '料罐编号',
        disabled: true,
        size: 'large',
        style: { width: '100%' }
      },
      {
        prop: 'shiftType',
        label: '班次',
        type: 'radio',
        placeholder: '请选择班次',
        required: true,
        options: [
          { label: '白班', value: 'day' },
          { label: '夜班', value: 'night' }
        ],
        size: 'large',
        style: { width: '100%' }
      },
      {
        prop: 'materialName',
        label: '材料名称',
        type: 'radio',
        placeholder: '请选择材料名称',
        required: true,
        options: [
          { label: '10KV', value: '10KV' },
          { label: '35KV', value: '35KV' }
        ],
        size: 'large',
        style: { width: '100%' }
      },
      {
        prop: 'productSpec',
        label: '产品规格型号',
        type: 'input',
        placeholder: '请输入产品规格型号',
        required: true,
        size: 'large',
        style: { width: '100%' }
      },
      {
        prop: 'planWeight',
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
      tankNo: [
        { required: true, message: '料罐编号不能为空', trigger: 'blur' }
      ],
      shiftType: [
        { required: true, message: '请选择班次', trigger: 'change' }
      ],
      materialName: [
        { required: true, message: '请选择材料名称', trigger: 'change' }
      ],
      productSpec: [
        { required: true, message: '请输入产品规格型号', trigger: 'blur' }
      ],
      planWeight: [
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