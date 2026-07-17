import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/dashboard' },
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue'), meta: { guest: true } },
    { path: '/register', name: 'register', component: () => import('@/views/RegisterView.vue'), meta: { guest: true } },
    {
      path: '/', component: () => import('@/layouts/AppShell.vue'), meta: { requiresAuth: true },
      children: [
        { path: 'dashboard', name: 'dashboard', component: () => import('@/views/DashboardView.vue') },
        { path: 'certificates', name: 'certificates', component: () => import('@/views/CertificatesView.vue') },
        { path: 'trash', name: 'trash', component: () => import('@/views/TrashView.vue') },
        { path: 'analytics', name: 'analytics', component: () => import('@/views/AnalyticsView.vue') },
        { path: 'profile', name: 'profile', component: () => import('@/views/ProfileView.vue') },
      ],
    },
    { path: '/:pathMatch(.*)*', redirect: '/dashboard' },
  ],
})

router.beforeEach((to) => {
  const authenticated = Boolean(localStorage.getItem('certificate_token'))
  if (to.meta.requiresAuth && !authenticated) return { name: 'login', query: { redirect: to.fullPath } }
  if (to.meta.guest && authenticated) return { name: 'dashboard' }
})

export default router
