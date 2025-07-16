// FeedManage页面配置
export const searchFields = [
  {
    key: 'userKey',
    label: '人员',
    placeholder: '请输入人员姓名/工号'
  },
  {
    key: 'tankNo',
    label: '料罐编号',
    placeholder: '请输入料罐编号'
  },
  {
    key: 'shiftType',
    label: '班次',
    type: 'select',
    placeholder: '请选择班次',
    options: [
      { label: '白班', value: 'day' },
      { label: '夜班', value: 'night' }
    ]
  },
  {
    key: 'materialName',
    label: '材料名称',
    type: 'select',
    placeholder: '请选择材料名称',
    options: [
      { label: '10KV', value: '10KV' },
      { label: '35KV', value: '35KV' }
    ]
  }
]

export const columns = [
  {
    prop: 'userName',
    label: '人员',
    width: '160',
    render: (row) => `${row.applyUserName}(${row.applyUserAccount})`
  },
  {
    prop: 'tankNo',
    label: '料罐编号',
    width: '120'
  },
  {
    prop: 'shiftType',
    label: '班次',
    width: '80',
    render: (row) => row.shiftType === 'day' ? '白班' : row.shiftType === 'night' ? '夜班' : '-'
  },
  {
    prop: 'materialName',
    label: '材料名称',
    width: '100'
  },
  {
    prop: 'productSpec',
    label: '产品规格型号',
    width: '120'
  },
  {
    prop: 'planWeight',
    label: '计划加料重量',
    width: '120',
    render: (row) => `${row.planWeight} kg`
  },
  {
    prop: 'applyTime',
    label: '申请时间',
    width: '160'
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
