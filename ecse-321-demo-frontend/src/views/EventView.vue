<template>
  <div class="events-container">
    <h1 class="text-3xl font-bold mb-4">Events</h1>

    <div v-if="loading" class="flex justify-center items-center p-4">
      <ProgressSpinner />
    </div>

    <div v-else-if="error" class="p-4 bg-red-100 text-red-700 rounded">
      {{ error }}
    </div>

    <div v-else class="grid gap-4">
      <DataTable :value="events" :paginator="true" :rows="10" responsive-layout="scroll">
        <Column field="description" header="Description" />
        <Column field="eventType" header="Type" />
        <Column header="Start Time">
          <template #body="slotProps">
            {{ formatDate(slotProps.data.startTime) }}
          </template>
        </Column>
        <Column header="End Time">
          <template #body="slotProps">
            {{ formatDate(slotProps.data.endTime) }}
          </template>
        </Column>
        <Column field="capacity" header="Capacity" />
        <Column field="locationOrLink" header="Location/Link" />
      </DataTable>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { eventService } from '@/services/eventService'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import ProgressSpinner from 'primevue/progressspinner'

const events = ref([])
const loading = ref(true)
const error = ref(null)

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}

const fetchEvents = async () => {
  try {
    loading.value = true
    error.value = null
    const fetchedEvents = await eventService.getAllEvents()
    events.value = fetchedEvents
  } catch (err) {
    error.value = 'Failed to load events. Please try again later.'
    console.error('Error loading events:', err)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchEvents()
})
</script>

<style scoped>
.events-container {
  padding: 1rem;
  max-width: 1200px;
  margin: 0 auto;
}
</style>
