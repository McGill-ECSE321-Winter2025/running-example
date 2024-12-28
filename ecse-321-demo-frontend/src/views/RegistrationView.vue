<template>
  <div class="registration-view-container">
    <Toast />

    <Titlebar title="Registrations">
      <template #filterButton>
        <RegistrationFilterPopover />
      </template>
    </Titlebar>

    <DataTableCard :loading="loading" :error="error">
      <template #datatable>
        <DataTable
          :expandedRows="expandedRows"
          :value="registrations"
          :paginator="true"
          :rows="25"
          :resizeableColumns="true"
          columnResizeMode="fit"
          scrollable
          scrollHeight="flex"
          dataKey="id"
          @rowExpand="onRowExpand"
        >
          <template #header>
            <div class="flex justify-between items-center w-full">
              <div>Total Registrations: {{ registrations.length }}</div>
              <div class="flex flex-wrap justify-end gap-2">
                <Button text icon="pi pi-plus" label="Expand All" @click="expandAll" />
                <Button text icon="pi pi-minus" label="Collapse All" @click="collapseAll" />
              </div>
            </div>
          </template>
          <Column expander style="width: 3rem" />
          <Column field="creatorUsername" header="Creator" />
          <Column field="eventDescription" header="Event Title" />
          <Column header="Registerd At">
            <template #body="slotProps">
              {{ formatDate(slotProps.data.registeredAt) }}
            </template>
          </Column>
          <Column class="w-24 !text-end">
            <template #body>
              <Button icon="pi pi-trash" severity="secondary" rounded @click="unregister" />
            </template>
          </Column>

          <template #expansion="slotProps">
            <div class="expanded-row-content">
              <div v-if="eventDetails[slotProps.data.eventId]">
                <p>
                  <strong>Description:</strong>
                  {{ eventDetails[slotProps.data.eventId].description }}
                </p>
                <p><strong>Type:</strong> {{ eventDetails[slotProps.data.eventId].eventType }}</p>
                <p>
                  <strong>Start Time:</strong>
                  {{ formatDate(eventDetails[slotProps.data.eventId].startTime) }}
                </p>
                <p>
                  <strong>End Time:</strong>
                  {{ formatDate(eventDetails[slotProps.data.eventId].endTime) }}
                </p>
                <p>
                  <strong>Location/Link:</strong>
                  {{ eventDetails[slotProps.data.eventId].locationOrLink }}
                </p>
              </div>
              <div v-else>
                <ProgressSpinner />
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
import { useToast } from 'primevue/usetoast'
import { eventService } from '@/services/eventService'
import { registrationService } from '@/services/registrationService'
import formatDate from '@/utils'
import Column from 'primevue/column'
import DataTableCard from '@/components/DataTableCard.vue'
import Button from 'primevue/button'
import DataTable from 'primevue/datatable'
import Titlebar from '@/components/TitleBar.vue'
import Toast from 'primevue/toast'
import ConfirmPopup from 'primevue/confirmpopup'
import RegistrationFilterPopover from '@/components/RegistrationFilterPopover.vue'

const registrations = ref([])
const loading = ref(true)
const error = ref(null)
const expandedRows = ref({})
const eventDetails = ref({})
const toast = useToast()

const fetchRegistrations = async () => {
  try {
    loading.value = true
    error.value = null
    const response = await registrationService.getUserRegistrations()
    registrations.value = response
  } catch (err) {
    error.value = 'Failed to load registrations. Please try again later.'
    console.error('Error loading registrations:', err)
  } finally {
    loading.value = false
  }
}

const fetchEventDetails = async (eventId) => {
  try {
    if (!eventDetails.value[eventId]) {
      const event = await eventService.getEvent(eventId)
      eventDetails.value = {
        ...eventDetails.value,
        [eventId]: event,
      }
    }
  } catch (err) {
    console.error('Error loading event details:', err)
  }
}

const onRowExpand = async (event) => {
  await fetchEventDetails(event.data.eventId)
}

const expandAll = async () => {
  expandedRows.value = registrations.value.reduce((acc, p) => {
    acc[p.id] = true
    return acc
  }, {})

  for (const registration of registrations.value) {
    await fetchEventDetails(registration.eventId)
  }
}

const collapseAll = () => {
  expandedRows.value = {}
}

const unregister = () => {
  toast.add({
    severity: 'info',
    summary: 'Up to you to implement!',
    life: 3000,
  })
}

onMounted(() => {
  fetchRegistrations()
})
</script>

<style scoped>
.registration-view-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  padding: 1rem;
  gap: 1rem;
}
</style>
