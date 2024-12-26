<template>
  <div class="event-container flex flex-col h-full pt-4">
    <div class="titlebar flex-shrink-0">
      <TitlebarItem title="Events" :showNewButton="true" @search="handleSearch" @new="handleNew">
        <template #filterContent> </template>
      </TitlebarItem>
    </div>
    <div class="content flex-grow">
      <div v-if="loading" class="flex justify-center items-center p-4">
        <ProgressSpinner />
      </div>

      <div v-else-if="error" class="p-4 bg-red-100 text-red-700 rounded">
        {{ error }}
      </div>

      <div v-else class="h-full">
        <Card class="h-full">
          <template #content>
            <div class="datatable-container h-full">
              <DataTable
                :value="events"
                :paginator="true"
                :rows="25"
                scrollable
                scrollHeight="flex"
                class="h-full"
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
              </DataTable>
            </div>
          </template>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { eventService } from '@/services/eventService'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import ProgressSpinner from 'primevue/progressspinner'
import Card from 'primevue/card'
import TitlebarItem from '@/components/TitlebarItem.vue'

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
.event-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  gap: 1rem;
}

.content {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 100%;
}

:deep(.p-card) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.p-card-body) {
  height: 100%;
}

:deep(.p-card-content) {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0;
}

.datatable-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.datatable-container .p-datatable {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.p-datatable-scrollable-body {
  flex: 1;
  overflow-y: auto;
}
</style>
