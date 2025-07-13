// UserManage 页面表单配置
import { ROLE_MAP } from '@/utils/roleMap'

// 用户表单字段配置
export const userFormFields = [
  {
    prop: 'roleCode',
    label: '角色',
    type: 'radio',
    size: 'large',
    options: Object.entries(ROLE_MAP).map(([key, value]) => ({
      label: value.name,
      value: key
    }))
  },
  {
    prop: 'account',
    label: '工号',
    type: 'input',
    placeholder: '请输入工号',
    size: 'large'
  },
  {
    prop: 'userName',
    label: '姓名',
    type: 'input',
    placeholder: '请输入姓名',
    size: 'large'
  },
  {
    prop: 'remark',
    label: '描述',
    type: 'textarea',
    placeholder: '请输入描述',
    rows: 3,
    size: 'large'
  }
]

// 用户表单验证规则
export const userFormRules = {
  roleCode: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  account: [
    { required: true, message: '请输入工号', trigger: 'blur' }
  ],
  userName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ]
}

// 密码修改表单字段配置
export const passwordFormFields = [
  {
    prop: 'newPassword',
    label: '新密码',
    type: 'input',
    inputType: 'password',
    placeholder: '请输入新密码',
    size: 'large'
  },
  {
    prop: 'confirmPassword',
    label: '确认密码',
    type: 'input',
    inputType: 'password',
    placeholder: '请再次输入新密码',
    size: 'large'
  }
]

// 密码修改验证规则
export const passwordFormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' }
  ]
}

// 获取用户表单配置
export function getUserFormConfig() {
  return {
    title: '用户信息',
    width: '600px',
    fields: userFormFields,
    rules: userFormRules
  }
}

// 获取密码修改表单配置
export function getPasswordFormConfig() {
  return {
    title: '修改密码',
    width: '500px',
    fields: passwordFormFields,
    rules: passwordFormRules
  }
} 