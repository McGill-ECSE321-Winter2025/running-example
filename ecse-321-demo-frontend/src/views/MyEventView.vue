<template>
  <div class="my-event-view-container">
    <Toast />
    <ConfirmPopup></ConfirmPopup>

    <Titlebar title="My Events" :showNewButton="true">
      <template #filterButton>
        <EventFilterPopover />
      </template>
      <template #newButton>
        <NewEventFormPopover />
      </template>
    </Titlebar>

    <DataTableCard :loading="loading" :error="error">
      <template #datatable>
        <DataTable
          :expandedRows="expandedRows"
          :value="events"
          :paginator="true"
          :rows="10"
          :resizeableColumns="true"
          columnResizeMode="fit"
          scrollable
          scrollHeight="flex"
          dataKey="id"
          @rowExpand="onRowExpand"
        >
          <template #header>
            <div class="flex justify-between items-center w-full">
              <div>Total Events: {{ events.length }}</div>
              <div class="flex flex-wrap justify-end gap-2">
                <Button text icon="pi pi-plus" label="Expand All" @click="expandAll" />
                <Button text icon="pi pi-minus" label="Collapse All" @click="collapseAll" />
              </div>
            </div>
          </template>
          <Column expander style="width: 3rem" />
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
          <Column field="capacity" header="Capacity" />

          <template #expansion="slotProps">
            <div class="expanded-row-content">
              <div class="flex justify-between items-center">
                <div>
                  <p><strong>Location/Link:</strong> {{ slotProps.data.locationOrLink }}</p>
                  <p><strong>Created At:</strong> {{ formatDate(slotProps.data.createdAt) }}</p>
                </div>
                <div>
                  <Button label="Edit" icon="pi pi-pencil" @click="editEvent(slotProps.data)" />
                </div>
              </div>
              <div class="flex flex-col gap-2">
                <h3 class="font-bold">Registrations</h3>
                <DataTable
                  :value="eventRegistrations[slotProps.data.id]"
                  scrollable
                  scrollHeight="200px"
                >
                  <Column field="username" header="Username" />
                  <Column field="registrationDate" header="Registration Date" />
                  <Column class="w-24 !text-end">
                    <template #body>
                      <Button
                        icon="pi pi-trash"
                        @click="deleteRegistration"
                        severity="secondary"
                        rounded
                      ></Button>
                    </template>
                  </Column>
                </DataTable>
              </div>
            </div>
          </template>
        </DataTable>
      </template>
    </DataTableCard>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Column from 'primevue/column'
import { eventService } from '@/services/eventService'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import DataTableCard from '@/components/DataTableCard.vue'
import NewEventFormPopover from '@/components/NewEventFormPopover.vue'
import Titlebar from '@/components/TitleBar.vue'
import EventFilterPopover from '@/components/EventFilterPopover.vue'
import DataTable from 'primevue/datatable'
import Toast from 'primevue/toast'
import ConfirmPopup from 'primevue/confirmpopup'
import Button from 'primevue/button'

const events = ref({})
const loading = ref(true)
const error = ref(null)
const expandedRows = ref({})
const eventRegistrations = ref({})
const toast = useToast()
const confirm = useConfirm()

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
    const userId = localStorage.getItem('userId')
    events.value = await eventService.getEventsByCreator(userId)
  } catch (err) {
    error.value = 'Failed to load events. Please try again later.'
    console.error('Error loading events:', err)
  } finally {
    loading.value = false
  }
}

const fetchRegistrations = async (eventId) => {
  try {
    const registrations = await eventService.getEventRegistrations(eventId)
    eventRegistrations.value = {
      ...eventRegistrations.value,
      [eventId]: registrations,
    }
  } catch (err) {
    console.error('Error loading registrations:', err)
  }
}

const onRowExpand = (event) => {
  fetchRegistrations(event.data.id)
}

const expandAll = async () => {
  expandedRows.value = events.value.reduce((acc, p) => {
    acc[p.id] = true
    return acc
  }, {})

  for (const event of events.value) {
    await fetchRegistrations(event.id)
  }
}

const collapseAll = () => {
  expandedRows.value = {}
}

const editEvent = (event) => {
  confirm.require({
    target: event.currentTarget,
    message: `Are you sure you want to edit this event?`,
    icon: 'pi pi-exclamation-circle',
    rejectProps: {
      label: 'Cancel',
      severity: 'secondary',
    },
    acceptProps: {
      label: 'Confirm',
    },
    accept: async () => {
      try {
        await eventService.registerForEvent(event.id)
        toast.add({
          severity: 'success',
          summary: 'Event Updated',
          detail: `You have successfully edited the event: ${event.description}`,
          life: 3000,
        })
      } catch (err) {
        toast.add({
          severity: 'error',
          summary: 'Edit Failed',
          detail: `Failed to edit for the event: ${err}`,
          life: 3000,
        })
      }
    },
  })
}

const deleteRegistration = () => {
  toast.add({
    severity: 'info',
    summary: 'Up to you to implement!',
    life: 3000,
  })
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

.expanded-row-content {
  display: flex;
  flex-direction: column;
  width: 100%;
  overflow: hidden;
  padding: 1rem;
  gap: 1rem;
}

.expanded-row-content .flex {
  flex-wrap: wrap;
  gap: 1rem;
}

.expanded-row-content p {
  margin: 0;
}
</style>
