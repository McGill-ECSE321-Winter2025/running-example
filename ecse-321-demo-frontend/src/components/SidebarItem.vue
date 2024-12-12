<template>
  <div class="sidebar flex flex-column" style="width: 250px">
    <PanelMenu :model="menuItems" style="width: 100%; height: 100%" class="flex-grow-1" />
    <div class="sidebar-footer mt-auto p-4">
      <Button
        v-if="!isLoggedIn"
        label="Login"
        icon="pi pi-sign-in"
        class="p-button-text"
        @click="navigateToLogin"
      />
      <Button v-else label="Logout" icon="pi pi-sign-out" class="p-button-text" @click="logout" />
      <Button
        label="Account Settings"
        icon="pi pi-user"
        class="p-button-text"
        @click="navigateToAccount"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import PanelMenu from 'primevue/panelmenu'
import Button from 'primevue/button'

const router = useRouter()
const isLoggedIn = ref(false)

const menuItems = ref([
  {
    label: 'Home',
    icon: 'pi pi-home',
    command: () => router.push({ name: 'Home' }),
  },
  {
    label: 'Events',
    icon: 'pi pi-calendar',
    command: () => router.push({ name: 'Events' }),
  },
  {
    label: 'Registrations',
    icon: 'pi pi-pencil',
    command: () => router.push({ name: 'Registrations' }),
  },
  // You can add more menu items here
])

const navigateToAccount = () => {
  router.push({ name: 'AccountSettings' })
}

const navigateToLogin = () => {
  router.push({ name: 'Login' })
}

const logout = () => {
  // Implement your logout logic here
  isLoggedIn.value = false
  router.push({ name: 'Home' })
}
</script>

<style scoped>
.sidebar {
  height: 100vh;
  background-color: #f4f4f4;
}
.sidebar-footer {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>
