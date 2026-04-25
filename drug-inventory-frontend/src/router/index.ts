import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/manufacturer',
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/LoginPage.vue'),
      meta: { layout: 'blank', title: '登录' },
    },
    {
      path: '/manufacturer',
      name: 'Manufacturer',
      component: () => import('../views/ManufacturerPage.vue'),
      meta: { title: '厂家管理' },
    },
    {
      path: '/drug',
      name: 'Drug',
      component: () => import('../views/DrugPage.vue'),
      meta: { title: '药品管理' },
    },
    {
      path: '/inbound',
      name: 'Inbound',
      component: () => import('../views/InboundPage.vue'),
      meta: { title: '药品入库管理' },
    },
    {
      path: '/inbound-return',
      name: 'InboundReturn',
      component: () => import('../views/InboundReturnPage.vue'),
      meta: { title: '入库退货管理' },
    },
    {
      path: '/inbound-return/create',
      name: 'InboundReturnCreate',
      component: () => import('../views/InboundReturnCreatePage.vue'),
      meta: { title: '新增入库退货' },
    },
    {
      path: '/outbound',
      name: 'Outbound',
      component: () => import('../views/OutboundPage.vue'),
      meta: { title: '药品出库管理' },
    },
    {
      path: '/outbound-return',
      name: 'OutboundReturn',
      component: () => import('../views/OutboundReturnPage.vue'),
      meta: { title: '出库退库管理' },
    },
    {
      path: '/outbound-return/create',
      name: 'OutboundReturnCreate',
      component: () => import('../views/OutboundReturnCreatePage.vue'),
      meta: { title: '新增出库退库' },
    },
  ],
})

router.beforeEach((to) => {
  if (to.path === '/login') return true
  const token = localStorage.getItem('drug_inventory_token') ?? ''
  if (!token) {
    return {
      path: '/login',
      query: { redirect: to.fullPath },
    }
  }
  return true
})

export default router
