import { createRouter, createWebHistory } from 'vue-router'
import store from '../store'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/forget-password',
    name: 'ForgetPassword',
    component: () => import('../views/ForgetPassword.vue'),
    meta: { title: '忘记密码' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('../views/AdminLogin.vue'),
    meta: { title: '管理员登录' }
  },
  {
    path: '/admin/forget-password',
    name: 'AdminForgetPassword',
    component: () => import('../views/AdminForgetPassword.vue'),
    meta: { title: '管理员忘记密码' }
  },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: '/home',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: '/my-express',
        name: 'MyExpress',
        component: () => import('../views/MyExpress.vue'),
        meta: { title: '我的快递', requiresAuth: true }
      },
      {
        path: '/reservation',
        name: 'Reservation',
        component: () => import('../views/Reservation.vue'),
        meta: { title: '取件预约', requiresAuth: true }
      },
      {
        path: '/notifications',
        name: 'Notifications',
        component: () => import('../views/Notifications.vue'),
        meta: { title: '消息通知', requiresAuth: true }
      },
      {
        path: '/profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('../views/AdminLayout.vue'),
    redirect: '/admin/home',
    meta: { requiresAuth: true, isAdmin: true },
    children: [
      {
        path: '/admin/home',
        name: 'AdminHome',
        component: () => import('../views/admin/Home.vue'),
        meta: { title: '后台首页', requiresAuth: true, isAdmin: true }
      },
      {
        path: '/admin/express/add',
        name: 'ExpressAdd',
        component: () => import('../views/admin/ExpressAdd.vue'),
        meta: { title: '录入快递', requiresAuth: true, isAdmin: true }
      },
      {
        path: '/admin/express/manage',
        name: 'ExpressManage',
        component: () => import('../views/admin/ExpressManage.vue'),
        meta: { title: '快递管理', requiresAuth: true, isAdmin: true }
      },
      {
        path: '/admin/reservation/manage',
        name: 'ReservationManage',
        component: () => import('../views/admin/ReservationManage.vue'),
        meta: { title: '预约管理', requiresAuth: true, isAdmin: true }
      },
      {
        path: '/admin/user/manage',
        name: 'UserManage',
        component: () => import('../views/admin/UserManage.vue'),
        meta: { title: '用户管理', requiresAuth: true, isAdmin: true }
      },
      {
        path: '/admin/notice/manage',
        name: 'NoticeManage',
        component: () => import('../views/admin/NoticeManage.vue'),
        meta: { title: '公告管理', requiresAuth: true, isAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = localStorage.getItem('userInfo')
  
  if (to.meta.requiresAuth) {
    if (!token || !userInfo) {
      next('/login')
      return
    }
    
    // 检查管理员权限
    if (to.meta.isAdmin) {
      const user = JSON.parse(userInfo)
      if (user.role !== 'ADMIN') {
        next('/home')
        return
      }
    }
  }
  
  next()
})

export default router
