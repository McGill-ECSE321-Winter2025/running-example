<template>
  <div class="registration-view-container">
    <Toast />
    <ConfirmPopup></ConfirmPopup>

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
          <Column field="registrationDate" header="Registration Date" />
          <Column class="w-24 !text-end">
            <template #body>
              <Button icon="pi pi-trash" severity="secondary" rounded @click="unregister" />
            </template>
          </Column>

          <template #expansion="slotProps">
            <div class="p-2">
              <p><strong>Location/Link:</strong> {{slotProps.}}</p>
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
import { useConfirm } from 'primevue/useconfirm'
import Column from 'primevue/column'
import api from '@/services/api'
import DataTableCard from '@/components/DataTableCard.vue'
import Button from 'primevue/button'
import DataTable from 'primevue/datatable'
import Titlebar from '@/components/TitleBar.vue'
import Toast from 'primevue/toast'
import ConfirmPopup from 'primevue/confirmpopup'
import RegistrationFilterPopover from '@/components/RegistrationFilterPopover.vue'

const registrations = ref({})
const loading = ref(true)
const error = ref(null)
const expandedRows = ref({})
const toast = useToast()
const confirm = useConfirm()

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
.registration-view-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  padding: 1rem;
  gap: 1rem;
}
</style>
