<template>
  <div class="app-container flex">
    <Sidebar />
    <div class="main-content flex-grow-1 p-4">
      <router-view />
    </div>
  </div>

  <LoginModal v-model:visible="loginModalVisible" @loginSuccess="handleLoginSuccess" />
</template>

<script setup>
import { ref, onMounted, onUnmounted, provide } from 'vue'
import Sidebar from './components/SidebarItem.vue'
import LoginModal from './components/LoginModalItem.vue'

const loginModalVisible = ref(false)

const showLoginModal = () => {
  loginModalVisible.value = true
}

const handleLoginSuccess = () => {
  window.location.reload()
}

const handleShowLoginModal = () => {
  showLoginModal()
}

onMounted(() => {
  window.addEventListener('show-login-modal', handleShowLoginModal)
})

onUnmounted(() => {
  window.removeEventListener('show-login-modal', handleShowLoginModal)
})

provide('showLoginModal', showLoginModal)
</script>

<style>
.app-container {
  height: 100vh;
}
.main-content {
  overflow-y: auto;
}
</style>
