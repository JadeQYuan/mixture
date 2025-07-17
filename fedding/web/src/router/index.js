// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import activityMonitor from '@/utils/activityMonitor'

// 引入页面组件
import Home from '@/views/Home.vue'
import Layout from '@/components/Layout.vue'
import UserManage from '@/views/UserManage'
import TankManage from '@/views/TankManage'
import Apply from '@/views/Apply'
import Return from '@/views/Return'
import Feed from '@/views/Feed'
import Record from '@/views/Record'
import Stats from '@/views/Stats'
import FaceLogin from '@/views/Login/FaceLogin'
import CodeLogin from '@/views/Login/CodeLogin'



const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '首页' }
  },
  {
    path: '/code',
    name: 'CodeLogin',
    component: CodeLogin,
    meta: { title: '密码登录' }
  },
  { path: '/face', name: 'FaceLogin', component: FaceLogin, meta: { title: '人脸识别' } },
  {
    path: '/app',
    name: 'Layout',
    component: Layout,
    meta: { title: '主界面' },
    children: [
      {
        path: '',
        name: '',
      },
      {
        path: 'apply',
        name: 'Apply',
        component: Apply,
        meta: { roles: ['SeniorOperator', 'Operator'], title: '加料申请' }
      },
      {
        path: 'return',
        name: 'Return',
        component: Return,
        meta: { roles: ['SeniorOperator', 'Operator'], title: '退料' }
      },
      {
        path: 'feed',
        name: 'Feed',
        component: Feed,
        meta: { roles: ['SeniorOperator', 'MaterialClerk'], title: '加料管理' }
      },
      {
        path: 'record',
        name: 'Record',
        component: Record,
        meta: { roles: ['MaterialClerk'], title: '加料记录' }
      },
      {
        path: 'stats',
        name: 'Stats',
        component: Stats,
        meta: { roles: ['Accountant'], title: '领料统计' }
      },
      {
        path: 'tank-manage',
        name: 'TankManage',
        component: TankManage,
        meta: { roles: ['MaterialClerk'], title: '料罐管理' }
      },  
      {
        path: 'user-manage',
        name: 'UserManage',
        component: UserManage,
        meta: { roles: ['MaterialClerk'], title: '用户管理' }
      },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {

  
  // 如果是首页，停止活动监测
  if (to.path === '/') {
    activityMonitor.stop()
  }
  
  next()
})

export default router