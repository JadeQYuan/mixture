// FeedRecord页面配置
export const searchFields = [
  {
    key: 'person',
    label: '人员',
    placeholder: '请输入人员姓名/工号'
  },
  {
    key: 'tank',
    label: '罐号',
    placeholder: '请输入罐号'
  },
  {
    key: 'time',
    label: '时间范围',
    type: 'daterange',
    placeholder: '请选择时间范围'
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
    label: '加料规格',
    width: '160'
  },
  {
    prop: 'capacity',
    label: '加料重量',
    width: '160',
    render: (row) => `${row.capacity} kg`
  },
  {
    prop: 'updateTime',
    label: '时间'
  }
]

export const actionButtons = []

export const headerButtons = [
  {
    action: 'export',
    text: '导出',
    type: 'success',
    size: 'large'
  }
] 