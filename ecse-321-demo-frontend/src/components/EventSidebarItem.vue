<template>
  <Sidebar
    :visible="visible"
    position="right"
    :modal="true"
    :closeable="true"
    @update:visible="$emit('update:visible', false)"
    class="w-full md:w-30rem"
  >
    <template #header>
      <h3>Event Registrations</h3>
    </template>

    <div v-if="loading" class="flex justify-content-center">
      <ProgressSpinner />
    </div>
    <div v-else-if="error" class="p-error">
      {{ error }}
    </div>
    <div v-else>
      <DataTable :value="registrations" class="p-datatable-sm">
        <Column field="username" header="Username"></Column>
      </DataTable>

      <div v-if="registrations.length === 0" class="text-center p-4">No registrations found</div>
    </div>
  </Sidebar>
</template>

<script setup>
import { ref, watch } from 'vue'
import Sidebar from 'primevue/sidebar'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import ProgressSpinner from 'primevue/progressspinner'
import { eventService } from '@/services/eventService'

const props = defineProps({
  visible: Boolean,
  eventId: String,
})

defineEmits(['update:visible'])

const registrations = ref([])
const loading = ref(false)
const error = ref(null)

const fetchRegistrations = async () => {
  if (!props.eventId) return

  try {
    loading.value = true
    error.value = null
    registrations.value = await eventService.getEventRegistrations(props.eventId)
  } catch (err) {
    error.value = 'Failed to load registrations'
    console.error(err)
  } finally {
    loading.value = false
  }
}

watch(
  () => props.visible,
  (newValue) => {
    if (newValue) {
      fetchRegistrations()
    }
  },
)
</script>
