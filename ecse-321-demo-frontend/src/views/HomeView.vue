<template>
  <div class="home-container">
    <div class="title-section">
      <Toolbar class="border-none">
        <template #start>
          <h1 class="text-3xl font-bold">Home: Welcome to Event+</h1>
        </template>
      </Toolbar>
    </div>

    <div class="content-grid">
      <div class="info-section">
        <Card>
          <template #title> About Event+ </template>
          <template #content>
            <p class="m-0">herro herro herro</p>
          </template>
        </Card>
      </div>

      <div class="events-section">
        <Card class="h-full">
          <template #title> Recently Added Events </template>
          <template #content>
            <div class="table-wrapper">
              <div v-if="loading" class="flex justify-center items-center p-4">
                <ProgressSpinner />
              </div>

              <div v-else-if="error" class="p-4 bg-red-100 text-red-700 rounded">
                {{ error }}
              </div>

              <div v-else class="datatable-container">
                <DataTable
                  :value="events"
                  :paginator="true"
                  :rows="25"
                  scrollable
                  scrollHeight="flex"
                  class="h-full"
                  responsiveLayout="scroll"
                >
                  <Column field="description" header="Description" />
                  <Column field="eventType" header="Type" />
                  <Column header="Start Time">
                    <template #body="slotProps">
                      {{ formatDate(slotProps.data.startTime) }}
                    </template>
                  </Column>
                  <Column field="remainingSeats" header="Available Seats" />
                </DataTable>
              </div>
            </div>
          </template>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Card from 'primevue/card'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import ProgressSpinner from 'primevue/progressspinner'
import Toolbar from 'primevue/toolbar'
import { eventService } from '@/services/eventService'

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
  }
  return new Date(dateString).toLocaleString(undefined, options)
}

const fetchTrendingEvents = async () => {
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
  fetchTrendingEvents()
})
</script>

<style scoped>
.home-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 1rem;
  gap: 1rem;
}

.title-section {
  flex-shrink: 0;
}

.content-grid {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 3fr;
  gap: 1rem;
  min-height: 0;
  overflow: hidden;
}

.info-section,
.events-section {
  min-height: 0;
  overflow: hidden;
}

:deep(.p-card) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.p-card-body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

:deep(.p-card-content) {
  flex: 1;
  padding: 1rem;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.table-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.datatable-container {
  flex: 1;
  overflow: hidden;
}

:deep(.p-datatable) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

:deep(.p-datatable-wrapper) {
  flex: 1;
  min-height: 0;
}

:deep(.p-datatable-table) {
  min-width: 100%;
}

:deep(.p-datatable-scrollable-body) {
  overflow-y: scroll !important;
}

@media (max-width: 768px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}
</style>
