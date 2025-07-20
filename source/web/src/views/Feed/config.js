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
    width: '220'
  },
  {
    type: 'actions',
    label: '操作',
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

// 加料操作对话框表单配置
export function getFeedOperationConfig() {
  return {
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
              suffix: 'kg', disabled: true, visible: (currentStep) => currentStep >= 1 },
      { prop: 'flameRetardantWeight', label: '阻燃粉', type: 'number', required: true, size: 'large', style: { width: '100%' }, 
              suffix: 'kg', inputType: 'number', disabled: (currentStep) => currentStep !== 2 , visible: (currentStep) => currentStep >= 2, 
              props: { min: 0, step: 0.1 } }
    ],
    rules: {
      bottomWeight: [ { required: true, message: '请获取底罐重量', trigger: 'blur' } ],
      fullWeight: [ { required: true, message: '请获取加料后重量', trigger: 'blur' } ],
      flameRetardantWeight: [ { required: true, message: '请输入阻燃粉重量', trigger: 'blur' } ]
    },
    steps: [
      {
        title: '底罐重量',
        description: '记录底罐重量'
      },
      {
        title: '加料重量',
        description: '记录加料重量'
      },
      {
        title: '阻燃粉重量',
        description: '输入阻燃粉重量'
      },
      {
        title: '确认提交',
        description: '确认信息并提交'
      }
    ]
  }
}
