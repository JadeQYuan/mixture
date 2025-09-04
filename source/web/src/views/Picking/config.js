import { SHIFT_TYPE_MAP, MATERIAL_MAP, getLabel } from '@/utils/constant'

// 领料组件配置
export const pickingConfig = {
  // 显示字段配置
  displayFields: [
    {
      prop: 'materialName',
      label: '材料名称',
      render: (row) => getLabel(MATERIAL_MAP, row.materialName)
    },
    {
      prop: 'productSpec',
      label: '产品规格',
      width: '150px'
    },
    {
      prop: 'shiftType',
      label: '班次',
      render: (row) => getLabel(SHIFT_TYPE_MAP, row.shiftType)
    },
    {
      prop: 'planWeight',
      label: '计划重量',
      render: (row) => row.planWeight ? `${row.planWeight} kg` : ''
    },
    {
      prop: 'fullWeight',
      label: '满罐重量',
      render: (row) => row.fullWeight ? `${row.fullWeight} kg` : ''
    },
    {
      prop: 'actualWeight',
      label: '实际重量',
      render: (row) => row.fullWeight ? `${(row.fullWeight - row.bottomWeight).toFixed(2)} kg` : ''
    }
  ]
}
