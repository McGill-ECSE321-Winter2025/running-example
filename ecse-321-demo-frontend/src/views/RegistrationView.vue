<template>
  <div class="registrations-container">
    <TitlebarItem title="My Registrations" @search="handleSearch" @new="handleNew">
      <template #filterContent> </template>
    </TitlebarItem>

    <div v-if="loading" class="flex justify-content-center">
      <ProgressSpinner />
    </div>

    <div v-else-if="error" class="p-4 bg-red-100 text-red-700 rounded">
      {{ error }}
    </div>

    <div v-else>
      <Card>
        <template #content>
          <DataTable :value="registrations" :paginator="true" :rows="10" responsiveLayout="scroll">
            <Column field="username" header="Creator"></Column>
            <Column field="eventDescription" header="Event Title"></Column>
          </DataTable>

          <div v-if="registrations.length === 0" class="text-center p-4">
            No registrations found
          </div>
        </template>
      </Card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import ProgressSpinner from 'primevue/progressspinner'
import Card from 'primevue/card'
import api from '@/services/api'
import TitlebarItem from '@/components/TitlebarItem.vue'

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
