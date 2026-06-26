// FeedManage页面配置
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
  }
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
    width: '180'
  },
  {
    prop: 'planWeight',
    label: '计划加料',
    width: '120',
    render: (row) => row.planWeight ? `${row.planWeight} kg` : ''
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
    width: '220'
  },
  {
    prop: 'bottomWeight',
    label: '罐底重量',
    width: '120',
    render: (row) => row.bottomWeight ? `${row.bottomWeight} kg` : ''
  },
  {
    type: 'actions',
    label: '操作',
  }
]

// 底罐操作对话框表单配置
export const bottomTankConfig = {
  title: '底罐重量',
  width: '700px',
  fields: [
    { prop: 'tankNo', label: '料罐编号', type: 'input', disabled: true, size: 'large', style: { width: '100%' } },
    { prop: 'shiftType', label: '班次', type: 'select', disabled: true, options: getOptions(SHIFT_TYPE_MAP), size: 'large', style: { width: '100%' } },
    { prop: 'materialName', label: '材料名称', type: 'select', disabled: true, options: getOptions(MATERIAL_MAP), size: 'large', style: { width: '100%' } },
    { prop: 'productSpec', label: '产品型号', type: 'input', disabled: true, size: 'large', style: { width: '100%' } },
    { prop: 'planWeight', label: '计划加料', type: 'input', disabled: true, size: 'large', style: { width: '100%' }, 
            suffix: 'kg' },
    { prop: 'bottomWeight', label: '底罐', type: 'input', required: true, size: 'large', style: { width: '100%' }, 
            suffix: 'kg', disabled: true },
    { prop: 'check', label: '确认', type: 'redio', required: true, size: 'large', style: { width: '100%' }, 
            options: [{label: '是', value: true}, {label: '否', value: false}], visible: false},
    { prop: 'opinion', label: '意见', type: 'textarea', required: true, size: 'large', style: { width: '100%' }, 
            visible: (formdata) => formdata && formdata.check }
  ],
  rules: {
    bottomWeight: [ { required: true, message: '请获取底罐重量', trigger: 'blur' } ],
    opinion: [ { required: true, message: '请输入意见', trigger: 'blur' } ]
  },
  steps: [
    { title: '底罐重量', description: '记录底罐重量' },
    { title: '确认提交', description: '确认底罐重量' }
  ]
}

// 加料操作对话框表单配置
export const feedDialogConfig = {
  title: '加料',
  width: '700px',
  fields: [
    { prop: 'tankNo', label: '料罐编号', type: 'input', disabled: true, size: 'large', style: { width: '100%' } },
    { prop: 'shiftType', label: '班次', type: 'select', disabled: true, options: getOptions(SHIFT_TYPE_MAP), size: 'large', style: { width: '100%' } },
    { prop: 'materialName', label: '材料名称', type: 'select', disabled: true, options: getOptions(MATERIAL_MAP), size: 'large', style: { width: '100%' } },
    { prop: 'productSpec', label: '产品型号', type: 'input', disabled: true, size: 'large', style: { width: '100%' } },
    { prop: 'planWeight', label: '计划加料', type: 'input', disabled: true, size: 'large', style: { width: '100%' }, 
            suffix: 'kg' },
    { prop: 'bottomWeight', label: '底罐', type: 'input', required: true, size: 'large', style: { width: '100%' }, 
            suffix: 'kg', disabled: true },
    { prop: 'fullWeight', label: '满罐', type: 'input', required: true, size: 'large', style: { width: '100%' }, 
            suffix: 'kg', disabled: true },
    { prop: 'actualWeight', label: '实际加料', type: 'input', required: true, size: 'large', style: { width: '100%' }, 
            suffix: 'kg', disabled: true }
  ],
  rules: {
    fullWeight: [ { required: true, message: '请获取加料后重量', trigger: 'blur' } ]
  },
  steps: [
    { title: '加料重量', description: '记录加料重量' },
    { title: '确认提交', description: '确认加料数据' }
  ]
}
