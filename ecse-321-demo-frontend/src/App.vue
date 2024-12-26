<template>
  <div class="app-container flex flex-col min-h-screen">
    <!-- Menubar at the top -->
    <MenubarItem class="flex-shrink-0" />

    <!-- Main Content Area -->
    <div class="main-content flex-grow overflow-hidden">
      <router-view />
    </div>

    <!-- Login Modal -->
    <LoginModal v-model:visible="loginModalVisible" @loginSuccess="handleLoginSuccess" />
  </div>
</template>

<script setup>
import { ref, provide } from 'vue'
import MenubarItem from './components/MenubarItem.vue'
import LoginModal from './components/LoginModalItem.vue'

const loginModalVisible = ref(false)

const username = ref(localStorage.getItem('username') || 'Guest')
const isLoggedIn = ref(!!localStorage.getItem('userId'))

const showLoginModal = () => {
  loginModalVisible.value = true
}

const handleLoginSuccess = () => {
  username.value = localStorage.getItem('username') || 'Guest'
  isLoggedIn.value = !!localStorage.getItem('userId')
  loginModalVisible.value = false
}

provide('showLoginModal', showLoginModal)
provide('username', username)
provide('isLoggedIn', isLoggedIn)
</script>

<style>
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.main-content {
  flex-grow: 1;
  overflow: hidden;
}
</style>
