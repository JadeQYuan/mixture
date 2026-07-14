import { SHIFT_TYPE_MAP, MATERIAL_MAP, getOptions, getLabel } from '@/utils/constant'

// 卡片显示字段配置
export const displayFields = [
  {
    prop: 'materialName',
    label: '材料名称',
    render: (row) => getLabel(MATERIAL_MAP, row.materialName)
  },
  {
    prop: 'productSpec',
    label: '产品型号',
  },
  {
    prop: 'shiftType',
    label: '班次',
    render: (row) => getLabel(SHIFT_TYPE_MAP, row.shiftType)
  },
  {
    prop: 'planWeight',
    label: '计划重量',
    render: (row) => row.planWeight != null ? `${row.planWeight} kg` : ''
  },
  {
    prop: 'actualWeight',
    label: '实际加料',
    render: (row) => row.fullWeight && row.bottomWeight ? `${(row.fullWeight - row.bottomWeight).toFixed(2)} kg` : ''
  }
]

// 领料操作对话框表单配置
export const pickingDialogConfig = {
  width: '700px',
  fields: [
    { prop: 'tankNo', label: '料罐编号', type: 'input', disabled: true, size: 'large', style: { width: '100%' } },
    { prop: 'shiftType', label: '班次', type: 'select', disabled: true, options: getOptions(SHIFT_TYPE_MAP), size: 'large', style: { width: '100%' } },
    { prop: 'materialName', label: '材料名称', type: 'select', disabled: true, options: getOptions(MATERIAL_MAP), size: 'large', style: { width: '100%' } },
    { prop: 'productSpec', label: '产品型号', type: 'input', disabled: true, size: 'large', style: { width: '100%' } },
    { prop: 'planWeight', label: '计划加料', type: 'input', disabled: true, size: 'large', style: { width: '100%' }, suffix: 'kg' },
    // 满罐重量（始终显示）
    { prop: 'fullWeight', label: '满罐重量', type: 'input', disabled: true, size: 'large', style: { width: '100%' }, suffix: 'kg' },
    // 底罐重量（step >= 0 显示）
    { prop: 'pickingBottomWeight', label: '底罐重量', type: 'input', required: true, size: 'large', style: { width: '100%' }, 
            suffix: 'kg', disabled: true, visible: (formdata, step) => formdata.needFlameRetardant && step >= 0 },
    // 阻燃粉相关字段（step >= 1 显示）
    { prop: 'pickingTotalWeight', label: '阻燃粉后总重', type: 'input', required: true, size: 'large', style: { width: '100%' }, 
            suffix: 'kg', disabled: true, visible: (formdata, step) => formdata.needFlameRetardant && step >= 1 },
    { prop: 'flameRetardantWeight', label: '阻燃粉重量', type: 'input', disabled: true, size: 'large', style: { width: '100%' }, 
            suffix: 'kg', visible: (formdata, step) => formdata.needFlameRetardant && step >= 1 }
  ],
  rules: {
    pickingBottomWeight: [ { required: true, message: '请获取底罐重量', trigger: 'blur' } ],
    pickingTotalWeight: [ { required: true, message: '请获取阻燃粉后总重', trigger: 'blur' } ]
  },
  steps: [
    { title: '底罐称重', description: '称底罐重量' },
    { title: '添加阻燃粉', description: '添加阻燃粉后称重' },
    { title: '信息确认', description: '确认领料信息' }
  ]
}
