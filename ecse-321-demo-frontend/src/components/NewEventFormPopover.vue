<template>
  <Toast />
  <Button icon="pi pi-plus" class="gap-2" label="New Event" @click="toggle" />

  <Popover ref="op">
    <span class="font-medium block pb-2">Create a New Event</span>
    <Form
      v-slot="$form"
      :initialValues="formValues"
      :resolver="resolver"
      @submit="onFormSubmit"
      class="form-container"
    >
      <div class="form-grid">
        <div class="form-item">
          <IftaLabel>
            <InputText name="description" />
            <label for="description">Description</label>
            <Message
              v-if="$form.description?.invalid"
              severity="error"
              size="small"
              variant="simple"
            >
              {{ $form.description.error?.message }}
            </Message>
          </IftaLabel>
        </div>

        <div class="form-item">
          <IftaLabel>
            <Select name="eventType" :options="eventTypes" optionLabel="label" fluid />
            <label for="eventType">Event Type</label>
            <Message v-if="$form.eventType?.invalid" severity="error" size="small" variant="simple">
              {{ $form.eventType.error?.message }}
            </Message>
          </IftaLabel>
        </div>

        <div class="form-item">
          <IftaLabel>
            <InputText name="locationOrLink" />
            <label for="locationOrLink">{{ locationLinkLabel }}</label>
            <Message
              v-if="$form.locationOrLink?.invalid"
              severity="error"
              size="small"
              variant="simple"
            >
              {{ $form.locationLink.error?.message }}
            </Message>
          </IftaLabel>
        </div>

        <div class="form-item">
          <!-- THIS SHOULD BE AN INPUTNUMBER BUT IT'S ACTUALLY BUSTED AND IDK HOW TO FIX IT -->
          <IftaLabel>
            <InputText name="capacity" fluid />
            <label for="capacity">Event Capacity</label>
            <Message v-if="$form.capacity?.invalid" severity="error" size="small" variant="simple">
              {{ $form.capacity.error?.message }}
            </Message>
          </IftaLabel>
        </div>

        <div class="form-item">
          <IftaLabel>
            <DatePicker name="startTime" showTime hourFormat="12" />
            <label for="startTime">Start Time</label>
            <Message v-if="$form.startTime?.invalid" severity="error" size="small" variant="simple">
              {{ $form.startTime.error?.message }}
            </Message>
          </IftaLabel>
        </div>

        <div class="form-item">
          <IftaLabel>
            <DatePicker name="endTime" showTime hourFormat="12" />
            <label for="endTime">End Time</label>
            <Message v-if="$form.endTime?.invalid" severity="error" size="small" variant="simple">
              {{ $form.endTime.error?.message }}
            </Message>
          </IftaLabel>
        </div>
      </div>
      <Button type="submit" label="Submit" class="submit-button" />
    </Form>
  </Popover>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Form } from '@primevue/forms'
import { useToast } from 'primevue/usetoast'
import { eventService } from '@/services/eventService'
import Button from 'primevue/button'
import Select from 'primevue/select'
import Popover from 'primevue/popover'
import IftaLabel from 'primevue/iftalabel'
import InputText from 'primevue/inputtext'
import DatePicker from 'primevue/datepicker'
import Message from 'primevue/message'
import Toast from 'primevue/toast'

const op = ref()

const toggle = (event) => {
  op.value.toggle(event)
}

const toast = useToast()

const eventTypes = ref([
  { label: 'Online Event', value: 'ONLINE' },
  { label: 'In-Person Event', value: 'INPERSON' },
])

const formValues = ref({
  description: '',
  startTime: null,
  endTime: null,
  locationOrLink: '',
  eventType: '',
  capacity: null,
})

//This function is stupid, you should fix this...
const resolver = ({ values }) => {
  const errors = {}

  if (!values.description) {
    errors.description = [{ message: 'Description is required' }]
  }

  if (!values.startTime) {
    errors.startTime = [{ message: 'Start time is required' }]
  } else if (new Date(values.startTime) < new Date()) {
    errors.startTime = [{ message: 'Start time must be in the future' }]
  }

  if (!values.endTime) {
    errors.endTime = [{ message: 'End time is required' }]
  } else if (values.startTime && new Date(values.endTime) <= new Date(values.startTime)) {
    errors.endTime = [{ message: 'End time must be after start time' }]
  }

  if (!values.locationOrLink) {
    errors.locationOrLink = [{ message: 'Location/Link is required' }]
  }

  if (!values.capacity) {
    errors.capacity = [{ message: 'Capacity is required' }]
  } else if (isNaN(parseInt(values.capacity)) || parseInt(values.capacity) <= 0) {
    errors.apacity = [{ message: 'Capacity must be a positive number' }]
  }

  if (!values.eventType) {
    errors.eventType = [{ message: 'Event type is required' }]
  }

  return { errors, values }
}

//This doesn't work, fix it eventually
const locationLinkLabel = computed(() => {
  switch (formValues.value.eventType) {
    case 'ONLINE':
      return 'Link'
    case 'INPERSON':
      return 'Location'
    default:
      return 'Location/Link'
  }
})

const onFormSubmit = async ({ values, valid }) => {
  if (valid) {
    try {
      const eventData = {
        ...values,
        eventType: values.eventType.value,
        capacity: parseInt(values.capacity),
      }
      await eventService.createEvent(eventData)
      toast.add({
        severity: 'success',
        summary: 'Event created',
        life: 3000,
      })
      op.value.hide()
    } catch (error) {
      toast.add({
        severity: 'error',
        summary: 'Failed to create event',
        detail: error.message,
        life: 3000,
      })
    }
  }
}
</script>

<style scoped>
.form-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-item {
  display: flex;
  flex-direction: column;
}
</style>
