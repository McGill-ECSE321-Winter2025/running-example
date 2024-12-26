<template>
  <div class="app-container flex flex-col min-h-screen">
    <MenubarItem class="flex-shrink-0" />

    <div class="main-content flex-grow overflow-hidden">
      <router-view />
    </div>

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
  width: 100vw;
  max-width: 100%;
}

.main-content {
  flex-grow: 1;
  overflow: hidden;
}
</style>
