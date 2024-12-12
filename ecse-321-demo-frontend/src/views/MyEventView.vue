<template>
  <div class="my-events-container">
    <h1 class="text-3xl font-bold mb-4">My Events</h1>

    <div v-if="loading" class="flex justify-content-center">
      <ProgressSpinner />
    </div>

    <div v-else-if="error" class="p-4 bg-red-100 text-red-700 rounded">
      {{ error }}
    </div>

    <div v-else>
      <DataTable :value="events" :paginator="true" :rows="10" stripedRows responsiveLayout="scroll">
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
        <Column field="participantsCount" header="Participants" />
        <Column field="locationOrLink" header="Location/Link" />
        <Column header="Actions">
          <template #body="slotProps">
            <div class="flex gap-2">
              <Button
                icon="pi pi-pencil"
                class="p-button-rounded p-button-text"
                @click="openUpdateDialog(slotProps.data)"
              />
              <Button
                icon="pi pi-users"
                class="p-button-rounded p-button-text"
                @click="openRegistrationsSidebar(slotProps.data)"
              />
            </div>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>

  <UpdateEventDialog
    v-model:visible="updateDialogVisible"
    :event="selectedEvent"
    @eventUpdated="handleEventUpdated"
  />

  <EventSidebar v-model:visible="sidebarVisible" :eventId="selectedEventId" />
</template>

<script setup>
import { ref, onMounted } from 'vue'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Button from 'primevue/button'
import ProgressSpinner from 'primevue/progressspinner'
import { eventService } from '@/services/eventService'
import UpdateEventDialog from '@/components/UpdateEventItem.vue'
import EventSidebar from '@/components/EventSidebarItem.vue'

const events = ref([])
const loading = ref(true)
const error = ref(null)
const updateDialogVisible = ref(false)
const sidebarVisible = ref(false)
const selectedEvent = ref(null)
const selectedEventId = ref(null)

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}

const fetchEvents = async () => {
  try {
    loading.value = true
    error.value = null
    const userId = localStorage.getItem('userId')
    events.value = await eventService.getEventsByCreator(userId)
  } catch (err) {
    error.value = 'Failed to load events. Please try again later.'
    console.error('Error loading events:', err)
  } finally {
    loading.value = false
  }
}

const openUpdateDialog = (event) => {
  selectedEvent.value = event
  updateDialogVisible.value = true
}

const openRegistrationsSidebar = (event) => {
  selectedEventId.value = event.id
  sidebarVisible.value = true
}

const handleEventUpdated = async () => {
  await fetchEvents()
}

onMounted(() => {
  fetchEvents()
})
</script>

<style scoped>
.my-events-container {
  padding: 1rem;
  max-width: 1200px;
  margin: 0 auto;
}
</style>
