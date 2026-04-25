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
