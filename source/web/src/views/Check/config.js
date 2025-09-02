// Check页面配置
export const searchFields = [
  {
    key: 'tankNo',
    label: '料罐编号',
    placeholder: '请输入料罐编号'
  },
  {
    key: 'status',
    label: '状态',
    type: 'select',
    placeholder: '请选择状态',
    options: [
      { label: '未处理', value: '0' },
      { label: '已更正', value: '1' },
      { label: '未更正', value: '2' },
    ]
  }
]

export const columns = [
  {
    prop: 'tankNo',
    label: '料罐',
    width: '120'
  },
  {
    prop: 'returnWeight',
    label: '退料重量',
    width: '120',
    render: (row) => row.returnWeight ? `${row.returnWeight} kg` : ''
  },
  {
    prop: 'userAccount',
    label: '物料员',
    width: '160',
    render: (row) => row.userAccount ? `${row.userName}(${row.userAccount})` : ''
  },
  {
    prop: 'bottomWeight',
    label: '罐底重量',
    width: '120',
    render: (row) => row.bottomWeight ? `${row.bottomWeight} kg` : ''
  },
  {
    prop: 'opinion',
    label: '物料员意见',
    width: '220',
  },
  {
    prop: 'adminAccount',
    label: '管理员',
    width: '160',
    render: (row) => row.adminAccount ? `${row.adminName}(${row.adminAccount})` : ''
  },
  {
    prop: 'adminOpinion',
    label: '管理员意见',
    width: '220'
  },
  {
    prop: 'correctWeight',
    label: '更正重量',
    width: '120',
    render: (row) => row.correctWeight ? `${row.correctWeight} kg` : ''
  },
  {
    type: 'actions',
    label: '操作',
    width: '120'
  }
]

export const dialogConfig = {
  fields: [
    {
      prop: 'tankNo',
      label: '料罐',
      type: 'input',
      size: 'large',
      style: { width: '100%' },
      disabled: true
    },
    { 
      prop: 'returnWeight', 
      label: '退料重量', 
      type: 'input', 
      disabled: true, 
      size: 'large', 
      style: { width: '100%' }, 
      suffix: 'kg' 
    },
    { 
      prop: 'bottomWeight', 
      label: '罐底重量', 
      type: 'input', 
      disabled: true, 
      size: 'large', 
      style: { width: '100%' }, 
      suffix: 'kg' 
    },
    {
      prop: 'opinion',
      label: '物料员意见',
      type: 'textarea',
      size: 'large',
      style: { width: '100%' },
      disabled: true
    },
    {
      prop: 'adminOpinion',
      label: '管理员意见',
      type: 'textarea',
      size: 'large',
      style: { width: '100%' },
      required: true
    },
    {
      prop: 'status',
      label: '更正',
      type: 'radio',
      options: [{label: '是', value: 1}, {label: '否', value: 2}],
      required: true,
      size: 'large',
      style: { width: '100%' },
    },
    {
      prop: 'correctWeight',
      label: '更正重量',
      type: 'number',
      required: true,
      size: 'large',
      style: { width: '100%' },
      placeholder: '请输入更正重量',
      suffix: 'kg',
      visible: (formdata, currentStep) => formdata.status === 1, 
      props: { min: 0, step: 0.1 }
    },
  ],
  rules: {
    checkType: [
      { required: true, message: '请选择检查类型', trigger: 'change' }
    ],
    checkItem: [
      { required: true, message: '请输入检查项目', trigger: 'blur' }
    ],
    checkValue: [
      { required: true, message: '请输入检查值', trigger: 'blur' }
    ],
    expectedValue: [
      { required: true, message: '请输入期望值', trigger: 'blur' }
    ],
    status: [
      { required: true, message: '请选择状态', trigger: 'change' }
    ]
  }
}
