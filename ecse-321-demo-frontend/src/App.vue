<template>
  <div class="app-container flex flex-col min-h-screen">
    <MenubarItem class="flex-shrink-0" @show-login="showLoginModal" />

    <div class="main-content flex-grow overflow-hidden">
      <router-view />
    </div>

    <LoginModalItem
      ref="loginModal"
      @loginSuccess="handleLoginSuccess"
      :visible="isLoginModalVisible"
      @update:visible="isLoginModalVisible = $event"
    />
  </div>
</template>

<script setup>
import { ref, provide, onMounted } from 'vue'
import MenubarItem from './components/MenubarItem.vue'
import LoginModalItem from './components/LoginModalItem.vue'

const username = ref(localStorage.getItem('username') || 'Guest')
const isLoggedIn = ref(!!localStorage.getItem('userId'))
const isLoginModalVisible = ref(false)

provide('username', username)
provide('isLoggedIn', isLoggedIn)

const showLoginModal = () => {
  isLoginModalVisible.value = true
}

const handleLoginSuccess = () => {
  username.value = localStorage.getItem('username') || 'Guest'
  isLoggedIn.value = !!localStorage.getItem('userId')
}
onMounted(() => {
  window.addEventListener('show-login-modal', showLoginModal)
})
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
