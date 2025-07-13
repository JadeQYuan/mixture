// UserManage页面配置
import { ROLE_MAP } from '@/utils/roleMap'



export const searchFields = [
  {
    key: 'userName',
    label: '姓名',
    placeholder: '请输入姓名'
  },
  {
    key: 'account',
    label: '工号',
    placeholder: '请输入工号'
  }
]

export const columns = [
  {
    prop: 'roleCode',
    label: '角色',
    width: '150',
    render: (row) => {
      return ROLE_MAP[row.roleCode]?.name || row.roleCode
    }
  },
  {
    prop: 'account',
    label: '工号',
    width: '180'
  },
  {
    prop: 'userName',
    label: '姓名',
    width: '240'
  },
  {
    prop: 'facePath',
    label: '照片',
    width: '80',
    renderHtml: true,
    render: (row) => {
      if (row.facePath) {
        return `<a href="javascript:void(0)" style="color: #409eff; text-decoration: none; cursor: pointer;" data-row-id="${row.id}">查看</a>`
      } else {
        return `<span style="color: #999;">-</span>`
      }
    }
  },
  {
    prop: 'remark',
    label: '描述',
    width: '240',
    render: (row) => row.remark || '-'
  },
  {
    type: 'actions',
    label: '操作',
    width: '420'
  }
]

export const actionButtons = [
  {
    action: 'edit',
    text: '编辑',
    type: 'primary',
    size: 'large'
  },
  {
    action: 'photo',
    text: '人脸录入',
    type: 'warning',
    size: 'large'
  },
  {
    action: 'password',
    text: '修改密码',
    type: 'info',
    size: 'large'
  },
  {
    action: 'delete',
    text: '删除',
    type: 'danger',
    size: 'large'
  }
]

export const headerButtons = [
  {
    action: 'add',
    text: '新增用户',
    type: 'primary',
    size: 'large'
  }
] 