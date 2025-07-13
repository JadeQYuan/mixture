// FeedManage页面配置
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
    prop: 'spec',
    label: '计划加料规格',
    width: '160'
  },
  {
    prop: 'capacity',
    label: '计划加料重量',
    width: '160',
    render: (row) => `${row.capacity} kg`
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
    action: 'feed',
    text: '加料',
    type: 'primary',
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

export const tableClass = 'feed-table'

export const pageSizeCalculator = 'default'
