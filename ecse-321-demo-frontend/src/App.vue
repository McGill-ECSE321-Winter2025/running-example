<template>
  <div class="app-container flex">
    <Sidebar />

    <div class="main-content flex-grow-1 p-4">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import Sidebar from './components/SidebarItem.vue'
</script>

<style>
.app-container {
  height: 100vh;
}
.main-content {
  overflow-y: auto;
}
</style>
