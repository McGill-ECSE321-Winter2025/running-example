<template>
  <div class="event-view-container">
    <Toast />
    <ConfirmPopup></ConfirmPopup>

    <Titlebar title="Events" :showNewButton="true">
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
          v-model:expandedRows="expandedRows"
          :value="events"
          :paginator="true"
          :rows="25"
          :resizableColumns="true"
          columnResizeMode="fit"
          scrollable
          scrollHeight="flex"
          dataKey="id"
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

          <Column expander style="width: 5rem" />
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

          <template #expansion="slotProps">
            <div class="expanded-row-content">
              <div class="flex justify-between items-center">
                <div>
                  <p><strong>Location/Link:</strong> {{ slotProps.data.locationOrLink }}</p>
                  <p><strong>Created By:</strong> {{ slotProps.data.createdBy }}</p>
                </div>
                <div>
                  <Button
                    label="Register"
                    icon="pi pi-check"
                    @click="registerForEvent(slotProps.data)"
                  />
                </div>
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
import { eventService } from '@/services/eventService'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import Button from 'primevue/button'
import Column from 'primevue/column'
import DataTable from 'primevue/datatable'
import Titlebar from '@/components/TitleBar.vue'
import DataTableCard from '@/components/DataTableCard.vue'
import NewEventFormPopover from '@/components/NewEventFormPopover.vue'
import EventFilterPopover from '@/components/EventFilterPopover.vue'
import Toast from 'primevue/toast'
import ConfirmPopup from 'primevue/confirmpopup'

const events = ref()
const loading = ref(true)
const error = ref(null)
const expandedRows = ref({})
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
    const fetchedEvents = await eventService.getAllEvents()
    events.value = fetchedEvents
  } catch (err) {
    error.value = 'Failed to load events. Please try again later.'
    console.error('Error loading events:', err)
  } finally {
    loading.value = false
  }
}

const expandAll = () => {
  expandedRows.value = events.value.reduce((acc, p) => {
    acc[p.id] = true
    return acc
  }, {})
}

const collapseAll = () => {
  expandedRows.value = null
}

const registerForEvent = (event) => {
  const isLoggedIn = !!localStorage.getItem('userId')

  if (!isLoggedIn) {
    window.dispatchEvent(new CustomEvent('show-login-modal'))
    return
  }

  confirm.require({
    target: event.currentTarget,
    message: `Are you sure you want to register for event: ${event.description}?`,
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
          summary: 'Registered',
          detail: `You have successfully registered for the event: ${event.description}`,
          life: 3000,
        })
      } catch (err) {
        toast.add({
          severity: 'error',
          summary: 'Registration Failed',
          detail: `Failed to register for the event: ${err}`,
          life: 3000,
        })
      }
    },
  })
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

.expanded-row-content {
  display: flex;
  flex-direction: column;
  width: 100%;
  overflow: hidden;
  padding-right: 2rem;
  padding-left: 2rem;
}

.expanded-row-content .flex {
  flex-wrap: wrap;
  gap: 1rem;
}

.expanded-row-content p {
  margin: 0;
}
</style>
