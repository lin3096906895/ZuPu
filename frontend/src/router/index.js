import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('@/views/DashboardView.vue'), meta: { title: '仪表盘' } },
      { path: 'families', name: 'families', component: () => import('@/views/FamilyView.vue'), meta: { title: '家族管理' } },
      { path: 'persons', name: 'persons', component: () => import('@/views/PersonView.vue'), meta: { title: '人物管理' } },
      { path: 'persons/:id', name: 'person-detail', component: () => import('@/views/PersonDetailView.vue'), meta: { title: '人物详情' } },
      { path: 'tree', name: 'tree', component: () => import('@/views/TreeView.vue'), meta: { title: '家族树' } },
      { path: 'ai', name: 'ai', component: () => import('@/views/AiChatView.vue'), meta: { title: 'AI 助手' } },
      { path: 'stats', name: 'stats', component: () => import('@/views/StatisticsView.vue'), meta: { title: '统计看板' } },
      { path: 'relationship', name: 'relationship', component: () => import('@/views/RelationshipView.vue'), meta: { title: '关系查询' } },
      { path: 'migration', name: 'migration', component: () => import('@/views/MigrationMapView.vue'), meta: { title: '迁徙地图' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('accessToken')
  if (to.path !== '/login' && !token) {
    return { path: '/login' }
  }
  if (to.path === '/login' && token) {
    return { path: '/dashboard' }
  }
})

export default router
