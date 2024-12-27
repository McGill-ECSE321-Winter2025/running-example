<template>
  <Menubar :model="menuItems" class="border-none">
    <template #start>
      <svg
        width="35"
        height="40"
        viewBox="0 0 35 40"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
        class="h-8"
      >
        <path d="M5 20 L15 40 L25 20 Z" fill="var(--p-primary-color)" />
        <path d="M5 20 L15 0 L25 20 Z" fill="var(--p-text-color)" />
      </svg>
      <span class="text-xl font-semibold"> Event<span class="text-primary">+</span> </span>
    </template>
    <template #end>
      <div class="flex items-center gap-2">
        <span class="text-sm">Hello, {{ username }}</span>
        <Button
          v-if="isLoggedIn"
          icon="pi pi-cog"
          class="p-button-text end-action-button"
          @click="goToSettings"
          aria-label="Settings"
        />
        <Button
          v-if="isLoggedIn"
          icon="pi pi-sign-out"
          class="p-button-text end-action-button"
          @click="handleLogout"
          aria-label="Logout"
        />
        <Button
          v-else
          icon="pi pi-sign-in"
          class="p-button-text end-action-button"
          @click="showLoginModal"
          aria-label="Login"
        />
      </div>
    </template>
  </Menubar>
</template>

<script setup>
import { inject, computed } from 'vue'
import Menubar from 'primevue/menubar'
import Button from 'primevue/button'
import { useRouter } from 'vue-router'

const emit = defineEmits(['show-login'])

const router = useRouter()
const username = inject('username')
const isLoggedIn = inject('isLoggedIn')

const showLoginModal = () => {
  emit('show-login')
}

const handleLogout = () => {
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  isLoggedIn.value = false
  username.value = 'Guest'
  router.push({ name: 'Home' })
}

const goToSettings = () => {
  router.push({ name: 'AccountSettings' })
}

const menuItems = computed(() => [
  {
    label: 'Home',
    icon: 'pi pi-home',
    command: () => router.push({ name: 'Home' }),
  },
  {
    label: 'Events',
    icon: 'pi pi-calendar',
    items: [
      {
        label: 'All Events',
        icon: 'pi pi-calendar',
        command: () => router.push({ name: 'Events' }),
      },
      {
        label: 'My Events',
        icon: 'pi pi-user',
        command: () => {
          if (!isLoggedIn.value) {
            showLoginModal()
          } else {
            router.push({ name: 'My Events' })
          }
        },
      },
    ],
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
</script>

<style scoped>
.text-primary {
  color: var(--p-primary-color);
}

.end-action-button {
  color: white;
}

.end-action-button .p-button-label {
  color: white;
}

.end-action-button .pi {
  color: white;
}

:deep(.p-menubar) {
  border: none;
  background: transparent;
  border-radius: 0;
}
</style>
