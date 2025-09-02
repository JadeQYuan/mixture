// Stats页面配置

import { SHIFT_TYPE_MAP, MATERIAL_MAP, getOptions, getLabel } from '@/utils/constant'
import { ref } from 'vue'
import dayjs from 'dayjs';

const selectDate = ref(null);

export const searchFields = [
  {
    key: 'applyUserKey',
    label: '申请人员',
    placeholder: '请输入姓名/工号'
  },
  {
    key: 'pickingUserKey',
    label: '用料人员',
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
      { label: '用料时间', value: 'pickingTime' },
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
    defaultValue: [ dayjs().subtract(1, 'month').startOf('day').format("YYYY-MM-DD HH:mm:ss"), dayjs().endOf('day').format("YYYY-MM-DD HH:mm:ss") ],
    disabledDate: (date) => (date < dayjs(selectDate.value).subtract(3, 'month')) || (date > dayjs(selectDate.value).add(3, 'month')),
    calendarChange: (date) => selectDate.value = date[0]
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
    exportLabel: '计划加料(kg)',
    width: '120',
    render: (row) => `${row.planWeight} kg`,
    exportRender: (row) => `${row.planWeight}`
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
    width: '200'
  },
  {
    prop: 'pickingUserAccount',
    label: '用料人员',
    width: '160',
    render: (row) => row.pickingUserAccount ? `${row.pickingUserName}(${row.pickingUserAccount})` : ''
  },
  {
    prop: 'pickingTime',
    label: '用料时间',
    width: '200'
  },
  {
    prop: 'bottomWeight',
    label: '罐底重量',
    exportLabel: '罐底重量(kg)',
    width: '120',
    render: (row) => `${row.bottomWeight} kg`,
    exportRender: (row) => `${row.bottomWeight}`
  },
  {
    prop: 'fullWeight',
    label: '满罐重量',
    exportLabel: '满罐重量(kg)',
    width: '120',
    render: (row) => `${row.fullWeight} kg`,
    exportRender: (row) => `${row.fullWeight}`
  },
  {
    prop: 'flameRetardantWeight',
    label: '阻燃粉重量',
    exportLabel: '阻燃粉重量(kg)',
    width: '120',
    render: (row) => `${row.flameRetardantWeight} kg`,
    exportRender: (row) => `${row.flameRetardantWeight}`
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
    exportLabel: '退料重量(kg)',
    width: '120',
    render: (row) => `${row.returnWeight} kg`,
    exportRender: (row) => `${row.returnWeight}`
  },
  {
    prop: 'actualWeight',
    label: '实际用料',
    exportLabel: '实际用料(kg)',
    width: '120',
    render: (row) => `${row.actualWeight} kg`,
    exportRender: (row) => `${row.actualWeight}`
  },
  {
    prop: 'remark',
    label: '备注',
    width: '200',
  }
]

