<template>
  <div class="registrations-container">
    <h1 class="text-3xl font-bold mb-4">My Registrations</h1>

    <div v-if="loading" class="flex justify-content-center">
      <ProgressSpinner />
    </div>

    <div v-else-if="error" class="p-4 bg-red-100 text-red-700 rounded">
      {{ error }}
    </div>

    <div v-else>
      <DataTable :value="registrations" :paginator="true" :rows="10" responsiveLayout="scroll">
        <Column field="username" header="Username"></Column>
        <Column field="eventDescription" header="Event Description"></Column>
      </DataTable>

      <div v-if="registrations.length === 0" class="text-center p-4">No registrations found</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import ProgressSpinner from 'primevue/progressspinner'
import api from '@/services/api'

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
.registrations-container {
  padding: 1rem;
  max-width: 1200px;
  margin: 0 auto;
}

:deep(.p-datatable) {
  margin-top: 1rem;
}

:deep(.p-column-header-content) {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>
