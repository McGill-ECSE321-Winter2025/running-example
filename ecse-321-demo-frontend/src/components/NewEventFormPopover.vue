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
            <InputText name="locationLink" />
            <label for="locationLink">{{ locationLinkLabel }}</label>
            <Message
              v-if="$form.locationLink?.invalid"
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
            <InputText name="eventCapacity" fluid />
            <label for="eventCapacity">Event Capacity</label>
            <Message
              v-if="$form.eventCapacity?.invalid"
              severity="error"
              size="small"
              variant="simple"
            >
              {{ $form.eventCapacity.error?.message }}
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
  locationLink: '',
  eventType: '',
  eventCapacity: null,
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

  if (!values.locationLink) {
    errors.locationLink = [{ message: 'Location/Link is required' }]
  }

  if (!values.eventCapacity) {
    errors.eventCapacity = [{ message: 'Capacity is required' }]
  } else if (isNaN(parseInt(values.eventCapacity)) || parseInt(values.eventCapacity) <= 0) {
    errors.eventCapacity = [{ message: 'Capacity must be a positive number' }]
  }

  if (!values.eventType) {
    errors.eventType = [{ message: 'Event type is required' }]
  }

  return { errors }
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

const onFormSubmit = ({ valid }) => {
  if (valid) {
    toast.add({
      severity: 'success',
      summary: 'Event created',
      life: 3000,
    })
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
