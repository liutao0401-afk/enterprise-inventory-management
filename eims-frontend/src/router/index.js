import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../layout/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'material',
        name: 'Material',
        component: () => import('../views/Material.vue'),
        meta: { title: '物资管理' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('../views/Category.vue'),
        meta: { title: '物资分类' }
      },
      {
        path: 'supplier',
        name: 'Supplier',
        component: () => import('../views/Supplier.vue'),
        meta: { title: '供应商管理' }
      },
      {
        path: 'warehouse',
        name: 'Warehouse',
        component: () => import('../views/Warehouse.vue'),
        meta: { title: '仓库管理' }
      },
      {
        path: 'requirement',
        name: 'Requirement',
        component: () => import('../views/Requirement.vue'),
        meta: { title: '采购需求' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('../views/Order.vue'),
        meta: { title: '采购订单' }
      },
      {
        path: 'inventory',
        name: 'Inventory',
        component: () => import('../views/Inventory.vue'),
        meta: { title: '库存管理' }
      },
      {
        path: 'qc',
        name: 'QC',
        component: () => import('../views/QC.vue'),
        meta: { title: '质检管理' }
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
  const userStore = useUserStore()
  const token = userStore.token

  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router
