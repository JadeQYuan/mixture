// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import activityMonitor from '../utils/activityMonitor'
import { ElMessage } from 'element-plus'

// 引入页面组件
import Home from '../views/Home.vue'
import Layout from '../components/Layout.vue'
import UserManage from '../views/UserManage.vue'
import TankManage from '../views/TankManage.vue'
import FeedApply from '../views/FeedApply.vue'
import ReturnApply from '../views/ReturnApply.vue'
import FeedManage from '../views/FeedManage.vue'
import ReturnManage from '../views/ReturnManage.vue'
import FeedRecord from '../views/FeedRecord.vue'
import FaceLogin from '../views/FaceLogin.vue'
import CodeLogin from '../views/CodeLogin.vue'


const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'CodeLogin',
    component: CodeLogin,
    meta: { title: '密码登录' }
  },
  { path: '/face-login', name: 'FaceLogin', component: FaceLogin, meta: { title: '人脸识别' } },
  {
    path: '/app',
    name: 'Layout',
    component: Layout,
    meta: { title: '主界面' },
    children: [
      {
        path: 'feed-apply',
        name: 'FeedApply',
        component: FeedApply,
        meta: { roles: ['SeniorOperator', 'Operator'], title: '加料申请' }
      },
      {
        path: 'return-apply',
        name: 'ReturnApply',
        component: ReturnApply,
        meta: { roles: ['SeniorOperator', 'Operator'], title: '退料申请' }
      },
      {
        path: 'feed-manage',
        name: 'FeedManage',
        component: FeedManage,
        meta: { roles: ['SeniorOperator', 'MaterialClerk'], title: '加料管理' }
      },
      {
        path: 'return-manage',
        name: 'ReturnManage',
        component: ReturnManage,
        meta: { roles: ['SeniorOperator', 'MaterialClerk'], title: '退料管理' }
      },
      {
        path: 'feed-record',
        name: 'FeedRecord',
        component: FeedRecord,
        meta: { roles: ['MaterialClerk'], title: '加料记录' }
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