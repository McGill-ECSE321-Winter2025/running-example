<template>
  <div class="my-event-view-container">
    <Titlebar title="My Events" :showNewButton="true">
      <template #newButton>
        <NewEventFormPopover />
      </template>
    </Titlebar>

    <DataTableCard
      title="My Events"
      :data="events"
      :loading="loading"
      :error="error"
      :showNewButton="true"
      @search="handleSearch"
      @new="handleNew"
    >
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
      <Column>
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
    </DataTableCard>
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
import Column from 'primevue/column'
import Button from 'primevue/button'
import { eventService } from '@/services/eventService'
import UpdateEventDialog from '@/components/UpdateEventItem.vue'
import EventSidebar from '@/components/EventSidebarItem.vue'
import DataTableCard from '@/components/DataTableCard.vue'
import NewEventFormPopover from '@/components/NewEventFormPopover.vue'
import Titlebar from '@/components/TitleBar.vue'

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
.my-event-view-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  padding: 1rem;
  gap: 1rem;
}
</style>
