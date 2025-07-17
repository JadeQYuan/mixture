// Stats页面配置

// 生成默认时间范围：过去4小时到今晚结束
function getDefaultTimeRange() {
  const now = new Date()
  const endTime = new Date(now.getFullYear(), now.getMonth(), now.getDate(), 23, 59, 59) // 今晚结束
  const startTime = new Date(now.getTime() - 4 * 60 * 60 * 1000) // 过去4小时
  
  return [startTime, endTime]
}

import { SHIFT_TYPE_MAP, MATERIAL_MAP, getOptions, getLabel } from '@/utils/constant'

export const searchFields = [
  {
    key: 'applyUserKey',
    label: '申请人员',
    placeholder: '请输入姓名/工号'
  },
  {
    key: 'tankNo',
    label: '料罐',
    placeholder: '请输入料罐编号'
  },
  {
    key: 'shiftType',
    label: '班次',
    type: 'select',
    placeholder: '请选择班次',
    options: getOptions(SHIFT_TYPE_MAP)
  },
  {
    key: 'materialName',
    label: '材料名称',
    type: 'select',
    placeholder: '请选择材料名称',
    options: getOptions(MATERIAL_MAP)
  },
  {
    key: 'timeType',
    label: '查询时间',
    type: 'select',
    placeholder: '请选择时间类型',
    options: [
      { label: '申请时间', value: 'applyTime' },
      { label: '加料时间', value: 'feedingTime' },
      { label: '退料时间', value: 'returnTime' }
    ],
    defaultValue: 'applyTime'
  },
  {
    key: 'timeRange',
    label: '时间范围',
    type: 'datetimerange',
    placeholder: '请选择时间范围',
    defaultValue: getDefaultTimeRange()
  },
]

export const columns = [
  {
    prop: 'tankNo',
    label: '料罐',
    width: '120'
  },
  {
    prop: 'shiftType',
    label: '班次',
    width: '80',
    render: (row) => getLabel(SHIFT_TYPE_MAP, row.shiftType)
  },
  {
    prop: 'materialName',
    label: '材料名称',
    width: '100',
    render: (row) => getLabel(MATERIAL_MAP, row.materialName)
  },
  {
    prop: 'productSpec',
    label: '产品型号',
    width: '120'
  },
  {
    prop: 'planWeight',
    label: '计划加料',
    width: '120',
    render: (row) => `${row.planWeight} kg`
  },
  {
    prop: 'applyUserAccount',
    label: '申请人员',
    width: '160',
    render: (row) => `${row.applyUserName}(${row.applyUserAccount})`
  },
  {
    prop: 'applyTime',
    label: '申请时间',
    width: '160'
  },
  {
    prop: 'bottomWeight',
    label: '罐底重量',
    width: '120',
    render: (row) => `${row.bottomWeight} kg`
  },
  {
    prop: 'fullWeight',
    label: '满罐重量',
    width: '120',
    render: (row) => `${row.fullWeight} kg`
  },
  {
    prop: 'flameRetardantWeight',
    label: '阻燃粉重量',
    width: '120',
    render: (row) => `${row.flameRetardantWeight} kg`
  },
  {
    prop: 'feedingTime',
    label: '加料时间',
    width: '200'
  },
  {
    prop: 'feedingUserAccount',
    label: '加料人员',
    width: '160',
    render: (row) => `${row.feedingUserName}(${row.feedingUserAccount})`
  },
  {
    prop: 'returnTime',
    label: '退料时间',
    width: '200'
  },
  {
    prop: 'returnWeight',
    label: '退料重量',
    width: '120',
    render: (row) => `${row.returnWeight} kg`
  },
  {
    prop: 'actualWeight',
    label: '实际用料',
    width: '100',
    render: (row) => `${row.actualWeight} kg`
  }
]

export const headerButtons = [
  {
    action: 'export',
    text: '导出',
    type: 'success',
    size: 'large',
  }
] 
