// TankManage页面配置
export const searchFields = [
  {
    key: 'bucketNo',
    label: '罐号',
    placeholder: '请输入罐号'
  }
]

export const columns = [
  {
    prop: 'bucketNo',
    label: '罐号',
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
  },
  {
    action: 'view',
    text: '查看',
    type: 'info',
    size: 'large'
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