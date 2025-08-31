// FeedRecord页面配置

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
    render: (row) => row.planWeight ? `${row.planWeight} kg`: ''
  },
  {
    prop: 'applyUserAccount',
    label: '申请人员',
    width: '160',
    render: (row) => row.applyUserAccount ? `${row.applyUserName}(${row.applyUserAccount})` : ''
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
    render: (row) => row.returnTime ? `${row.returnWeight} kg` : ''
  },
  {
    prop: 'actualWeight',
    label: '实际用料',
    width: '100',
    render: (row) => row.returnTime ? `${row.actualWeight} kg` : ''
  },
  {
    prop: 'remark',
    label: '备注',
    width: '200'
  },
  {
    type: 'actions',
    label: '操作',
    width: '100',
    fixed: 'right'
  }
]

export const remarkDialogConfig = {
  title: '备注',
  width: '500px',
  fields: [
    {
      prop: 'materialName',
      label: '材料名称',
      type: 'radio',
      required: true,
      placeholder: '请选择材料名称',
      options: getOptions(MATERIAL_MAP)
    },
    {
      prop: 'productSpec',
      label: '产品型号',
      type: 'input',
    },
    { 
      prop: 'flameRetardantWeight', 
      label: '阻燃粉', 
      type: 'number', 
      required: true,
      suffix: 'kg', 
      inputType: 'number', 
      props: { min: 0, step: 0.1 } 
    },
    {
      prop: 'remark',
      label: '备注',
      type: 'textarea',
      placeholder: '请输入备注',
      rows: 4,
      required: false,
      style: { width: '100%' }
    }
  ],
  rules: {
    materialName: [
      { required: true, message: '请选择材料名称', trigger: 'change' }
    ]
  }
} 
