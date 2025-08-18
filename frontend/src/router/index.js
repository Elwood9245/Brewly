import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import { useAuthStore } from '../stores/auth.js'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
      meta: { hideForAuth: true }
    },
    {
      path: '/register',
      name: 'register',
      component: Register,
      meta: { hideForAuth: true }
    },

    {
      path: '/explore',
      name: 'explore',
      component: () => import('../views/ExploreView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/inventory',
      name: 'inventory',
      component: () => import('../views/InventoryView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/ai',
      name: 'ai',
      component: () => import('../views/AIView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/addbrew',
      name: 'addbrew',
      component: () => import('../views/AddBrewView.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/beans/add',
      name: 'addbean',
      component: () => import('../views/BeanForm.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/beans/:id/edit',
      name: 'editbean',
      component: () => import('../views/BeanForm.vue'),
      meta: { requiresAuth: true }
    },
    // Catch all route - redirect to login if not authenticated, home if authenticated
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      redirect: (to) => {
        const auth = useAuthStore()
        return auth.isAuthenticated ? '/' : '/login'
      }
    }
  ],
})

// Navigation guards
router.beforeEach(async (to, from, next) => {
  const auth = useAuthStore()
  
  // Check authentication status
  const isAuthenticated = await auth.checkAuthStatus()
  
  console.log('Route guard:', { 
    to: to.name, 
    requiresAuth: to.meta.requiresAuth, 
    hideForAuth: to.meta.hideForAuth, 
    isAuthenticated 
  })
  
  // If route requires authentication and user is not authenticated
  if (to.meta.requiresAuth && !isAuthenticated) {
    console.log('Redirecting to login - authentication required')
    next('/login')
    return
  }
  
  // If user is authenticated and trying to access login/register pages
  if (to.meta.hideForAuth && isAuthenticated) {
    console.log('Redirecting to home - user already authenticated')
    next('/')
    return
  }
  
  // If no specific route meta and user is not authenticated, redirect to login
  if (!to.meta.hideForAuth && !to.meta.requiresAuth && !isAuthenticated) {
    console.log('Redirecting to login - default for unauthenticated users')
    next('/login')
    return
  }
  
  next()
})

export default router
