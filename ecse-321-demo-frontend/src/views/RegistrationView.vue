<template>
  <div class="registration-view-container">
    <Titlebar title="Registrations" />
    <DataTableCard
      title="My Registrations"
      :data="registrations"
      :loading="loading"
      :error="error"
      @search="handleSearch"
      @new="handleNew"
    >
      <Column field="username" header="Creator"></Column>
      <Column field="eventDescription" header="Event Title"></Column>
    </DataTableCard>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Column from 'primevue/column'
import api from '@/services/api'
import DataTableCard from '@/components/DataTableCard.vue'
import Titlebar from '@/components/Titlebar.vue'

const registrations = ref([])
const loading = ref(true)
const error = ref(null)

const fetchRegistrations = async () => {
  try {
    loading.value = true
    error.value = null
    const response = await api.get('/registrations/my-registrations')
    registrations.value = response.data
  } catch (err) {
    error.value = 'Failed to load registrations. Please try again later.'
    console.error('Error loading registrations:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRegistrations()
})
</script>

<style scoped>
.registration-view-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  padding: 1rem;
  gap: 1rem;
}
</style>
