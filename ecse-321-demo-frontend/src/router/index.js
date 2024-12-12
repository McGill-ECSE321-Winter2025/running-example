import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/HomeView.vue'
import Events from '../views/EventView.vue'
import Registrations from '../views/RegistrationView.vue'
import AccountSettings from '../views/AccountView.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/events', name: 'Events', component: Events },
  { path: '/registrations', name: 'Registrations', component: Registrations },
  { path: '/account', name: 'AccountSettings', component: AccountSettings },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
