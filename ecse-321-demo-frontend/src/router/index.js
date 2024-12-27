import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/HomeView.vue'
import Events from '../views/EventView.vue'
import MyEvents from '../views/MyEventView.vue'
import Registrations from '../views/RegistrationView.vue'
import AccountSettings from '../views/AccountView.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/events',
    name: 'Events',
    component: Events,
  },
  {
    path: '/my-events',
    name: 'My Events',
    meta: { requiresAuth: true },
    component: MyEvents,
  },
  {
    path: '/registrations',
    name: 'Registrations',
    meta: { requiresAuth: true },
    component: Registrations,
  },
  {
    path: '/account',
    name: 'AccountSettings',
    meta: { requiresAuth: true },
    component: AccountSettings,
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('userId')

  if (to.meta.requiresAuth && !isAuthenticated) {
    window.dispatchEvent(new CustomEvent('show-login-modal'))
    next(false)
  } else {
    next()
  }
})

export default router
