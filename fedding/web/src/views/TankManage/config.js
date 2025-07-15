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
    render: (row) => row.currentUser ? `${row.currentUser}(${row.currentAccount})` : '-'
  },
  {
    prop: 'updateTime',
    label: '更新时间'
  },
  {
    type: 'actions',
    label: '操作',
    width: '300'
  }
]

export const actionButtons = [
  {
    action: 'edit',
    text: '编辑',
    type: 'primary',
    size: 'large'
  },
  {
    action: 'delete',
    text: '删除',
    type: 'danger',
    size: 'large',
    disabled: (row) => row.status === 'active' // 正常状态禁用删除
  }
]

export const headerButtons = [
  {
    action: 'add',
    text: '新增',
    type: 'primary',
    size: 'large'
  }
] 