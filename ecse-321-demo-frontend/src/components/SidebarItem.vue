<template>
  <div class="sidebar flex flex-column" style="width: 250px">
    <PanelMenu :model="menuItems" style="width: 100%; height: 100%" class="flex-grow-1" />
    <div class="sidebar-footer mt-auto p-4">
      <Button label="Settings" icon="pi pi-user" class="p-button-text" @click="navigateToAccount" />
      <Button
        v-if="!isLoggedIn"
        label="Login"
        icon="pi pi-sign-in"
        class="p-button-text"
        @click="showLoginModal"
      />
      <Button v-else label="Logout" icon="pi pi-sign-out" class="p-button-text" @click="logout" />
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import PanelMenu from 'primevue/panelmenu'
import Button from 'primevue/button'

const router = useRouter()
const isLoggedIn = ref(!!localStorage.getItem('userId'))
const showLoginModal = inject('showLoginModal')

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
    label: 'My Events',
    icon: 'pi pi-calendar',
    command: () => {
      if (!isLoggedIn.value) {
        showLoginModal()
      } else {
        router.push({ name: 'My Events' })
      }
    },
  },
  {
    label: 'Registrations',
    icon: 'pi pi-pencil',
    command: () => {
      if (!isLoggedIn.value) {
        showLoginModal()
      } else {
        router.push({ name: 'Registrations' })
      }
    },
  },
])

const navigateToAccount = () => {
  if (!isLoggedIn.value) {
    showLoginModal()
  } else {
    router.push({ name: 'AccountSettings' })
  }
}

const logout = () => {
  localStorage.removeItem('userId')
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
