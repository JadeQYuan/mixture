// 卡片显示字段配置
export const displayFields = [
  // {
  //   prop: 'tankNo',
  //   label: '料罐',
  // }
]

export const returnDialogConfig = {
  fileds: [
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
      type: 'input',
      required: true,
      size: 'large',
      style: { width: '100%' },
      suffix: 'kg',
      disabled: true,
    }
  ],
  rules: {
    returnWeight: [
      { required: true, message: '请输入退料重量', trigger: 'blur' }
    ]
  },
  steps:  [
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
