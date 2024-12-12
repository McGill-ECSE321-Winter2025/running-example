<template>
  <Dialog
    :visible="visible"
    @update:visible="$emit('update:visible', false)"
    :modal="true"
    header="Update Event"
  >
    <div class="flex flex-column gap-3">
      <div class="p-float-label">
        <InputText id="description" v-model="formData.description" />
        <label for="description">Description</label>
      </div>

      <div class="p-float-label">
        <Calendar
          id="startTime"
          v-model="formData.startTime"
          showTime
          :minDate="new Date()"
          :maxDate="formData.endTime"
        />
        <label for="startTime">Start Time</label>
      </div>

      <div class="p-float-label">
        <Calendar
          id="endTime"
          v-model="formData.endTime"
          showTime
          :minDate="formData.startTime || new Date()"
        />
        <label for="endTime">End Time</label>
      </div>

      <div class="p-float-label">
        <InputNumber
          id="capacity"
          v-model="formData.capacity"
          :min="currentParticipants"
          :step="1"
        />
        <label for="capacity">Capacity</label>
      </div>

      <div class="p-float-label">
        <InputText
          id="locationOrLink"
          v-model="formData.locationOrLink"
          :placeholder="event.eventType === 'ONLINE' ? 'Meeting Link' : 'Location'"
        />
        <label for="locationOrLink">
          {{ event.eventType === 'ONLINE' ? 'Meeting Link' : 'Location' }}
        </label>
      </div>

      <small v-if="error" class="p-error">{{ error }}</small>
    </div>

    <template #footer>
      <Button
        label="Cancel"
        icon="pi pi-times"
        class="p-button-text"
        @click="$emit('update:visible', false)"
      />
      <Button label="Update" icon="pi pi-check" @click="handleSubmit" :loading="loading" />
    </template>
  </Dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import InputNumber from 'primevue/inputnumber'
import Calendar from 'primevue/calendar'
import Button from 'primevue/button'
import { eventService } from '@/services/eventService'

const props = defineProps({
  visible: Boolean,
  event: Object,
})

const emit = defineEmits(['update:visible', 'eventUpdated'])

const loading = ref(false)
const error = ref('')
const currentParticipants = ref(0)
const formData = ref({
  description: '',
  startTime: null,
  endTime: null,
  capacity: null,
  locationOrLink: '',
})

onMounted(() => {
  if (props.event) {
    formData.value = {
      description: props.event.description,
      startTime: new Date(props.event.startTime),
      endTime: new Date(props.event.endTime),
      capacity: props.event.capacity,
      locationOrLink: props.event.locationOrLink,
    }
    currentParticipants.value = props.event.participantsCount || 0
  }
})

const handleSubmit = async () => {
  try {
    loading.value = true
    error.value = ''

    await eventService.updateEvent(props.event.id, formData.value)
    emit('eventUpdated')
    emit('update:visible', false)
  } catch (err) {
    error.value = err.response?.data?.message || 'Failed to update event'
  } finally {
    loading.value = false
  }
}
</script>
