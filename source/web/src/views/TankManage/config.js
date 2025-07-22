// TankManage页面配置
export const searchFields = [
  {
    key: 'tankNo',
    label: '料罐编号',
    placeholder: '请输入料罐编号'
  }
]

export const columns = [
  {
    prop: 'tankNo',
    label: '料罐编号',
    width: '160'
  },
  {
    prop: 'remark',
    label: '描述',
    width: '300'
  },
  {
    prop: 'currentUser',
    label: '当前用户',
    width: '160',
    render: (row) => row.userAccount ? `${row.userName}(${row.userAccount})` : ''  // 当前用户(工号)
  },
  {
    prop: 'createTime',
    label: '创建时间'
  },
  {
    type: 'actions',
    label: '操作',
    width: '300'
  }
]

export const dialogConfig = {
  fields: [
    {
      prop: 'tankNo',
      label: '料罐编号',
      type: 'input',
      required: true,
      size: 'large',
      style: { width: '100%' }
    },
    {
      prop: 'remark',
      label: '描述',
      type: 'textarea',
      required: false,
      size: 'large',
      style: { width: '100%' }
    }
  ],
  rules: {
    tankNo: [
      { required: true, message: '请输入料罐编号', trigger: 'blur' }
    ]
  }
}
