// ReturnManage页面配置
export const searchFields = [
  {
    key: 'userKey',
    label: '人员',
    placeholder: '请输入人员姓名/工号'
  },
  {
    key: 'bucketNo',
    label: '罐号',
    placeholder: '请输入罐号'
  }
]

export const columns = [
  {
    prop: 'userName',
    label: '人员',
    width: '160',
    render: (row) => `${row.userName}(${row.account})`
  },
  {
    prop: 'bucketNo',
    label: '罐号',
    width: '160'
  },
  {
    prop: 'updateTime',
    label: '时间'
  },
  {
    type: 'actions',
    label: '操作',
    width: '240'
  }
]

export const actionButtons = [
  {
    action: 'return',
    text: '退料',
    type: 'primary',
    size: 'large'
  }
]

export const headerButtons = [] 