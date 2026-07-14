import { SHIFT_TYPE_MAP, MATERIAL_MAP, getOptions } from '@/utils/constant'

// 共享表单字段
const fields = [
  {
    prop: 'tankNo',
    label: '料罐',
    type: 'input',
    disabled: true,
    size: 'large',
    style: { width: '100%' }
  },
  {
    prop: 'shiftType',
    label: '班次',
    type: 'radio',
    placeholder: '请选择班次',
    required: true,
    options: getOptions(SHIFT_TYPE_MAP),
    size: 'large',
    style: { width: '100%' }
  },
  {
    prop: 'materialName',
    label: '材料名称',
    type: 'radio',
    placeholder: '请选择材料名称',
    required: true,
    options: getOptions(MATERIAL_MAP),
    size: 'large',
    style: { width: '100%' }
  },
  {
    prop: 'productSpec',
    label: '产品型号',
    type: 'input',
    placeholder: '请输入产品规格型号',
    required: false,
    size: 'large',
    style: { width: '100%' }
  },
  {
    prop: 'planWeight',
    label: '计划重量',
    type: 'number',
    placeholder: '请输入计划重量',
    required: true,
    size: 'large',
    style: { width: '100%' },
    suffix: 'kg',
    inputType: 'number',
    props: {
      min: 0,
      step: 1
    }
  }
]

// 共享验证规则
const rules = {
  tankNo: [
    { required: true, message: '料罐不能为空', trigger: 'blur' }
  ],
  shiftType: [
    { required: true, message: '请选择班次', trigger: 'change' }
  ],
  materialName: [
    { required: true, message: '请选择材料名称', trigger: 'change' }
  ],
  planWeight: [
    { required: true, message: '请输入计划重量', trigger: 'blur' }
  ]
}

// 加料申请表单配置
export const applyDialogConfig = {
  title: '加料申请',
  width: '600px',
  fields,
  rules
}

// 备料申请表单配置
export const prepareDialogConfig = {
  title: '备料申请',
  width: '600px',
  fields,
  rules
}