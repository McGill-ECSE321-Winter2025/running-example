<template>
  <div class="event-view-container">
    <Titlebar title="Events" :showNewButton="true">
      <template #newButton>
        <NewEventFormPopover />
      </template>
    </Titlebar>

    <DataTableCard
      title="Events"
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
      <Column field="remainingSeats" header="Seats" />
    </DataTableCard>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { eventService } from '@/services/eventService'
import Column from 'primevue/column'
import Titlebar from '@/components/Titlebar.vue'
import DataTableCard from '@/components/DataTableCard.vue'
import NewEventFormPopover from '@/components/NewEventFormPopover.vue'

const events = ref([])
const loading = ref(true)
const error = ref(null)

const formatDate = (dateString) => {
  if (!dateString) return ''

  const options = {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    hour12: true,
  }
  return new Date(dateString).toLocaleString(undefined, options)
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
.event-view-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  padding: 1rem;
  gap: 1rem;
}
</style>
