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
import { ref, provide } from 'vue'
import Sidebar from './components/SidebarItem.vue'
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
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}
.sidebar-fixed {
  flex: 0 0 240px;
  height: 100vh;
}
.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
}
</style>
