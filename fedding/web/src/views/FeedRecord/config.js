// FeedRecord页面配置

// 生成默认时间范围：过去4小时到今晚结束
function getDefaultTimeRange() {
  const now = new Date()
  const endTime = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59) // 今晚结束
  const startTime = new Date(now.getTime() - 4 * 60 * 60 * 1000) // 过去4小时
  
  return [startTime, endTime]
}

export const searchFields = [
  {
    key: 'userKey',
    label: '人员',
    placeholder: '请输入姓名/工号'
  },
  {
    key: 'bucketNo',
    label: '罐号',
    placeholder: '请输入罐号'
  },
  {
    key: 'spec',
    label: '加料规格',
    placeholder: '请选择加料规格',
    type: 'select',
    options: [
      { label: '10KV', value: '10KV' },
      { label: '35KV', value: '35KV' },
    ]
  },
  {
    key: 'time',
    label: '时间范围',
    type: 'datetimerange',
    placeholder: '请选择时间范围',
    defaultValue: getDefaultTimeRange()
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
    width: '100'
  },
  {
    prop: 'spec',
    label: '加料规格',
    width: '100'
  },
  {
    prop: 'spec',
    label: '计划加料',
    width: '100',
    render: (row) => `${row.capacity} kg`
  },
  {
    prop: 'userName',
    label: '领料员',
    width: '160',
    render: (row) => `${row.userName}(${row.account})`
  },
  {
    prop: 'spec',
    label: '罐底重量',
    width: '100',
    render: (row) => `${row.capacity} kg`
  },
  {
    prop: 'capacity',
    label: '加料重量',
    width: '100',
    render: (row) => `${row.capacity} kg`
  },
  {
    prop: 'userName',
    label: '加料员',
    width: '160',
    render: (row) => `${row.userName}(${row.account})`
  },
  {
    prop: 'abs',
    label: '阻燃粉',
    width: '80',
    render: (row) => `${row.abs} kg`
  },
  {
    prop: 'updateTime',
    label: '申请时间'
  },
  {
    prop: 'updateTime',
    label: '操作时间'
  },
  {
    type: 'actions',
    label: '操作',
    width: '100'
  }
]

export const actionButtons = [
  {
    action: 'view',
    text: '详情',
    type: 'primary',
    size: 'large'
  }
]

export const headerButtons = [
  {
    action: 'export',
    text: '导出',
    type: 'success',
    size: 'large',
    roles: ['Accountant'] // 只有会计角色有导出权限
  }
] 