<template>
  <div class="sidebar flex flex-col bg-surface-50 dark:bg-surface-900 h-full p-4">
    <!-- Menu -->
    <Menu :model="menuItems" class="flex flex-col flex-grow">
      <!-- Start Slot (Logo) -->
      <template #start>
        <span class="inline-flex items-center gap-2 px-2 py-2">
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
        </span>
      </template>

      <!-- Submenu Label Slot -->
      <template #submenulabel="{ item }">
        <span class="text-primary font-bold">{{ item.label }}</span>
      </template>

      <!-- Menu Item Slot -->
      <template #item="{ item, props }">
        <a
          v-ripple
          class="flex items-center p-2 rounded-md hover:bg-surface-200 dark:hover:bg-surface-700"
          v-bind="props.action"
        >
          <i :class="item.icon" class="mr-2"></i>
          <span>{{ item.label }}</span>
        </a>
      </template>

      <!-- End Slot (Avatar Section) -->
      <template #end>
        <button
          v-ripple
          class="relative overflow-hidden w-full border-0 bg-transparent flex items-center p-2 pl-4 hover:bg-surface-100 dark:hover:bg-surface-800 rounded-none cursor-pointer transition-colors duration-200"
        >
          <span class="inline-flex flex-col items-start">
            <span class="font-bold">{{ username }}</span>
          </span>
        </button>
      </template>
    </Menu>
  </div>
</template>

<script setup>
import { inject, watch, computed } from 'vue'
import Menu from 'primevue/menu'
import { useRouter } from 'vue-router'

const router = useRouter()
const showLoginModal = inject('showLoginModal')
const username = inject('username')
const isLoggedIn = inject('isLoggedIn')

const menuItems = computed(() => [
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

  { separator: true }, // Acts as the spacer

  {
    label: 'Settings',
    icon: 'pi pi-cog',
    command: () => {
      if (!isLoggedIn.value) {
        showLoginModal()
      } else {
        router.push({ name: 'AccountSettings' })
      }
    },
  },
  {
    label: isLoggedIn.value ? 'Logout' : 'Login',
    icon: isLoggedIn.value ? 'pi pi-sign-out' : 'pi pi-sign-in',
    command: () => {
      if (isLoggedIn.value) {
        localStorage.removeItem('userId')
        localStorage.removeItem('username')
        isLoggedIn.value = false
        username.value = 'Guest'
        router.push({ name: 'Home' })
      } else {
        showLoginModal()
      }
    },
  },
])

watch(isLoggedIn, (newVal) => {
  if (!newVal) {
    username.value = 'Guest'
  }
})
</script>

<style scoped>
.sidebar {
  width: 240px;
  box-sizing: border-box;
  overflow: hidden;
  background-color: var(--surface-color);
  border-right: 1px solid var(--surface-border);
}

::v-deep .p-menu-list {
  display: flex;
  flex-direction: column;
  height: 100%;
}

::v-deep .p-menu-separator {
  flex-grow: 1;
  height: 0;
  margin: 0;
  border: none;
}

.text-primary {
  color: var(--p-primary-color);
}

.text-secondary {
  color: var(--p-secondary-color);
}
</style>
